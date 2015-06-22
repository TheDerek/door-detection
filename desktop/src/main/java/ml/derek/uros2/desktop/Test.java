package ml.derek.uros2.desktop;


import ml.derek.uros2.desktop.util.Convert;
import ml.derek.uros2.desktop.util.Display;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Test
{
    public static void main(String[] args) throws IOException
    {
        System.loadLibrary( Core.NATIVE_LIBRARY_NAME );

        File file = new File("simple/far1.jpg");
        BufferedImage image = ImageIO.read(file);
        Mat door = Convert.mat(image);

        Mat lines = Detection.doorLines(door);
        Mat doorLines = Draw.lines(lines, door);

        BufferedImage doorLinesImage = Convert.bufferedImage(doorLines);

        JFrame frame = Display.image(doorLinesImage, image);
    }

}
