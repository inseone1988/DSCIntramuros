package mx.com.vialogika.dscintramuros;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.app.Activity;
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
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response;
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

public class MainActivity extends Activity {
    String musername;
    String password;
    Integer serverStatus;
    String imei_number;
    Button lgbutton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        lgbutton = findViewById(R.id.login);
        lgbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }

    private void requestPermissionForImei(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_PHONE_STATE},1);
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
                Integer serverError = error.networkResponse.statusCode;
                MainActivity.this.showMyDialog("Ha ocurrido un error de servidor  \n Response StatusCode: " + serverError.toString());
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

    public void AuthenticateUser(String username, String password, final IsAllowedUser isAllowed){
        String url = "https://www.vialogika.com.mx/dscic/requesthandler.php?function=au&username="+username+"&password="+password;
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
                checkServer(new checkStatusCode() {
                    //This chunk almost a day from my life
                    @Override
                    public Integer ServerStatusCode(Integer statuscode) {
                        isAllowedDevice(imei_number, new IsAllowedDevice() {
                            @Override
                            public void check(Boolean response) {
                                if(response){
                                    AuthenticateUser(uname.getText().toString(), pwd.getText().toString(), new IsAllowedUser() {
                                        @Override
                                        public void check(Boolean repsonse) {
                                            if(repsonse){
                                                //Once all testings has been checked get user to user workspace
                                                startActivity(myIntent);
                                                finish();
                                            }else{
                                                MainActivity.this.showMyDialog("Usuario o contraseña incorrectos");
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
