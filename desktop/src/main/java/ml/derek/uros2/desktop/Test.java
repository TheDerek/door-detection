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

        File file = new File("simple/far2.jpg");
        BufferedImage image = ImageIO.read(file);
        Mat door = Convert.mat(image);

        Mat lines = Detection.doorLines(door);
        List<MatOfPoint> contours = Detection.doorContours(door);

        Mat doorLines = Draw.lines(lines, door);
        Mat contourMat = Draw.contours(contours, door);

        BufferedImage doorLinesImage = Convert.bufferedImage(doorLines);
        BufferedImage contourImage = Convert.bufferedImage(contourMat);

        JFrame frame = Display.image(doorLinesImage, contourImage, image);
    }
}