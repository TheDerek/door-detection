package ml.derek.uros2.desktop;


import ml.derek.uros2.desktop.util.Convert;
import ml.derek.uros2.desktop.util.Display;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.io.File;

public class Test
{
    public static void main(String[] args)
    {
        System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
        detectDoor();
    }

    public static void detectDoor()
    {
        Mat door = Convert.mat(new File("doors/door.jpg"));

        if(door == null)
        {
            System.out.println("Couldn't load file");
            return;
        }

        Imgproc.blur(door, door, new Size(25.0, 1));
        Display.image(Convert.bufferedImage(door));
    }
}
