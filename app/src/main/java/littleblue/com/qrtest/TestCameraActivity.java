package littleblue.com.qrtest;

import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class TestCameraActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    private final String TAG = "XQY.TestCameraActivity";

    private Camera mCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_camera);
    }


    @Override
    protected void onResume() {
        super.onResume();

        initCamera();
    }

    @Override
    protected void onPause() {
        if (mCamera != null) {
            mCamera.stopPreview();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
        super.onDestroy();
    }

    private void initCamera() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Log.w(TAG, "Do not support camera");
        } else {
            int cameraId = getCameraId();
            if (cameraId != -1) {
                try {
                    mCamera = Camera.open(cameraId);
                    Log.i(TAG, "initCamera open camera mCameta: " + mCamera);
                    if (mCamera != null) {
                        mCamera.setDisplayOrientation(90);
                        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surface_view);
                        SurfaceHolder surfaceHolder = surfaceView.getHolder();
                        Log.i(TAG, "initCamera surfaceHolder: " + surfaceHolder);
                        surfaceHolder.addCallback(this);
                        setPreviewCallback(mCamera);

                    } else {
                        return;
                    }

                } catch (Exception e) {
                    Log.w(TAG, "Open camera error");
                }
            }
        }
    }

    private void setPreviewCallback(Camera camera) {
        camera.setOneShotPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                Log.i(TAG, "onPreviewFrame data: " + data);
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                setPreviewCallback(mCamera);
            }
        });
    }

    private int getCameraId() {
        int cameraId = -1;
        int cameraNumbers = Camera.getNumberOfCameras();
        for (int i = 0; i < cameraNumbers; i ++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                //后置摄像头 CAMERA_FACING_FRONT为前置
                Log.i(TAG, "getCameraId CAMERA_FACING_BACK i = " + i);
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "surfaceCreated surfaceHolder: " + holder);
        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(holder);//surface创建，设置预览SurfaceHolder
                mCamera.startPreview(); //开启预览
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "surfaceDestroyed surfaceHolder: " + holder);

        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }
}
