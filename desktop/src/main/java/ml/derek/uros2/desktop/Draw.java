package ml.derek.uros2.desktop;

import ml.derek.uros2.desktop.util.Convert;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

public class Draw
{
    public static void contours(List<MatOfPoint> contours, Mat baseImage)
    {
        Random rng = new Random();
        for( int i = 0; i < contours.size(); i++ )
        {
            Scalar color = new Scalar(rng.nextInt(255), rng.nextInt(255), rng.nextInt(255));
            Imgproc.drawContours(baseImage, contours, i, color, 2, 8, new Mat(), 0, new Point());
        }
    }
}
