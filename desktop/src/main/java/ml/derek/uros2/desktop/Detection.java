package ml.derek.uros2.desktop;

import ml.derek.uros2.desktop.util.Convert;
import ml.derek.uros2.desktop.util.Line;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class Detection
{
    /**
     * Returns the lines from the image
     * @param mat the image to get the lines from
     * @return A matrix of the lines present in the image
     */
    public static Mat doorLines(Mat mat)
    {
        int edgeThresh = 1;
        int lowThreshold= 40;
        int max_lowThreshold = 100;
        int ratio = 5;
        int kernel_size = 3;

        Mat door = mat.clone();

        if(door == null)
        {
            System.out.println("Couldn't load mat");
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
        Imgproc.HoughLinesP(door, lines, 1, Math.PI / 180, 50, 3, 50);

        return lines;
    }

    public static List<Line> mergeLines(Mat unmerged)
    {
        System.out.println(unmerged.dump());
        ArrayList<Line> lineList = new ArrayList<>();
        ArrayList<Line> mergedLineList = new ArrayList<>();

        // Build up a list of lines in the image
        for(int i = 0; i < unmerged.height(); i++)
        {
            double[] vec = unmerged.get(i, 0);
            double  x1 = vec[0],
                    y1 = vec[1],
                    x2 = vec[2],
                    y2 = vec[3];

            Point start = new Point(x1, y1);
            Point end = new Point(x2, y2);
            lineList.add(new Line(start, end));
        }

        // Compare each line to every other line
        /*
        for(Line line1 : lineList)
        {
            for(Line line2 : lineList)
            {
                if(!line1.equals(line2))
                {
                    if(line1.angle() != 0 && line1.angle() != -90 &&
                            line2.angle() != 0 && line2.angle() != 0)
                        if(line1.similarTo(line2))
                        {
                            System.out.println(line2.angle() + ", " + line2.angle());
                            mergedLineList.add(line2.merge(line2));
                        }
                }
            }
        }*/

        return lineList;
    }
}
