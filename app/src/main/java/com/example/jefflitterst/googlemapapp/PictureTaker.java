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
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PictureTaker extends AppCompatActivity{
    /* TAG allows for easier debugging */
    private static final String TAG = "PicTaker: ";
    private static int TAKE_PIC = 1;
    private Uri imageUri;
    CameraManager mCameraManager;
    CameraDevice mCameraDevice;
    //private Button configure;
    private Mat photoToDetect;// = new Mat();

    /*
     * Various lists, being the images, image descriptors, image key points, image bitmaps,
     * and the sorted key points lengths
    */
    private ArrayList<Mat> imageList = new ArrayList<Mat>();
    private ArrayList<Mat> descList = new ArrayList<Mat>();
    private ArrayList<MatOfKeyPoint> kpList = new ArrayList<MatOfKeyPoint>();
    private ArrayList<Bitmap> photoList = new ArrayList<Bitmap>();
    private ArrayList<Integer> sortedLengths = new ArrayList<Integer>();
    private int count = 0;
    private int listItem = 0;

    /* Confirms that OpenCV has successfully loaded */
    static {
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV not loaded");
        } else {
            Log.d(TAG, "OpenCV loaded");
        }
    }

    /* Creation of the PictureTaker activity */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_taker);

        photoList = MapsActivity.getPhotos();
        detectKeyPoints();

        photoToDetect = new Mat();
        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        Button cameraButton = (Button)findViewById(R.id.button_camera2);
        cameraButton.setOnClickListener(cameraListener);
    }

    public OnClickListener cameraListener = new OnClickListener() {
        public void onClick(View v) {
            takePhoto(v);
        }
    };

    /* This method allows us to access the phone's camera, and take and store an image */
    private void takePhoto(View v) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Intent intent = new Intent("android.action.media.IMAGE_CAPTURE");
        File photo = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "picture.jpg");
        imageUri = Uri.fromFile(photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PIC);
    }

    /* After the photo is taken, the image is brought to this method, where it is
     * converted to a bitmap. It then has its key points extracted for comparison
     */
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

                /* Our key point detector, descriptor extractor, and descriptor matcher */
                FeatureDetector detector = FeatureDetector.create(FeatureDetector.ORB);
                DescriptorExtractor descriptor = DescriptorExtractor.create(DescriptorExtractor.ORB);
                DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);

                Mat img1 = new Mat();
                Imgproc.cvtColor(photo, img1, Imgproc.COLOR_RGB2GRAY);
                Mat descriptors1 = new Mat();
                MatOfKeyPoint keypoints1 = new MatOfKeyPoint();
                detector.detect(img1, keypoints1);
                descriptor.compute(img1, keypoints1, descriptors1);

                Mat featuredImg = new Mat();

                // Various key point color options (red is most visible)
                Scalar red = new Scalar(255,0,0);
                Scalar green = new Scalar(0,255,0);
                Scalar blue = new Scalar(0,0,255);

                //featuredImg will be the output of first image
                Features2d.drawKeypoints(img1, keypoints1, featuredImg , red, 0);

                int matchmaker;
                matchmaker = comparePhotos(img1, keypoints1, descriptors1);

                Utils.matToBitmap(featuredImg, bitmap);

                // Show image with key points on PictureTaker activity screen
                imageView.setImageBitmap(bitmap);

                // Confirms a match
                if(matchmaker <= 45) {
                    Intent data = new Intent();
                    data.setData(Uri.parse(""+listItem));
                    setResult(RESULT_OK, data);
                    Toast.makeText(PictureTaker.this, " Congratulations!\n You found a match", Toast.LENGTH_LONG).show();
                    finish();
                }
                else{
                    Toast.makeText(PictureTaker.this, "No match :(\nPlease try again", Toast.LENGTH_LONG).show();
                }

            } catch(Exception e) {
                Log.e(TAG, e.toString());
            }

        }
    }


    /* This is where the user's image is compared to the stored clue location photos. */
    private int comparePhotos(Mat image, MatOfKeyPoint keypoints, Mat descriptors){
        int minavg = 1000;
        for(listItem = 0; listItem < count; listItem++) {
            ArrayList<Float> sortedLengths = new ArrayList<Float>();
            Mat featuredImg = new Mat();
            DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
            MatOfDMatch matches = new MatOfDMatch();
            matcher.match(descList.get(listItem), descriptors, matches);

            // Each match length is stored in sortedLengths
            List<DMatch> matcheslist = matches.toList();
            for (int i = 0; i < 500; i++) {
                sortedLengths.add(matcheslist.get(i).distance);
            }

            // These lengths are sorted in terms of length in increasing order
            Collections.sort(sortedLengths);
            int average = 0;

            // Only the shortest 100 distances are used to compute the overall average distance.
            for (int i = 0; i < 100; i++) {
                average += sortedLengths.get(i);
            }

            // Distances are averaged out
            average = average / 100;

            if(average < minavg){
                minavg = average;
            }

            // Confirms a match and removes photos, key points, and descriptors from their respective lists
            if(minavg <= 45){
                removePhotos(listItem);
                imageList.remove(listItem);
                kpList.remove(listItem);
                descList.remove(listItem);
                count--;

                // Confirms that game is over
                if(photoList.isEmpty()){
                    Intent data = new Intent();
                    data.setData(Uri.parse(""+100));
                    setResult(RESULT_OK, data);
                    finish();
                }
                return minavg;
            }

            sortedLengths.clear();
        }
        return minavg;
    }

    public void removePhotos(int index){
        photoList.remove(index);
        return;
    }

    // Extracts key points from each clue location photo. Key points, descriptors,
    // and images are added to their respective lists
    private void detectKeyPoints() {
        FeatureDetector detector = FeatureDetector.create(FeatureDetector.ORB);
        DescriptorExtractor descriptor = DescriptorExtractor.create(DescriptorExtractor.ORB);


        for(int i = 0; i < photoList.size(); i++){
            Mat pic = new Mat();
            Utils.bitmapToMat(photoList.get(i), pic);
            Mat image = new Mat();
            Imgproc.cvtColor(pic, image, Imgproc.COLOR_RGB2GRAY);
            Mat descriptors1 = new Mat();
            MatOfKeyPoint keypoints1 = new MatOfKeyPoint();
            detector.detect(image, keypoints1);
            descriptor.compute(image, keypoints1, descriptors1);

            imageList.add(count, image);
            kpList.add(count, keypoints1);
            descList.add(count, descriptors1);
            count++;
        }
        return;
    }
}
