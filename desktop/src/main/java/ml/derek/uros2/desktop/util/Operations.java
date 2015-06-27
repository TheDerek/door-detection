package ml.derek.uros2.desktop.util;

import ml.derek.uros2.desktop.Detection;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class Operations
{
    public static int POINT_MERGE_MAX_DISTANCE = 100;


    public static List<Point> mergePoints(Mat unmerged)
    {
        return mergePoints(Convert.pointList(unmerged));
    }

    public static void mergeAllPoints(List<Point> unmerged)
    {
        for(int x = 0; x < 1000; x++)
        {
            unmerged = mergePoints(unmerged);
        }
    }

    public static List<Point> mergePoints(List<Point> unmerged)
    {
        List<Point> merged = new ArrayList<>();

        for(Point p1 : unmerged)
        {
            Point closest = null;
            for (Point p2 : unmerged)
            {
                if (!p1.equals(p2))
                {
                   if(closest == null)
                       closest = p2;
                    else
                   {
                       if(p1.dot(p2) < p1.dot(closest))
                           closest = p2;
                   }
                }
            }

            if(p1.dot(closest) <  POINT_MERGE_MAX_DISTANCE)
            {
                merged.add(merge(p1, closest));
            }
            else
                merged.add(p1);
        }

        return merged;
    }

    public static Point merge(Point p1, Point p2)
    {
        return new Point((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
    }
}
