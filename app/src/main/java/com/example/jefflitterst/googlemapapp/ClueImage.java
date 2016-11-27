package com.example.jefflitterst.googlemapapp;

//import org.opencv.core.KeyPoint;
//import org.opencv.core.Mat;
//import org.opencv.core.MatOfDMatch;
//import org.opencv.core.MatOfKeyPoint;
//import org.opencv.core.Point;
//import org.opencv.features2d.DescriptorExtractor;
//import org.opencv.features2d.DescriptorMatcher;
//import org.opencv.features2d.FeatureDetector;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Scalar;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jefflitterst on 11/6/16.
 */

public class ClueImage {
    private static final String TAG = "ObRec: ";

//    private Mat frame;
//    private List<Mat> images;
//    private List<Point> usedKeyPoints;
//    private List<KeyPoint> keyPoints;
//
//    private MatOfKeyPoint frameKeyPoints;
//    private MatOfKeyPoint realKeyPoints;
//
//    private Mat frameDescriptors;
//    private List<Mat> realDescriptors;
//    private Mat prevDescriptors;
//    private List<MatOfDMatch> realMatch = new ArrayList<>();
//
//    private FeatureDetector detector;
//    private DescriptorExtractor extractor;
//    private DescriptorMatcher matcher;
//    private int matchingPoints;
//
//
//    ClueImage() {
//        frameKeyPoints = new MatOfKeyPoint();
//        frameDescriptors = new Mat();
//        usedKeyPoints = new ArrayList<Point>();
//
//        detector = FeatureDetector.create(FeatureDetector.SIFT);
//        extractor = DescriptorExtractor.create(FeatureDetector.SURF);
//        matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE);
//        matchingPoints = 0;
//    }

    private Mat image;
    private MatOfKeyPoint keypoints;



    public ClueImage(Mat image, MatOfKeyPoint keypoints){
        this.image = image;
        this.keypoints = keypoints;
    }


//public void detectPhoto() {
//    Mat photo = new Mat();
//
//    Utils.bitmapToMat(bitmap, photo);
//
//    FeatureDetector detector = FeatureDetector.create(FeatureDetector.ORB);
//    DescriptorExtractor descriptor = DescriptorExtractor.create(DescriptorExtractor.ORB);
//    ;
//    DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
//
//    //first image
////                Mat img1 = Highgui.imread(selectedImage.toString(), 0);
//    Mat img1 = new Mat();
//    Imgproc.cvtColor(photo, img1, Imgproc.COLOR_RGB2GRAY);
//    Mat descriptors1 = new Mat();
//    MatOfKeyPoint keypoints1 = new MatOfKeyPoint();
//    detector.detect(img1, keypoints1);
//    descriptor.compute(img1, keypoints1, descriptors1);
//
////                Imgproc.cvtColor(img1, photo, Imgproc.COLOR_GRAY2RGB);
//
//    Mat featuredImg = new Mat();
//    Scalar kpColor = new Scalar(0, 255, 0);//this will be color of keypoints
//    //featuredImg will be the output of first image
//    Features2d.drawKeypoints(img1, keypoints1, featuredImg, kpColor, 0);
//
//
//    Utils.matToBitmap(featuredImg, bitmap);
//    imageView.setImageBitmap(bitmap);
//    Toast.makeText(PictureTaker.this, "Key points successfully saved!", Toast.LENGTH_LONG).show();
//    //Toast.makeText(PictureTaker.this, selectedImage.toString(), Toast.LENGTH_LONG).show();
//}


}
