package ml.derek.uros2.android;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import ml.derek.uros2.desktop.Detection;
import ml.derek.uros2.desktop.Draw;
import ml.derek.uros2.desktop.ShapeDetect;
import ml.derek.uros2.desktop.util.MatType;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.util.Map;

public class MyActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2
{
    private CameraBridgeViewBase mOpenCvCameraView;
    private MatType selectedMat = MatType.Full;
    private Rect regionOfIntrest;
    private double thresh1 = 150;
    private double thresh2 = 80;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Create buttons for enums
        LinearLayout buttonHolder = (LinearLayout) findViewById(R.id.buttonHolder);
        for(MatType matType : MatType.values())
        {
            final Button button = new Button(getApplicationContext());
            button.setText(matType.toString());
            button.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    selectedMat = MatType.valueOf(button.getText().toString());
                }
            });
            buttonHolder.addView(button);

        }

        // Create threshold sliders
        EditText editThresh1 = new EditText(getApplicationContext());
        editThresh1.setInputType(InputType.TYPE_CLASS_NUMBER);
        editThresh1.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                try
                {

                    double dthresh = Double.parseDouble(s.toString());
                    if (dthresh > 0)
                        thresh1 = Double.parseDouble(s.toString());
                }
                catch(NumberFormatException e)
                {
                    thresh1 = 100;
                }
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });
        editThresh1.setText(String.valueOf(thresh1));
        buttonHolder.addView(editThresh1);

        // Create threshold sliders
        EditText editThresh2 = new EditText(getApplicationContext());
        editThresh2.setInputType(InputType.TYPE_CLASS_NUMBER);
        editThresh2.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                try
                {
                    double dthresh = Double.parseDouble(s.toString());
                    if (dthresh > 0)
                        thresh2 = Double.parseDouble(s.toString());
                }
                catch(NumberFormatException e)
                {
                    thresh2 = 50;
                }
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });
        editThresh2.setText(String.valueOf(thresh2));
        buttonHolder.addView(editThresh2);

        Log.i("camera", "called onCreate");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE);
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.HelloOpenCvView);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setMaxFrameSize(640, 360);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this)
    {
        @Override
        public void onManagerConnected(int status)
        {
            switch (status)
            {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("camera", "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                }
                break;
                default:
                {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
    }

    public void onDestroy()
    {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height)
    {
    }

    public void onCameraViewStopped()
    {
    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame)
    {
        Mat newImage = inputFrame.rgba();
        MatOfPoint door = ShapeDetect.getDoor(thresh1, thresh2, newImage);

        if(door != null)
        {
            newImage = Draw.contours(newImage, door);
            return newImage;
        }
        else
            return newImage;
    }
}
