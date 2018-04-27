package mx.com.vialogika.dscintramuros;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ActionBar;
import android.view.View;
import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

interface onDatabaseSave{
    void saveData();
}

public class editElement extends Activity {
    elements elementData;
    SimpleDateFormat today = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    String profile_image_path;
    EditText nombre, apellidoP, apellidoM;
    ImageView profile_photo;
    AutoCompleteTextView dropdown;
    Button btn;
    String Nombre,ApellidoM,ApellidoP,TipoElemento;
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
                Nombre = nombre.getText().toString();
                ApellidoP = apellidoP.getText().toString();
                ApellidoM = apellidoM.getText().toString();
                TipoElemento = dropdown.getText().toString();
                Elementos element = new Elementos(Nombre,ApellidoP,ApellidoM,TipoElemento,profile_image_path);
                element.save();
                finish();

            }
        });
    }

    class saveElementData extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... Avoid){
            elementData = new elements();
            elementData.setNombre(Nombre);
            elementData.setFname(ApellidoP);
            elementData.setLname(ApellidoM);
            elementData.setCreated(today.toString());
            elementData.setGuard_range(TipoElemento);
            elementData.setPhoto_path(profile_image_path);
            AppDatabase mydb =  AppDatabase.getAppDatabase(getApplicationContext());
            mydb.eDao().insert(elementData);
            return null;
        }

    }

    public boolean verifyInputs(){
        return false;
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

