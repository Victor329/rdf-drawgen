package rdf_drawgen.model;

public class Point {

	double x;
	double y;

	public Point() {
		this(0d, 0d);
	}
	
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	// vector operation
	public double length() {
		return Math.sqrt(x*x + y*y);
	}
	
	// vector operation
	public Point normalize() {
		return scale(1d / length());
	}
	
	public Point add(Point p) {
		return new Point(x + p.x, y + p.y);
	}
	
	public Point scale(double s) {
		return new Point(x * s, y * s);
	}
	
	public Point subtract(Point p) {
		return add(p.scale(-1d));
	}
	
	@Override
	public String toString() {
		return String.format("(%.2f, %.2f)", x, y);
	}
	
}
