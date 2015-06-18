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

        File file = new File("simple/close2.jpg");
        BufferedImage image = ImageIO.read(file);

        BufferedImage doorContours = Detection.doorContours(image);
        BufferedImage doorLines = Detection.doorLines(image);

        JFrame frame = Display.image(doorContours, doorLines, image);
    }

}
