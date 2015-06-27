package ml.derek.uros2.desktop.util;

import org.opencv.core.Point;
import org.opencv.core.Size;

public class Measure
{
	
	// computation is done every [waitingFrames] seconds
	public static final int waitingFrames = 15;
	
	/************ IMAGE *******************/
	
	// work image dimensions
	public static final Size dsSize = new Size(320, 240); // final dimension
	public static final double diag = 400;

	// Params Gaussian Blur
	public static Size kSize = new Size(9, 9);
	public static double sigmaX = 2.5, sigmaY = 2.5;
	
	// Params Canny
	public static int cannyLowThres = 70, cannyUpThres = 120;
	public static int apertureSize = 3; // This must be odd

	// Params Hough Line Transform
	public static int houghRho = 1;
	public static double houghTheta = Math.PI/180;
	public static int houghThreshold = 60;
	public static int houghMinLineSize = 50;
	public static int houghLineGap = 30;
	
	
	/************ DOORS DETECTION *******************/
	
	// Params geometrici
	public static double heightThresL = 0.3; // 30% of camera diag
	public static double heightThresH = 0.7; // 70% of camera diag
	public static double widthThresL = 0.1; // 10% of camera diag
	public static double widthThresH = 0.8; // 80% of camera diag

	public static int dirThresL = 40;
	public static int dirThresH = 80;
	public static int parallelThres = 3;

	public static double HWThresL = 1.5;
	public static double HWThresH = 2.5;
	
	// Params Fill Ratio
	public static double FRThresL = 0.4;
	public static double FRThresH = 0.8;
	
	
	/*
	 * Calculate the relative distance between two points.
	 * 
	 * @return the distance within a range of [0,1]
	 */
	public static double calcRelDistance(Point i, Point j) {
		return calcDistance(i, j) / Measure.diag;
	}

	/*
	 * Calculate the absolute distance between two points.
	 * 
	 * @return the distance
	 */
	public static double calcDistance(Point i, Point j) {
		double sizX = Math.pow((i.x - j.x), 2);
		double sizY = Math.pow((i.y - j.y), 2);
		return Math.sqrt(sizX + sizY);
	}

	public static double calcDirection(Point i, Point j) {
		double dfX = i.x - j.x;
		double dfY = i.y - j.y;
		double dfRatio = Math.abs(dfX / dfY);
		return Math.abs(Math.atan(dfRatio) * 180 / Math.PI);
	}
}
