package service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

public class FoofleSearch {
	static Comparator<Double> descendingComparator = new Comparator<Double>() {
		  public int compare(Double o1, Double o2) {
		    if (o1 > o2) return -1;
		    if (o1 < o2) return 1;
		    return 0;
		  }
	};
	public static List<String> search(String query) {
		FoofleMatching foo = new FoofleMatching();
		Map<String, Double> res = foo.mesureCosinus("Adama Intouchables");
		Map<Double, String> reverseRes = new HashMap<>();
		for (Entry<String, Double> e:res.entrySet()) {
			reverseRes.put(e.getValue(), e.getKey());
		}
		SortedSet<Double> keys = new TreeSet<Double>(descendingComparator);
		keys.addAll(reverseRes.keySet());
		List<String> result = new ArrayList<>();
		for (Double key: keys) {
			String value = reverseRes.get(key);
			result.add(value);
		}
		return result;
	}
	
	public static void main(String[] args) {
		System.out.println(search("Adama Intouchables"));
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
