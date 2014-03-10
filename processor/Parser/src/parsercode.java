/**
 * 
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang3.StringEscapeUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class parsercode extends DefaultHandler {
	/* */
	private StringBuilder temp;
	int id = 0;
	public String timestamp = "";
	public String title = "";
	public String text = "";
	public String username = "";
	public String ip = "";
	public String author = "";
	public int flag = 0;
	public int count = 0;
	public int needed = 4000;
	FileWriter fw;
	static BufferedWriter bw;
	File file;

	// public static Collection<WikipediaDocument> wikidocs;

	/**
	 * 
	 * @param idxConfig
	 * @param parser
	 */
	public parsercode(FileWriter f, BufferedWriter b, File fl) {
		fw = f;
		bw = b;
		file = fl;
	}

	public void characters(char[] buffer, int start, int length) {
		temp.append(new String(buffer, start, length));
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		temp = new StringBuilder();
		if (qName.equals("page")) {

		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {

		if (qName.equals("title"))
			title = temp.toString();

		if (qName.equals("id") && flag == 0) {
			id = Integer.parseInt(temp.toString());
			flag = 1;
		}
		if (qName.equals("timestamp"))
			timestamp = temp.toString();
		if (qName.equals("username"))
			username = temp.toString();
		if (qName.equals("ip"))
			ip = temp.toString();
		if (qName.equals("text"))
			text = temp.toString();
		if (qName.equals("page")) {
			flag = 0;
			if (username != "")
				author = username;
			else
				author = ip;

			// WikipediaParser wParser = new WikipediaParser();
			// wikiDoc = wParser.parseDoc(wikiDoc, text);
			// System.out.println(text);
			// add(wikiDoc, wikidocs);

			if (text.matches("(?s).*\\[\\[Category:Living people(?s).*")) {
				if (count < needed) {
					count = count + 1;
					title = StringEscapeUtils.escapeXml(title);

					author = StringEscapeUtils.escapeXml(author);
					timestamp = StringEscapeUtils.escapeXml(timestamp);
					text = StringEscapeUtils.escapeXml(text);
					try {
						bw.write("<page>\n<title>" + title + "</title>\n" + "<id>" + id + "</id>\n" + "<revision>" + "<timestamp>" + timestamp + "</timestamp>\n" + "<contributor><username>" + author + "</username></contributor>\n" + "<text>" + text + "</text>\n\t\t</revision>\n\t</page>\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println(count + "\t" + author);
				} else
					throw new SAXException(new SaxTerminatorException());
			}
		}
	}

	/* TODO: Implement this method */
	/**
	 * 
	 * @param filename
	 * @param docs
	 * @throws SAXException
	 */
	public void parse(String filename) throws SAXException {
		try {

			SAXParserFactory sax = SAXParserFactory.newInstance();
			sax.setValidating(false);
			sax.setNamespaceAware(false);
			SAXParser sp = sax.newSAXParser();
			XMLReader xmlReader = sp.getXMLReader();

			xmlReader.setContentHandler(new parsercode(fw, bw, file));

			xmlReader.parse(filename);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println("Either no filename/invalid filename");
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			File file = new File("E:/IRProj3Checkout/solr-webapp/Dumps/enwikipedia/WikiParsed.xml");// Ignore this and the next
													// line
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("E:/IRProj3Checkout/solr-webapp/Dumps/enwikipedia/WikiParsed.xml"), "UTF-8"));
			bw.write("<mediawiki>\n");
			parsercode p = new parsercode(fw, bw, file);
			p.parse("E:/IRProj3Checkout/solr-webapp/Dumps/enwikipedia/Wikipedia_Dump.xml");
			bw.write("</mediawiki>\n");
			bw.close();
		} catch (SAXException e) {
			if (e.getCause() instanceof SaxTerminatorException) {
				// we have broken the parsing process
				System.out.println("Done with parsing");
				try {
					bw.write("</mediawiki>\n");
					bw.close();
					return;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
