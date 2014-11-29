package com.example.cameracvsample;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import java.util.*;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;

public class RectExtractor implements CameraFrameListener
{
	Mat rgba = null;
	Mat gray = null;
	Mat canny = null;
	Mat work = null;
	Mat hsv = null;

	@Override
	public Mat onCameraFrameReady(Mat _rgba)
	{
		if(rgba==null) rgba = new Mat();
		if(gray==null) gray = new Mat(); 
		if(canny==null) canny = new Mat();
		if(work==null) work = new Mat();
		if(hsv==null) hsv = new Mat();
		
		rgba = _rgba;
		Imgproc.cvtColor(rgba, gray, Imgproc.COLOR_RGBA2GRAY);
		Imgproc.Canny(gray, canny, 80, 100);
		
		Imgproc.cvtColor(rgba, hsv, Imgproc.COLOR_RGB2HSV);
		// Core.inRange(hsv , new Scalar(0, 60, 80), new Scalar(25, 255, 255), hsv );
		Core.inRange(hsv , new Scalar(0, 0, 0), new Scalar(255, 50, 255), hsv );
		// HSV
		// H : Hue (kind of color) êFëä
		// S : Saturation 0-100(%) ç ìx
 		// V : Value 0-100(%) brightness ñæìx

/*
		Mat lines = new Mat();
		Mat tmp = new Mat();
		tmp = hsv.clone();
		Imgproc.HoughLines(tmp, lines,1,Math.PI/180,200,0,0);
		
		float[] data = new float[2];
		for (int i=0; i<lines.cols(); ++i) {
			lines.get(0,  i, data);
			double rho = data[0];
			double theta = data[1];
			Point pt1 = new Point();
			Point pt2 = new Point();
			double a = Math.cos(theta);
			double b = Math.sin(theta);
			double x0 = a * rho;
			double y0 = b * rho;
			pt1.x = Math.round(x0 + 1000*(-b));
			pt1.y = Math.round(y0 + 1000*(a));
			pt2.x = Math.round(x0 - 1000*(-b));
			pt2.y = Math.round(y0 - 1000*(a));
			// Core.line(rgba, pt1, pt2, new Scalar(0, 0, 255), 3);
		}
	*/	
		/* Contours
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat hierarchy = Mat.zeros(new Size(5,5), CvType.CV_8UC1);
		// input image for find contours must be "1-channel".  
		// Imgproc.findContours(hsv,  contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_TC89_L1);
		Imgproc.findContours(hsv,  contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		Imgproc.drawContours(rgba, contours, -1, new Scalar(0,0,255), 1); 
		
		int i=0;
        for(i=0;i<contours.size();i++)
        {
            MatOfPoint ptmat= contours.get(i);
            MatOfPoint2f ptmat2 = new MatOfPoint2f( ptmat.toArray() );
            RotatedRect bbox = Imgproc.minAreaRect(ptmat2);
            Rect box=bbox.boundingRect();
			
			if(box.width < 100 || box.height < 100) continue;
			if(box.width > 500 || box.height > 500) continue;
			if(box.height / box.width > 2 ) continue;
			
            Core.rectangle(rgba,box.tl(),box.br(),new Scalar(0,0,255),2);
        }*/
		return hsv;
	}
}
