package mx.com.vialogika.dscintramuros;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.ActionBar;
import android.view.View;
import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

interface onDatabaseSave{
    void saveData();
    void onDataUploaded(JSONObject obj);
}


public class editElement extends Activity {
    Context context = getApplication();
    SimpleDateFormat today = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    String profile_image_path;
    EditText nombre, apellidoP, apellidoM;
    ImageView profile_photo;
    AutoCompleteTextView dropdown;
    Button btn;
    String Nombre,ApellidoM,ApellidoP,TipoElemento;
    UserProfileSettings us;

    private static final String[] APOSTAMIENTOS = new String[]{"Jefe de turno","Jefe de servicio","Elemento de seguridad","Guardia armado"};

    @Override
    protected void onActivityResult(int REQUEST_CODE,int RESULT_CODE,Intent data){
        if(REQUEST_CODE == 1){
            if(RESULT_CODE == Activity.RESULT_OK){
                profile_image_path  = data.getStringExtra("file_path");
                if(profile_image_path != null){
                    File imgFile = new File(profile_image_path);
                    if(imgFile.exists()){
                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        ImageView myImage = (ImageView) findViewById(R.id.picture);
                        myImage.setImageBitmap(myBitmap);

                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionbar = getActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_edit_element);

        final ImageView imageview = findViewById(R.id.picture);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,APOSTAMIENTOS);
        dropdown = findViewById(R.id.element_type);
        dropdown.setAdapter(adapter);
        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int REQUEST_CODE = 1;
                Intent intent = new Intent(editElement.this,camera_preview.class);
                startActivityForResult(intent,REQUEST_CODE);
            }
        });
        btn = findViewById(R.id.submit_element);
        nombre = findViewById(R.id.element_name);
        apellidoP = findViewById(R.id.element_ln);
        apellidoM = findViewById(R.id.element_fname);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //Submit data to database and network
                //First local database
                if(verifyInputs()){
                    try {
                       final Long  insId = saveElement();
                        JSONObject obj = formatElementData(insId);
                        uploadData(obj, new onDatabaseSave() {
                            @Override
                            public void saveData() {

                            }

                            @Override
                            public void onDataUploaded(JSONObject obj){
                                try{
                                    String hash = obj.getString("ghash");
                                    uploadMultipart(obj);
                                    Databases.saveGhash(insId,hash);
                                }catch(JSONException error){
                                    error.printStackTrace();
                                }

                            }
                        });

                    }catch(JSONException e){
                        new MaterialDialog.Builder(getApplication())
                                .title("Error")
                                .content(e.toString())
                                .positiveText("Ok")
                                .show();
                    }
                }
                finish();

            }
        });
    }

    public JSONObject formatElementData(Long id)throws JSONException{
        SharedPreferences sp = getSharedPreferences("MainActivity",Context.MODE_PRIVATE);
        Long providerid = sp.getLong("user_provider_id",0);
        Long siteid = sp.getLong("user_site_id",0);
        JSONObject obj = new JSONObject();
        Elementos element = Elementos.findById(Elementos.class,id);
        obj.put("function","saveGuardInfo");
        obj.put("person_providerid",providerid);
        obj.put("person_type","Intramuros");
        obj.put("person_position",element.getGuard_range());
        obj.put("person_name",element.getPerson_name());
        obj.put("person_fname",element.getPerson_fname());
        obj.put("person_lname",element.getPerson_lname());
        obj.put("person_site_id",siteid);
        obj.put("uid",id);
        return obj;
    }

    public void uploadData(final JSONObject object, final onDatabaseSave dbOp){
        String url = "https://www.vialogika.com.mx/dscic/raw.php";
        JsonObjectRequest JOB = new JsonObjectRequest(Request.Method.POST, url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dbOp.onDataUploaded(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error instanceof TimeoutError || error instanceof NetworkError){
                    new MaterialDialog.Builder(getApplication())
                            .title("Network error")
                            .content("Se ha agotado el tiempo de espera de conexion con el servidor,favor de verificar la conexion a internet.")
                            .positiveText("OK")
                            .show();
                    try{
                        Databases.deleteElement(object.getLong("uid"));
                    }catch(JSONException e){
                        Toast toast = Toast.makeText(context,"No se pudo obtener el id",Toast.LENGTH_SHORT);
                        toast.show();
                    }

                }
                error.printStackTrace();

            }
        });
        Volley.newRequestQueue(getApplication()).add(JOB);
    }

    public void uploadMultipart(JSONObject object)throws JSONException{
        String id = object.getString("gid");
        String url = "https://www.vialogika.com.mx/dscic/requesthandler.php";
        try{
            String uploadId = UUID.randomUUID().toString();
            new MultipartUploadRequest(this,uploadId,url)
                    .addFileToUpload(profile_image_path,"profile_photo")
                    .addParameter("function","uploadProfilePhoto")
                    .addParameter("gid",id)
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .startUpload();
        }catch(Exception error){
            error.printStackTrace();
        }
    }

    public Long saveElement(){
        Elementos element = new Elementos(Nombre,ApellidoP,ApellidoM,TipoElemento,profile_image_path);
        element.save();
        return element.getId();
    }

    public boolean verifyInputs(){
        boolean result = false;
        Nombre = nombre.getText().toString();
        ApellidoP = apellidoP.getText().toString();
        ApellidoM = apellidoM.getText().toString();
        TipoElemento = dropdown.getText().toString();
        if(!isEmpty(Nombre) && !isEmpty(ApellidoP) && !isEmpty(ApellidoM) && !isEmpty(TipoElemento)){
            result = true;
        }else{

        }
        return result;
    }
    public static boolean isEmpty(String edittext){
        String input = edittext.trim();
        return input.length() == 0;
    }
    @Override
    protected void onResume(){
        super.onResume();
    }

}

