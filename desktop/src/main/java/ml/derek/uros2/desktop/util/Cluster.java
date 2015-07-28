package ml.derek.uros2.desktop.util;

import org.opencv.core.Point;
import org.opencv.core.Size;

import java.util.*;

public class Cluster
{
    public static final Random rad = new Random();

    public static Set<Point> cluster(List<Point> points, Size bounds, int clusters, int iterations)
    {
        assert clusters > 0;

        // Create the centroids
        Map<Point, List<Point>> centroids = new HashMap<>();
        for(int i = 0; i < clusters; i++)
            centroids.put(randomPoint(bounds), new ArrayList<Point>());

        for(int i = 0; i < iterations; i++)
        {
            // Fill the centroids with their closest points
            Point[] contenders = centroids.keySet().toArray(new Point[centroids.size()]);
            for(Point point : points)
            {
                Point closest = getClosest(point, contenders);
                if(centroids.containsKey(closest))
                    centroids.get(closest).add(point);
            }

            // Update the centroids with the average location of all their points
            for(Map.Entry<Point, List<Point>> entries : centroids.entrySet())
            {
                Point average = averagePoint(entries.getValue());
                entries.getKey().x = average.x;
                entries.getKey().y = average.y;
            }
        }

        return centroids.keySet();
    }

    public static Point randomPoint(Size bounds)
    {
        return new Point(rad.nextInt((int) bounds.width), rad.nextInt((int) bounds.height));
    }

    public static Point averagePoint(List<Point> points)
    {
        Point a = new Point(0, 0);
        for(Point point : points)
        {
            a.x += point.x;
            a.y += point.y;
        }

        a.x /= (double) points.size();
        a.y /= (double) points.size();

        return a;
    }

    public static Point getClosest(Point point, Point[] contenders)
    {
        double shortestDst = Double.MAX_VALUE;
        Point shortest = contenders[0];

        for(Point contender : contenders)
        {
            double dst = distance(contender, point);
            if(dst < shortestDst)
            {
                shortestDst = dst;
                shortest = contender;
            }
        }

        return shortest;
    }

    public static double distance(Point p1, Point p2)
    {
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) +  Math.pow(p1.y - p2.y, 2));
    }
}
