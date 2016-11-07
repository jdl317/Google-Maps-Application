package com.example.jefflitterst.googlemapapp;

import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Point;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jefflitterst on 11/6/16.
 */

public class ObjectRec {
    private static final String TAG = "ObRec: ";

    private Mat frame;
    private List<Mat> images;
    private List<Point> usedKeyPoints;
    private List<KeyPoint> keyPoints;

    private MatOfKeyPoint frameKeyPoints;
    private MatOfKeyPoint realKeyPoints;

    private Mat frameDescriptors;
    private List<Mat> realDescriptors;
    private Mat prevDescriptors;
    private List<MatOfDMatch> realMatch = new ArrayList<>();

    private FeatureDetector detector;
    private DescriptorExtractor extractor;
    private DescriptorMatcher matcher;
    private int matchingPoints;


    ObjectRec() {
        frameKeyPoints = new MatOfKeyPoint();
        frameDescriptors = new Mat();
        usedKeyPoints = new ArrayList<Point>();

        detector = FeatureDetector.create(FeatureDetector.SIFT);
        extractor = DescriptorExtractor.create(FeatureDetector.SURF);
        matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE);
        matchingPoints = 0;
    }

    

}
