package com.example.cameracvsample;

import org.opencv.core.Mat;

public interface CameraFrameListener
{
	Mat onCameraFrameReady(Mat rgba);
}
