package ml.derek.uros2.desktop.util;

import org.opencv.core.Point;

public class Line
{
    public Point p1;
    public Point p2;

    public Line(Point p1, Point p2)
    {
        this.p1 = p1;
        this.p2 = p2;
    }

    public double angle()
    {
        double xDiff = p2.x - p1.x;
        double yDiff = p2.y - p1.y;

        return Math.toDegrees(Math.atan2(yDiff, xDiff));
    }

    public boolean similarTo(Line line2, double withinAngle)
    {
        boolean p1x = Math.abs(this.p1.x - line2.p1.x) < withinAngle;
        boolean p1y = Math.abs(this.p1.y - line2.p1.y) < withinAngle;
        boolean p2x = Math.abs(this.p2.x - line2.p2.x) < withinAngle;
        boolean p2y = Math.abs(this.p2.y - line2.p2.y) < withinAngle;

        //return p1x && p1y && p2x && p2y;
        return Math.abs(this.angle() - line2.angle()) < withinAngle;
    }

    public boolean similarTo(Line line2)
    {
        return similarTo(line2, 5);
    }

    public Line merge(Line line2)
    {
        return new Line(
            new Point((this.p1.x + line2.p1.x) / 2, (this.p1.y + line2.p1.y) / 2),
            new Point((this.p2.x + line2.p2.x) / 2, (this.p2.y + line2.p2.y) / 2)
        );
    }

    public Point intersect(Line that)
    {
        return null;
    }
}
