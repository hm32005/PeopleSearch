/**
 * 
 */
package wikipedia_solr;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Stack;
import java.util.StringTokenizer;

/**
 * @author nikhillo This class implements Wikipedia markup processing. Wikipedia
 *         markup details are presented here:
 *         http://en.wikipedia.org/wiki/Help:Wiki_markup It is expected that all
 *         methods marked "todo" will be implemented by students. All methods
 *         are static as the class is not expected to maintain any state.
 */
public class WikinewsParser {
	private WikipediaDocument doc;
	private ArrayList<String> wikipediaLinks = new ArrayList<String>();

	/* TODO */
	/**
	 * Method to parse section titles or headings. Refer:
	 * http://en.wikipedia.org/wiki/Help:Wiki_markup#Sections
	 * 
	 * @param titleStr
	 *            : The string to be parsed
	 * @return The parsed string with the markup removed
	 */
	public String parseSectionTitle(String titleStr) {
		if (titleStr == null)
			return null;
		else if (titleStr.equals(""))
			return "";
		else {
			StringBuffer sb = new StringBuffer();
			char[] inputArray = titleStr.toCharArray();
			Stack<Character> s = new Stack<Character>();
			for (int i = 0; i < inputArray.length; i++) {
				if (s.isEmpty() && inputArray[i] == '=' && (i + 1 < inputArray.length && inputArray[i + 1] == '='))
					s.push(inputArray[i]);
				else if (inputArray[i] == '=' && (i + 1 < inputArray.length && inputArray[i + 1] == '\n')) {
					if (!s.isEmpty())
						s.pop();
				} else if (!s.isEmpty() && inputArray[i] != '=')
					sb.append(inputArray[i]);
			}
			return sb.toString().trim();
		}
	}

	/* TODO */
	/**
	 * Method to parse list items (ordered, unordered and definition lists).
	 * Refer: http://en.wikipedia.org/wiki/Help:Wiki_markup#Lists
	 * 
	 * @param itemText
	 *            : The string to be parsed
	 * @return The parsed string with markup removed
	 */
	public String parseListItem(String itemText) {
		if (itemText == null)
			return null;
		else if (itemText.equals(""))
			return "";
		else {
			StringBuffer sb = new StringBuffer();
			char[] inputArray = itemText.toCharArray();
			for (int i = 0; i < inputArray.length; i++) {
				if ((inputArray[i] == '*' || inputArray[i] == '#' || inputArray[i] == ':') && i + 1 < inputArray.length && (inputArray[i + 1] == ' ' || inputArray[i + 1] == '[' || inputArray[i + 1] == '{' || inputArray[i + 1] == '*' || inputArray[i + 1] == '#'))
					continue;
				else
					sb.append(inputArray[i]);
			}
			return sb.toString().trim();
		}
	}

	/* TODO */
	/**
	 * Method to parse text formatting: bold and italics. Refer:
	 * http://en.wikipedia.org/wiki/Help:Wiki_markup#Text_formatting first point
	 * 
	 * @param text
	 *            : The text to be parsed
	 * @return The parsed text with the markup removed
	 */
	public String parseTextFormatting(String text) {
		if (text == null)
			return null;
		else if (text.equals(""))
			return "";
		else {
			StringBuffer sb = new StringBuffer();
			char[] inputArray = text.toCharArray();
			Stack<Character> s = new Stack<Character>();
			for (int i = 0; i < inputArray.length; i++) {
				if (inputArray[i] == '\'' && ((i + 1) < inputArray.length && inputArray[i + 1] == '\''))
					s.push(inputArray[i]);
				else if (s.isEmpty() || inputArray[i] != '\'') {
					sb.append(inputArray[i]);
					if (!s.isEmpty())
						s.pop();
				}
			}
			return sb.toString().trim();
		}
	}

