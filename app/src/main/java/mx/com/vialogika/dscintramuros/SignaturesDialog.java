package mx.com.vialogika.dscintramuros;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;


public class SignaturesDialog {

    private String name;

    public SignaturesDialog(Context context){
        setup(context);
    }

    private MaterialDialog setup(Context context){
       return new MaterialDialog.Builder(context)
               .customView(R.layout.signatures_dialog,true)
               .title(R.string.signature_capture_title)
               .show();
    }
}
