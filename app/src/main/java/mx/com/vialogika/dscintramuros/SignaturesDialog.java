package mx.com.vialogika.dscintramuros;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.simplify.ink.InkView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class SignaturesDialog {

    private String pName;
    private String pRole;
    private String currentSignatureImagePath;

    private InkView signView;
    private MaterialDialog dialog;
    private EditText personName,personRole;
    private onSignatureSaveCallback callback;

    public SignaturesDialog(Context context,onSignatureSaveCallback mCallback){
        this.callback = mCallback;
        dialog = setup(context);
        getItems();
    }

    private MaterialDialog setup(Context context){
       return new MaterialDialog.Builder(context)
               .customView(R.layout.signatures_dialog,true)
               .title(R.string.signature_capture_title)
               .positiveText(R.string.signature_positive)
               .onPositive(new MaterialDialog.SingleButtonCallback() {
                   @Override
                   public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        getValues();
                        saveSignature();
                   }
               })
               .negativeText(R.string.signature_neutral)
               .show();
    }

    private void getValues(){
        getName();
    }

    private void getItems(){
        View v = dialog.getCustomView();
        signView = v.findViewById(R.id.signature);
        personName = v.findViewById(R.id.person_name);
        personRole = v.findViewById(R.id.person_role);
    }

    private void getName(){
        pName = personName.getText().toString();
        pRole = personRole.getText().toString();
    }

    private void saveSignature(){
        Bitmap signature = signView.getBitmap();
        storeImage(signature);
        callback.onSignatureSave(currentSignatureImagePath,pName,pRole);
    }

    private void mediaFileErrorDialog(){
        new MaterialDialog.Builder(dialog.getContext())
                .title("Error de archivo")
                .content(R.string.media_output_error_text)
                .negativeText("Cerrar")
                .show();
    }

    private void storeImage(Bitmap image){
        File mImage = getOutputMediaFile();
        if(mImage == null){
            mediaFileErrorDialog();
        }
        try{
            FileOutputStream fos = new FileOutputStream(mImage);
            image.compress(Bitmap.CompressFormat.PNG,100,fos);
        }catch (IOException e){
            mediaFileErrorDialog();
            e.printStackTrace();
        }
    }

    /** Create a File for saving an image or video */
    private File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + dialog.getContext().getPackageName()
                + "/Files/Signatures");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
        File mediaFile;
        String mImageName="DSC_EVSGN_"+ timeStamp +".jpg";
        currentSignatureImagePath = mediaStorageDir.getPath() + File.separator + mImageName;
        mediaFile = new File(currentSignatureImagePath);
        return mediaFile;
    }

    interface onSignatureSaveCallback{
        void onSignatureSave(String signatureImagePath,String name,String role);
    }

}