	/* TODO */
	/**
	 * Method to parse *any* HTML style tags like: <xyz ...> </xyz> For most
	 * cases, simply removing the tags should work.
	 * 
	 * @param text
	 *            : The text to be parsed
	 * @return The parsed text with the markup removed.
	 */
	public String parseTagFormatting(String text) {
		if (text == null)
			return null;
		else if (text.equals(""))
			return "";
		else {
			StringBuffer sb = new StringBuffer();
			// char[] inputArray = text.toCharArray();
			int oldPos = 0;
			int currentPos = 0;
			int numSpaces = 0;
			while (currentPos <= text.length()) {
				if (text.indexOf("&lt;", oldPos) != -1)
					currentPos = text.indexOf("&lt;", oldPos);
				else if (text.indexOf("<", oldPos) != -1)
					currentPos = text.indexOf("<", oldPos);
				else
					currentPos = -1;

				if (currentPos == -1)
					/* currentPos = text.length(); */
					break;
				if (currentPos >= 1 && text.charAt(currentPos - 1) == ' ')
					numSpaces++;
				if (sb.length() > 0 && sb.charAt(sb.length() - 1) != ' ')
					sb.append(' ');
				sb.append(new String(text.substring(oldPos, currentPos)));

				if (text.indexOf("&lt;", oldPos) != -1)
					currentPos = text.indexOf("&lt;", oldPos) + 4;
				else if (text.indexOf("<", oldPos) != -1)
					currentPos = text.indexOf("<", oldPos) + 1;
				else
					currentPos = -1;

				if (text.indexOf("&gt;", currentPos) != -1)
					oldPos = text.indexOf("&gt;", currentPos) + 4;
				else if (text.indexOf(">", currentPos) != -1)
					oldPos = text.indexOf(">", currentPos) + 1;
				else
					oldPos = -1;

				if (oldPos == -1)
					break;

				if (text.indexOf("&gt;", currentPos) != -1)
					oldPos = text.indexOf("&gt;", currentPos) + 4;
				else if (text.indexOf(">", currentPos) != -1)
					oldPos = text.indexOf(">", currentPos) + 1;
				else
					oldPos = -1;

				if (oldPos != text.length() && text.charAt(oldPos) == ' ' && numSpaces != 0)
					oldPos++;
				currentPos = oldPos;
				numSpaces = 0;
			}
			if (currentPos == -1)
				sb.append(new String(text.substring(oldPos, text.length())));
			/*
			 * for(int i = 0; i < inputArray.length; i++){
			 * if(inputArray[i]=='<') s.push(inputArray[i]); else
			 * if(inputArray[i]=='>'){ if(!s.isEmpty()) s.pop(); } else
			 * if(s.isEmpty()){ if (!(i > 0 && inputArray[i-1] == '>' &&
			 * inputArray[i] == ' ')) sb.append(inputArray[i]); } }
			 */
			return sb.toString().trim();
		}
	}

	/* TODO */
	/**
	 * Method to parse wikipedia templates. These are *any* {{xyz}} tags For
	 * most cases, simply removing the tags should work.
	 * 
	 * @param text
	 *            : The text to be parsed
	 * @return The parsed text with the markup removed
	 */
	public String parseTemplates(String text) {
		if (text == null)
			return null;
		else if (text.equals(""))
			return "";
		else {
			StringBuffer sb = new StringBuffer();
			char[] inputArray = text.toCharArray();
			Stack<Character> s = new Stack<Character>();
			for (int i = 0; i < inputArray.length; i++) {
				if (inputArray[i] == '{')
					s.push(inputArray[i]);
				else if (inputArray[i] == '}') {
					if (!s.isEmpty())
						s.pop();
				} else if (s.isEmpty())
					sb.append(inputArray[i]);
			}
			return sb.toString().trim();
		}
	}

