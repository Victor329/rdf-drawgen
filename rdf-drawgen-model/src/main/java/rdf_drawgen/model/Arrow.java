package rdf_drawgen.model;

public class Arrow extends Element {

	private Element from;
	private Element to;

	public Arrow(String label, Element from, Element to) {
		super(new Point(), label);
		this.from = from;
		this.to = to;
	}

	public Element getFrom() {
		return from;
	}

	public Element getTo() {
		return to;
	}

	@Override
	public String toString() {
		return "Arrow " + hashCode() + " [" + getLabel() + "] from [" + from + "] to [" + to + "]";
	}
	
}
