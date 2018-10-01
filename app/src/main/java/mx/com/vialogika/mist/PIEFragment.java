package mx.com.vialogika.mist;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextClock;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.VolleyError;
import com.esafirm.imagepicker.features.ImagePicker;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import mx.com.vialogika.mist.Utils.Permissions;
/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PIEFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PIEFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PIEFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private final int PERMISSION_STORAGE= 1150;
    private final int REQUESTCODE_PHOTO = 1151;
    private String datetime;
    private String eventDate;
    private String eventType;
    private String eventHighlight;
    private String eventTime;
    private Boolean suspectIdentified = false;
    private Boolean evidenceCollected = false;
    private String what;
    private String how;
    private String when;
    private String where;
    private String facts;
    private String images = "";
    private String signatureNames = "";
    private String signatures = "";
    private String signaturePersonRoles = "";
    private String redactor;

    private List<String> incidenceNames;
    private ArrayList<String> alreadyselectedimages = new ArrayList<>();
    private String[] incNames = new String[]{"Malas practicas","Condicion insegura","Intento/Sustraccion de producto","Consumo de producto","Acto inseguro","Daño a instalaciones","Riña","Intento de intrusion","Colision de Vehiculos","Lesiones","Ingreso con objetos prohibidos","Otro incidente"};


    private Spinner eType;
    private RadioGroup highlights;
    private RadioButton highlightSelected;
    private CheckBox responsible,evidence;
    private EditText eventHour,mWhat,mHow,mWhen,mWhere,mFacts,mRedactor;
    private Button firmas,takeEvidence,save;
    private EditText eDate;

    private OnFragmentInteractionListener mListener;

    public PIEFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PIEFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PIEFragment newInstance(String param1, String param2) {
        PIEFragment fragment = new PIEFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
       // super.onActivityResult(requestCode, resultCode, data);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_pie, container, false);
        getViewElements(root);
        setButtonsOnClick();
        setUp();
        return root;
    }

    private void setUp(){
        //Spinners and other data initializations that depends on layout already inflated
        eDate.setText(getCurrentDate());
        setUpSpinner();
    }

    private void setUpSpinner(){
        incidenceNames = new ArrayList<>();
        incidenceNames.addAll(Arrays.asList(incNames));
        ArrayAdapter adapter = new ArrayAdapter(getActivity(),R.layout.spinner_item,incidenceNames);
        adapter.setDropDownViewResource(R.layout.dsc_spinner_dropdown);
        eType.setAdapter(adapter);
    }

    private void getViewElements(View v){
        eDate = v.findViewById(R.id.event_time);
        eType = v.findViewById(R.id.event_type);
        eventHour = v.findViewById(R.id.event_timex);
        highlights = v.findViewById(R.id.event_highlight);
        highlightSelected = v.findViewById(highlights.getCheckedRadioButtonId());
        responsible = v.findViewById(R.id.responsible);
        evidence = v.findViewById(R.id.evidence_taked);
        mWhat = v.findViewById(R.id.event_what);
        mHow = v.findViewById(R.id.event_how);
        mWhen = v.findViewById(R.id.event_when);
        mWhere = v.findViewById(R.id.event_where);
        mFacts = v.findViewById(R.id.event_facts);
        mRedactor = v.findViewById(R.id.event_user);
        takeEvidence = v.findViewById(R.id.take_evidence);
        firmas = v.findViewById(R.id.sign_capture);
        save = v.findViewById(R.id.save_and_send);
    }

    private String getCurrentDate(){
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    private void clearFields(){
        eventHour.setText("");
        highlights.check(R.id.pie_sem_null);
        responsible.setChecked(false);
        evidence.setChecked(false);
        mWhat.setText("");
        mHow.setText("");
        mWhen.setText("");
        mWhere.setText("");
        mFacts.setText("");
        mRedactor.setText("");
        images = "";
        signaturePersonRoles = "";
        signatures = "";
        signatureNames = "";
        eventHour.requestFocus();
    }

    private void setButtonsOnClick(){
        takeEvidence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEvidenceFiles();
                //captureEvidences();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImagePaths();
                confirmPlantillaSend(new OnIncidenceConfirm() {
                    @Override
                    public void send() {
                        saveIncidence();
                    }
                });

            }
        });
        firmas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SignaturesDialog(getActivity(), new SignaturesDialog.onSignatureSaveCallback() {
                    @Override
                    public void onSignatureSave(String signatureImagePath, String name,String role) {
                        addSiganturesToCollection(signatureImagePath,name,role);
                    }
                });
            }
        });
        highlights.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                highlightSelected = group.findViewById(checkedId);
            }
        });
    }

    private void getImagePaths(){
        images = dsc_dashboard.getImages();
    }

    private void getEvidenceFiles(){
        if(Permissions.hasPermission(this.getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            ImagePicker.create(this.getActivity())
                    .start();
        }else{
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION_STORAGE);
        }
    }

    private void saveIncidence(){
        //first upload evidences then data
        getFormValues();
        SiteIncidences inc = new SiteIncidences(redactor,eventDate,eventTime,eventType,eventHighlight,String.valueOf(suspectIdentified),String.valueOf(evidenceCollected),what,how,when,where,facts,images,signatureNames,signaturePersonRoles,signatures);
        inc.setEvent_user_site(String.valueOf(Databases.siteId(getActivity())));
        inc.save();
        long sId = inc.getId();
        if(sId > 0){
            if(signatures.equals("") && images.equals("")){
                uploadIncidence(sId);
            }else{
                uploadMultipart(sId);
            }
        }
    }

    private void uploadIncidence(final long incid){
        Databases.sendIncidence(getActivity(), incid, new Databases.callbacks() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    if(response.getBoolean("success")){
                        long rIId = response.getLong("id");
                        //Update incidence and set flag that incidence has been uploaded
                        SiteIncidences incidence = SiteIncidences.findById(SiteIncidences.class,incid);
                        incidence.setRemoteId(rIId);
                        incidence.save();
                        Toast.makeText(getActivity(),"Incidencia enviada correctamente.",Toast.LENGTH_SHORT).show();
                        clearFields();
                    }else{
                        String error = response.getString("error");
                        new MaterialDialog.Builder(getActivity())
                                .title("Network error")
                                .content(error)
                                .positiveText("Ok")
                                .show();
                    }
                }catch(JSONException e ){
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseError(VolleyError error) {

            }

            @Override
            public void onDbUpdateSuccess() {

            }
        });
    }

    private void confirmPlantillaSend(final OnIncidenceConfirm callback){
        new MaterialDialog.Builder(getActivity())
                .title("Enviar Incidencia")
                .content("Enviar evidencia?. Una vez enviada ya no es posible editar los datos")
                .positiveText("Ok")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        callback.send();
                    }
                })
                .negativeText("Cancelar")
                .show();
    }

    private void uploadIncidence(final long incid, final JSONObject evidencesSavedResponse) throws JSONException{
        long rIId = evidencesSavedResponse.getLong("id");
        //Update incidence and set flag that incidence has been uploaded
        SiteIncidences incidence = SiteIncidences.findById(SiteIncidences.class,incid);
        incidence.setRemoteId(rIId);
        incidence.save();
        Databases.sendIncidence(getActivity(), incid, new Databases.callbacks() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    if(response.getBoolean("success")){
                        Toast.makeText(getActivity(),"Incidencia enviada correctamente.",Toast.LENGTH_SHORT).show();
                        clearFields();
                        finishFragment();
                    }

                }catch(JSONException e ){
                    e.printStackTrace();
                }

            }

            @Override
            public void onResponseError(VolleyError error) {

            }

            @Override
            public void onDbUpdateSuccess() {

            }
        });
    }


    private void finishFragment(){
        PIEFragment pie = new PIEFragment();
        getActivity().getFragmentManager().beginTransaction()
                .replace(R.id.pie,pie,"NEW_PIE")
                .addToBackStack(null)
                .commit();
    }

    private void checkForPendingReports(){

    }

    private void addSiganturesToCollection(String signaturepath,String name,String role){
        signatureNames += name + ",";
        signatures += signaturepath + ",";
        signaturePersonRoles += role +",";
    }

    private void captureEvidences(){
        int REQUEST_CODE = 1;
        Intent intent = new Intent(getActivity(),TakeEvidences.class);
        startActivityForResult(intent,REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case PERMISSION_STORAGE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getEvidenceFiles();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private String mapEvicencesArray(List<String> response){
        String values = "";
        for(int i = 0; i < response.size(); i++ ){
            values += response.get(i);
        }
        return values;
    }

    private void getFormValues(){
        eventDate = eDate.getText().toString();
        eventType = eType.getSelectedItem().toString();
        eventTime = eventHour.getText().toString();
        eventHighlight = highlightSelected.getText().toString();
        suspectIdentified = responsible.isChecked();
        evidenceCollected = evidence.isChecked();
        what = mWhat.getText().toString();
        how = mHow.getText().toString();
        when = mWhen.getText().toString();
        where = mWhere.getText().toString();
        facts = mFacts.getText().toString();
        redactor = mRedactor.getText().toString();
    }

    private boolean FileExists(String path){
        File image = new File(path);
        return image.exists();
    }

    private void uploadMultipart(final long incId){
        //First check for empty signatures or evidences
        String url = "https://www.vialogika.com.mx/dscic/requesthandler.php";
        try{
            String uploadId = UUID.randomUUID().toString();
            String[] evidences = images.split(",");
            String[] mSignatures = signatures.split(",");
            MultipartUploadRequest ur = new MultipartUploadRequest(getActivity(),uploadId,url);
                for(int i = 0;i<evidences.length;i++){
                    if(FileExists(evidences[i])){
                        ur.addFileToUpload(evidences[i],"evidence_"+i);
                    }
                }
                for(int i = 0;i<mSignatures.length;i++){
                    if(FileExists(mSignatures[i])){
                        ur.addFileToUpload(mSignatures[i],"signature_"+i);
                    }
                }
            ur.addParameter("function","saveEvidences")
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(3)
                    .setAutoDeleteFilesAfterSuccessfulUpload(true)
                    .setDelegate(new UploadStatusDelegate() {
                        @Override
                        public void onProgress(Context context, UploadInfo uploadInfo) {

                        }

                        @Override
                        public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {

                        }

                        @Override
                        public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
                            try{
                                JSONObject response = new JSONObject(serverResponse.getBodyAsString());
                                if(response.getBoolean("success")){
                                    uploadIncidence(incId,response);
                                }else{
                                    requestErrorDialog(response.getString("error"));
                                }

                            }catch(JSONException e ){
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onCancelled(Context context, UploadInfo uploadInfo) {

                        }
                    })
                    .startUpload();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void requestErrorDialog(String error){
        new MaterialDialog.Builder(getActivity())
                .title("Error")
                .content(error)
                .positiveText("OK")
                .show();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    interface OnIncidenceConfirm{
        void send();
    }
}
