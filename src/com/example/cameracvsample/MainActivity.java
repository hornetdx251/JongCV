package com.example.cameracvsample;

import java.util.*;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;
import android.widget.SeekBar.OnSeekBarChangeListener;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.android.*;
import android.widget.RelativeLayout.*;
import android.view.*;

public class MainActivity extends Activity 
	implements CvCameraViewListener2{
	
	private CameraBridgeViewBase cameraView = null;	
	// private List<CameraFrameListener> listeners = null;
	RectExtractor listener = null;
	private Mat rgbaFrame = null;
	// private Mat debugImage = null;
	TextView txt1 = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// full screen & no title
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Root
		FrameLayout frameLayout = new FrameLayout(this);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
			FrameLayout.LayoutParams.MATCH_PARENT,
			FrameLayout.LayoutParams.MATCH_PARENT);
		params.gravity = Gravity.RIGHT;
		frameLayout.setLayoutParams(params);
		
			// Camera
			cameraView  = new JavaCameraView(this,0);		
			cameraView.setCvCameraViewListener(this);
			LinearLayout cameraLayout =new LinearLayout(this);
			cameraLayout.setOrientation(LinearLayout.HORIZONTAL);
			cameraLayout.addView(cameraView,
						   new LinearLayout.LayoutParams(
							   LinearLayout.LayoutParams.MATCH_PARENT,
							   LinearLayout.LayoutParams.MATCH_PARENT));

			// UI
			LinearLayout uiLayout = new LinearLayout(this);
			LinearLayout.LayoutParams uiLayoutParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT);
			uiLayoutParams.gravity = Gravity.RIGHT | Gravity.BOTTOM;
			uiLayoutParams.setMargins(50, 50, 50, 50);
			uiLayout.setLayoutParams(uiLayoutParams);
			uiLayout.setOrientation(LinearLayout.VERTICAL);
			
				// UI contens
				SeekBar seekbar = new SeekBar(this);
				seekbar.setMax(100);
				seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

					@Override
					public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
					}

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						listener.seek = seekBar.getProgress();
						if(txt1!=null) txt1.setText(seekBar.getProgress() + "");
					}
					
				});
				seekbar.setProgress(0);
				
				Button btn = new Button(this);
				btn.setText("hogehoge");
				
				txt1 = new TextView(this);
				txt1.setText(seekbar.getProgress() + "");
			
			uiLayout.addView(btn, new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT));
			uiLayout.addView(seekbar, new LinearLayout.LayoutParams(
					   LinearLayout.LayoutParams.MATCH_PARENT,
					   LinearLayout.LayoutParams.WRAP_CONTENT));
			uiLayout.addView(txt1, new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT));
			
		frameLayout.addView(cameraLayout,
				new LinearLayout.LayoutParams(
						   LinearLayout.LayoutParams.MATCH_PARENT,
						   LinearLayout.LayoutParams.MATCH_PARENT));
		frameLayout.addView(uiLayout);
		
		setContentView(frameLayout);
		
		// initialization for camera frame listener.
		// listeners = new ArrayList<CameraFrameListener>();
		// listeners.add(new RectExtractor());
		listener = new RectExtractor();
		listener.seek = seekbar.getProgress();
	}

	@Override
	protected void onResume() {
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_7, this, loaderCallback);
	}
	
	private BaseLoaderCallback loaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch(status){
			case LoaderCallbackInterface.SUCCESS:
				cameraView.enableView();
				break;
			default:
				super.onManagerConnected(status);
				break;
			}	
		};
	};
	
	@Override
	protected void onPause() {
		if(cameraView!=null) cameraView.disableView();
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(cameraView!=null) cameraView.disableView();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
// -------------------------------------------------------------------------------------
	@Override
	public void onCameraViewStarted(int width, int height) {
		rgbaFrame = new Mat();
	}

	@Override
	public void onCameraViewStopped() {
		rgbaFrame.release();
	}

	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		/*
		if(debugImage==null){
			// initialization for debug image.
			Bitmap debugBmp = BitmapFactory.decodeResource(getResources(), R.drawable.jong);
			debugImage = new Mat();
			org.opencv.android.Utils.bitmapToMat(debugBmp, debugImage);
		}
		rgbaFrame = debugImage;
		*/
		rgbaFrame = inputFrame.rgba();
		Mat dispImage = null;
		
		/*
		for(CameraFrameListener listener : listeners){
			dispImage = listener.onCameraFrameReady(rgbaFrame);
		}*/
		dispImage = listener.onCameraFrameReady(rgbaFrame);
		
		return dispImage;
	}

}
