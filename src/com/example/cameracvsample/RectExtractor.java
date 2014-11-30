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

	public int seek = 0;
	
	@Override
	public Mat onCameraFrameReady(Mat _rgba)
	{
		if(rgba==null) rgba = new Mat();
		if(gray==null) gray = new Mat(); 
		if(canny==null) canny = new Mat();
		if(work==null) work = new Mat();
		if(hsv==null) hsv = new Mat();
		
		rgba = _rgba;
		
		// to gray
		Imgproc.cvtColor(rgba, gray, Imgproc.COLOR_RGBA2GRAY);
		
		// smoothing
		gray = smoothing(gray, seek);
		
		// to canny
		Imgproc.Canny(gray, canny, 80, 100);
		
		/* to HSV
		Imgproc.cvtColor(rgba, hsv, Imgproc.COLOR_RGB2HSV);
		// Core.inRange(hsv , new Scalar(0, 60, 80), new Scalar(25, 255, 255), hsv );
		Core.inRange(hsv , new Scalar(0, 0, 0), new Scalar(255, 30, 255), hsv );
		// HSV
		// H : Hue (kind of color) 色相
		// S : Saturation 0-100(%) 彩度
 		// V : Value 0-100(%) brightness 明度
		 */

		/* Contours 
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat hierarchy = Mat.zeros(new Size(5,5), CvType.CV_8UC1);
		// input image for find contours must be "1-channel".  
		// Imgproc.findContours(hsv,  contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_TC89_L1);
		Imgproc.findContours(canny,  contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		
        for(int i=0;i<contours.size();i++){
        	Imgproc.drawContours(canny, contours, i, getRndScalar(), 1); 
        }	
        */
        
		// to approx polyline
		/*
		if(contours.size() != 0){
			double epsilon = contours.get(0).size().height * 0.01;
	        for(MatOfPoint contour : contours){
				MatOfPoint2f approx = new MatOfPoint2f();
				Imgproc.approxPolyDP(new MatOfPoint2f(contour.toArray()), approx, epsilon, true);
				if(approx.size().height==4){
		            RotatedRect bbox = Imgproc.minAreaRect(approx);
		            Rect box=bbox.boundingRect();
		            
					if(box.width < 100 || box.height < 100) continue;
					
		            Core.rectangle(rgba,box.tl(),box.br(),getRndScalar(),1);
				}
	        }
        }*/
		

		
		/* 重すぎる
		int i=0;
        for(i=0;i<contours.size();i++)
        {
            MatOfPoint ptmat= contours.get(i);
            MatOfPoint2f ptmat2 = new MatOfPoint2f( ptmat.toArray() );
            RotatedRect bbox = Imgproc.minAreaRect(ptmat2);
            Rect box=bbox.boundingRect();
			
			if(box.width < 100 || box.height < 100) continue;
			
            Core.rectangle(rgba,box.tl(),box.br(),getRndScalar(),1);
        }*/
		
		return canny;
	}
	
	Scalar getRndScalar(){
		Random rnd = new Random();
		return new Scalar(rnd.nextInt(255),rnd.nextInt(255),rnd.nextInt(255));
	}
	
	// blur
	public Mat smoothing(Mat input, int numberOfTimes){
        Mat sourceImage = new Mat();
        Mat destImage = input.clone();
        for(int i=0;i<numberOfTimes;i++){
            sourceImage = destImage.clone();
            Imgproc.blur(sourceImage, destImage, new Size(3.0, 3.0));
        }
        return destImage;
    }
}
/* Hough
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
