package mx.com.vialogika.dscintramuros;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
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
import android.widget.SpinnerAdapter;
import android.widget.TextClock;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


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

    private List<String> incidenceNames;
    private String[] incNames = new String[]{"Malas practicas","Condicion insegura","Intento/Sustraccion de producto","Consumo de producto","Acto inseguro","Daño a instalaciones","Riña","Intento de intrusion","Colision de Vehiculos","Lesiones","Ingreso con objetos prohibidos","Otro incidente"};


    private Spinner eType;
    private RadioGroup highlights;
    private RadioButton highlightSelected;
    private CheckBox responsible,evidence;
    private EditText eventHour,mWhat,mHow,mWhen,mWhere,mFacts;
    private Button firmas,takeEvidence,save;
    private TextClock eDate;

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
        takeEvidence = v.findViewById(R.id.take_evidence);
        firmas = v.findViewById(R.id.sign_capture);
        save = v.findViewById(R.id.save_and_send);
    }

    private void setButtonsOnClick(){
        takeEvidence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureEvidences();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveIncidence();
            }
        });
        firmas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SignaturesDialog(getActivity(), new SignaturesDialog.onSignatureSaveCallback() {
                    @Override
                    public void onSignatureSave(String signatureImagePath, String name) {
                        addSiganturesToCollection(signatureImagePath,name);
                    }
                });
            }
        });
    }

    private void saveIncidence(){
        getFormValues();
        SiteIncidences inc = new SiteIncidences(eventDate,eventTime,eventType,eventHighlight,String.valueOf(suspectIdentified),String.valueOf(evidenceCollected),what,how,when,where,facts,images,signatureNames,signatures);
        inc.save();
        long sId = inc.getId();
        if(sId > 0){

        }
    }

    private void uploadIncidence(final long incid){
        Databases.sendIncidence(getActivity(), incid, new Databases.callbacks() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    long rIId = response.getLong("incidence_id");
                    //Update incidence and set flag that incidence has been uploaded
                    SiteIncidences incidence = SiteIncidences.findById(SiteIncidences.class,incid);
                    incidence.setRemoteId(rIId);
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

    private void checkForPendingReports(){

    }

    private void addSiganturesToCollection(String signaturepath,String name){
        signatureNames += name + ",";
        signatures += signaturepath + ",";
    }

    private void captureEvidences(){
        int REQUEST_CODE = 1;
        Intent intent = new Intent(getActivity(),TakeEvidences.class);
        startActivityForResult(intent,REQUEST_CODE);
    }

    /**
     * Receive the result from a previous call to
     * {@link #startActivityForResult(Intent, int)}.  This follows the
     * related Activity API as described there in
     * {@link Activity#onActivityResult(int, int, Intent)}.
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case 1:
                if(resultCode == Activity.RESULT_OK){
                    images += data.getStringExtra("images_paths");
                }
                break;
            default :
                break;
        }
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
    }

    private void uploadMultipart(){
        //First check for empty signatures or evidences
        String url = "https://www.vialogika.com.mx/dscic/requesthandler.php";
        try{
            String uploadId = UUID.randomUUID().toString();
            String[] evidences = images.split(",");
            String[] mSignatures = signatures.split(",");
            MultipartUploadRequest ur = new MultipartUploadRequest(getActivity(),uploadId,url);
                for(int i = 0;i<evidences.length;i++){
                    ur.addFileToUpload(evidences[i],"evidence_"+i);
                }
                for(int i = 0;i<mSignatures.length;i++){
                    ur.addFileToUpload(mSignatures[i],"signature_"+i);
                }
            ur.addParameter("function","saveEvidences")
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(3)
                    .setAutoDeleteFilesAfterSuccessfulUpload(true)
                    .startUpload();
        }catch(Exception e){
            e.printStackTrace();
        }
    }



    private boolean validateEditTexts(int editText, String value){
        boolean isValid = false;
        switch(editText){
            case R.id.event_timex:

                break;
        }
        return isValid;
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
}
