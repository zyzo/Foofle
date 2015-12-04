import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class FoofleAnalyzer {
    private TextCleaner cleaner;

    public FoofleAnalyzer() {
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

    public class Cell {
    	String link;
    	int occur;
    	@Override
    	public String toString() {
    		// TODO Auto-generated method stub
    		return "[Cell " + link + ", " + occur + "]";
    	}
    }
    
    public void run() throws IOException {
         File corpusDir = new File("CORPUS");
         File[] fileList = corpusDir.listFiles();
         Map<String, List<Cell>> map = new HashMap<>();
         for (File file : fileList) {
         	List<String> terms = processFile(file);
         	for (String term : terms) {
         		if (map.get(term) == null) map.put(term, new ArrayList<Cell>());
         		List<Cell> cells = map.get(term);
         		boolean found = false;
         		for (Cell cell: cells) {
         			if (cell.link.equals(file.getName())) {
         				found = true;
         				cell.occur += 1;
         				break;
         			}
         		}
         		if (!found) {
         			Cell cell = new Cell();
         			cell.link = file.getName();
         			cell.occur = 1;
         			cells.add(cell);
         		}
         	}
         }
         /*
         for (Object c : map.entrySet().toArray()) {
         	Entry d = (Entry) c;
         	System.out.println(d.getKey());
         	System.out.println(Arrays.toString(((List<Cell>) d.getValue()).toArray()));
         }
         */
         SqlDAO dao = SqlDAO.getInstance();
         for (Object c : map.entrySet().toArray()) {
          	Entry d = (Entry) c;
          	System.out.println(d.getKey());
          	System.out.println("Inserting " + d.getKey());
          	System.out.println(Arrays.toString(((List<Cell>) d.getValue()).toArray()));
          	for (Cell cell : (List<Cell>) d.getValue()) {
          		FoofleItem item = new FoofleItem();
          		item.setLink(cell.link);
          		item.setOccur(cell.occur);
          		item.setTerm((String)d.getKey());
          		dao.insert(item);
          	}
          }
         dao.close();
    }

    public static void main(String[] args) throws IOException {
    	FoofleAnalyzer z = new FoofleAnalyzer();
    	z.run();
    }
}
