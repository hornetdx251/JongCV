package com.example.cameracvsample;
import java.io.File;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.core.Mat;
import org.opencv.objdetect.CascadeClassifier;

import android.content.Context;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.opencv.core.Core;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.core.*;

public class ObjectDetector implements CameraFrameListener{
	private CascadeClassifier detector = null;
	private File cascadeFile = null;
	private float relativeObjSize = 0.4f;
	private int absoluteObjSize = 0;
	void init(){
		/*
		try {
			InputStream is = getResources().getAssets().open("lbpcascade_frontalface.xml");
			// InputStream is = getResources().getAssets().open("cascade.xml");
            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
            cascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
            FileOutputStream os = new FileOutputStream(cascadeFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();
            
            detector = new CascadeClassifier(cascadeFile.getAbsolutePath());
            
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
	}
	
	@Override
	public Mat onCameraFrameReady(Mat _rgba)
	{
		/* for debugging.

        if (absoluteObjSize == 0) {
            int height = grayFrame.rows();
            if (Math.round(height * relativeObjSize) > 0) {
            	absoluteObjSize = Math.round(height * relativeObjSize);
            }
            // mNativeDetector.setMinFaceSize(absoluteObjSize);
        }

        MatOfRect faces = new MatOfRect();

        if (detector != null)
        	detector.detectMultiScale(grayFrame, faces, 1.1, 2, 2, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
                    new Size(absoluteObjSize, relativeObjSize), new Size());

        Rect[] facesArray = faces.toArray();
        for (int i = 0; i < facesArray.length; i++)
            Core.rectangle(rgbaFrame, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0, 255), 3);
	
		// Imgproc.Canny(inputFrame.gray(), outputFrame, 80, 100);
		// Core.bitwise_not(outputFrame, outputFrame);
		*/
		
		return _rgba;
	}
}
