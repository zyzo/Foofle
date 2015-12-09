package scraper;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TextCleaner {
    private List<String> stopList;
    private String specialCharList;
    public TextCleaner() {
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(
                                    new File("stopliste.txt")), "ISO-8859-15"));
            stopList = new ArrayList<>();
            String line;
            while((line = reader.readLine()) != null) {
                stopList.add(line);
            }
            System.out.println(stopList);
            reader.close();
            BufferedReader reader2 = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(
                                    new File("specialchar.txt")), "UTF-8"));
            specialCharList =  reader2.readLine();
            reader2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public List<String> clean(List<String> list) {
        List<String> result;
        result = cleanSpecialCharacters(list);
        result = cleanStopList(result);
        return result;
    }

    private List<String> cleanSpecialCharacters(List<String> list) {
        List<String> newList = new ArrayList<>();
        for (String text: list) {
            String text2 = text.replaceAll("\\p{Punct}", " ");
            String[] text3 = text2.split(" ");
            for (int i = 0; i < text3.length; i++)
                newList.add(text3[i]);
        }
        return newList;
    }

    private List<String> cleanStopList(List<String> list) {
        List<String> newList = new ArrayList<>();
        for (String text: list) {
            if (FoofleUtils.isNullOrEmpty(text)) continue;
            boolean isStopString = false;
            for (String stopString : stopList) {
                if (stopString.equals(text)) {
                    isStopString = true;
                }
            }
            if (!isStopString)
                newList.add(text);
        }
        return newList;
    }
}
