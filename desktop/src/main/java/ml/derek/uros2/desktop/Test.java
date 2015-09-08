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
    private static double thresh1 = 150;
    private static double thresh2 = 80;

    public static void main(String[] args) throws IOException
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        //File file = new File("shapes/rects.jpg");
        File file = new File("simple/far2.jpg");
        BufferedImage image = ImageIO.read(file);
        Mat newImage = Convert.mat(image);

        // Best: (350, 115) (300, 90)
        double ratio = 1/3;
        double upper = 350;

        /*MatOfPoint corners = Detection.getCorners2(door);
        List<Line> lines = Detection.imageLines(door);
        List<Point> corners = Detection.lineIntersections(lines, door.size());
        List<Point> clusters = new ArrayList<>(Cluster.cluster(corners, door.size(), 5, 8));
        System.out.println("Number of clusters: " + clusters.size());


        Mat mat = Draw.lines(lines, door, new Scalar(255, 0, 0));
        mat = Draw.points(corners, mat, new Scalar(0, 255, 0));
        mat = Draw.points(clusters, mat, new Scalar(255, 0, 0));*/

        MatOfPoint door = ShapeDetect.getDoor(thresh1, thresh2, newImage, newImage, MatType.Full);
        //newImage = Draw.contours(newImage, door);
        //Rect bounds = Imgproc.boundingRect(door);
        //newImage = Draw.rect(bounds, newImage);

        JFrame frame = Display.image(newImage);
    }
}