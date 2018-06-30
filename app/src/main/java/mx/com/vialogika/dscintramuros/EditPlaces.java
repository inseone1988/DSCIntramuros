package mx.com.vialogika.dscintramuros;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

interface actions{
    void onApostamientoSaved(EditPlaces instance);
    void onIncidenceConfirm(EditPlaces instance);
    void onSaveToDb(EditPlaces instance);
}
public class EditPlaces{
    private int total;
    private int count;
    private String grupo;
    private String MODE;
    private List<String> elementos;
    private List<String> apList = Databases.apNames();
    private List<String> incList;
    private List<String> incTypelist;
    private ArrayAdapter gAdapter;
    private ArrayAdapter apAdapter;
    private ArrayAdapter incAdapter;
    private ArrayAdapter increasonAdapter;
    private Boolean plComplete = false;
    private Boolean hasincidence = false;
    private Boolean hasErrors = false;
    private MaterialDialog mDialog;
    private Aps lastSavedAp;
    private actions callbacks;

    public EditPlaces(Context context,MaterialDialogPayload data,actions mCalbbacks){
        this.MODE = data.getMODE();
        this.grupo = data.getGrupo();
        this.callbacks = mCalbbacks;
        getDialogData();
        mDialog = setupDialog(context);
        //By default disable incidence reason spinner
        View v = mDialog.getCustomView();
        disableSpinner(v);
        setDialogData();
        setDialogIteractions(v);
        monitor();
        alreadySavedGroupMonitor();
    }

    private MaterialDialog setupDialog(Context context){
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title("Asignar Apostamiento")
                .customView(R.layout.add_plantilla_apostamiento,true)
                .positiveText("Agregar")
                .negativeText("Guardar")
                .autoDismiss(false)
                .show();
        return dialog;
    }

    private void setDialogData(){

        View v = mDialog.getCustomView();
        //TODO:Set Counters
        //Set group Text
        setGrupoText(v);
        setElementsdata(v);
        setApAutocomplete(v);
        setIncidenceList(v);
        setInReasList(v);
    }

    private void setGrupoText(View v){
        TextView grupotext = v.findViewById(R.id.groupNo);
        grupotext.setText(grupo);
    }

    private void getGuardNames(){
        if(MODE.equals("new")){
            elementos = Databases.enames();
        }else{
            elementos = Databases.availableElementos(grupo);
        }
    }

    private void setElementsdata(View v){
        AutoCompleteTextView elementsnames = v.findViewById(R.id.guardname);
        gAdapter = new ArrayAdapter(elementsnames.getContext(),android.R.layout.simple_dropdown_item_1line,elementos);
        elementsnames.setAdapter(gAdapter);
    }

    private void setApAutocomplete(View v){
        AutoCompleteTextView aps = v.findViewById(R.id.apostamiento);
        apAdapter = new ArrayAdapter(aps.getContext(),android.R.layout.simple_dropdown_item_1line,apList);
        aps.setAdapter(apAdapter);
    }

    private void setIncidenceList(View v){
        Spinner spinc = v.findViewById(R.id.incidencia);
        incAdapter = new ArrayAdapter(spinc.getContext(),android.R.layout.simple_spinner_item,incList);
        incAdapter.setDropDownViewResource(R.layout.dsc_spinner_dropdown);
        spinc.setAdapter(incAdapter);
    }

    private void setInReasList(View v){
        Spinner spinre = v.findViewById(R.id.tipoincidncia);
        increasonAdapter = new ArrayAdapter(spinre.getContext(),android.R.layout.simple_spinner_item,incTypelist);
        increasonAdapter.setDropDownViewResource(R.layout.dsc_spinner_dropdown);
        spinre.setAdapter(increasonAdapter);
    }

    private void disableSpinner(View v){
        Spinner sp = v.findViewById(R.id.tipoincidncia);
        sp.setSelection(0,true);
                sp.setEnabled(false);
    }

    private void getDialogData(){
        //Get counters data
        //Get all elements,Get asigned elements,resolve which list to show
        getGuardNames();
        setUpIncidences();
        incidencesReasons();
    }

