package com.example.cameracvsample;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import java.util.*;

public class RectExtractor2 implements CameraFrameListener
{
	private Mat mPyrDownMat = null;
	private Mat mHsvMat = null;
	private Scalar mLowerBound = null;
	private Scalar mUpperBound = null;
	private Mat mMask = null;
	private Mat mDilatedMask = null;
	private Mat mHierarchy = null;
	private List<MatOfPoint> mContours = null;
	private double mMinContourArea = 0.3;

	@Override
	public Mat onCameraFrameReady(Mat rgbaImage)
	{
		if(mPyrDownMat == null) mPyrDownMat = new Mat();
		if(mHsvMat == null) mHsvMat =  new Mat();
		if(mLowerBound == null) mLowerBound = new Scalar(0,0,0);
		if(mUpperBound == null) mUpperBound = new Scalar(50,255,255);
		if(mMask == null) mMask = new Mat();
		if(mDilatedMask == null) mDilatedMask = new Mat();
		if(mHierarchy == null) mHierarchy = Mat.zeros(new Size(5,5), CvType.CV_8UC1);
		if(mContours == null) mContours = new ArrayList<MatOfPoint>();
	
		
		Imgproc.pyrDown(rgbaImage, mPyrDownMat); // 1/2
		Imgproc.pyrDown(mPyrDownMat, mPyrDownMat); // 1/2

        Imgproc.cvtColor(mPyrDownMat, mHsvMat, Imgproc.COLOR_RGB2HSV_FULL);
        Core.inRange(mHsvMat, mLowerBound, mUpperBound, mMask);
		
        Imgproc.dilate(mMask, mDilatedMask, new Mat());

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(mDilatedMask, contours, mHierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
/*
        // Find max contour area
        double maxArea = 0;
        Iterator<MatOfPoint> each = contours.iterator();
        while (each.hasNext()) 
        {
            MatOfPoint wrapper = each.next();
            double area = Imgproc.contourArea(wrapper);
            if (area > maxArea)
                maxArea = area;
        }

        //Imgproc.approxPolyDP(mSpectrum, approxCurve, epsilon, closed);
		List<MatOfPoint> squareContours = getSquareContours(contours);
	
		// Filter contours by area and resize to fit the original image size
		mContours.clear();
		each = squareContours.iterator();

		while (each.hasNext()) 
		{
			MatOfPoint contour = each.next();
			if (Imgproc.contourArea(contour) > mMinContourArea * maxArea) 
			{
				Core.multiply(contour, new Scalar(4,4), contour);
				mContours.add(contour);
			}
		}
		*/
		
		for(int i=0;i<contours.size();i++){
        	Imgproc.drawContours(rgbaImage, contours, i, getRndScalar(), 1); 
        }	

		return rgbaImage;
	}

	public static List<MatOfPoint> getSquareContours(List<MatOfPoint> contours) {

		List<MatOfPoint> squares = null;

		for (MatOfPoint c : contours) {

			if (isContourSquare(c)) {

				if (squares == null)
					squares = new ArrayList<MatOfPoint>();
				squares.add(c);
			}
		}

		return squares;
	}
	
	public static boolean isContourSquare(MatOfPoint thisContour) {

		Rect ret = null;

		MatOfPoint2f thisContour2f = new MatOfPoint2f();
		MatOfPoint approxContour = new MatOfPoint();
		MatOfPoint2f approxContour2f = new MatOfPoint2f();

		thisContour.convertTo(thisContour2f, CvType.CV_32FC2);

		Imgproc.approxPolyDP(thisContour2f, approxContour2f, 2, true);

		approxContour2f.convertTo(approxContour, CvType.CV_32S);

		if (approxContour.size().height == 4) {
			ret = Imgproc.boundingRect(approxContour);
		}

		return (ret != null);
	}
	
	Scalar getRndScalar(){
		Random rnd = new Random();
		return new Scalar(rnd.nextInt(255),rnd.nextInt(255),rnd.nextInt(255));
	}
}
