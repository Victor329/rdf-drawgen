package rdf_drawgen.model;

public class Circle extends Element {

	private double radius;

	public Circle(Point position, String label, double radius) {
		super(position, label);
		this.radius = radius;
	}
	
	public Circle move(Point by) {
		return new Circle(
			getPosition().add(by),
			getLabel(),
			radius
		);
	}

	public double getRadius() {
		return radius;
	}

	@Override
	public String toString() {
		return "Circle " + hashCode() + " [" + getLabel() + "]";
	}
	
}