	/* TODO */
	/**
	 * Method to parse links and URLs. Refer:
	 * http://en.wikipedia.org/wiki/Help:Wiki_markup#Links_and_URLs
	 * 
	 * @param text
	 *            : The text to be parsed
	 * @return An array containing two elements as follows - The 0th element is
	 *         the parsed text as visible to the user on the page The 1st
	 *         element is the link url
	 */
	public String[] parseLinks(String text) {
		text = parseTagFormatting(text);
		if (text == null)
			return new String[] {
									"",
									"" };
		else if (text.equals(""))
			return new String[] {
									"",
									"" };
		else {
			int num_braces = 0;
			StringBuffer sb = new StringBuffer();
			StringBuffer externalUrl = new StringBuffer();
			ArrayList<String> output = new ArrayList<String>();
			ArrayList<String> linkList = new ArrayList<String>();
			char[] inputArray = text.toCharArray();

			// sb = new StringBuffer();
			StringBuffer url = new StringBuffer();
			Stack<Character> s = new Stack<Character>();
			for (int i = 0; i < inputArray.length; i++) {
				if (inputArray[i] == '[')
					s.push(inputArray[i]);
				else if (inputArray[i] == ']') {
					if (!s.isEmpty())
						s.pop();
				} else if (s.size() == 2) {
					num_braces = 2;
					url.append(inputArray[i]);
				} else if (s.size() == 1) {
					num_braces = 1;
					externalUrl.append(inputArray[i]);
				}
				if (s.isEmpty() && num_braces == 2) {
					if (url.length() > 0) {
						if (url.indexOf(":") == -1 || url.toString().startsWith("Category:") || url.toString().startsWith("w:")) {
							if (url.indexOf("|") != -1) {
								String temp_url;
								if (url.indexOf(":") != 0) {
									if (url.toString().startsWith("w:")) {
										temp_url = new String(url.toString().substring("w:".length(), url.indexOf("|"))).trim();
									} else
										temp_url = new String(url.substring(0, url.indexOf("|"))).trim();
								} else
									temp_url = new String(url.substring(1, url.indexOf("|"))).trim();
								char[] temp_url_array = temp_url.toCharArray();
								if (temp_url_array != null && temp_url_array.length > 0) {
									temp_url_array[0] = Character.toUpperCase(temp_url_array[0]);
									for (int j = 0; j < temp_url_array.length; j++) {
										if (temp_url_array[j] == ' ')
											temp_url_array[j] = '_';
									}
									if (url.toString().startsWith("w:"))
										wikipediaLinks.add(new String(temp_url_array));
									else
										linkList.add(new String(temp_url_array));
								}
							} else {
								if (url.toString().startsWith("Category:")) {
									sb.append(new String(url.toString().substring("Category:".length())).trim());
									linkList.add("");
								} else {
									String temp_url = url.toString().trim();
									if (url.toString().startsWith("w:")) {
										temp_url = new String(url.toString().substring("w:".length())).trim();
										sb.append(temp_url);
									}
									char[] temp_url_array = temp_url.toCharArray();
									if (temp_url_array != null && temp_url_array.length > 0) {
										temp_url_array[0] = Character.toUpperCase(temp_url_array[0]);
										for (int j = 0; j < temp_url_array.length; j++) {
											if (temp_url_array[j] == ' ')
												temp_url_array[j] = '_';
										}
										if (url.toString().startsWith("w:"))
											wikipediaLinks.add(new String(temp_url_array));
										else
											linkList.add(new String(temp_url_array));
									}
								}
							}
						} else
							linkList.add("");

						String actualText = new String(url.substring(url.lastIndexOf("|") + 1));
						if (actualText.trim().length() > 0) {
							if (url.toString().startsWith("Wikipedia:") || url.toString().startsWith("Wiktionary:") || url.toString().startsWith("media:") || url.toString().startsWith("File:") || url.toString().startsWith("Wiktionary:"))
								linkList.set(linkList.size() - 1, "");
							if (actualText.startsWith(":Category:")) {
								actualText = new String(actualText.substring(1));
								sb.append(actualText);
								linkList.set(linkList.size() - 1, "");
							}
							if (/* !actualText.contains("Category:") && */!actualText.startsWith("File:")) {
								if (actualText.contains("Category:"))
									actualText = new String(actualText.substring(actualText.indexOf("Category:") + 9, actualText.length()));
								sb.append(actualText);
							}
						} else {
							if (url.indexOf("|") != -1)
								actualText = new String(url.substring(0, url.lastIndexOf("|")));
							else
								actualText = url.toString();
							if (actualText.startsWith("Wikipedia:") || actualText.startsWith("Wiktionary:") || actualText.startsWith("media:") || actualText.startsWith("File:") || actualText.startsWith("Wiktionary:"))
								linkList.set(linkList.size() - 1, "");
							if (actualText.indexOf("(") != -1)
								actualText = new String(actualText.substring(0, actualText.indexOf("("))).trim();
							if (actualText.indexOf(",") != -1)
								actualText = new String(actualText.substring(0, actualText.indexOf(","))).trim();
							if (actualText.startsWith("Wikipedia:")) {
								if (!actualText.contains("#"))
									actualText = new String(actualText.substring(10));
							}
							sb.append(actualText);
						}
						url.delete(0, url.length());
					}
					if (inputArray[i] != ']')
						sb.append(inputArray[i]);
					num_braces = 0;
				} else if (s.isEmpty() && num_braces == 1) {
					if (externalUrl.length() > 0 && !externalUrl.toString().startsWith("[")) {
						// System.out.println(externalUrl);
						String actualText = "";
						if (externalUrl.indexOf(" ") != -1)
							actualText = new String(externalUrl.substring(externalUrl.indexOf(" "), externalUrl.length()));
						else
							actualText = "";
						// System.out.println(actualText);
						if (sb.length() > 0)
							sb.append(" " + actualText);
						else
							sb.append(actualText);
						linkList.add("");
					}
					num_braces = 0;
					externalUrl = new StringBuffer();
				} else if (s.isEmpty() && num_braces == 0) {
					sb.append(inputArray[i]);
				} else
					continue;
			}

			if (s.isEmpty() && num_braces == 2 && url.length() > 0) {
				if (url.indexOf(":") == -1) {
					if (url.indexOf("|") != -1) {
						String temp_url;
						if (url.indexOf(":") != 0)
							temp_url = new String(url.substring(0, url.indexOf("|"))).trim();
						else
							temp_url = new String(url.substring(1, url.indexOf("|"))).trim();
						char[] temp_url_array = temp_url.toCharArray();
						if (temp_url_array != null && temp_url_array.length > 0) {
							temp_url_array[0] = Character.toUpperCase(temp_url_array[0]);
							for (int j = 0; j < temp_url_array.length; j++) {
								if (temp_url_array[j] == ' ')
									temp_url_array[j] = '_';
							}
							linkList.add(new String(temp_url_array));
						}
					} else {
						if (url.toString().startsWith("Category:")) {
							sb.append(new String(url.toString().substring("Category:".length())).trim());
							linkList.add("");
						}
						/*
						 * else if (url.toString().startsWith("w:")) {
						 * sb.append(
						 * url.toString().substring("w:".length()).trim());
						 * linkList.add(""); }
						 */
						else {
							String temp_url = url.toString().trim();
							char[] temp_url_array = temp_url.toCharArray();
							if (temp_url_array != null && temp_url_array.length > 0) {
								temp_url_array[0] = Character.toUpperCase(temp_url_array[0]);
								for (int j = 0; j < temp_url_array.length; j++) {
									if (temp_url_array[j] == ' ')
										temp_url_array[j] = '_';
								}
								linkList.add(new String(temp_url_array));
							}
						}
					}
				} else
					linkList.add("");

				String actualText = new String(url.substring(url.lastIndexOf("|") + 1));
				if (actualText.trim().length() > 0) {
					if (url.toString().startsWith("Wikipedia:") || url.toString().startsWith("Wiktionary:") || url.toString().startsWith("media:") || url.toString().startsWith("File:") || url.toString().startsWith("Wiktionary:"))
						linkList.set(linkList.size() - 1, "");
					if (actualText.startsWith(":Category:")) {
						actualText = new String(actualText.substring(1));
						sb.append(actualText);
						linkList.set(linkList.size() - 1, "");
					} else if (/* !actualText.contains("Category:") && */!actualText.startsWith("File:")) {
						if (actualText.contains("Category:"))
							actualText = new String(actualText.substring(actualText.indexOf("Category:") + 9, actualText.length()));
						sb.append(actualText);
					}
				} else {
					if (url.indexOf("|") != -1)
						actualText = new String(url.substring(0, url.lastIndexOf("|")));
					else
						actualText = url.toString();
					if (actualText.startsWith("Wikipedia:") || actualText.startsWith("Wiktionary:") || actualText.startsWith("media:") || actualText.startsWith("File:") || actualText.startsWith("Wiktionary:"))
						linkList.set(linkList.size() - 1, "");
					if (actualText.indexOf("(") != -1)
						actualText = new String(actualText.substring(0, actualText.indexOf("("))).trim();
					if (actualText.indexOf(",") != -1)
						actualText = new String(actualText.substring(0, actualText.indexOf(","))).trim();
					if (actualText.contains(":") && !actualText.contains("#")) {
						if (actualText.startsWith(":"))
							actualText = new String(actualText.substring(actualText.indexOf(':') + 1));
						actualText = new String(actualText.substring(actualText.indexOf(':') + 1));
					}
					sb.append(actualText);
				}
				url.delete(0, url.length());
				num_braces = 0;
			} else if (s.isEmpty() && num_braces == 1) {
				if (externalUrl.length() > 0 && !externalUrl.toString().startsWith("[")) {
					String actualText = externalUrl.toString();
					if (externalUrl.indexOf(" ") != -1)
						actualText = new String(externalUrl.substring(externalUrl.indexOf(" "), externalUrl.length())).trim();
					else
						actualText = "";
					if (sb.length() > 0)
						sb.append(" " + actualText);
					else
						sb.append(actualText);
					linkList.add("");
				}
				num_braces = 0;
				externalUrl = new StringBuffer();
			}

			output.add(sb.toString());
			output.addAll(linkList);
			return output.toArray(new String[0]);
		}
	}

