package mx.com.vialogika.dscintramuros.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class Permissions {

    public static boolean hasPermission(Context context,String permission){
        //Lets assume that app doesnt have permission
        int permissionCheck = ContextCompat.checkSelfPermission(context,permission);
        return permissionCheck == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermission(Activity context, String[] permissions, int RequestCode){
        ActivityCompat.requestPermissions(context,permissions,RequestCode);
    }
}
