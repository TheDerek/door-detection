package ml.derek.uros2.desktop;

import ml.derek.uros2.desktop.util.Convert;
import ml.derek.uros2.desktop.util.Line;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Detection
{
    public static int CORNER_THRESHOLD = 60;

    /**
     * Returns the lines from the image
     * @param mat the image to get the lines from
     * @return A matrix of the lines present in the image
     */
    public static List<Line> imageLines(Mat mat)
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
        Imgproc.Canny(door, door, lowThreshold, lowThreshold * ratio);

        // Extract the lines from the image
        Mat lines = new Mat();
        Imgproc.HoughLinesP(door, lines, 1, Math.PI / 180, 100, 100, 200);

        List<Line> list = Convert.list(lines);
        List<Line> finalList = new ArrayList<>();
        double range = 30;
        for(Line line : list)
        {
            if(Math.abs(line.angle() - 90) < range)
                finalList.add(line);
            else if(Math.abs(line.angle()) < range)
                finalList.add(line);
            else if(Math.abs(line.angle() - 180) < range)
                finalList.add(line);
        }

        return list;
    }

    public static List<Point> lineIntersections(List<Line> lines, Size bounds)
    {
        Rect boundRect = new Rect(0, 0, (int) bounds.width, (int) bounds.height);
        List<Point> points = new ArrayList<>();
        for(Line l1 : lines)
            for(Line l2 : lines)
            {
                if(!l1.equals(l2))
                {
                    Point intersection = l1.intersects(l2);
                    if(intersection.inside(boundRect))
                    {
                        points.add(intersection);

                        if (l1.getIntersection(l2) == null)
                            l1.addIntersection(l2);

                        if (l2.getIntersection(l1) == null)
                            l2.addIntersection(l1);
                    }
                }
            }

        System.out.println("Number of intersections: " + points.size());
        return points;
    }


    public static List<Point> getPolysFromIntersections(MatOfPoint2f intersections)
    {
        MatOfPoint2f polys = new MatOfPoint2f();
        double epsilon = Imgproc.arcLength(intersections, true) * 0.0002;
        Imgproc.approxPolyDP(intersections, polys, 3, true);

        return polys.toList();
    }

    public static List<MatOfPoint> polygons(Mat image)
    {
        Mat door = image.clone();

        // Convert image to greyscale
        Imgproc.cvtColor(door, door, Imgproc.COLOR_BGR2GRAY);

        //Remove noise from the image
        //Imgproc.blur(door, door, new Size(3, 3));

        // Detect the edges of the image
        Imgproc.Canny(door, door, 100, 255);

        // Find the contours in the image
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(door, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));

        // Find the polys
        List<MatOfPoint> polys = new ArrayList<>();
        for(MatOfPoint contour : contours)
        {
            // Convert to MatOfPoint2f
            MatOfPoint2f contourf = new MatOfPoint2f(contour.toArray());

            // Get our Poly
            Imgproc.approxPolyDP(contourf, contourf, 0.001, true);

            // Convert back
            MatOfPoint poly2 = new MatOfPoint(contourf.toArray());
            polys.add(poly2);
        }

        return polys;
    }


    /*public static boolean isRectangle(Point p1, Point p2, Point p3, Point p4)
    {
        double buffer = 10;
        double cx, cy;
        double dd1, dd2, dd3, dd4;

        cx = (p1.y + p2.y + p3.y + p4.y) / 4;
        cy = (p1.y + p2.y + p3.y + p4.y) / 4;

        dd1 = Math.pow(cx - p1.x, 2) + Math.pow(cy - p1.y, 2);
        dd2 = Math.pow(cx - p2.x, 2) + Math.pow(cy - p2.y, 2);
        dd3 = Math.pow(cx - p3.x, 2) + Math.pow(cy - p3.y, 2);
        dd4 = Math.pow(cx - p4.x, 2) + Math.pow(cy - p4.y, 2);

        return  Math.abs(dd1 - dd2) <= buffer &&
                Math.abs(dd1 - dd3) <= buffer &&
                Math.abs(dd1 - dd4) <= buffer;
    }*/

    public static boolean hasLoop(Line startNode)
    {
        Line slowNode, fastNode1, fastNode2;
        slowNode = fastNode1 = fastNode2 = startNode;

        while   ((slowNode != null) &&
                ((fastNode1 = fastNode2.next()) != null) &&
                ((fastNode2 = fastNode1.next()) != null))
        {
            if (slowNode == fastNode1 || slowNode == fastNode2)
                return true;

            slowNode = slowNode.next();
        }
        return false;
    }




    public static Mat doorLines2(Mat mat)
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
        Imgproc.Canny(door, door, lowThreshold, lowThreshold * ratio);

        // Extract the lines from the image
        Mat lines = new Mat();
        Imgproc.HoughLinesP(door, lines, 1, Math.PI / 180, 70, 30, 10);

        return lines;

    }

    public static List<Line> bestFitDoor(List<Point> points)
    {
        for(Point p : points)
        {

        }

        return null;
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

    public static Mat colourBlobs(Mat image, Mat background)
    {
        return null;
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
        Imgproc.Canny(srcGrey, srcGrey, 90, 100);

        Imgproc.goodFeaturesToTrack(srcGrey, out, 100, 0.01, 30);

        return out;
    }



    public static Rect detectDoor(Mat door)
    {
        //Mat lines = Detection.imageLines(door);
        //Mat imageLines = Draw.lines(lines, new Mat(door.rows(), door.width(), door.type()));

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