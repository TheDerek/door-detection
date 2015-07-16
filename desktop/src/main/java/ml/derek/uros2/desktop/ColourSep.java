package ml.derek.uros2.desktop;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class ColourSep
{
    // TODO: Get two most common colours in image
    // Turn each pixel into one or the other colour depending on which one is closest
    // Apply shape detection algorithm
    // See what happens

    public static Mat seperateColours(Mat image, int numberOfColours)
    {
        // Split the image into it's different channels, so the size
        // of the list should be 3 with each mat successive mat being
        // one of R, G, B
        List<Mat> imgRGB = new ArrayList<>();
        Core.split(image, imgRGB);

        // Create a mat where each row is a set of RGB (?)
        int k = 5, n = image.rows() * image.cols();
        Mat img3xN = new Mat(n, 3, CvType.CV_8U);

        // Magical stuff happens here, fear not weary traveller
        for(int i = 0; i < 3; i++)
            imgRGB.get(i).reshape(1, n).copyTo(img3xN.col(i));

        img3xN.convertTo(img3xN, CvType.CV_32F);
        Mat bestLabels = new Mat();

        // Do the real business right here
        Core.kmeans(img3xN, k, bestLabels, new TermCriteria(), 10, Core.KMEANS_RANDOM_CENTERS);

        // Convert the result back
        bestLabels = bestLabels.reshape(0, image.rows());
        Core.convertScaleAbs(bestLabels, bestLabels, 255 / k, 0);
        return bestLabels;
    }
}
