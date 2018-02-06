package rdf_drawgen.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;

import rdf_drawgen.drawio.TransformToDrawIo;
import rdf_drawgen.drawio.model.GraphModel;
import rdf_drawgen.model.Element;

public class Main {

	public static void main(String... args) {
		
		// TODO sort subjects/objects before iterating over them?
		
		Model model = load("data.ttl");
		
		List<Element> elements = new GenerateElements(model).run()
			.collect(Collectors.toList());
		
		// do layout
		for (int i = 0; i < 1000; i ++) {
			DoLayout layout = new DoLayout(elements);
			elements = layout.cycle();
//			System.out.println("MOVED: " + layout.getTotalMovement());
		}

		
		// transform to draw.io model
		TransformToDrawIo t = new TransformToDrawIo();
		GraphModel g = t.transform(elements);
		marshal(g);
		
	}
	
	private static Model load(String resource) {
		Path file = Paths.get(System.getProperty("user.dir"), "src", "main", "resources")
			.resolve(resource);
//		InputStream stream = Main.class.getClassLoader().getResourceAsStream(resource);
		try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
			return Rio.parse(reader, "", RDFFormat.TURTLE);
		} catch (RDFParseException | UnsupportedRDFormatException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static void marshal(GraphModel model) {
		try {
			JAXBContext context = JAXBContext.newInstance(GraphModel.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(model, System.out);
		}
		catch (JAXBException e) {
			throw new RuntimeException("error while writing XML", e);
		}
	}
	
	@SuppressWarnings("unused")
	private static void marshal(GraphModel model, Path path) {
		try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			JAXBContext context = JAXBContext.newInstance(GraphModel.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(model, writer);
		}
		catch (IOException | JAXBException e) {
			throw new RuntimeException("error while writing XML", e);
		}
	}
	
}
