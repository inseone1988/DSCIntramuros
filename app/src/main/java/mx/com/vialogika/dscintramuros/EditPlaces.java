package mx.com.vialogika.dscintramuros;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

public class EditPlaces {
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


    public EditPlaces(Context context,MaterialDialogPayload data){
        this.MODE = data.getMODE();
        this.grupo = data.getGrupo();
        getDialogData();
        setupDialog(context);
    }

    private MaterialDialog setupDialog(Context context){
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title("Asignar Apostamiento")
                .customView(R.layout.add_plantilla_apostamiento,true)
                .positiveText("Agregar")
                .negativeText("Guardar")
                .autoDismiss(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        //Agregar Apostamiento
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        //Enviar apostamiento
                    }
                })
                .show();
        //By default disable incidence reason spinner
        View v = dialog.getCustomView();
        disableSpinner(v);
        setDialogData(dialog);
        setDialogIteractions(v);
        return dialog;
    }

    private void setDialogData(MaterialDialog dialog){
        View v = dialog.getCustomView();
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
            hasincidence = true;
        }
        return isIncidence;
    }

    private boolean isValidAp(String ap){
        boolean isvalid = false;
        if(apIsAvailable(ap)){
            isvalid = true;
        }
        return isvalid;
    }

    private void onElementoSave(){

    }

    private void getGuardNames(){
        if(MODE.equals("new")){
            elementos = Databases.enames();
        }else{
            elementos = Databases.availableElementos(grupo);
        }
    }

    private void removeListItem(List mList,String element){
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

    private void setDialogIteractions(View v){

    }

    private void setGuardOnIncidenceSelect(View v){
        String inc = getIncidenceType(v);
        if(checkIncidence(inc)){
            
        }
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
        incList.add("Tiempo Ordinario");
        incList.add("Tiempo extra");
        incList.add("Vacante");
    }

    private void incidencesReasons(){
        incTypelist = new ArrayList<>();
        incTypelist.add("Falta");
        incTypelist.add("Requerimiento del cliente");
        incTypelist.add("Otro");
    }
}
