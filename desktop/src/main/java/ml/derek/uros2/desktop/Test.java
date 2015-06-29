package ml.derek.uros2.desktop;


import ml.derek.uros2.desktop.util.Convert;
import ml.derek.uros2.desktop.util.Display;
import ml.derek.uros2.desktop.util.Line;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Test
{
    public static void main(String[] args) throws IOException
    {
        System.loadLibrary( Core.NATIVE_LIBRARY_NAME );

        File file = new File("simple/close1.jpg");
        BufferedImage image = ImageIO.read(file);
        Mat door = Convert.mat(image);
        //Mat door2 = door.clone();

        //Mat door = Imgcodecs.imread("simple/close1.jpg");

        //Imgproc.cvtColor(door2, door2, Imgproc.COLOR_GRAY2BGR);

        List<Line> lineList =  Detection.imageLines(door);
        List<Point> intersections = Detection.lineIntersections(lineList);
        //lineList = Detection.getDoor(lineList);

        Mat doorLines = Draw.lines(lineList, door);
        doorLines = Draw.corners(intersections, doorLines);
        //List<MatOfPoint> contours = Detection.doorContours(door);
        //List<Rect> rects = Detection.getBounds(contours);
        //List<MatOfPoint> hulls = Detection.convexHulls(contours);


        //Mat corners = Detection.getCorners(door);

        Point[] corners2 = Detection.getCorners2(door).toArray();
        //List<Line> lines = Operations.joinPoints(corners2);
        //List<Line> lines2 = Operations.compareLines(lines);
        //List<Point> pointList = Convert.pointList(corners);
        //Operations.mergeAllPoints(pointList);

        //Mat contourMat = Draw.contours(contours, new Mat(door.size(), door.type()));
        //Mat rectangle = Draw.rect(Detection.largestRect(rects), door);
        //Mat hullMat = Draw.convexHulls(hulls, door);
        Mat cornerMat = Draw.corners(corners2, door);
        //Mat lineMat = Draw.lines(lines, door);
        //Mat lineMat2 = Draw.lines(lines2, door);

        //BufferedImage doorLinesImage = Convert.bufferedImage(imageLines);
        //BufferedImage contourImage = Convert.bufferedImage(contourMat);
        // BufferedImage rectImage = Convert.bufferedImage(rectangle);
        BufferedImage cornerImage = Convert.bufferedImage(cornerMat);
        BufferedImage lineImage = Convert.bufferedImage(doorLines);
        //BufferedImage lineImage2 = Convert.bufferedImage(lineMat2);
        //Imgcodecs.imwrite("compiled.jpg", door2);
        //BufferedImage door2Image = ImageIO.read(new File("compiled.jpg"));


        JFrame frame = Display.image(lineImage);
    }
}