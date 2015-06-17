package ml.derek.uros2.desktop;


import ml.derek.uros2.desktop.util.Convert;
import ml.derek.uros2.desktop.util.Display;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Test
{
    public static void main(String[] args)
    {
        System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
        detectDoor();
    }

    public static void detectDoor()
    {
        File[] doorFiles = new File("doors/").listFiles();
        Mat door;

        try
        {
            door = Convert.mat(doorFiles[new Random().nextInt(doorFiles.length)]);
        } catch (IOException | NullPointerException e)
        {
            e.printStackTrace();
            return;
        }

        if(door == null)
        {
            System.out.println("Couldn't load file");
            return;
        }

        Imgproc.cvtColor(door, door, Imgproc.COLOR_BGR2HLS);
        Imgproc.threshold(door, door, 100, 255, Imgproc.THRESH_BINARY);
        //Imgproc.blur(door, door, new Size(25.0, 1));
        Display.image(Convert.bufferedImage(door));
    }
}