	public String parseImageLink(String text) {
		int imstart = text.toLowerCase().indexOf("image");
		// System.out.println("imstart " + imstart);
		// System.out.println("= " + text.indexOf("=",imstart));
		if (imstart != -1 && ((text.indexOf("File:") != -1 && imstart < text.indexOf("File:")) || text.indexOf("File:") == -1)) {
			int start = text.indexOf("=", imstart);
			int end = text.indexOf("|", start);
			String imageLink = null;
			// System.out.println("start " + start + "   end " + end);
			if (start > 0 && end > 0 && end > start) {
				imageLink = new String(text.substring(start + 1, end));
				return imageLink.trim();
			}
		} else {
			int start = text.indexOf("File:");
			int end = text.indexOf("|", start);
			if (end == -1 || end > text.indexOf("]", start))
				end = text.indexOf("]", start);
			String imageLink = null;
			// System.out.println("start1 " + start + "   end " + end);
			if (start > 0 && end > 0 && end > start) {
				imageLink = new String(text.substring(start, end));
				return imageLink;
			}
		}
		return null;
	}

	public String parseDate(String text) {
		int start = text.toLowerCase().indexOf("{{date");
		// System.out.println("imstart " + imstart);
		// System.out.println("= " + text.indexOf("=",imstart));
		if (start != -1) {
			int istart = text.indexOf("|", start);
			int end = text.indexOf("}", istart);
			String date = null;
			// System.out.println("start " + start + "   end " + end);
			if (istart > 0 && end > 0 && end > istart) {
				date = new String(text.substring(istart + 1, end));
				return date.trim();
			}
		}
		return null;
	}

