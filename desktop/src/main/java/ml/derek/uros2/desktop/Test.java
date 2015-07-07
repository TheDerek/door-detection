package ml.derek.uros2.desktop;


import ml.derek.uros2.desktop.util.Convert;
import ml.derek.uros2.desktop.util.Display;
import ml.derek.uros2.desktop.util.Line;
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

        File file = new File("simple/close2.jpg");
        BufferedImage image = ImageIO.read(file);
        Mat door = Convert.mat(image);

        List<Line> lineList =  Detection.imageLines(door);
        List<Point> intersections = Detection.lineIntersections(lineList, door.size());
        List<Point> polys = Detection.getPolysFromIntersections(new MatOfPoint2f(intersections.toArray(new Point[intersections.size()])));

        Mat doorLines = Draw.lines(lineList, door);
        //doorLines = Draw.points(intersections, doorLines);

        List<MatOfPoint> polyList = new ArrayList<>();
        polyList.add(new MatOfPoint(polys.toArray(new Point[polys.size()])));
        Mat doorPolys = Draw.contours(polyList, door);

        List<MatOfPoint> polys2 = Detection.polygons(doorPolys);
        Mat doorPolys2 = Draw.contours(polys2, door);


        JFrame frame = Display.image(doorPolys2, doorPolys);
    }
}