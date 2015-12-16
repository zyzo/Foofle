package scraper;

import java.util.Map;
import java.util.Map.Entry;

public class FoofleUtils {
    public static boolean isNullOrEmpty(String s) {
        return s == null || "".equals(s.trim()
                // Replace no-break space by normal space
                .replaceAll("(^\\h*)|(\\h*$)",""));
    }

    public static void main(String[] args) {
        System.out.println(isNullOrEmpty("      "));
    }
    
    public static void printVector(Map mapVector) {
		for (Object e : mapVector.entrySet()){
			Entry<String, Object> entry = (Entry<String, Object>) e;
			String key = entry.getKey().toString();;
            Object value = entry.getValue();
            System.out.println("key =" + key + " value =" + value.toString() ); 
		}
	}
}
