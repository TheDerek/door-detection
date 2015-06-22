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

    public static Mat lines(Mat lines, Mat baseImage)
    {
        // Copy the baseImage so we don't override it
        Mat drawing = baseImage.clone();


        Random rng = new Random();

        for(int i = 0; i < lines.height(); i++)
        {
            double[] vec = lines.get(i, 0);
            double x1 = vec[0],
                    y1 = vec[1],
                    x2 = vec[2],
                    y2 = vec[3];

            Point start = new Point(x1, y1);
            Point end = new Point(x2, y2);
            Scalar color = new Scalar(rng.nextInt(255), rng.nextInt(255), rng.nextInt(255));

            Imgproc.line(drawing, start, end, color, 3);
        }

        // Return the new image with the lines
        return drawing;
    }
}
