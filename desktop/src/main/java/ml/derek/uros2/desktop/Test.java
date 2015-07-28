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
import java.util.Map;

public class Test
{
    public static void main(String[] args) throws IOException
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        //File file = new File("shapes/rects.jpg");
        File file = new File("simple/far2.jpg");
        BufferedImage image = ImageIO.read(file);
        Mat door = Convert.mat(image);

        // Best: (350, 115) (300, 90)
        double ratio = 1/3;
        double upper = 350;

        //MatOfPoint corners = Detection.getCorners2(door);
        List<Line> lines = Detection.imageLines(door);
        List<Point> corners = Detection.lineIntersections(lines, door.size());

        MatOfPoint pointsMat = new MatOfPoint(corners.toArray(new Point[corners.size()]));
        Mat bestLabels = new Mat();
        Core.kmeans(pointsMat, 4, bestLabels, new TermCriteria(TermCriteria.EPS, 10, 0.3), 5 ,0);
        //List<Line> topLines = Operations.trim(lines, corners.toList(), 10);

        Mat mat = Draw.lines(lines, door, new Scalar(255, 0, 0));
        mat = Draw.points(corners, mat);
        //Core.kmeans(new Mat())

        JFrame frame = Display.image(mat);
    }
}