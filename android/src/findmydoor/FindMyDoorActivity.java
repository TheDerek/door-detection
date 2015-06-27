package findmydoor;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import ml.derek.uros2.android.R;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FindMyDoorActivity extends Activity implements
		CvCameraViewListener2 {
	private static final String TAG = "OCV::Activity";
	private static final Scalar white = new Scalar(255, 255, 255);

	private Mat mRgba; // original image
	private Mat mEdit; // work image (canny)
	private Mat mReturn; // pointer to the image to show
	private Mat mLine; // found edge image

	private List<Point> corners;

	private Size imgSize;
	private CameraBridgeViewBase mOpenCvCameraView; // interface to camera

	private boolean willResize;

	private List<Door> doors;
	private List<Point> cornersList;
	private ArrayList<Line> lineList;
	private int waited;

	public FindMyDoorActivity() {
		super();
		Log.i(TAG, "Instantiated new " + this.getClass());
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "called onCreate");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.find_my_door_surface_view);

		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.find_my_door_activity_surface_view);
		mOpenCvCameraView.setCvCameraViewListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	@Override
	public void onResume() {
		super.onResume();
		/* check if OpenCVManager is installed on the device */
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this,
				new BaseLoaderCallback(this) {
					@Override
					public void onManagerConnected(int status) {
						switch (status) {
						case LoaderCallbackInterface.SUCCESS: {
							Log.i(TAG, "OpenCV loaded successfully");
							mOpenCvCameraView.enableView();
						}
							break;
						default: {
							super.onManagerConnected(status);
						}
							break;
						}
					}
				});
	}

	public void onDestroy() {
		super.onDestroy();
		if (mOpenCvCameraView != null)
			mOpenCvCameraView.disableView();
	}

	public void onCameraViewStarted(int width, int height) {
		imgSize = new Size(width, height);
		Math.sqrt(Math.pow(height, 2) + Math.pow(width, 2));
		willResize = !Measure.dsSize.equals(imgSize);

		mRgba = new Mat(imgSize, CvType.CV_8UC4);
		corners = new ArrayList<Point>();

		Log.d(TAG, "height: " + height);
		Log.d(TAG, "width: " + width);

		waited = Measure.waitingFrames;
	}

	public void onCameraViewStopped() {
		mRgba.release();
	}

	private Mat addBorder(Mat img, int borderSize) {
		// add an inner white border in order to create a corner
		// when the real one is occluded

		Size roiSize = new Size(img.width() - borderSize * 2, img.height()
				- borderSize * 2);
		Rect roi = new Rect(new Point(borderSize, borderSize), roiSize);

		Mat mCrop = img.submat(roi);

		Mat mBorder = img.clone();

		Core.copyMakeBorder(mCrop, mBorder, borderSize, borderSize,
				borderSize, borderSize, Core.BORDER_ISOLATED, white);


		return mBorder;
	}

	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		if (waited < Measure.waitingFrames) {
			waited++;
//			return printMat(mReturn);
			return printMat(inputFrame.rgba());
		}
		waited = 0;
		mRgba = inputFrame.rgba();
		mEdit = new Mat();
		corners.clear();

		// Smoothing
		Imgproc.GaussianBlur(mRgba, mEdit, Measure.kSize, Measure.sigmaX,
				Measure.sigmaY);

		// Down-sampling
		if (willResize) {
			Imgproc.resize(mEdit, mEdit, Measure.dsSize);
		}
		mEdit = addBorder(mEdit, 2);

		// Detecting edge
		Imgproc.Canny(mEdit, mEdit, Measure.cannyLowThres,
				Measure.cannyUpThres, Measure.apertureSize, false);

		// Detect lines
		mLine = new Mat();
		Imgproc.HoughLinesP(mEdit, mLine, Measure.houghRho, Measure.houghTheta,
				Measure.houghThreshold, Measure.houghMinLineSize,
				Measure.houghLineGap);

		lineList = matToListLines(mLine);
		Log.v(TAG, lineList.size() + " lines detected");

		cornersList = new ArrayList<Point>();

		mLine = Mat.zeros(mEdit.height(), mEdit.width(), mEdit.type());
		int thickness = 2;

		// Detect Doors
		doors = new ArrayList<Door>();

		for (int i = 0; i < lineList.size(); i++) {
			Line line1 = lineList.get(i);
			Imgproc.line(mLine, line1.start, line1.end, white, thickness);
		}

		for (int i = 0; i < lineList.size(); i++) {
			Line line1 = lineList.get(i);

			for (int j = i + 1; j < lineList.size(); j++) {
				Line line2 = lineList.get(j);

				for (int k = j + 1; k < lineList.size(); k++) {
					Line line3 = lineList.get(k);
					for (int l = k + 1; l < lineList.size(); l++) {
						Line line4 = lineList.get(l);

						Door newDoor = doorDetect(line1, line2, line3, line4);
						if (newDoor != null) {
							Log.d(TAG, "Door found!");
							doors.add(newDoor);
							break;
						}
					}
				}
			}
		}

		Collections.sort(doors);

		// Up-sampling mEdit (edge image)
		if (willResize) {
			// up-sampling points position
			for (Point c : cornersList) {
				c.x = imgSize.width / Measure.dsSize.width * (c.x);
				c.y = imgSize.height / Measure.dsSize.height * (c.y);
			}

			for (Line l : lineList) {
				Point[] pts = { l.start, l.end };
				for (Point p : pts) {
					p.x = imgSize.width / Measure.dsSize.width * (p.x);
					p.y = imgSize.height / Measure.dsSize.height * (p.y);
				}
			}

			Imgproc.resize(mEdit, mEdit, imgSize);
			Imgproc.resize(mLine, mLine, imgSize);

		}

		mReturn = mRgba;

		return printMat(mReturn);
	}

	/*
	 * Convert the output Mat(1XNX4) of HoughLineP in an array of lines.
	 */
	private ArrayList<Line> matToListLines(Mat src) {
		ArrayList<Line> dstList = new ArrayList<Line>();

		for (int x = 0; x < src.cols(); x++) {
			double[] vec = src.get(0, x);
			double x1 = vec[0], y1 = vec[1], x2 = vec[2], y2 = vec[3];

			Point start = new Point(x1, y1);
			Point end = new Point(x2, y2);

			try {
				dstList.add(new Line(start, end));
			} catch (RuntimeException e) {
				// do nothing --> useless line
			}
		}
		return dstList;
	}

	private Mat printMat(Mat img) {
		if (img.type() != mRgba.type()) {
			Imgproc.cvtColor(img, img, Imgproc.COLOR_GRAY2RGBA);
		}

		if (lineList != null) {
			for (int i = 0; i < lineList.size(); i++) {
				Line line = lineList.get(i);
				Point p1 = line.start;
				Point p2 = line.end;

				Scalar yellow = new Scalar(0, 255, 255);
				Imgproc.line(img, p1, p2, yellow, 3);
			}
		}

		if (doors.size() > 0) {
			// disegna la porta pi� probabile
			drawDoor(img, doors.get(0));
		}

		// if (doors != null) {
		// for (Door door : doors) {
		// drawDoor(img, door);
		// }
		// }

		// Draw Corners
		if (cornersList != null) {
			for (Point c : cornersList)
				Imgproc.circle(img, c, 15, new Scalar(255, 0, 0), 2, 8, 0);
		}
		return img;
	}

	private void drawDoor(Mat image, Door door) {
		Scalar doorColor = new Scalar(0, 255, 0);
		Imgproc.line(image, door.getP1(), door.getP2(), doorColor, 4);
		Imgproc.line(image, door.getP2(), door.getP3(), doorColor, 4);
		Imgproc.line(image, door.getP3(), door.getP4(), doorColor, 4);
		Imgproc.line(image, door.getP4(), door.getP1(), doorColor, 4);
	}

	private Door doorDetect(Line line1, Line line2, Line line3, Line line4) {
		ArrayList<Line> lineArray = new ArrayList<Line>();
		lineArray.add(line1);
		lineArray.add(line2);
		lineArray.add(line3);
		lineArray.add(line4);

		Door detectedDoor = null;

		try {
			detectedDoor = new Door(lineArray);
		} catch (RuntimeException re) {
			// do nothing
			return null;
		}

		cornersList.add(detectedDoor.getP1());
		cornersList.add(detectedDoor.getP2());
		cornersList.add(detectedDoor.getP3());
		cornersList.add(detectedDoor.getP4());

		return fillRatioCheck(detectedDoor);
	}

	private Door fillRatioCheck(Door detectedDoor) {
		// Compare with edge img
		double FR12 = calculateFillRatio(detectedDoor.getP1(),
				detectedDoor.getP2());

		if (FR12 < Measure.FRThresL) {
			return null;
		}
		// Log.d(TAG, "fill ratio 12: " + FR12);

		double FR23 = calculateFillRatio(detectedDoor.getP2(),
				detectedDoor.getP3());

		if (FR23 < Measure.FRThresL) {
			return null;
		}

		double FR34 = calculateFillRatio(detectedDoor.getP3(),
				detectedDoor.getP4());

		if (FR34 < Measure.FRThresL) {
			return null;
		}

		double FR41 = calculateFillRatio(detectedDoor.getP4(),
				detectedDoor.getP1());
		// Log.d(TAG, "fillRatio41: " + FR41);

		if (FR41 < Measure.FRThresL) {
			return null;
		}

		double avgFR = (FR12 + FR23 + FR34 + FR41) / 4;
		Log.w(TAG, "AVGfr: " + avgFR);
		if (avgFR < Measure.FRThresH)
			return null;

		detectedDoor.setAvgFillRatio(avgFR);

		return detectedDoor;
	}

	private double calculateFillRatio(Point pA, Point pB) {
		Mat lineImg = Mat.zeros(mEdit.height(), mEdit.width(), CvType.CV_32FC1);

		int thickness = 2;
		Imgproc.line(lineImg, pA, pB, white, thickness);

		int overLapAB = 0, lengthAb = 0; // length white px in this line

		double linePx = 0, oldLinePx;
		// oldpix = previous px

		Size roiSize = new Size(Math.abs(pA.x - pB.x), Math.abs(pA.y - pB.y));
		Rect roi = new Rect(new Point(Math.min(pA.x, pB.x),
				Math.min(pA.y, pB.y)), roiSize);

		Mat lineCrop = lineImg.submat(roi);
		Mat allLinesCrop = mLine.submat(roi);

		for (int i = 0; i < roiSize.width; i++) {
			for (int j = 0; j < roiSize.height; j++) {

				oldLinePx = linePx;
				linePx = lineCrop.get(j, i)[0];
				double thres = 20;
				if (linePx < thres) {
					if (oldLinePx > thres) // border passed
						break;
					else
						continue;
				}
				lengthAb++;

				if (allLinesCrop.get(j, i)[0] > thres) {
					overLapAB++;
				}
			}
		}

		lineCrop.release();
		allLinesCrop.release();

		double fillRatio;
		if (overLapAB == 0) {
			fillRatio = 0;
		} else {
			fillRatio = (double) overLapAB / lengthAb;
			Log.w(TAG, "overLap :" + fillRatio);
		}

		return fillRatio;
	}

}