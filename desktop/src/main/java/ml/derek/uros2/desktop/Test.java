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

        File file = new File("shapes/rects.jpg");
        //File file = new File("simple/door4.jpg");
        BufferedImage image = ImageIO.read(file);
        Mat door = Convert.mat(image);

        // Best: (350, 115) (300, 90)
        double ratio = 1/3;
        double upper = 350;
        Mat shapes = ShapeDetect.detectShapes(300, 90, door, MatType.Full, 4);

        JFrame frame = Display.image(shapes);
    }
}