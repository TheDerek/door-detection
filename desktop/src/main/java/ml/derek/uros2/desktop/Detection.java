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

        //Convert image to greyscale
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
        Imgproc.findContours(door, contours, hierarchy, 3, 2, new Point(0, 0));

        // Draw contours
        Mat drawing = Mat.zeros(door.size(), CvType.CV_8UC3);
        Random rng = new Random();
        for( int i = 0; i < contours.size(); i++ )
        {
            Scalar color = new Scalar(rng.nextInt(255), rng.nextInt(255), rng.nextInt(255));
            Imgproc.drawContours(drawing, contours, i, color, 2, 8, hierarchy, 0, new Point());
        }

       return contours;
    }
}
