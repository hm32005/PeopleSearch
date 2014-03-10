
package wikipedia_solr;

import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.solr.handler.dataimport.Context;
//import wikipedia_solr.SimpleParser;
import org.apache.solr.handler.dataimport.Transformer;

import wikipedia_solr.WikipediaDocument.Section;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;

public class WikimediaToTextTransformer extends Transformer {
	public String serializedClassifier = "classifiers/english.all.3class.distsim.crf.ser.gz";
	public AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifierNoExceptions(serializedClassifier);

	public Map<String, Object> transformRow(Map<String, Object> row, Context context) {
		row.put("error", "false");
		String wikiMediaText = (String) row.get("wikimediaMarkup");
		int wmCount = wikiMediaText.length();
		row.put("wikimediaMarkupCount", wmCount);
		System.out.println(row.get("id"));
		try {
			if (row.get("$skipDoc") == "true") {
				System.out.println("fast skipping" + row.get("id"));
				row.put("error", "true");
				row.put("exception", "RegexTransformerSucces");

				return row;

			}
			if (wikiMediaText.startsWith("#REDIRECT")) {
				System.out.println("regex transfromer failed on " + row.get("id"));
				row.put("error", "true");
				row.put("execption", "RegexTransformerFail");
				return row;
			}
			if (wikiMediaText != null) {
				WikinewsParser wikinewsParser = new WikinewsParser(Integer.parseInt((String) row.get("id")), null, (String) row.get("user"), (String) row.get("title"));
				WikipediaDocument wikipediaDocument = wikinewsParser.parseText(wikiMediaText);
				row.put("links", wikipediaDocument.getWikipediaLinks());
				row.put("categories", wikipediaDocument.getCategories());
				StringBuffer text = new StringBuffer();
//				StringBuffer sectionTitles = new StringBuffer();
				for (Section section : wikipediaDocument.getSections()) {
//					sectionTitles.append(section.getTitle());
//					sectionTitles.append(" ");
					text.append(section.getText());
					text.append(" ");
				}
				ArrayList<String> names = new ArrayList<String>();
				StringBuffer cumulNames = new StringBuffer();
				//StringBuffer Links = new StringBuffer();
				for (String link : wikipediaDocument.getWikipediaLinks()) {
					String parsedText = new String(new String(new String(link.toString().replaceAll("_", " ")).replaceAll("[^\\w\\s\\n\\r]", " ")).replaceAll("\\d", ""));
					String cTerms = new String(new String(classifier.classifyToString(parsedText).replaceAll("(\\w+)/PERSON", "$1")).replaceAll("\\w+/[^\\s]+", "").replaceAll("\\s{2,}", " ")).trim();
				String[] classified = cTerms.split("\\s\\s+");
				for (String classifiedString : classified) {
					if (classifiedString.trim().length() > 0) {
						names.add(classifiedString.trim());
						cumulNames.append(classifiedString.trim() + " ");
					}

				}
				}
				row.put("sectionParsed", cumulNames.toString());
				row.put("names", names);
				row.put("articlePlainText", text.toString());
				row.put("entireText", (String) row.get("title") + " " + text.toString());

				int pltCount = text.toString().length();

				row.put("articlePlainTextCount", pltCount);
				row.put("markupPlainRatio", ((float) pltCount) / (1 + wmCount));
				row.put("imageLink", wikipediaDocument.getImageLink());
				row.put("createDate", wikipediaDocument.getDate());
			} /*else {
				System.out.println("Category identifier failed on " + row.get("id"));
				row.put("error", "true");
				row.put("execption", "CategoryFail");
				return row;
			}*/
		} catch (java.lang.Exception e) {
			System.out.println("error with  " + row.get("title"));
			e.printStackTrace();
			row.put("exception", e);
			row.put("stackTrace", ExceptionUtils.getStackTrace(e));
			row.put("error", "true");
		}

		return row;
	}
}
