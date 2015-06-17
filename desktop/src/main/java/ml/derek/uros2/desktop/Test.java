package ml.derek.uros2.desktop;


import ml.derek.uros2.desktop.util.Convert;
import ml.derek.uros2.desktop.util.Display;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Test
{
    public static void main(String[] args) throws IOException
    {
        System.loadLibrary( Core.NATIVE_LIBRARY_NAME );

        File[] doorFiles = new File("doors/positive").listFiles();
        File file = doorFiles[new Random().nextInt(doorFiles.length)];
        BufferedImage image = ImageIO.read(file);
        BufferedImage door = filterDoor(image);
        Display.image(door, image);
    }

    /**
     * Attempts to filter an image, leaving only the door remaining
     * @return The filtered image
     */
    public static BufferedImage filterDoor(BufferedImage image)
    {
        int edgeThresh = 1;
        int lowThreshold= 40;
        int max_lowThreshold = 100;
        int ratio = 5;
        int kernel_size = 3;

        Mat door;


        door = Convert.mat(image);

        if(door == null)
        {
            System.out.println("Couldn't load file");
            return null;
        }

        //Convert image to greyscale
        Imgproc.cvtColor(door, door, Imgproc.COLOR_BGR2GRAY);

        //Remove noise from the image
        Imgproc.blur(door, door, new Size(3, 3));

        // Detect the edges of the image
        Imgproc.Canny(door, door, lowThreshold, lowThreshold*ratio);

        // Extract the lines from the image
        Mat lines = new Mat();
        Imgproc.HoughLinesP(door, lines, 40, Math.PI / 180, 50, 50, 10);

        // Draw the lines onto the image
        for(int i = 0; i < lines.cols(); i++)
        {
            System.out.println("Writing line");
            double[] vec = lines.get(0, i);
            double x1 = vec[0],
                   y1 = vec[1],
                   x2 = vec[2],
                   y2 = vec[3];

            Point start = new Point(x1, y1);
            Point end = new Point(x2, y2);

            Imgproc.line(door, start, end, new Scalar(255, 0, 0), 3);
        }

        return Convert.bufferedImage(door, BufferedImage.TYPE_BYTE_GRAY);
    }
}
