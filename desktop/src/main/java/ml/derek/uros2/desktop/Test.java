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
import java.util.List;
import java.util.Random;

public class Test
{
    public static void main(String[] args) throws IOException
    {
        System.loadLibrary( Core.NATIVE_LIBRARY_NAME );

        File file = new File("simple/close1.jpg");
        BufferedImage image = ImageIO.read(file);
        Mat door = Convert.mat(image);

        Mat lines = Detection.doorLines(door);
        Mat doorLines = Draw.lines(lines, new Mat(door.rows(), door.width(), door.type()));

        List<MatOfPoint> contours = Detection.doorContours(doorLines);
        List<Rect> rects = Detection.getBounds(contours);

        Mat contourMat = Draw.contours(contours, door);
        Mat rectangle = Draw.rect(Detection.largestRect(rects), door);
        //rectangle = Draw.rects(rects, rectangle);

        BufferedImage doorLinesImage = Convert.bufferedImage(doorLines);
        BufferedImage contourImage = Convert.bufferedImage(contourMat);
        BufferedImage rectImage = Convert.bufferedImage(rectangle);

        JFrame frame = Display.image(doorLinesImage, contourImage, rectImage, image);
    }
}