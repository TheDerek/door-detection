package ml.derek.uros2.desktop.util;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
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
}
