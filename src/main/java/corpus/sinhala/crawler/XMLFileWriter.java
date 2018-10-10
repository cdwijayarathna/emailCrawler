package corpus.sinhala.crawler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.util.StAXUtils;

import javanet.staxutils.IndentingXMLStreamWriter;

public class XMLFileWriter {
	private String filePath;
	private String path;

	private OMFactory factory;
	private OMElement root;
	private QName rootName;

	ArrayList<OMElement> docs;
	
	Constructor<?> cons;	

	public XMLFileWriter(String location) throws IOException {
		init();
		//createFolder();
		factory = OMAbstractFactory.getOMFactory();
		root = factory.createOMElement(rootName);
	}

	private void init() {
		rootName = new QName("root");

		docs = new ArrayList<>();
		
	}

	

	public void addDocument(String name, String email, String url) throws IOException,
			XMLStreamException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		

		OMElement doc = factory.createOMElement(new QName("user"));
		
		OMElement profile = factory.createOMElement(new QName("profile"));
		OMElement nameEl = factory.createOMElement(new QName("name"));
		OMElement emailEl = factory.createOMElement(new QName("email"));
		profile.setText(url);
		nameEl.setText(name);
		emailEl.setText(email);
		doc.addChild(profile);
		doc.addChild(nameEl);
		doc.addChild(emailEl);
		docs.add(doc);
	}

	
	public void writeToFile(String fileName) throws IOException, XMLStreamException {
		path = fileName;
		//System.out.println("---------------------------" + path);
		OutputStream out = new FileOutputStream(path);
		XMLStreamWriter writer = StAXUtils.createXMLStreamWriter(out);
		writer = new IndentingXMLStreamWriter(writer);
		root.serialize(writer);
		writer.flush();
	}

	public void update(int k) {
		
		for (int i = 0; i < docs.size(); i++) {

			root.addChild(docs.get(i));
		}

		try {
			writeToFile("C:\\DDDDDDDDDDDDDDDDDDDDDDDDDD\\myphdstuff\\paper1\\crawler\\" + k + "-3.xml");
			System.out.println("Writing to " + k + " complete");

		} catch (IOException | XMLStreamException e) {
			e.printStackTrace();
		}
		root = factory.createOMElement(rootName);
		
		docs.clear();

	}

	 public static void main(String[] args) throws IOException {
//	 XMLFileWriter a = new XMLFileWriter("/home/maduranga/bb/");
	 }
}
