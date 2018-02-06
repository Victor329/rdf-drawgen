package rdf_drawgen.drawio.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class Cell {

	private int id;
	private Integer parent;
	private String style;
	private Integer edge;
	private Integer source;
	private Integer target;
	private String value;
	private Integer vertex;
	private Geometry geometry;
	private Integer connectable;
	
	public Cell() {
		this(0);
	}
	
	public Cell(int id) {
		this.id = id;
	}

	@XmlAttribute
	public int getId() {
		return id;
	}

	@XmlAttribute
	public Integer getParent() {
		return parent;
	}

	public void setParent(Integer parent) {
		this.parent = parent;
	}

	@XmlAttribute
	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	@XmlAttribute
	public Integer getEdge() {
		return edge;
	}

	public void setEdge(Integer edge) {
		this.edge = edge;
	}

	@XmlAttribute
	public Integer getSource() {
		return source;
	}

	public void setSource(Integer source) {
		this.source = source;
	}

	@XmlAttribute
	public Integer getTarget() {
		return target;
	}

	public void setTarget(Integer target) {
		this.target = target;
	}

	@XmlAttribute
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@XmlAttribute
	public Integer getVertex() {
		return vertex;
	}

	public void setVertex(Integer vertex) {
		this.vertex = vertex;
	}

	@XmlElement(name = "mxGeometry")
	public Geometry getGeometry() {
		return geometry;
	}

	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}

	@XmlAttribute
	public Integer getConnectable() {
		return connectable;
	}

	public void setConnectable(Integer connectable) {
		this.connectable = connectable;
	}

}
