package ml.derek.uros2.desktop.util;

import org.opencv.core.Point;

import java.util.*;

public class Line
{
    public final Point p1;
    public final Point p2;
    private final Map<Line, Point> intersections;
    private Iterator<Line> iterator;

    public Line(Point p1, Point p2)
    {
        this.p1 = p1;
        this.p2 = p2;
        intersections = new HashMap<>();
    }

    public double length()
    {
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }

    public boolean isVertical()
    {
        // Calculate the difference between the lines angle and the horizontal
        // and vertical angles (0 and 90 degrees respectively)
        double vDiff = Math.abs(Math.abs(angle()) - 90);
        double hDiff = Math.abs(angle());
        System.out.println(vDiff < hDiff);

        return vDiff < hDiff;
    }

    public double distance(Point p)
    {
        double x0 = p.x;
        double y0 = p.y;

        double x1 = p1.x;
        double y1 = p1.y;

        double x2 = p2.x;
        double y2 = p2.y;

        double numerator = Math.abs((y2 - y1)*x0 - (x2 - x1) * y0 + x2*y1 - y2*x1);
        double denominator = this.length();

        return numerator / denominator;
    }

    public void addIntersection(Line line, Point intersection)
    {
        this.intersections.put(line, intersection);
    }

    public void addIntersection(Line line)
    {
        this.addIntersection(line, this.intersects(line));
    }

    public Point getIntersection(Line line)
    {
        return intersections.get(line);
    }

    // Gets the next line intersection
    public Line next()
    {
        if(iterator == null)
            resetIterator();

        return iterator.hasNext() ? iterator.next() : null;
    }

    public void resetIterator()
    {
        iterator = intersections.keySet().iterator();
    }

    public Map<Line, Point> getIntersections()
    {
        return intersections;
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
