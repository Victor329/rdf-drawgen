package rdf_drawgen.drawio.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "mxGraphModel")
public class GraphModel {

	@XmlAttribute private int dx = 1042;
	@XmlAttribute private int dy = 480;
	@XmlAttribute private int grid = 1;
	@XmlAttribute private int gridSize = 10;
	@XmlAttribute private int guides = 1;
	@XmlAttribute private int tooltips = 1;
	@XmlAttribute private int connect = 1;
	@XmlAttribute private int arrows = 1;
	@XmlAttribute private int fold = 1;
	@XmlAttribute private int page = 1;
	@XmlAttribute private int pageScale = 1;
	@XmlAttribute private int pageWidth = 850;
	@XmlAttribute private int pageHeight = 1100;
	@XmlAttribute private String background = "#ffffff";
	@XmlAttribute private int math = 0;
	@XmlAttribute private int shadow = 0;
	
	private List<Cell> cells = new ArrayList<>();
	
	public void setCells(List<Cell> cells) {
		this.cells = new ArrayList<>(cells);
	}

	@XmlElementWrapper(name = "root")
	@XmlElement(name = "mxCell")
	public List<Cell> getCells() {
		return cells;
	}

}
