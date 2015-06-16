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

public class Convert
{

    public static Mat mat(BufferedImage image)
    {
        byte[] data = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
        Mat mat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
        mat.put(0, 0, data);
        return mat;
    }

    public static Mat mat(File file)
    {
        try
        {
            BufferedImage img = ImageIO.read(file);
            return Convert.mat(img);
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public static BufferedImage bufferedImage(Mat mat)
    {
        byte[] pixels = new byte[(int) (mat.total() * mat.channels())];
        mat.get(0, 0, pixels);

        BufferedImage image = new BufferedImage(mat.width(), mat.height(), BufferedImage.TYPE_3BYTE_BGR);
        image.getRaster().setDataElements(0, 0, mat.width(), mat.height(), pixels);

        return image;
    }
}