    private boolean validateElement(String element){
        boolean isvalid = false;
        if(isAvailable(element)){
            isvalid = true;
        }
        return isvalid;
    }

    private boolean checkIncidence(String incidenceField){
        boolean isIncidence = false;
        if(!incidenceField.equals("Tiempo ordinario")){
            isIncidence = true;
            //TODO:Check explicit incidence check by field avoiding set to false when we have incidence
            hasincidence = true;
        }
        return isIncidence;
    }

    private void disableSaveGuardButton(){
        mDialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
    }

    private void disableReportButton(){
        mDialog.getActionButton(DialogAction.NEGATIVE).setEnabled(false);
    }

    private void alreadySavedGroupMonitor(){
        if(!MODE.equals("new")){
            if(Databases.plantillaIsSaved(grupo)){
                disableReportButton();
                disableSaveGuardButton();
            }
        }
    }

    private void noElementsMonitor(){
        if(elementos.size()==0){
            mDialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
        }
    }

    private void monitor(){
        noElementsMonitor();
        //alreadySavedGroupMonitor();
    }

    private boolean getHasIncidence(){
        return hasincidence;
    }

    private boolean isValidAp(String ap){
        boolean isvalid = false;
        if(apIsAvailable(ap)){
            isvalid = true;
        }
        return isvalid;
    }

    private TextView getErrorTextView(View v){
        return (TextView) v.findViewById(R.id.errortext);
    }

    private void setErrorText(View v,String message){
        final TextView errorField = getErrorTextView(v);
        errorField.setText(message);
        new CountDownTimer(3000, 1000){
            /**
             * Callback fired on regular interval.
             *
             * @param millisUntilFinished The amount of time until finished.
             */
            @Override
            public void onTick(long millisUntilFinished) {

            }

            /**
             * Callback fired when the time is up.
             */
            @Override
            public void onFinish() {
                errorField.setText("");
            }
        }.start();
    }

    private void onElementoSave(){
        long siteid = Databases.siteId(mDialog.getContext());
        long provid = Databases.providerId(mDialog.getContext());
        View v = mDialog.getCustomView();
        String ElementName = getGuardName(v);
        String ApName = getApName(v);
        String incidence = getIncidenceType(v);
        String increason  = getIncidenceReason(v);
        Elementos element = Databases.getElemento(ElementName);
        if(isAvailable(ElementName)){
            if(isValidAp(ApName)){
                if(checkIncidence(incidence)){
                Incidences inc = new Incidences(Databases.sNow(),incidence,increason,null);
                PlantillaPlace pl = new PlantillaPlace(grupo,ElementName,ApName,incidence);
                inc.save();
                pl.setSiteId(siteid);
                pl.setProvId(String.valueOf(provid));
                pl.setIcId(inc.getMUID());
                pl.save();
                removeElement(ElementName);
                lastSavedAp = new Aps(pl.getId(),ElementName,ApName,element.getPerson_photo_path());
                callbacks.onIncidenceConfirm(this);
                callbacks.onApostamientoSaved(this);
                clearTextFields();
                monitor();
                }else{
                    PlantillaPlace pl = new PlantillaPlace(grupo,ElementName,ApName,incidence);
                    pl.setSiteId(siteid);
                    pl.setProvId(String.valueOf(provid));
                    long id = pl.save();
                    removeElement(ElementName);
                    lastSavedAp = new Aps(id,ElementName,ApName,element.getPerson_photo_path());
                    callbacks.onApostamientoSaved(this);
                    clearTextFields();
                    monitor();
                }
            }else{
                setErrorText(v,"Apostamiento no valido");
            }
        }else{
            setErrorText(v,"Elemento no valido");
        }

    }

    public Aps getLastSavedAp() {
        return lastSavedAp;
    }

    private void removeElement(String element){
        elementos.remove(element);
        gAdapter.notifyDataSetChanged();
    }

    private void removeListItem(List mList, String element){
        for(int i = 0;i < mList.size();i++){
            if(mList.get(i).equals(element)){
                mList.remove(i);
            }
        }
    }

    private boolean apIsAvailable(String ap){
        boolean found = false;
        for(int i = 0;i < apList.size();i++){
            if(apList.get(i).equals(ap)){
                found = true;
            }
        }
        return  found;
    }

