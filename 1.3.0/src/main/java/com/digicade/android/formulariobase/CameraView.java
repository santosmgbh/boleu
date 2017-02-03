package com.digicade.android.formulariobase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by almir.santos on 03/03/2015.
 */
public class CameraView extends LinearLayout implements View.OnClickListener {

    public static final String SERIALIZABLE_NOME_ARQUIVO = "nomeArquivo", TYPE_CAMERA = "type_camera";
    public static final int PHOTO_SAVED = 20, PHOTO_DISCARD = 21;
    public static final int CAMERA_DEFAULT_REQUEST = 11;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final String TAG = "camera";
    private static String lastMediaPath = "";
    private Camera mCamera;
    private CameraPreview mPreview;
    private LinearLayout layoutPhoto;
    private ImageButton savePhoto, discardPhoto, captureButton;
    private String nomeArquivo;
    private MediaRecorder mMediaRecorder;
    private int typeMedia;
    private OnCameraResult mOnCameraResult;
    private OnClickListener mOnClickVideoCapture = new OnClickListener() {
        private boolean isRecording = false;
        @Override
        public void onClick(View v) {
            if (isRecording) {
                // stop recording and release camera
                mMediaRecorder.stop();  // stop the recording
                releaseMediaRecorder(); // release the MediaRecorder object
                mCamera.lock();         // take camera access back from MediaRecorder

                // inform the user that recording has stopped
                layoutPhoto.setVisibility(View.VISIBLE);
                captureButton.setVisibility(View.GONE);
                isRecording = false;
            } else {
                // initialize video camera
                if (prepareVideoRecorder()) {
                    // Camera is available and unlocked, MediaRecorder is prepared,
                    // now you can start recording
                    mMediaRecorder.start();

                    // inform the user that recording has started
                    captureButton.setImageResource(R.drawable.control_stop);
                    isRecording = true;
                } else {
                    // prepare didn't work, release the camera
                    releaseMediaRecorder();
                    // inform user
                }
            }
        }
    };
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null){
                Log.d(TAG, "Error creating media file, check storage permissions: ");
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
                lastMediaPath = pictureFile.getPath();
                layoutPhoto.setVisibility(View.VISIBLE);
                captureButton.setVisibility(View.GONE);
                mCamera.stopPreview();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
        }
    };
    private OnClickListener mOnClickCameraCapture = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // get an image from the camera
            mCamera.takePicture(null, null, mPicture);
        }
    };

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    public void setOnCameraResult(OnCameraResult mOnCameraResult) {
        this.mOnCameraResult = mOnCameraResult;
    }

    public interface OnCameraResult{
        void onCapture(String url);
        void onDiscard();
    }

    public CameraView(Context context, String nomeArquivo, int typeCamera) {
        super(context);
        View v = LayoutInflater.from(context).inflate(R.layout.camera_activity, null);
        this.nomeArquivo = nomeArquivo;
        this.typeMedia = typeCamera;

        layoutPhoto = (LinearLayout) v.findViewById(R.id.layoutOpcaoCamera);
        savePhoto = (ImageButton) v.findViewById(R.id.savePhoto);
        discardPhoto = (ImageButton) v.findViewById(R.id.discardPhoto);

        savePhoto.setOnClickListener(this);
        discardPhoto.setOnClickListener(this);

        // Create an instance of Camera
        mCamera = getCameraInstance();
        if(mCamera == null){
            Toast.makeText(context, R.string.KEY_PROBLEMA_NA_CAMERA, Toast.LENGTH_LONG).show();
            return;
        }
        setCameraDisplayOrientation(mCamera);

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(context, mCamera);
        FrameLayout preview = (FrameLayout) v.findViewById(R.id.camera_preview);
        if(typeMedia == MEDIA_TYPE_IMAGE)
            preview.setOnClickListener(mOnClickCameraCapture);
        preview.addView(mPreview);

        // Add a listener to the Capture button
        captureButton = (ImageButton) v.findViewById(R.id.button_capture);
        if(typeMedia == MEDIA_TYPE_VIDEO) {
            captureButton.setOnClickListener(mOnClickVideoCapture);
            captureButton.setImageResource(R.drawable.video_capture);
        }else
            captureButton.setOnClickListener(mOnClickCameraCapture);
        addView(v);
    }


    private void releaseMediaRecorder(){
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.lock();           // lock camera for later use
        }
    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    private boolean prepareVideoRecorder(){

//        mCamera = getCameraInstance();
        mMediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        // Step 4: Set output file
        mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());

        // Step 5: Set the preview output
        mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());

        // Step 6: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    /** Create a file Uri for saving an image or video */
    private Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }


        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String identificador = nomeArquivo;
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ identificador + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ identificador + ".mp4");
        } else {
            return null;
        }
        lastMediaPath = mediaFile.getPath();
        return mediaFile;
    }

    public void setCameraDisplayOrientation(Camera mCamera)
    {
        if (mCamera == null)
        {
            Log.d(TAG,"setCameraDisplayOrientation - camera null");
            return;
        }

        int numberOfCameras = Camera.getNumberOfCameras();

        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(0, info);

        WindowManager winManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        int rotation = winManager.getDefaultDisplay().getRotation();

        int degrees = 0;

        switch (rotation)
        {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
        {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        mCamera.setDisplayOrientation(result);
    }

    @Override
    public void onClick(View v) {
        if (typeMedia == MEDIA_TYPE_VIDEO)
            releaseMediaRecorder();       // if you are using MediaRecorder, release it first
        releaseCamera();
        Log.e("Camera", "cameraStop");
        if (v.getId() == R.id.savePhoto) {
            mOnCameraResult.onCapture(lastMediaPath);
        } else if (v.getId() == R.id.discardPhoto) {
            mOnCameraResult.onDiscard();
        }
    }

//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        if (typeMedia == MEDIA_TYPE_VIDEO)
//            releaseMediaRecorder();       // if you are using MediaRecorder, release it first
//        releaseCamera();
//    }
}