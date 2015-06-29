package ml.derek.uros2.desktop.util;

import org.opencv.core.Point;

import java.awt.geom.Line2D;

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

    public Point intersects(Line line2)
    {
        double x1 = p1.x;       double y1 = p1.y;
        double x2 = p2.x;       double y2 = p2.y;
        double x3 = line2.p1.x; double y3 = line2.p1.y;
        double x4 = line2.p2.x; double y4 = line2.p2.y;

        Point pt = new Point();
        double d = ((x1-x2) * (y3-y4)) - ((y1-y2) * (x3-x4));
        pt.x = ((x1*y2 - y1*x2) * (x3-x4) - (x1-x2) * (x3*y4 - y3*x4)) / d;
        pt.y = ((x1*y2 - y1*x2) * (y3-y4) - (y1-y2) * (x3*y4 - y3*x4)) / d;
        return pt;
        //return Line2D.linesIntersect(this.p1.x, this.p1.y, this.p2.x, this.p2.y, line2.p1.x, line2.p1.y, line2.p2.x, line2.p2.y);
    }

    public double graident()
    {
        return (p1.y - p2.y) / (p1.x - p2.x);
    }

    public double m()
    {
        return graident();
    }
}
