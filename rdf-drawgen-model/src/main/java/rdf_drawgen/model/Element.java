package rdf_drawgen.model;

public class Element {

	private Point position;
	private String label;

	public Element(Point position, String label) {
		this.position = position;
		this.label = label;
	}

	public Point getPosition() {
		return position;
	}

	public String getLabel() {
		return label;
	}
	
}
