package mx.com.vialogika.dscintramuros;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

interface alertCallback{
    void okbutton();
    void cancelbutton();
}

public class alerts {

    public  alerts(Context context,String title,String dialogMessage){
        //simply display a generic message
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setMessage(dialogMessage)
                .setTitle(title)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    public alerts(Context context, int resourceTitle, int resourceMessage, final alertCallback buttoncallbacks){
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setMessage(resourceMessage)
                .setTitle(resourceTitle)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        buttoncallbacks.okbutton();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        buttoncallbacks.cancelbutton();
                    }
                }).show();
    }
}
