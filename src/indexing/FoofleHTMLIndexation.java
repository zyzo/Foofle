package indexing;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

public class FoofleHTMLIndexation {
	private TextCleaner cleaner;
	private HTMLTagsPonderation tagLib;
	public FoofleHTMLIndexation() {
		cleaner = new TextCleaner();
		tagLib = new HTMLTagsPonderation();
	}

	public Map<String, Integer> processFileHTML(File f) throws IOException {
		Document doc = Jsoup.parse(f, "UTF-8");
		Element html = doc.getElementsByTag("html").get(0);
		Map<String, Integer> htmlResult = new HashMap<>();
		// special tags (h1, title, ..)
		for (Entry<String, Integer> tag: tagLib.SPECIAL_TAGS_MAP.entrySet()) {
			Elements elements = html.getElementsByTag(tag.getKey());
			for (Element e : elements) {
				String[] splitted = e.text().split("\\s+");
				List<String> cleaned = cleaner.clean(Arrays.asList(splitted));
				for (String s: cleaned) putResult(htmlResult, s, tag.getValue());
			}
		}
		// meta tags
		for (Element metaTag: html.getElementsByTag("meta")) {
			Integer value = tagLib.META_KEYWORDS_MAPS.get(metaTag.attr("keyword"));
			if (value == null) continue;
			String[] splitted = metaTag.attr("content").split("\\s+");
			List<String> cleaned = cleaner.clean(Arrays.asList(splitted));
			for (String s: cleaned) putResult(htmlResult, s, value);
		}
		// normal body text
		Element body = doc.getElementsByTag("body").get(0);
		String bodyText = extractText(body);
		// nettoyer les mots
		List<String> normalString = cleaner.clean(Arrays.asList(bodyText));
		for (String s: normalString) putResult(htmlResult, s, 1);
		return htmlResult;
	}
	public List<String> processFile(File f) throws IOException {
		// normal body text
		Document doc = Jsoup.parse(f, "UTF-8");
		Element body = doc.getElementsByTag("body").get(0);
		String bodyText = extractText(body);
		// nettoyer les mots
		List<String> normalString = cleaner.clean(Arrays.asList(bodyText));
		return normalString;
	}

	private void putResult(Map<String, Integer> result, String s, Integer value) {
		Integer oldValue = result.get(s);
		if (oldValue == null) result.put(s, value);
		else result.put(s, oldValue+value);
		
	}

	private String extractText(Element element) {
		String result = "";
		for (TextNode node : element.textNodes()) {
			if (!FoofleUtils.isNullOrEmpty(node.text()))
				result += " " + node.text();
		}
		for (Element child : element.children()) {
			String childText = extractText(child);
			if (!FoofleUtils.isNullOrEmpty(childText))
				result += " " + childText;
		}
		return result;
	}

	public void run() throws IOException {
		Date date1 = new Date();
		File corpusDir = new File("CORPUS");
		Map<String, List<FoofleItem>> map = invertIndex(corpusDir);
		SqlDAO dao = SqlDAO.getInstance();

		System.out.println("Saving to db..");
		List<FoofleItem> items = new ArrayList<>();
		for (Entry<String, List<FoofleItem>> c : map.entrySet()) {
			items.addAll(c.getValue());
		}
		dao.insert(items);
		Date date2 = new Date();
		long diff = date2.getTime() -  date1.getTime();
		long nbMin = diff/60000;
		long nbSec = (diff - nbMin)/1000;
		System.out.println("Indexation took " + nbMin + "m" + nbSec + "s");
		dao.close();
	}

	private Map<String, List<FoofleItem>> invertIndex(File corpusDir) throws IOException {
		System.out.println("Inverting index..");
		File[] fileList = corpusDir.listFiles();
		Map<String, List<FoofleItem>> map = new HashMap<>();
		Map<String, Integer> fileListSize = new HashMap<>();
		int i = 0;
		for (File file : fileList) {
			List<String> terms = processFile(file);
			Map<String, Integer> htmlTerms = processFileHTML(file);
			fileListSize.put(file.getName(), terms.size());
			for (String term: terms) {
				if (map.get(term) == null) map.put(term, new ArrayList<FoofleItem>());
				List<FoofleItem> FoofleItems = map.get(term);
				boolean found = false;
				for (FoofleItem item: FoofleItems) {
					if (item.getLink().equals(file.getName())) {
						found = true;
						item.setOccur(item.getOccur()+1);
						break;
					}
				}
				if (!found) {
					FoofleItem item = new FoofleItem();
					item.setTerm(term);
					item.setLink(file.getName());
					item.setOccur(1);
					item.setHTMLP((htmlTerms.get(term)));
					FoofleItems.add(item);
				}
			}
		}

		// Add pondération
		System.out.println("Computing ponderation (tf*idf, RobertsonTF, etc.)..");
		double avgFileSize = 0;
		for (int size : fileListSize.values()) {
			avgFileSize += size;
		}
		avgFileSize /= fileListSize.values().size();
		System.out.println("Average file size : " + avgFileSize);
		
		for (Object c: map.entrySet().toArray()) {
			Entry d = (Entry) c;
			for (FoofleItem item : (List<FoofleItem>) d.getValue()) {
				int fileSize = fileListSize.get(item.getLink());
				item.setTfidf(item.getOccur()/(float)(1+ Math.log(fileSize)));
				item.setCustomRobertsonTF(item.getOccur()/ (item.getOccur() + 2*(fileSize / avgFileSize)));
				item.setRobertsonTF(item.getOccur()/ (item.getOccur() + 0.5 + 1.5*(fileSize/avgFileSize)));
				item.setNormalizedTF(item.getOccur()/(float)fileSize);
			}
		}
		return map;
	}


	public static void main(String[] args) throws IOException {
		FoofleHTMLIndexation z = new FoofleHTMLIndexation();
		z.run();
	}
}
