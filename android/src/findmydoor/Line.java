package findmydoor;

import org.opencv.core.Point;

public class Line {
	public Point start, end;
	public double dir;
	public boolean isHorizontal;

	public Line(Point start, Point end) {
		this.start = start;
		this.end = end;

		dir = Measure.calcDirection(start, end);

		if (dir < Measure.dirThresL) {
			// short line
			isHorizontal = true;
			return;
		} else if (dir > Measure.dirThresH) {
			// long line
			isHorizontal = false;
			return;
		}

		throw new RuntimeException("Useless line.");
	}

	public Point getIntersection(Line other) {
		if (other.isHorizontal == this.isHorizontal)
			return null;

		Point p1a = this.start, p1b = this.end;
		Point p2a = other.start, p2b = other.end;
		int x1 = (int) p1a.x, y1 = (int) p1a.y, x2 = (int) p1b.x, y2 = (int) p1b.y;
		int x3 = (int) p2a.x, y3 = (int) p2a.y, x4 = (int) p2b.x, y4 = (int) p2b.y;

		// find intersection
		float d = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);

		if (d != 0) {
			int ix = (int) (((x1 * y2 - y1 * x2) * (x3 - x4) - (x1 - x2)
					* (x3 * y4 - y3 * x4)) / d);
			int iy = (int) (((x1 * y2 - y1 * x2) * (y3 - y4) - (y1 - y2)
					* (x3 * y4 - y3 * x4)) / d);

			if (ix > 0 && iy > 0 
					&& ix < Measure.dsSize.width
					&& iy < Measure.dsSize.height)
				return new Point(ix, iy);
		}
		return null;
	}
}
