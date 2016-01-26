package indexing;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import select.FoofleConfig;

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

    public static Double getEvaluation(FoofleItem item) {
    	switch(FoofleConfig.PONDERATION) {
	    case NUM_OCCURS :
			return (double) item.getOccur();
		case TF_IDF:
			return (double) item.getTfidf();
		case ROBERTSON_TF:
			return item.getRobertsonTF();
		case TF_NORMALIZED:
			return item.getNormalizedTF();
		case TAGS_HTML:
			return item.getHtmlp();
		case CUSTOM_ROBERTSON_TF:
			return item.getCustomRobertsonTF();
		default:
			throw new RuntimeException();
		}
    }
    
    
    public static void printVector(Map mapVector) {
		for (Object e : mapVector.entrySet()){
			Entry<String, Object> entry = (Entry<String, Object>) e;
            System.out.println("key =" + parseString(entry.getKey()) 
            + " value =" + parseString(entry.getValue())); 
		}
	}
}
