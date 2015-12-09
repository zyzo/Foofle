package service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.print.attribute.HashAttributeSet;

import scraper.FoofleItem;
import scraper.SqlDAO;



public class FoofleMatching {

	private SqlDAO dao;
	
	public FoofleMatching() {
		dao = SqlDAO.getInstance();
	}
	public float mesureCosinus(String query) {
		String[] queryTerms = query.split(" ");
		Map<String, List<Integer>> vector = constructVector(queryTerms);
		printVector(vector);
		return 0;
	}

	private Map<String, List<Integer>> constructVector(String[] queryTerms) {
		// set de documents concernés
		// Set<String> docs = new HashSet<>();
		Map<String, List<Integer>> vector = new HashMap<>();
		for (String term: queryTerms) {
			List<FoofleItem> items = dao.get(term);
			for (FoofleItem item: items) {
				List<Integer> list = vector.get(item.getLink());
				if (list == null) list = new ArrayList<>();
				list.add(item.getOccur());
				vector.put(item.getLink(), list);
			}
		}
		return vector;
	}
	private void printVector(Map<String, List<Integer>> mapVector) {
		for (Entry<String, List<Integer>> entry : mapVector.entrySet()){
			String key = entry.getKey().toString();;
            List<Integer> value = entry.getValue();
            System.out.println("key =" + key + " value =" + value.toString() ); 
		}
	}
	public static void main(String[] args) {
		FoofleMatching foo = new FoofleMatching();
		float res = foo.mesureCosinus("Adama Intouchables");
		
	}
}
