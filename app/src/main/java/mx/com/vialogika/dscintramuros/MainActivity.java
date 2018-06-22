package mx.com.vialogika.dscintramuros;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.app.Activity;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.app.AlertDialog;
import android.app.ActionBar;
import android.content.DialogInterface;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.os.CountDownTimer;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.Request;
import com.android.volley.toolbox.Volley;

import java.util.concurrent.CountDownLatch;

interface imei{
    void onCheckFinalized();
        }

interface checkStatusCode{
    Integer ServerStatusCode(Integer statuscode);
}

interface IsAllowedDevice{
    void check(Boolean response);
}

interface IsAllowedUser{
    void check(Boolean repsonse);
}
//TODO: implement a method to save group info
//TODO:In dreamweaver set the modify the user payload
public class MainActivity extends Activity {
    Integer serverStatus;
    String imei_number;
    Button lgbutton;
    TextView progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        lgbutton = findViewById(R.id.login);
        progress = (TextView) findViewById(R.id.progress);
        lgbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
        setVersionText();
    }

    private void requestPermissionForImei(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_PHONE_STATE},1);
    }

    private void setVersionText(){
        try{
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(),0);
            String versionName = pInfo.versionName;
            TextView tv = findViewById(R.id.app_version_text);
            tv.setText(versionName);
        }catch(PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED){
            switch (requestCode) {
                case 1 :
                    imei_number = imei();
                    break;
            }
        }
    }

    @TargetApi(15)
    public String imei() {
        String imei = null;
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
               requestPermissionForImei();
            }else{
                imei = tm.getDeviceId();
            }
        } catch (NullPointerException e) {
            //API level above 25
            imei = tm.getImei();
        }
        return imei;
    }

    public boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void checkServer(final checkStatusCode statusCode){
        String url = "https://www.vialogika.com.mx/dscic/requesthandler.php?function=heartbeat";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    serverStatus = response.getInt("statuscode");
                    statusCode.ServerStatusCode(response.getInt("statuscode"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error instanceof TimeoutError || error instanceof NoConnectionError){
                    new MaterialDialog.Builder(MainActivity.this)
                            .title("Network error")
                            .content("Se ha agotado el tiempo de espera de conexion con el servidor,favor de verificar la conexion a internet.")
                            .positiveText("OK")
                            .show();
                }
                progress.setText("");
            }
        });
        Volley.newRequestQueue(this).add(jsonObjectRequest);

    }

    public static boolean isEmpty(EditText edittext){
        String input = edittext.getText().toString().trim();
        return input.length() == 0;
    }

    public void showMyDialog(Integer dialogText){
        AlertDialog.Builder builder =  new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(dialogText)
                .setTitle(R.string.login_alert_title)
               .setPositiveButton(R.string.ok,new DialogInterface.OnClickListener(){
               public void onClick(DialogInterface dialog,int id){
                    //Callback for ok click button
               }
               })
               .show();
    }

    public void showMyDialog(String dialogText){
        AlertDialog.Builder builder =  new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(dialogText)
                .setTitle(R.string.login_alert_title)
               .setPositiveButton(R.string.ok,new DialogInterface.OnClickListener(){
               public void onClick(DialogInterface dialog,int id){
                    //Callback for ok click button
               }
               })
               .show();
    }

    public void saveUserSP(JSONObject udata)throws JSONException{
        SharedPreferences sp = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong("user_id",udata.getLong("user_id"));
        editor.putString("user_fullname",udata.getString("user_fullname"));
        editor.putString("user_provider_name",udata.getString("users_corp"));
        editor.putLong("user_provider_id",udata.getLong("user_provider_id"));
        editor.putLong("user_site_id",udata.getLong("user_site_id"));
        editor.putString("site_name",udata.getString("site_name"));
        editor.apply();
    }

    public void AuthenticateUser(String username, String password, final IsAllowedUser isAllowed){
        String url = "https://www.vialogika.com.mx/dscic/requesthandler.php?function=au&username="+username+"&password="+password;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response){
                //We got a response
                try {
                    if(response.getBoolean("isAllowed")){
                        JSONArray ja = response.getJSONArray("userdata");
                        JSONObject jo = ja.getJSONObject(0);
                        saveUserSP(jo);
                    }
                    isAllowed.check(response.getBoolean("isAllowed"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Server error
                MainActivity.this.showMyDialog(R.string.server_error);
            }
        });
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    public void isAllowedDevice(String deviceId, final IsAllowedDevice isAllowed){
        if(imei_number == null){
            new alerts(MainActivity.this,"Permisos Denegados","No se han otorgado los permisos necesarios para que la aplicacion funcione");
        }else{
            String url = "https://www.vialogika.com.mx/dscic/requesthandler.php?function=checkimei&deviceId="+deviceId;
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    //We got a response
                    try {
                        isAllowed.check(response.getBoolean("isAllowed"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //Server error
                    MainActivity.this.showMyDialog(R.string.server_error);
                }
            });
            Volley.newRequestQueue(this).add(jsonObjectRequest);
        }

    }

    public void loginUser(){
        imei_number = imei();
        final Intent myIntent = new Intent(MainActivity.this.getApplication(),dsc_dashboard.class);
        final EditText uname = (EditText)findViewById(R.id.user);
        final EditText pwd = (EditText)findViewById(R.id.pass);
        //if blank fields tell user fields cannot be empty
        Boolean result = this.isEmpty(uname);
        if(this.isEmpty(uname)){
          this.showMyDialog(R.string.user_empty);
        }else if(this.isEmpty(pwd)){
            this.showMyDialog(R.string.pwd_empty);
        }else{
           //if all goes good log in user
            Boolean netAvailable = this.isNetworkAvailable();
            if (netAvailable){
                //TO DO Delete this variable
                final String finalImei = imei_number;
                progress.setText("Checando conexion al servidor...");
                checkServer(new checkStatusCode() {
                    //This chunk almost a day from my life
                    @Override
                    public Integer ServerStatusCode(Integer statuscode) {
                        progress.setText("Autenticando dispositivo..");
                        isAllowedDevice(imei_number, new IsAllowedDevice() {
                            @Override
                            public void check(Boolean response) {
                                if(response){
                                    progress.setText("Autenticando Usuario..");
                                    AuthenticateUser(uname.getText().toString(), pwd.getText().toString(), new IsAllowedUser() {
                                        @Override
                                        public void check(Boolean repsonse) {
                                            if(repsonse){
                                                progress.setText("Actualizando Bases de datos");
                                                //Once all testings has been checked update databases and get user to user workspace
                                                String usersite = String.valueOf(Databases.siteId(getApplication()));
                                                String provid = String.valueOf(Databases.providerId(getApplication()));
                                                String getParams = "&siteid=" + usersite + "&providerid=" + provid;
                                                Databases db = new Databases();
                                                db.getDataFromServer(MainActivity.this, "deviceData", null, getParams, new Databases.callbacks() {
                                                    //Called when we got a server response if we need to do additional computation
                                                    @Override
                                                    public void onResponse(JSONObject response) {

                                                    }
                                                    //Called when database was succesfully updated from server
                                                    @Override
                                                    public void onDbUpdateSuccess(){
                                                        progress.setText("Bases actualizadas");
                                                        startActivity(myIntent);
                                                        finish();
                                                    }
                                                    //Called on volley response error
                                                    @Override
                                                    public void onResponseError(VolleyError error) {
                                                        if(error instanceof TimeoutError || error instanceof NoConnectionError){
                                                            new MaterialDialog.Builder(MainActivity.this)
                                                                    .title("Network error")
                                                                    .content("Se ha agotado el tiempo de espera de conexion con el servidor,favor de verificar la conexion a internet.")
                                                                    .positiveText("OK")
                                                                    .show();
                                                        }

                                                    }
                                                });

                                            }else{
                                                MainActivity.this.showMyDialog("Usuario o contraseña incorrectos");
                                                progress.setText(R.string.novalid_id_text);
                                            }
                                        }
                                    });
                                }else{
                                    MainActivity.this.showMyDialog("Dispositivo no autorizado");
                                }
                            }
                        });
                        return null;
                    }
                });
            }else{
                this.showMyDialog(R.string.no_network_advice);
            }

        }
    }

}
