package mx.com.vialogika.mist;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import mx.com.vialogika.mist.Utils.Permissions;

public class TakeEvidences extends AppCompatActivity {

    private FloatingActionButton mFab;
    private FloatingActionButton exitFab;
    private CameraView cView;
    private String currentImagePath;
    //Pictures paths must be delimited with commas
    private String evidencesPaths = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_evidences);
        //Check if we have permissions
        getItems();
        setup();
    }

    private void setup(){
        ActionBar bar = getSupportActionBar();
        bar.hide();
        setmFabClickListener();
        cView.addCameraListener(new CameraListener() {
            /**
             * Notifies that a picture previously captured with {@link CameraView#capturePicture()}
             * or {@link CameraView#captureSnapshot()} is ready to be shown or saved.
             * <p>
             * If planning to get a bitmap, you can use {@link CameraUtils#decodeBitmap(byte[], CameraUtils.BitmapCallback)}
             * to decode the byte array taking care about orientation.
             *
             * @param jpeg captured picture
             */
            @Override
            public void onPictureTaken(byte[] jpeg) {
                super.onPictureTaken(jpeg);
                storeImage(jpeg);
            }
        });
    }

    private void getItems(){
        mFab = findViewById(R.id.snapshot_fab);
        exitFab = findViewById(R.id.exit_fab);
        cView = findViewById(R.id.evidence_viewport);
    }

    private void setmFabClickListener(){
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeSnapshot();
            }
        });
        exitFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setIntentResult();
                finish();
            }
        });
    }

    private void takeSnapshot(){
        //Check Permissions
        int REQUESTCCODE = 1;
        boolean hasCameraPermission = Permissions.hasPermission(getApplicationContext(), Manifest.permission.CAMERA);
        boolean hasStoragePermission = Permissions.hasPermission(getApplicationContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(hasCameraPermission && hasStoragePermission){
            cView.captureSnapshot();
        }else{
            Permissions.requestPermission(this,new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUESTCCODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case 1:
                boolean allPassed = permissionsHasBeenGranted(grantResults);
                if(allPassed){
                    cView.captureSnapshot();
                }else{
                   permissionsNotGrantedDialog();
                }
                break;
        }
    }

    private void permissionsNotGrantedDialog(){
        new MaterialDialog.Builder(this)
                .title("Permisos")
                .content("No se han otorgado los permisos requeridos, volver a intentar?")
                .positiveText("OK")
                .negativeText("No gracias.")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        finish();
                    }
                })
                .show();
    }

    private boolean permissionsHasBeenGranted(int[] grantResults){
        for(int value : grantResults){
            if(value == PackageManager.PERMISSION_DENIED){
                return false;
            }
        }
        return true;
    }

    private void setIntentResult(){
        Intent intent = new Intent();
        intent.putExtra("images_paths",evidencesPaths);
        setResult(Activity.RESULT_OK,intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cView.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cView.destroy();
    }

    private void mediaFileErrorDialog(){
        new MaterialDialog.Builder(this)
                .title("Error de archivo")
                .content(R.string.media_output_error_text)
                .negativeText("Cerrar")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        finish();
                    }
                })
                .show();
    }

    private void storeImage(byte[] image){
        File mImage = getOutputMediaFile();
        if(mImage == null){
            mediaFileErrorDialog();
        }
        try{
            FileOutputStream fos = new FileOutputStream(mImage);
            fos.write(image);
            fos.flush();
            fos.close();
            //Once picture has been saved successfully add path to collection
            evidencesPaths += currentImagePath + ",";
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
                + getApplicationContext().getPackageName()
                + "/Files/Evidences");

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
        String mImageName="DSC_EV_"+ timeStamp +".jpg";
        currentImagePath = mediaStorageDir.getPath() + File.separator + mImageName;
        mediaFile = new File(currentImagePath);
        return mediaFile;
    }
}
