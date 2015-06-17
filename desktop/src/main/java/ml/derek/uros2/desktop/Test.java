package ml.derek.uros2.desktop;


import ml.derek.uros2.desktop.util.Convert;
import ml.derek.uros2.desktop.util.Display;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Test
{
    public static void main(String[] args)
    {
        System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
        BufferedImage door = filterDoor();
        Display.image(door);
    }

    public static BufferedImage filterDoor()
    {
        File[] doorFiles = new File("doors/").listFiles();
        Mat door;

        try
        {
            door = Convert.mat(doorFiles[new Random().nextInt(doorFiles.length)]);
        } catch (IOException | NullPointerException e)
        {
            e.printStackTrace();
            return null;
        }

        if(door == null)
        {
            System.out.println("Couldn't load file");
            return null;
        }

        Imgproc.cvtColor(door, door, Imgproc.COLOR_BGR2HLS);
        Imgproc.threshold(door, door, 100, 255, Imgproc.THRESH_BINARY);
        //Imgproc.blur(door, door, new Size(25.0, 1));
        return Convert.bufferedImage(door);
    }
}
