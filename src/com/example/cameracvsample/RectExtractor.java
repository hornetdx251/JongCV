package com.example.cameracvsample;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import java.util.*;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;

public class RectExtractor implements CameraFrameListener
{
	Mat dispImage = null;
	Mat canny = null;
	Mat work = null;
	
	@Override
	public Mat onCameraFrameReady(CvCameraViewFrame frame)
	{
		if(dispImage==null) dispImage = new Mat();
		if(canny==null) canny = new Mat();
		if(work==null) work = new Mat();
		
		dispImage = frame.rgba();
		
		
		// Imgproc.cvtColor(rangeImage, rangeImage, Imgproc.COLOR_RGB2HSV); //色空間をRGB→HSVに変換
		// Core.inRange(rangeImage , new Scalar(0, 60, 80), new Scalar(25, 255, 255), rangeImage );//肌色抽出

		Imgproc.Canny(frame.gray(), work, 80, 100);
		Mat lines = new Mat();
		Imgproc.HoughLines(work, lines,1,Math.PI/180,200,0,0);
		
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
			Core.line(dispImage, pt1, pt2, new Scalar(0, 0, 255), 3);
		}
		
		/* Contours
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Imgproc.Canny(frame.gray(), canny, 80, 100);
		Mat hierarchy = Mat.zeros(new Size(5,5), CvType.CV_8UC1);
		Imgproc.findContours(canny,  contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_TC89_L1);
		Imgproc.drawContours(dispImage, contours, -1, new Scalar(0,0,255), 1); 
		
		int i=0;
        for(i=0;i<contours.size();i++)
        {
            MatOfPoint ptmat= contours.get(i);
            MatOfPoint2f ptmat2 = new MatOfPoint2f( ptmat.toArray() );
            RotatedRect bbox = Imgproc.minAreaRect(ptmat2);
            Rect box=bbox.boundingRect();
			
			if(box.width < 100 || box.height < 100) continue;
			
            Core.rectangle(dispImage,box.tl(),box.br(),new Scalar(0,0,255),2);
        }*/
		return dispImage;
	}
}