package com.example.jefflitterst.googlemapapp;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Scalar;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.ArrayList;

public class PictureTaker extends AppCompatActivity{
    private static final String TAG = "PicTaker: ";
    private static int TAKE_PIC = 1;
    private Uri imageUri;
    CameraManager mCameraManager;
    CameraDevice mCameraDevice;
    private Button configure;
    private Mat photoToDetect;// = new Mat();

    private ArrayList<Mat> imageList = new ArrayList<Mat>();
    private ArrayList<MatOfKeyPoint> kpList = new ArrayList<MatOfKeyPoint>();
    private int count = 0;



    /*
    public PictureTaker() {
        onCreate(new Bundle());
    }
    */

    static {
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV not loaded");
        } else {
            Log.d(TAG, "OpenCV loaded");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_taker);

        photoToDetect = new Mat();
        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        Button cameraButton = (Button)findViewById(R.id.button_camera2);
        cameraButton.setOnClickListener(cameraListener);
        configure = (Button)findViewById(R.id.button_configure);
        configure.setOnClickListener(configListener);
    }

    public OnClickListener cameraListener = new OnClickListener() {
        public void onClick(View v) {
            takePhoto(v);
        }
    };

    private void takePhoto(View v) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Intent intent = new Intent("android.action.media.IMAGE_CAPTURE");
        File photo = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "picture.jpg");
        imageUri = Uri.fromFile(photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PIC);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if(resultCode == Activity.RESULT_OK) {
            Uri selectedImage = imageUri;
            getContentResolver().notifyChange(selectedImage, null);

            ImageView imageView = (ImageView)findViewById(R.id.image_camera);
            ContentResolver cr = getContentResolver();
            Bitmap bitmap;

            try{
                bitmap = MediaStore.Images.Media.getBitmap(cr, selectedImage);

                Mat photo = new Mat();

                Utils.bitmapToMat(bitmap, photo);

                FeatureDetector detector = FeatureDetector.create(FeatureDetector.ORB);
                DescriptorExtractor descriptor = DescriptorExtractor.create(DescriptorExtractor.ORB);
                DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);

                //first image
//                Mat img1 = Highgui.imread(selectedImage.toString(), 0);
                Mat img1 = new Mat();
                Imgproc.cvtColor(photo, img1, Imgproc.COLOR_RGB2GRAY);
                Mat descriptors1 = new Mat();
                MatOfKeyPoint keypoints1 = new MatOfKeyPoint();
                detector.detect(img1, keypoints1);
                descriptor.compute(img1, keypoints1, descriptors1);

                //ClueImage pic1 = new ClueImage(img1, keypoints1);

//                Imgproc.cvtColor(img1, photo, Imgproc.COLOR_GRAY2RGB);

                Mat featuredImg = new Mat();
                Scalar kpColor = new Scalar(0,255,0);//this will be color of keypoints
                //featuredImg will be the output of first image
                Features2d.drawKeypoints(img1, keypoints1, featuredImg , kpColor, 0);

                imageList.add(count, img1);
                kpList.add(count, keypoints1);
                count++;

                Utils.matToBitmap(featuredImg, bitmap);
                imageView.setImageBitmap(bitmap);
                Toast.makeText(PictureTaker.this, "Key points successfully uploaded!", Toast.LENGTH_LONG).show();
                //Toast.makeText(PictureTaker.this, selectedImage.toString(), Toast.LENGTH_LONG).show();

            } catch(Exception e) {
                Log.e(TAG, e.toString());
            }

        }
    }

    private View.OnClickListener configListener = new View.OnClickListener() {
        public void onClick(View v) {
            //detectKeyPoints();
        }
    };


    private void detectKeyPoints() {
        int detectorType = FeatureDetector.SURF;
        FeatureDetector detector = FeatureDetector.create(detectorType);

        Mat mask = new Mat();
        MatOfKeyPoint keypoints = new MatOfKeyPoint();
        detector.detect(photoToDetect, keypoints, mask);
        Toast.makeText(PictureTaker.this, "Photo successfully uploaded", Toast.LENGTH_LONG).show();

        /*
        if(!detector.empty()){
            Mat output = new Mat();
            Scalar color = new Scalar(0, 0, 255);
            int flags = Features2d.DRAW_RICH_KEYPOINTS;
            Features2d.drawKeypoints(photoToDetect, keypoints, output, color, flags);
            Utils.matToBitmap(output, bitmap);
        }
        */
        return;
    }

    /*
    @TargetApi(21)
    private void openCamera() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mCameraManager.openCamera("0", new StateListener() {
            @Override
            public void onOpened(CameraDevice cameraDevice) {
                mCameraDevice = cameraDevice;
            }
        }, null);

    }


    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA}, PackageManager.GET_PERMISSIONS);
        }
    }
    */
}
