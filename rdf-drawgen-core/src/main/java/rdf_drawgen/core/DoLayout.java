package rdf_drawgen.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.mutable.MutableDouble;

import rdf_drawgen.model.Arrow;
import rdf_drawgen.model.Circle;
import rdf_drawgen.model.Element;
import rdf_drawgen.model.Point;;

// TODO pretty inefficient. probably build a new model of Node and Edge instances to do all this.

public class DoLayout {

	private List<Element> elements;
	private Double totalMovement;

	public DoLayout(List<Element> elements) {
		this.elements = elements;
	}
	
	public Double getTotalMovement() {
		return totalMovement;
	}

	// do 1 iteration of layout algorithm, returning a new 'situation'
	// (clone/copy of input; so nothing is modified in input model)
	public List<Element> cycle() {

		Map<Circle, Point> forces =
			elements.stream()
				.filter(e -> e instanceof Circle)
				.collect(Collectors.toMap(
					e -> (Circle) e,
					this::determineCombinedForce
				));
		
		// create new circles by moving existing ones
		
		MutableDouble totalMovement = new MutableDouble(0d);
		
		Map<Circle, Circle> oldToNew = new LinkedHashMap<>();
		
		List<Element> nextState = new ArrayList<>();
		forces.forEach((c, f) -> {
			Circle newCircle = c.move(f);
			oldToNew.put(c, newCircle);
			nextState.add(newCircle);
			totalMovement.add(f.length());
		});
		
		// copy all non-circles
		elements.stream()
			.filter(e -> !(e instanceof Circle))
			.map(e -> copy(e, oldToNew))
			.forEach(nextState::add);
		
		this.totalMovement = totalMovement.getValue(); // TODO rly belongs in return value
		
		return nextState;
	}
	
	private Element copy(Element element, Map<Circle, Circle> oldToNew) {
		if (element instanceof Arrow) {
			Arrow a = (Arrow) element;
			return new Arrow(a.getLabel(), oldToNew.get(a.getFrom()), oldToNew.get(a.getTo()));
		}
		else throw new RuntimeException("unexpected element type " + element.getClass().getCanonicalName());
	}
	
	private Point determineCombinedForce(Element element) {
		return
		Stream.concat(
			determineRepulsionForces(element),
			determineConnectionsForces(element)
		)
		.reduce(Point::add)
		.orElse(new Point(0, 0));
	}

	private static final double
	
		REPULSE_MAX_FORCE = 20,
		REPULSE_RADIUS = 250;
	
	
	private Point smallRandomVector() {
		return new Point(Math.random() * 0.0001d, Math.random() * 0.0001d);
	}
	
	// determine the forces other elements exert on this element.
	// each other element behaves as if it has a repulsion field around it.
	private Stream<Point> determineRepulsionForces(Element element) {
		return
		elements.stream()
			.filter(e -> e instanceof Circle)
			.filter(e -> !e.equals(element))
			.map(e -> {
				Point v = e.getPosition().subtract(element.getPosition());
				if (v.length() == 0d)
					v = smallRandomVector();
				double d = v.length();
				double f = Math.max(1d - d / REPULSE_RADIUS, 0d) * REPULSE_MAX_FORCE;
				return v.normalize().scale(f * -1d);
			});
	}
	
	private static final double
	
		NATURAL_CONNECTION_LENGTH = 350,
		MAX_CONNECTION_SPRING_FORCE = 10;
	
	// determine the forces the connections of this element
	// exert on it. each connection behaves like a spring trying to
	// revert to its "natural length".
	private Stream<Point> determineConnectionsForces(Element element) {
		Point p = element.getPosition();
		return
		getConnectedElements(element).map(e -> {
		
			// get vector from 'element' to 'e'
			Point v = e.getPosition().subtract(p);
			if (v.length() == 0d)
				v = smallRandomVector();
			
			double diff = Math.abs(v.length() - NATURAL_CONNECTION_LENGTH);
			double factor = diff / NATURAL_CONNECTION_LENGTH;
			double f = factor * MAX_CONNECTION_SPRING_FORCE;
			
			Point force = v.normalize().scale(f);
			// TODO technically we should use half of 'f', but it doesn't matter for this purpose?
			
			// inverse direction if needed:
			// if connection is shorter than its natural length,
			// its spring effect will push the circle in the direction
			// opposite of the other circle.
			if (v.length() < NATURAL_CONNECTION_LENGTH)
				force = force.scale(-1d);
			
			return force;
		});
	}
	
	private Stream<Element> getConnectedElements(Element element) {
		return
		elements.stream()
			.filter(e -> e instanceof Arrow)
			.map(e -> (Arrow) e)
			.filter(a -> a.getFrom().equals(element) || a.getTo().equals(element))
			.map(a ->
				a.getFrom().equals(element)
					? a.getTo()
					: a.getFrom()
			);
	}
	
}
