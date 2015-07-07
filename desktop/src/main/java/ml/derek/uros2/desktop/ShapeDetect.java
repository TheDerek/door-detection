package ml.derek.uros2.desktop;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

public class ShapeDetect
{
    /**
     * Find the cosine angle between the vectors p0->p1 and
     * p0->p2.
     * @param p0 The point which both vectors have in common.
     * @param p1 The second point of the first vector.
     * @param p2 The second point of the second vector.
     * @return The cosign of the angle between p0->p1 and p0->p2
     */
    public static double angle(Point p0, Point p1, Point p2)
    {
        double dx1 = p1.x - p0.x;
        double dy1 = p1.y - p0.y;
        double dx2 = p2.x - p2.y;
        double dy2 = p2.y - p0.y;
        return (dx1*dx2 + dy1*dy2)/Math.sqrt((dx1*dx1 + dy1*dy1)*(dx2*dx2 + dy2*dy2) + 1e-10);
    }

    /**
     * Find the sum of two points.
     * @param p1 The first point.
     * @param p2 The second point.
     * @return The resultant sum.
     */
    public static Point sum(Point p1, Point p2)
    {
        return new Point(p1.x + p2.x, p1.y + p2.y);
    }

    /**
     * Draws text onto a contour in an image.
     * @param image The image to draw the text onto.
     * @param label The text to use when drawing.
     * @param contour The contour to place the text in
     */
    public static void setLabel(Mat image, String label, MatOfPoint contour)
    {
        int fontFace = Core.FONT_HERSHEY_SIMPLEX;
        double scale = 0.4;
        int thickness = 1;
        int baseline = 0;

        // The size of the text we need to draw
        Size textSize = Imgproc.getTextSize(label, fontFace, scale, thickness, new int[]{baseline});

        // The bounds of the contour and thus the bounds we can draw the text in
        Rect bounds = Imgproc.boundingRect(contour);

        // The point in the image in which we draw the text
        Point point = new Point(
            bounds.x + ((bounds.width - textSize.width) / 2),
            bounds.y + ((bounds.height + textSize.height) / 2)
        );

        // Draw a filled white rectangle as the background for our text
        Imgproc.rectangle(
            image,
            sum(point, new Point(0, baseline)),
            sum(point, new Point(textSize.width, - textSize.height)),
            new Scalar(255, 255, 255),
            -1
        );

        // Draw the text itself
        Imgproc.putText(image, label, point, fontFace, scale, new Scalar(0, 0, 0), thickness);
    }

    public static Mat detectShapes(Mat image)
    {
        throw new NotImplementedException();
    }
}
