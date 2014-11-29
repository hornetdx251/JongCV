package com.example.cameracvsample;

import org.opencv.core.Mat;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;

public interface CameraFrameListener
{
	Mat onCameraFrameReady(CvCameraViewFrame image);
}
