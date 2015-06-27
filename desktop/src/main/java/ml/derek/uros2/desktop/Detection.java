package ml.derek.uros2.desktop;

import ml.derek.uros2.desktop.util.Convert;
import ml.derek.uros2.desktop.util.Display;
import ml.derek.uros2.desktop.util.Line;
import ml.derek.uros2.desktop.util.Measure;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

public class Detection
{
    public static int CORNER_THRESHOLD = 60;

    /**
     * Returns the lines from the image
     * @param mat the image to get the lines from
     * @return A matrix of the lines present in the image
     */
    public static Mat doorLines(Mat mat)
    {
        int edgeThresh = 1;
        int lowThreshold= 40;
        int max_lowThreshold = 100;
        int ratio = 5;
        int kernel_size = 3;

        Mat door = mat.clone();

        if(door == null)
        {
            System.out.println("Couldn't load mat");
            return null;
        }

        // Convert image to greyscale
        Imgproc.cvtColor(door, door, Imgproc.COLOR_BGR2GRAY);

        //Remove noise from the image
        Imgproc.blur(door, door, new Size(3, 3));

        // Detect the edges of the image
        Imgproc.Canny(door, door, lowThreshold, lowThreshold*ratio);

        // Extract the lines from the image
        Mat lines = new Mat();
        Imgproc.HoughLinesP(door, lines, 1, Math.PI / 180, 50, 3, 50);

        return lines;
    }

    public static Mat doorLines2(Mat mat)
    {
        Mat drawing = new Mat();

        // Smoothing
        Imgproc.GaussianBlur(mat, drawing, Measure.kSize, Measure.sigmaX,
                Measure.sigmaY);

        drawing = addBorder(drawing, 2);

        // Detecting edge
        Imgproc.Canny(drawing, drawing, Measure.cannyLowThres,
                Measure.cannyUpThres, Measure.apertureSize, false);

        // Detect lines
        Mat mLine = new Mat();
        Imgproc.HoughLinesP(drawing, mLine, Measure.houghRho, Measure.houghTheta,
                Measure.houghThreshold, Measure.houghMinLineSize,
                Measure.houghLineGap);

        return mat;

    }

    private static Mat addBorder(Mat img, int borderSize) {
        // add an inner white border in order to create a corner
        // when the real one is occluded

        Size roiSize = new Size(img.width() - borderSize * 2, img.height()
                - borderSize * 2);
        Rect roi = new Rect(new Point(borderSize, borderSize), roiSize);

        Mat mCrop = img.submat(roi);

        Mat mBorder = img.clone();

        Core.copyMakeBorder(mCrop, mBorder, borderSize, borderSize,
                borderSize, borderSize, Core.BORDER_ISOLATED, new Scalar(255, 255, 255));


        return mBorder;
    }

    public static MatOfInt convexHull(MatOfPoint contour)
    {
        MatOfInt hull = new MatOfInt();
        Imgproc.convexHull(contour, hull);
        return hull;
    }

    public static List<MatOfPoint> convexHulls(List<MatOfPoint> contours)
    {
        List<MatOfPoint> hulls = new ArrayList<>();

        for(MatOfPoint contour: contours)
        {
            MatOfInt indices = new MatOfInt();
            Imgproc.convexHull(contour, indices);

            MatOfPoint hull = new MatOfPoint();
            hull.create(indices.size(), CvType.CV_32SC2);
            for(int i = 0; i < indices.size().height ; i++)
            {
                int index = (int)indices.get(i, 0)[0];
                double[] point = new double[] {
                        contour.get(index, 0)[0], contour.get(index, 0)[1]
                };
                hull.put(i, 0, point);
            }

            hulls.add(hull);
        }

        return hulls;
    }


    public static List<MatOfPoint> doorContours(Mat image)
    {
        int edgeThresh = 1;
        int lowThreshold= 40;
        int max_lowThreshold = 100;
        int ratio = 5;
        int kernel_size = 3;

        Mat door = image.clone();
        if(door == null)
        {
            System.out.println("Couldn't load file");
            return null;
        }

        //Convert image to greyscale
        Imgproc.cvtColor(door, door, Imgproc.COLOR_BGR2GRAY);

        //Remove noise from the image
        Imgproc.blur(door, door, new Size(3, 3));

        // Detect the edges of the image
        Imgproc.Canny(door, door, lowThreshold, lowThreshold * ratio);

        // Find the contours
        ArrayList<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(door, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));


       return contours;
    }

    public static List<Rect> getBounds(List<MatOfPoint> contours)
    {
        List<Rect> rects = new ArrayList<>();

        for(MatOfPoint contour : contours)
        {
            //if(Imgproc.isContourConvex(contour))
                rects.add(Imgproc.boundingRect(contour));
        }

        return rects;
    }

    public static Rect largestRect(List<Rect> rects)
    {
        Rect largestRect = rects.get(0);
        for(Rect rect : rects)
        {
            if(rect.area() > largestRect.area())
                largestRect = rect;
        }

        return largestRect;
    }

    public static Mat getCorners(Mat src)
    {
        int lowThreshold= 40;
        int ratio = 5;
        Mat out = Mat.zeros(src.size(), CvType.CV_32FC1);
        Mat srcGrey = new Mat();

        Imgproc.cvtColor(src, srcGrey, Imgproc.COLOR_BGR2GRAY);
        Imgproc.blur(srcGrey, srcGrey, new Size(3.9f, 3.9f));
        Imgproc.Canny(srcGrey, srcGrey, lowThreshold, lowThreshold * ratio);


        Imgproc.cornerHarris(srcGrey, out, 2, 3, 0.04);
        //Imgproc.cornerEigenValsAndVecs(srcGrey, out, 2, 3, 0);

        Core.normalize(out, out, 0, 255, Core.NORM_MINMAX, CvType.CV_32FC1, new Mat());
        Core.convertScaleAbs(out, out);

        return out;
    }

    public static MatOfPoint getCorners2(Mat src)
    {

        int lowThreshold= 40;
        int ratio = 5;
        MatOfPoint out = new MatOfPoint();
        Mat srcGrey = new Mat();

        Imgproc.cvtColor(src, srcGrey, Imgproc.COLOR_BGR2GRAY);
        Imgproc.blur(srcGrey, srcGrey, new Size(3.9f, 3.9f));
        Imgproc.Canny(srcGrey, srcGrey, lowThreshold, lowThreshold * ratio);

        Imgproc.goodFeaturesToTrack(srcGrey, out, 100, 0.01, 30);

        return out;
    }

    public static Rect detectDoor(Mat door)
    {
        //Mat lines = Detection.doorLines(door);
        //Mat doorLines = Draw.lines(lines, new Mat(door.rows(), door.width(), door.type()));

        List<MatOfPoint> contours = Detection.doorContours(door);
        List<Rect> rects = Detection.getBounds(contours);
        Collections.sort(rects, new Comparator<Rect>()
        {
            @Override
            public int compare(Rect o1, Rect o2)
            {
                return o1.area() > o2.area() ? -1 : 1;
            }
        });


        if(rects.size() == 0)
            return null;
        else
        {
            for(Rect rect : rects)
            {
                if(Detection.withinAspectRatio(rect, 1.81, 3.29))
                    return rect;
            }

            return null;
        }
    }

    /**
     * Checks to see if the given rectangle is within the aspect ratio.
     * Ratio is checked using height / width
     */
    public static boolean withinAspectRatio(Rect rect, double lowerBound, double upperbound)
    {
        double rectRatio = rect.height / rect.width;
        return rectRatio < upperbound && rectRatio > lowerBound;
    }
}