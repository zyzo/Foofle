package indexing;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

public class FoofleUtils {
    public static boolean isNullOrEmpty(String s) {
        return s == null || "".equals(s.trim()
                // Replace no-break space by normal space
                .replaceAll("(^\\h*)|(\\h*$)",""));
    }
    
    private static NumberFormat formatter = NumberFormat.getNumberInstance(Locale.FRANCE);
    public static String doubleToString(double d) {
    	return formatter.format(d);
    }
    
    public static Number stringToNum(String s) throws ParseException {
    	return formatter.parse(s);
    }

    public static String parseString(Object s) {
    	if (s == null) return "";
        if (s instanceof Number) {
        	return formatter.format(s);
        }
        String result;
        try {
        	result = (String) s;
        } catch (ClassCastException e) {
        	result = s.toString();
        }
		return result;
    }

    public static void main(String[] args) {
        System.out.println(isNullOrEmpty("      "));
    }
    
    public static void printVector(Map mapVector) {
		for (Object e : mapVector.entrySet()){
			Entry<String, Object> entry = (Entry<String, Object>) e;
            System.out.println("key =" + parseString(entry.getKey()) 
            + " value =" + parseString(entry.getValue())); 
		}
	}
}
