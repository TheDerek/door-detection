package ml.derek.uros2.desktop;

import ml.derek.uros2.desktop.util.Convert;
import ml.derek.uros2.desktop.util.Line;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Detection
{
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

    public static List<Line> mergeLines(Mat unmerged)
    {
        System.out.println(unmerged.dump());
        ArrayList<Line> mergedLineList = new ArrayList<>();
        List<Line> lineList = Convert.list(unmerged);

        // Compare each line to every other line

        for(Line line1 : lineList)
        {
            for(Line line2 : lineList)
            {
                if(!line1.equals(line2))
                {
                    if(line1.angle() != 0 && line1.angle() != -90 &&
                            line2.angle() != 0 && line2.angle() != 0)
                        if(line1.similarTo(line2, 1))
                        {
                            System.out.println(line2.angle() + ", " + line2.angle());
                            mergedLineList.add(line2.merge(line1));
                        }
                }
            }
        }

        return mergedLineList;
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
}
