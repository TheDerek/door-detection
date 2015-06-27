package findmydoor;

import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.List;

public class Door implements Comparable<Door> {
	protected static RuntimeException noDoorException = new RuntimeException(
			"This is not a door");

	private Point p1, p2, p3, p4;
	private Line l1, l2, l3, l4;
	private double avgFillRatio;
	private double geomScore;// score based on geometry

	public Door(Point p1, Point p2, Point p3, Point p4) {
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		this.p4 = p4;

		if (!checkGeometry()) {
			// commute long side points
			this.p1 = p1;
			this.p2 = p3;
			this.p3 = p2;
			this.p4 = p4;

			if (!checkGeometry()) {
				// commute short side points
				this.p1 = p1;
				this.p2 = p2;
				this.p3 = p4;
				this.p4 = p3;

				if (!checkGeometry())
					throw noDoorException;

			}
		}
	}

	public Door(ArrayList<Line> lineArray) {
		if (lineArray.size() != 4)
			throw noDoorException;

		List<Line> horLines = new ArrayList<Line>();
		List<Line> verLines = new ArrayList<Line>();

		for (Line l : lineArray) {
			if (l.isHorizontal)
				horLines.add(l);
			else
				verLines.add(l);
		}

		if (horLines.size() > 2 || verLines.size() > 2)
			throw noDoorException;

		l1 = horLines.get(0);
		l2 = verLines.get(0);
		l3 = horLines.get(1);
		l4 = verLines.get(1);

		p1 = l1.getIntersection(l2);
		p2 = l2.getIntersection(l3);
		p3 = l3.getIntersection(l4);
		p4 = l1.getIntersection(l4);

		if (p1 == null || p2 == null || p3 == null || p4 == null)
			throw noDoorException;

		if (Math.abs(l2.dir - l4.dir) > Measure.parallelThres)
			throw noDoorException;

		if (!checkGeometry()) {
			throw noDoorException;
		}
	}

	public Point getP1() {
		return p1;
	}

	public Point getP2() {
		return p2;
	}

	public Point getP3() {
		return p3;
	}

	public Point getP4() {
		return p4;
	}

	public void setAvgFillRatio(double avgFillRatio) {
		this.avgFillRatio = avgFillRatio;
	}

	@Override
	public int compareTo(Door another) {
		// weight to compute the total score
		int fillW = 200, geomW = 100;
		// reduce fillW of the param thickness of the activity
		// TODO: connect with activity
		fillW /= 2;

		int score = (int) (fillW * (this.avgFillRatio - another.avgFillRatio) + geomW
				* (this.geomScore - another.geomScore));
		return score;
	}

	/*
	 * Check if the rectangle p1-p2-p3-p4 is a door. The four point are in
	 * order.
	 */
	private boolean checkGeometry() {
		double siz12 = Measure.calcRelDistance(p1, p2);
		double siz41 = Measure.calcRelDistance(p1, p4);

		if (siz12 < siz41) {
			// commute the sides
			// return checkGeometry(p2, p3, p4, p1);
			Point temp = p1;
			this.p1 = p2;
			this.p2 = p3;
			this.p3 = p4;
			this.p4 = temp;

			siz41 = siz12;
			siz12 = Measure.calcRelDistance(p1, p2);
		}

		double dir12 = Measure.calcDirection(p1, p2);
		double dir41 = Measure.calcDirection(p1, p4);
		double dir23 = Measure.calcDirection(p2, p3);
		double dir34 = Measure.calcDirection(p3, p4);

		double cDir23 = dir23 - Measure.dirThresL;
		double cDir34 = dir34 - Measure.dirThresH;
		double cParal = Math.abs(dir12 - dir34) - Measure.parallelThres;

		double cDir12 = dir12 - Measure.dirThresH;
		double cDir41 = dir41 - Measure.dirThresL;
		if (cDir12 < 0 || cDir41 > 0 || cDir23 > 0 || cDir34 < 0 || cParal > 0) {
			return false;
		}

		double cSize12d = siz12 - Measure.heightThresL;
		double cSize12u = siz12 - Measure.heightThresH;
		double cSize41d = siz41 - Measure.widthThresL;
		double cSize41u = siz41 - Measure.widthThresH;
		if (cSize12d < 0 || cSize12u > 0 || cSize41d < 0 || cSize41u > 0) {
			return false;
		}

		double siz23 = Measure.calcRelDistance(p2, p3);
		double siz34 = Measure.calcRelDistance(p3, p4);

		double cSiz34d = siz34 - Measure.heightThresL;
		double cSiz34u = siz34 - Measure.heightThresH;
		double cSiz23d = siz23 - Measure.widthThresL;
		double cSiz23u = siz23 - Measure.widthThresH;

		if (cSiz34d < 0 || cSiz34u > 0 || cSiz23d < 0 || cSiz23u > 0) {
			return false;
		}

		double sizRatio = (siz12 + siz34) / (siz23 + siz41);
		double cSRatioDown = sizRatio - Measure.HWThresL;
		double cSRatioUp = sizRatio - Measure.HWThresH;

		if (cSRatioDown < 0 || cSRatioUp > 0) {
			return false;
		}

		// weight to compute geomRate
		double sizeW = 1, dirW = 2;

		// normalize on elements number
		sizeW /= 6;
		dirW /= 5;

		// normalize on range
		sizeW /= 1; // (0, 1]
		dirW /= 90; // [0, 90]

		geomScore = (cSize12d - cSize12u + cSize41d - cSize41u + cSiz34d
				- cSiz34u + cSiz23d - cSiz23u + cSRatioDown - cSRatioUp)
				* sizeW + (cDir12 - cDir41 - cDir23 + cDir34 - cParal) * dirW;

		// if here, 1234 is a door
		return true;
	}

}
