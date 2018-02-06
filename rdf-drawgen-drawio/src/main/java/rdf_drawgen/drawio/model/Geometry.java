package rdf_drawgen.drawio.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class Geometry {

	@XmlAttribute private Integer x;
	@XmlAttribute private Integer y;
	@XmlAttribute private Integer width;
	@XmlAttribute private Integer height;
	@XmlAttribute private String as= "geometry";
	
	private Integer relative = null;
	private Point point = null;
	
	public Geometry(Integer x, Integer y, Integer width, Integer height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	@XmlAttribute
	public Integer getRelative() {
		return relative;
	}

	public void setRelative(Integer relative) {
		this.relative = relative;
	}

	@XmlElement(name = "mxPoint")
	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

}
