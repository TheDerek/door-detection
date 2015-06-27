package ml.derek.uros2.desktop;

import ml.derek.uros2.desktop.util.Convert;
import ml.derek.uros2.desktop.util.Line;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

public class Draw
{
    public static Mat contours(List<MatOfPoint> contours, Mat baseImage)
    {
        Random rng = new Random();
        //Mat drawing = baseImage.clone();
        Mat drawing = new Mat(baseImage.rows(), baseImage.cols(), baseImage.type());
        for( int i = 0; i < contours.size(); i++ )
        {
            Scalar color = new Scalar(rng.nextInt(255), rng.nextInt(255), rng.nextInt(255));
            Imgproc.drawContours(drawing, contours, i, color, 1, Imgproc.RETR_FLOODFILL, new Mat(), 0, new Point());
            //Imgproc.fillConvexPoly(drawing, contours.get(i), color);
        }

        return drawing;
    }

    public static Mat corners(Point[] points, Mat baseImage)
    {
        Mat drawing = baseImage.clone();

        for(Point point : points)
            Imgproc.circle(drawing, point, 5, new Scalar(0, 255, 0));

        return drawing;
    }

    public static Mat corners(Mat corners, Mat baseImage)
    {
        Mat drawing = baseImage.clone();
        for (int j = 0; j < corners.rows(); j++)
            for (int i = 0; i < corners.cols(); i++)
            {
                if(corners.get(j, i)[0] > Detection.CORNER_THRESHOLD)
                    Imgproc.circle(drawing, new Point(i, j), 5, new Scalar(0), 1, 8, 0);
            }

        return drawing;
    }

    public static Mat corners(List<Point> points, Mat baseImage)
    {
        Mat drawing = baseImage.clone();
        for(Point p1 : points)
        {
            Imgproc.circle(drawing, p1, 5, new Scalar(0), 1, 8, 0);
        }

        return drawing;
    }

    public static Mat lines(Mat lines, Mat baseImage)
    {
        // Copy the baseImage so we don't override it
        Mat drawing = baseImage.clone();


        Random rng = new Random();

        for(int i = 0; i < lines.height(); i++)
        {
            double[] vec = lines.get(i, 0);
            double  x1 = vec[0],
                    y1 = vec[1],
                    x2 = vec[2],
                    y2 = vec[3];

            Point start = new Point(x1, y1);
            Point end = new Point(x2, y2);
            Scalar color = new Scalar(rng.nextInt(255), rng.nextInt(255), rng.nextInt(255));
            Scalar red = new Scalar(255, 0, 0);

            Imgproc.line(drawing, start, end, color, 2);
        }

        // Return the new image with the lines
        return drawing;
    }

    public static Mat lines(List<Line> lines, Mat baseImage)
    {
        // Copy the baseImage so we don't override it
        Mat drawing = baseImage.clone();


        Random rng = new Random();

        for(Line line : lines)
        {
            Scalar color = new Scalar(rng.nextInt(255), rng.nextInt(255), rng.nextInt(255));
            Imgproc.line(drawing, line.p1, line.p2, color, 1);
        }

        // Return the new image with the lines
        return drawing;
    }

    public static Mat rect(Rect rect, Mat baseImage)
    {
        Mat drawing = baseImage.clone();
        Random rng = new Random();

        Scalar color = new Scalar(255, 0, 0);
        Imgproc.rectangle(drawing, rect.br(), rect.tl(), color);


        return drawing;
    }

    public static Mat rects(List<Rect> rects, Mat baseImage)
    {
        Mat drawing = baseImage.clone();
        Random rng = new Random();
        for(Rect rect : rects)
        {
            Scalar color = new Scalar(rng.nextInt(255), rng.nextInt(255), rng.nextInt(255));
            Imgproc.rectangle(drawing, rect.br(), rect.tl(), color);
        }

        return drawing;
    }

    public static Mat convexHulls(List<MatOfPoint> hulls, Mat baseImage)
    {
        Random rng = new Random();
        //Mat drawing = baseImage.clone();
        Mat drawing = new Mat(baseImage.rows(), baseImage.cols(), baseImage.type());
        for( int i = 0; i < hulls.size(); i++ )
        {
            Scalar color = new Scalar(rng.nextInt(255), rng.nextInt(255), rng.nextInt(255));
            Imgproc.drawContours(drawing, hulls, i, color, 1, Imgproc.RETR_FLOODFILL, new Mat(), 0, new Point());
        }

        return drawing;
    }


}
