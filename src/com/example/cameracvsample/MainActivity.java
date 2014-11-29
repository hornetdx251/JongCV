package com.example.cameracvsample;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.android.*;


import org.opencv.core.*;
import java.util.*;
public class MainActivity extends Activity 
	implements CvCameraViewListener2{
	
	// OpenCVの提供するカメラを扱う2つのViewである、JavaCameraView or NativeCameraViewのスーパークラス
	private CameraBridgeViewBase cameraView = null;
	
	// for 画像認識
	private List<CameraFrameListener> listeners = null;
	
	private CascadeClassifier detector = null;
	private File cascadeFile = null;
	private float relativeObjSize = 0.4f;
	private int absoluteObjSize = 0;
	private Mat rgbaFrame = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// ステータスバー非表示
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		//  タイトルバー非表示
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		// CameraViewのインスタンス情報をリソースファイルから取得する
		// cameraView = (CameraBridgeViewBase)findViewById(R.id.camera_view);
		cameraView  = new JavaCameraView(this,0);
		
		// OpenCVのカメラ管理クラスのリスナーを自身で登録しておく(this implements CvCameraViewListener)
		cameraView.setCvCameraViewListener(this);
		
		LinearLayout layout =new LinearLayout(this);
		layout.setOrientation(LinearLayout.HORIZONTAL);
		layout.addView(cameraView,
					   new LinearLayout.LayoutParams(
						   LinearLayout.LayoutParams.FILL_PARENT,
						   LinearLayout.LayoutParams.FILL_PARENT));
		setContentView(layout);
		// setContentView(R.layout.activity_main);
		
		listeners = new ArrayList<CameraFrameListener>();
		listeners.add(new RectExtractor());
	}

	// アプリサイズ節約の為、OpenCVライブラリは外部モジュールからインストールされる
	@Override
	protected void onResume() {
		super.onResume();
		
		// OpenCVのランタイムを実行環境へインストールする為、OpenCVManagerを起動させる。
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_7, this, loaderCallback);
	}
	
	// OpenCVManagerでインストール完了時のイベントを受け取る為のコールバック
	private BaseLoaderCallback loaderCallback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch(status){
			case LoaderCallbackInterface.SUCCESS:
				/*
				// リソースからカスケードファイルを読み込む
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
				// 読み込みに成功した場合、カメラプレビューを開始する。
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
	
// ------------------------ CvCameraViewListener2の実装 -------------------------------------------------
	
	// カメラプレビュー開始時に呼ばれる
	@Override
	public void onCameraViewStarted(int width, int height) {
		// rgbaFrame = new Mat(height,width,CvType.CV_8UC(1));
		rgbaFrame = new Mat();
		
	}

	// カメラプレビュー終了時に呼ばれる
	@Override
	public void onCameraViewStopped() {
		rgbaFrame.release();
	}

	// フレームをキャプチャする毎に呼ばれる(**fps)
	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		rgbaFrame = inputFrame.rgba();
		Mat dispImage = null;
		
		for(CameraFrameListener listener : listeners){
			dispImage = listener.onCameraFrameReady(inputFrame);
		}
		
		
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
	
		// Cannyフィルタをかける
		// Imgproc.Canny(inputFrame.gray(), outputFrame, 80, 100);
		// bit反転する
		// Core.bitwise_not(outputFrame, outputFrame);
		*/
		return dispImage;
	}

}