	public WikinewsParser(int idFromXml, String timestampFromXml, String authorFromXml, String ttl) throws ParseException {
		doc = new WikipediaDocument(idFromXml, timestampFromXml, authorFromXml, ttl);
	}

	public WikipediaDocument parseText(String pageText) {
		doc.setImageLink(this.parseImageLink(pageText));
		doc.setDate(DateUtil.parseDate(this.parseDate(pageText)));
		int currentPos = 0;
		boolean categoriesFlag = false;
		do {
			currentPos = pageText.indexOf("==", currentPos);
			if (currentPos == -1)
				currentPos = pageText.length();
			String sectionText = new String(pageText.substring(0, currentPos));
			String currSectTitle = parseSectionTitle(sectionText);
			if (currSectTitle.equals(""))
				currSectTitle = "Default";
			StringBuffer currSecText = new StringBuffer();
			// System.out.println("Section Title: \t" + currSectTitle);
			int start = 0;
			if (!currSectTitle.equals("Default"))
				start = sectionText.indexOf('\n', sectionText.indexOf(currSectTitle) + currSectTitle.length());
			// System.out.println("start" + start);
			if (start != -1)
				sectionText = new String(sectionText.substring(start));
			else
				sectionText = null;
			// System.out.println("Section Text: \n\n" + sectionText);

			// if(sectionText.length() > 0){
			long startTime = System.nanoTime();
			sectionText = this.parseTagFormatting(sectionText);
			long endTime = System.nanoTime();
			// System.out.println("Tag parsing time: " + (endTime - startTime));
			// System.out.println("Text post tag parsing: \n\n" + sectionText);
			startTime = System.nanoTime();
			sectionText = this.parseListItem(sectionText);
			endTime = System.nanoTime();
			// System.out.println("Text post ListItem parsing: \n\n" +
			// sectionText);
			// System.out.println("ListItem parsing time: " + (endTime -
			// startTime));
			startTime = System.nanoTime();
			sectionText = this.parseTemplates(sectionText);
			endTime = System.nanoTime();
			// System.out.println("Templates parsing time: " + (endTime -
			// startTime));
			// System.out.println("Text post Template parsing: \n\n" +
			// sectionText);
			// System.out.println("Template parsing time: " + (endTime -
			// startTime));
			startTime = System.nanoTime();
			sectionText = this.parseTextFormatting(sectionText);
			endTime = System.nanoTime();
			// System.out.println("TextFormat parsing time: " + (endTime -
			// startTime));

			// System.out.println("Text post TextFormatting parsing: \n\n" +
			// sectionText);
			ArrayList<String> wikiLinks = new ArrayList<String>();
			ArrayList<String> wikiCategories = new ArrayList<String>();
			if (sectionText != null) {
				StringTokenizer sectionTextTokenizer = new StringTokenizer(sectionText, "\n");
				for (; sectionTextTokenizer.hasMoreTokens();) {
					// System.out.println("Adding categories to WikiDocument : "
					// + links[i]);
					String lineSectionText = sectionTextTokenizer.nextToken();
					if (lineSectionText.startsWith("[[Category:"))
						categoriesFlag = true;
					else
						categoriesFlag = false;
					// System.out.println(lineSectionText);
					// System.out.println(categoriesFlag);
					startTime = System.nanoTime();
					String[] links = this.parseLinks(lineSectionText);
					endTime = System.nanoTime();
					// System.out.println("Link parsing time: " + (endTime -
					// startTime));
					if (currSecText.length() > 0)
						currSecText.append(" " + links[0]);
					else
						currSecText.append(links[0]);
					// System.out.println("Text post Link parsing: \n\n" +
					// currSecText);
					if (!categoriesFlag) {
						for (int i = 1; i < links.length; i++) {
							// System.out.println("Adding link to WikiDocument : "
							// + links[i]);
							if (links[i].trim().length() > 0)
								wikiLinks.add(links[i].trim());
						}
					} else {
						/*
						 * StringTokenizer categoriesTokenizer = new
						 * StringTokenizer(links[0], "\n"); String category; for
						 * (; categoriesTokenizer.hasMoreTokens();) {
						 * System.out.
						 * println("Adding categories to WikiDocument : " +
						 * links[i]); category =
						 * categoriesTokenizer.nextToken();
						 */
						if (links[0].trim().length() > 0)
							wikiCategories.add(links[0].trim());
					}
				}
			}
			// System.out.println("Links in Document\n\n" + doc.getLinks());
			// System.out.println("Section text after parsing \n\n" +
			// currSecText);
			// }

			// if(currSecText.length() > 0 && currSectTitle != null)
			doc.addLInks(wikiLinks);
			doc.addCategories(wikiCategories);
			doc.addSection(currSectTitle, currSecText.toString());
			if (currentPos != pageText.length()) {
				pageText = new String(pageText.substring(currentPos));
				// System.out.println("pageText \n\n" + pageText);
				currentPos = pageText.indexOf('\n');
				// System.out.println("currentPos " + currentPos);
				// System.out.println("index of next == " +
				// pageText.indexOf("==", currentPos));
			}
		} while (currentPos != -1 && currentPos != pageText.length());
		doc.addWikipediaLinks(wikipediaLinks);
		return doc;
	}
}
