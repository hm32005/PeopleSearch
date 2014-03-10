package wikipedia_solr;

import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.solr.handler.dataimport.Context;
import org.apache.solr.handler.dataimport.Transformer;

import wikipedia_solr.WikipediaDocument.Section;

public class WikipediaToTextTransformer extends Transformer {

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
				WikipediaParser wikipediaParser = new WikipediaParser(Integer.parseInt((String) row.get("id")), null, (String) row.get("user"), (String) row.get("title"));
				WikipediaDocument wikipediaDocument = wikipediaParser.parseText(wikiMediaText);
				row.put("links", wikipediaDocument.getLinks());
				row.put("categories", wikipediaDocument.getCategories());
				StringBuffer text = new StringBuffer();
				for (Section section : wikipediaDocument.getSections()) {
//					text.append(section.getTitle());
//					text.append(" ");
					text.append(section.getText());
					text.append(" ");
				}
				row.put("articlePlainText", text.toString());
				row.put("entireText", (String) row.get("title") + " " + text.toString());

				int pltCount = text.toString().length();

				row.put("articlePlainTextCount", pltCount);
				row.put("markupPlainRatio", ((float) pltCount) / (1 + wmCount));
				row.put("imageLink", wikipediaDocument.getImageLink());
				System.out.println((String) row.get("title"));
				System.out.println(wikipediaDocument.getImageLink());
			}
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