    private Boolean isAvailable(String element){
        boolean found = false;
        for(int i = 0;i < elementos.size();i++){
            if(elementos.get(i).equals(element)){
                found = true;
            }
        }
        return  found;
    }

    private void setMBOnClick(View view){
        MDButton ok = mDialog.getActionButton(DialogAction.POSITIVE);
        MDButton guardar = mDialog.getActionButton(DialogAction.NEGATIVE);
        setOkButtonIOnClick(ok);
        setGuardarButtonOnClick(guardar);
    }

    private void setOkButtonIOnClick(MDButton okbutton){
        okbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onElementoSave();
            }
        });
    }

    private void setGuardarButtonOnClick(MDButton guardar){
        final EditPlaces ep = this;
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               callbacks.onSaveToDb(ep);
            }
        });
    }

    public void hideDialog (){
        mDialog.hide();
    }

    private void setDialogIteractions(View v){
        setGuardOnIncidenceSelect(v);
        setMBOnClick(v);
    }

    private void enableIncTypeSp(View v){
        Spinner sp = v.findViewById(R.id.tipoincidncia);
        sp.setEnabled(true);
    }

    private void handleOnIncidenceSelect(View v){
        String inc = getIncidenceType(v);
        if(checkIncidence(inc)){
            enableIncTypeSp(v);
            incTypelist.set(0,"Seleccione");
            increasonAdapter.notifyDataSetChanged();
        }else{
            disableSpinner(v);
            incTypelist.set(0,"N/A");
            increasonAdapter.notifyDataSetChanged();
        }
    }

    private void clearTextFields(){
        clearElementName();
        clearApName();
    }

    private void clearElementName(){
        View dView = mDialog.getCustomView();
        AutoCompleteTextView atv = dView.findViewById(R.id.guardname);
        atv.setText("");
    }

    private void clearApName(){
        View dView = mDialog.getCustomView();
        AutoCompleteTextView atv = dView.findViewById(R.id.apostamiento);
        atv.setText("");
    }

    private void setGuardOnIncidenceSelect(final View v){
        Spinner sp = v.findViewById(R.id.incidencia);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            /**
             * <p>Callback method to be invoked when an item in this view has been
             * selected. This callback is invoked only when the newly selected
             * position is different from the previously selected position or if
             * there was no selected item.</p>
             * <p>
             * Impelmenters can call getItemAtPosition(position) if they need to access the
             * data associated with the selected item.
             *
             * @param parent   The AdapterView where the selection happened
             * @param view     The view within the AdapterView that was clicked
             * @param position The position of the view in the adapter
             * @param id       The row id of the item that is selected
             */
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                handleOnIncidenceSelect(v);
            }
            /**
             * Callback method to be invoked when the selection disappears from this
             * view. The selection can disappear for instance when touch is activated
             * or when the adapter becomes empty.
             *
             * @param parent The AdapterView that now contains no selected item.
             */
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                parent.setEnabled(false);
            }
        });
    }

    private String getGuardName(View v){
        AutoCompleteTextView gname = v.findViewById(R.id.guardname);
        return gname.getText().toString();
    }

    private String getApName(View v){
        AutoCompleteTextView ap = v.findViewById(R.id.apostamiento);
        return ap.getText().toString();
    }

    private String getIncidenceType(View v){
        Spinner itype = v.findViewById(R.id.incidencia);
        return itype.getSelectedItem().toString();
    }

    private String getIncidenceReason(View v){
        Spinner ireason = v.findViewById(R.id.tipoincidncia);
        return ireason.getSelectedItem().toString();
    }

    private void setUpIncidences(){
        incList = new ArrayList<>();
        incList.add("Tiempo ordinario");
        incList.add("Tiempo extra");
        incList.add("Otro");
    }

    private void incidencesReasons(){
        incTypelist = new ArrayList<>();
        incTypelist.add("N/A");
        incTypelist.add("Falta");
        incTypelist.add("Requerimiento del cliente");
        incTypelist.add("Vacante");
        incTypelist.add("Otro");
    }

    public String getGrupo() {
        return grupo;
    }
}
