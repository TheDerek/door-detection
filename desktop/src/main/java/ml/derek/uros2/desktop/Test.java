package ml.derek.uros2.desktop;


import ml.derek.uros2.desktop.util.*;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Test
{
    public static void main(String[] args) throws IOException
    {
        System.loadLibrary( Core.NATIVE_LIBRARY_NAME );

        File file = new File("shapes/shapes.jpg");
        //File file = new File("simple/door4.jpg");
        BufferedImage image = ImageIO.read(file);
        Mat door = Convert.mat(image);

        /*List<Line> lineList =  Detection.imageLines(door);
        List<Point> intersections = Detection.lineIntersections(lineList, door.size());
        List<Point> polys = Detection.getPolysFromIntersections(new MatOfPoint2f(intersections.toArray(new Point[intersections.size()])));

        Mat doorLines = Draw.lines(lineList, door);
        //doorLines = Draw.points(intersections, doorLines);

        List<MatOfPoint> polyList = new ArrayList<>();
        polyList.add(new MatOfPoint(polys.toArray(new Point[polys.size()])));
        Mat doorPolys = Draw.contours(polyList, door);

        List<MatOfPoint> polys2 = Detection.polygons(doorLines);
        //polys2 = Operations.trim(polys2, 4);
        Mat doorPolys2 = Draw.contours(polys2, new Mat(door.size(), door.type()));*/

        Mat shapes = ShapeDetect.detectShapes(door, MatType.Full, 4);

        JFrame frame = Display.image(shapes);
    }
}