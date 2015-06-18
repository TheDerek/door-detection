package ml.derek.uros2.desktop;

import ml.derek.uros2.desktop.util.Convert;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Detection
{
    public static BufferedImage doorLines(BufferedImage image)
    {
        int edgeThresh = 1;
        int lowThreshold= 40;
        int max_lowThreshold = 100;
        int ratio = 5;
        int kernel_size = 3;

        Mat door;

        door = Convert.mat(image);

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
        Imgproc.Canny(door, door, lowThreshold, lowThreshold*ratio);

        // Extract the lines from the image
        Mat lines = new Mat();
        Imgproc.HoughLinesP(door, lines, 1, Math.PI / 180, 50, 3, 50);

        // Draw the lines onto the image
        for(int i = 0; i < lines.height(); i++)
        {
            System.out.println("Writing line");
            double[] vec = lines.get(i, 0);
            double x1 = vec[0],
                    y1 = vec[1],
                    x2 = vec[2],
                    y2 = vec[3];

            Point start = new Point(x1, y1);
            Point end = new Point(x2, y2);

            Imgproc.line(door, start, end, new Scalar(255, 0, 0), 3);
        }


        if(door.channels() == 1)
            return Convert.bufferedImage(door, BufferedImage.TYPE_BYTE_GRAY);
        else
            return Convert.bufferedImage(door, BufferedImage.TYPE_3BYTE_BGR);


    }

    public static BufferedImage doorContours(BufferedImage image)
    {
        int edgeThresh = 1;
        int lowThreshold= 40;
        int max_lowThreshold = 100;
        int ratio = 5;
        int kernel_size = 3;

        Mat door;

        door = Convert.mat(image);

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
        Imgproc.Canny(door, door, lowThreshold, lowThreshold*ratio);

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

        return Convert.bufferedImage(drawing, BufferedImage.TYPE_3BYTE_BGR);
    }
}
