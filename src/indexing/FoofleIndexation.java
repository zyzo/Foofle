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

public class FoofleIndexation {
	private TextCleaner cleaner;

	public FoofleIndexation() {
		cleaner = new TextCleaner();
	}

	public List<String> processFile(File f) throws IOException {
		Document doc = Jsoup.parse(f, "UTF-8");
		Elements content = doc.getElementsByTag("body");

		String s = extractText(content.get(0));
		String[] splitted = s.split("\\s+");

		// nettoyer les mots
		List<String> cleanedList = cleaner.clean(Arrays.asList(splitted));
		return cleanedList;
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
			fileListSize.put(file.getName(), terms.size());
			for (String term : terms) {
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
					FoofleItems.add(item);
				}
			}
		}

		// Add pondération
		System.out.println("Computing ponderation (tf*idf)..");
		for (Object c : map.entrySet().toArray()) {
			Entry d = (Entry) c;
			for (FoofleItem item : (List<FoofleItem>) d.getValue()) {
				item.setTfidf(item.getOccur()/(float)(1+ Math.log(fileListSize.get(item.getLink()))));
			}
		}
		return map;
	}

	public static void main(String[] args) throws IOException {
		FoofleIndexation z = new FoofleIndexation();
		z.run();
	}
}
