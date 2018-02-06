package rdf_drawgen.core;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.SimpleNamespace;

import rdf_drawgen.model.Arrow;
import rdf_drawgen.model.Circle;
import rdf_drawgen.model.Element;
import rdf_drawgen.model.Point;

class GenerateElements {

	private Model model;

	GenerateElements(Model model) {
		this.model = model;
	}
	
	private String createLabel(Value v) {
		if (v instanceof IRI)
			return createIriLabel((IRI) v);
		if (v instanceof BNode)
			return createBNodeLabel((BNode) v);
		if (v instanceof Literal)
			return createLiteralLabel((Literal) v);
		throw new RuntimeException("unhandled rdf value type " + v.getClass().getCanonicalName());
	}
	
	private String createLiteralLabel(Literal v) {
		// TODO v.getDatatype(); ??
		return "\"" + v.stringValue() + "\"";
	}

	private String createBNodeLabel(BNode bnode) {
		return bnode.stringValue();
	}

	private String createIriLabel(IRI iri) {
		return
		shortenIri(iri)
		.orElse("<" + iri.stringValue() + ">");
	}
	
	// TODO optimize for reuse
	private Optional<String> shortenIri(IRI iri) {
		String str = iri.stringValue();
		return
		Stream.concat(
			model.getNamespaces().stream(),
			// TODO rdf: prefix is NOT picked up when defined in .ttl source file. why?
			Stream.of(new SimpleNamespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#"))
		)
			.filter(n -> str.startsWith(n.getName()))
			.map(n -> n.getPrefix() + ":" + str.substring(n.getName().length()))
			.findFirst();
	}
	
	Stream<Element> run() {
		
		// gather all resources
		Set<Resource> resources = Stream.concat(
			model.subjects().stream(),
			model.objects().stream().filter(o -> o instanceof Resource).map(o -> (Resource) o)
		)
		.distinct()
		.collect(Collectors.toSet());
		
		// create element for each resource
		Map<Resource, Element> resourceElements = resources.stream().collect(Collectors.toMap(
			r -> r,
			r -> new Circle(new Point(), createLabel(r), 50)
		));
		
		return
		Stream.concat(

			resourceElements.values().stream(),

			// generate elements for relations to other resources
			// as well as literal values.
			resourceElements.entrySet().stream().flatMap(e ->
				generateElements(
					e.getKey(),
					e.getValue(),
					x -> Optional.ofNullable(resourceElements.get(x))
				)
			)
		);
	}
	
	private Stream<Element> generateElements(
		Resource subject,
		Element subjectElement,
		Function<Resource,
		Optional<Element>> getResourceElement
	) {
		return
		
		// generate elements for relations to other resources
		// as well as literal values.
		model.filter(subject, null, null).stream().flatMap(s -> {

			// determine/create element to draw the arrow *to*
			
			Value o = s.getObject();
			Element toElement;
			
			Stream<Element> addedToElement;
			
			// for resource objects, the object has already been processed and
			// had an element created for it - retrieve the element.
			if (o instanceof Resource) {
				toElement = getResourceElement.apply((Resource) o)
					.orElseThrow(() -> new RuntimeException(
						"no element found for resource " + o.stringValue()));
				addedToElement = Stream.empty();
			}
			
			// otherwise, the value is a literal, and we must
			// create an element here.
			else {
				toElement = new Circle(new Point(), createLabel(o), 50);
				addedToElement = Stream.of(toElement);
			}
			
			// create arrow
			Arrow arrow = new Arrow(
				createLabel(s.getPredicate()),
				subjectElement,
				toElement
			);
			
			return Stream.concat(
				addedToElement,
				Stream.of(arrow)
			);
		});
	}
	
}
