package rdf_drawgen.drawio;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.text.StringEscapeUtils;

import rdf_drawgen.drawio.model.Cell;
import rdf_drawgen.drawio.model.Geometry;
import rdf_drawgen.drawio.model.GraphModel;
import rdf_drawgen.model.Arrow;
import rdf_drawgen.model.Circle;
import rdf_drawgen.model.Element;
import rdf_drawgen.model.Point;

public class TransformToDrawIo {

	private Map<Element, Integer> elementIds = new LinkedHashMap<>();
	
	public GraphModel transform(List<Element> elements) {
		
		GraphModel model = new GraphModel();
		
		List<Cell> cells = new ArrayList<>();
		
		Cell c0 = new Cell(0); cells.add(c0);
		Cell c1 = new Cell(1); c1.setParent(0); cells.add(c1);
		nextId = 2;

		elements.stream().flatMap(e -> {
			List<Cell> c = createCell(e);
			if (e instanceof Circle) { // TODO eww
				if (c.size() != 1) throw new RuntimeException("for Circle elements, expected exactly 1 created cell, but got " + c.size());
				elementIds.put(e, c.get(0).getId());
			}
			return c.stream();
		})
		.forEach(cells::add);
		
		model.setCells(cells);

		return model;
	}

	// TODO
	private static final String CIRCLE_STYLE =
		"ellipse;whiteSpace=wrap;html=1;rounded=1;startSize=26;strokeColor=#6c8ebf;fillColor=#dae8fc;";
// WITH aspect=fixed:	"ellipse;whiteSpace=wrap;html=1;aspect=fixed;rounded=1;startSize=26;strokeColor=#6c8ebf;fillColor=#dae8fc;";

	// TODO
	private static final String ARROW_STYLE =
		"edgeStyle=none;rounded=1;html=1;labelBackgroundColor=none;startArrow=none;startFill=0;startSize=26;endArrow=open;endFill=0;jettySize=auto;orthogonalLoop=1;strokeColor=#000000;strokeWidth=1;fontSize=12;fontColor=#000000;";
	
	// TODO
	private static final String ARROW_LABEL_STYLE =
		"text;html=1;resizable=0;points=[];align=center;verticalAlign=middle;labelBackgroundColor=#ffffff;";
	
	private static int nextId;

	private Cell createBasicCell() {
		Cell cell = new Cell(nextId ++);
		cell.setParent(1);
		return cell;
	}
	
	private List<Cell> createCircleCell(Circle circle) {
		Cell cell = createBasicCell();
		cell.setValue(StringEscapeUtils.escapeHtml4(circle.getLabel()));
		cell.setVertex(1);
		cell.setStyle(CIRCLE_STYLE);
		Point p = circle.getPosition();
		int size = intValue(circle.getRadius() * 2);
		cell.setGeometry(new Geometry(intValue(p.getX()), intValue(p.getY()), size, size));
		return List.of(cell);
	}
	
	private Cell createArrowLabelCell(int parentId, String label) {
		Cell cell = createBasicCell();

		cell.setParent(parentId);
		cell.setValue(StringEscapeUtils.escapeHtml4(label));
		
		cell.setStyle(ARROW_LABEL_STYLE);
		cell.setVertex(1);
		cell.setConnectable(0);
		
		Geometry g = new Geometry(0, 0, null, null);
		g.setRelative(1);
		g.setPoint(new rdf_drawgen.drawio.model.Point());
		cell.setGeometry(g);
		
		return cell;
	}
	
	private List<Cell> createArrowCell(Arrow arrow) {
		Cell cell = createBasicCell();
		
		int fromId = elementIds.get(arrow.getFrom());
		int toId = elementIds.get(arrow.getTo());
		
		cell.setStyle(ARROW_STYLE);
		cell.setEdge(1);
		cell.setSource(fromId);
		cell.setTarget(toId);
		
		Geometry g = new Geometry(null, null, null, null);
		g.setRelative(1);
		cell.setGeometry(g);
		
		return List.of(
			cell,
			createArrowLabelCell(cell.getId(), arrow.getLabel())
		);
	}
	
	private int intValue(double value) {
		return (int) Math.round(value);
	}
	
	private List<Cell> createCell(Element element) {
		
		if (element instanceof Circle)
			return createCircleCell((Circle) element);
		
		else if (element instanceof Arrow)
			return createArrowCell((Arrow) element);
		
		else throw new RuntimeException("element type " + element.getClass().getCanonicalName() + " not handled");
	}
	
}
