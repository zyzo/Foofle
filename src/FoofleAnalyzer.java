import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class FoofleAnalyzer {
    private TextCleaner cleaner;

    public FoofleAnalyzer() {
        cleaner = new TextCleaner();
    }

    public void processFile(File f) throws IOException {
        Document doc = Jsoup.parse(f, "UTF-8");
        Elements content = doc.getElementsByTag("body");

        String s = extractText(content.get(0));
        String[] splitted = s.split("\\s+");

        System.out.println(Arrays.toString(splitted));
        // nettoyer les mots
        List<String> cleanedList = cleaner.clean(Arrays.asList(splitted));
        System.out.println(cleanedList);
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


    public static void main(String[] args) throws IOException {
        FoofleAnalyzer z = new FoofleAnalyzer();
        
        File corpusDir = new File("CORPUS");
        File[] fileList = corpusDir.listFiles();
        z.processFile(new File("CORPUS/D1.html"));
    }
}
