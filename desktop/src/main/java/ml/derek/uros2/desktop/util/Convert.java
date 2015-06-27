package ml.derek.uros2.desktop.util;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import ml.derek.uros2.desktop.Detection;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class Convert
{

    /**
     * Converts a BufferedImage to a a mat.
     * @param image the Buffered Image to Convert.
     * @return The resultant mat.
     */
    public static Mat mat(BufferedImage image)
    {
        byte[] data = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
        Mat mat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
        mat.put(0, 0, data);
        return mat;
    }

    /**
     * Converts a image via the way of a file to a Mat
     * @param file the file path of the image to convert.
     * @return The resultant mat
     */
    public static Mat mat(File file) throws IOException
    {
        BufferedImage img = ImageIO.read(file);
        return Convert.mat(img);
    }

    public static Mat mat(List<Line> lines)
    {
        Mat mat = Mat.zeros(lines.size(), 4, CvType.CV_16S);
        for(int i = 0; i < mat.height(); i++)
        {
            Line line = lines.get(i);
            /*double[] vec = mat.get(i, 0);
            double  x1 = vec[0],
                    y1 = vec[1],
                    x2 = vec[2],
                    y2 = vec[3];*/

            mat.put(i, 0, line.p1.x);
            mat.put(i, 1, line.p1.y);
            mat.put(i, 2, line.p2.x);
            mat.put(i, 3, line.p2.y);

            /*vec[0] = line.p1.x;
            vec[1] = line.p1.y;
            vec[2] = line.p2.x;
            vec[3] = line.p2.y;*/

        }
        System.out.println(mat.dump());
        return mat;
    }

    public static List<Line> list(Mat mat)
    {
        ArrayList<Line> lineList = new ArrayList<>();

        // Build up a list of lines in the image
        for(int i = 0; i < mat.height(); i++)
        {
            double[] vec = mat.get(i, 0);
            double  x1 = vec[0],
                    y1 = vec[1],
                    x2 = vec[2],
                    y2 = vec[3];

            Point start = new Point(x1, y1);
            Point end = new Point(x2, y2);
            lineList.add(new Line(start, end));
        }

        return lineList;
    }

    public static BufferedImage bufferedImage(Mat mat, int type)
    {
        byte[] pixels = new byte[(int) (mat.total() * mat.channels())];
        mat.get(0, 0, pixels);

        BufferedImage image = new BufferedImage(mat.width(), mat.height(), type);
        image.getRaster().setDataElements(0, 0, mat.width(), mat.height(), pixels);

        return image;
    }

    public static BufferedImage bufferedImage(Mat mat)
    {
        return bufferedImage(mat, BufferedImage.TYPE_3BYTE_BGR);
    }

    public static List<Point> pointList(Mat points)
    {
        List<Point> list = new ArrayList<>();

        for (int j = 0; j < points.rows(); j++)
            for (int i = 0; i < points.cols(); i++)
            {
                if(points.get(j, i)[0] > Detection.CORNER_THRESHOLD)
                {
                    list.add(new Point(j, i));
                }
            }

        return list;
    }
}
