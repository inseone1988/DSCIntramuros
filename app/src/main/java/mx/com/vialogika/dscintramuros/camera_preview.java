package mx.com.vialogika.dscintramuros;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraOptions;
import com.otaliastudios.cameraview.CameraView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

interface permissions{
    void callback();
}

public class camera_preview extends Activity {

    private CameraView cameraView;
    private String image_profile_path;
    private Boolean cameraPerm;
    private Boolean writetofile ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        ActionBar actionbar = getActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setTitle(R.string.edit_element_title);
        setContentView(R.layout.camerapreview);
        Button buton = findViewById(R.id.shot);
        buton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ActivityCompat.checkSelfPermission(camera_preview.this,Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                    if(ActivityCompat.checkSelfPermission(camera_preview.this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                        cameraView.captureSnapshot();
                    }else{
                        askForStoragePermissions();
                    }
                }else{
                    askForCameraPermissions();
                }
            }
        });
        cameraView = findViewById(R.id.cameraprev);
        cameraView.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(byte[] image) {
                storeImage(image);
                Intent intent = new Intent();
                intent.putExtra("file_path",image_profile_path);
                setResult(Activity.RESULT_OK,intent);
                finish();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraView.destroy();
    }

    private void storeImage(byte[] image) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {

            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(image);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void askForCameraPermissions(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},1);
    }

    private void askForStoragePermissions(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},2);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED){
            switch (requestCode) {
                case 16 :
                    this.cameraPerm = true;
                    break;
                case 2:
                    cameraView.captureSnapshot();
                    break;
            }
        }else{
            new alerts(camera_preview.this, R.string.camera_storage_denied_title, R.string.no_camera_no_storage, new alertCallback() {
                @Override
                public void okbutton() {
                    finish();
                }

                @Override
                public void cancelbutton() {
                    finish();
                }
            });

        }
    }



    /** Create a File for saving an image or video */
    private  File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getApplicationContext().getPackageName()
                + "/Files");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName="DSC_"+ timeStamp +".jpg";
        image_profile_path = mediaStorageDir.getPath() + File.separator + mImageName;
        mediaFile = new File(image_profile_path);
        return mediaFile;
    }
}
