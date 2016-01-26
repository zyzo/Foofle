package search;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import indexing.FoofleUtils;

public class FoofleSearch {
	
	private static FoofleMatching foo = new FoofleMatching();;
	static Comparator<Double> descendingComparator = new Comparator<Double>() {
		  public int compare(Double o1, Double o2) {
		    if (o1 > o2) return -1;
		    if (o1 < o2) return 1;
		    return 0;
		  }
	};
	public static List<String> search(String[] query) {
		List<String> splitQuery = new ArrayList<>();
		for (String q: query) {
			String[] split = q.split(" ");
			for (String s: split) splitQuery.add(s);
		}
		String[] convertedQuery = new String[splitQuery.size()];
		for (int i = 0; i < splitQuery.size(); i++) {
			convertedQuery[i] = splitQuery.get(i);
		}
		Map<String, Double> res = foo.mesure(convertedQuery);
		Map<Double, List<String>> reverseRes = new HashMap<>();
		for (Entry<String, Double> e:res.entrySet()) {
			List<String> listItems = reverseRes.get(e.getValue());
			if (listItems == null) {
				listItems = new ArrayList<>();
			}
			listItems.add(e.getKey());
			reverseRes.put(e.getValue(), listItems);
		}
		SortedSet<Double> keys = new TreeSet<Double>(descendingComparator);
		keys.addAll(reverseRes.keySet());
		List<String> result = new ArrayList<>();
		for (Double key: keys) {
			List<String> values = reverseRes.get(key);
			for (String value: values) result.add(value);
		}
		return result;
	}
	
	public static void main(String[] args) {
		System.out.println(search(new String[]{"personnes", "Intouchables"}));
	}
}
/**
 * key =D1.html value =[1, 1]
key =D3.html value =[0, 5]
key =D2.html value =[1, 5]
key =D1.html value =0.9999999999999998
key =D3.html value =0.7071067811865475
key =D2.html value =0.8320502943378437
*/
