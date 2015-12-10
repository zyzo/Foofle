package service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
	
	// sum(XnY)/(sqrt(X)*sqrt(Y)
	public Map<String, Double> mesureCosinus(String query) {
		String[] queryTerms = query.split(" ");
		List<Integer> queryWeights = new ArrayList<>(queryTerms.length);
		for (int i = 0; i < queryTerms.length; i++) queryWeights.add(1);
		Map<String, List<Integer>> vector = constructVector(queryTerms);
		printVector(vector);

		Map<String, Double> res = new HashMap<>();
		
		for (Entry<String, List<Integer>> entry: vector.entrySet()) {
			res.put(entry.getKey(), cosinus(queryWeights, entry.getValue()));
		}
		return res;
	}

	private double cosinus(List<Integer> l1, List<Integer> l2) {
		assert(l1.size() == l2.size());
		double sumXnY = 0;
		double sqrtX = 0;
		double sqrtY = 0;
		for (int i = 0; i < l1.size(); i++) {
			sumXnY += l1.get(i)*l2.get(i);
			sqrtX += l1.get(i)*l1.get(i);
			sqrtY += l2.get(i)*l2.get(i);
		}
		sqrtX = Math.sqrt(sqrtX);
		sqrtY = Math.sqrt(sqrtY);
		return sumXnY/(sqrtX*sqrtY);
	}

	private Map<String, List<Integer>> constructVector(String[] queryTerms) {
		// set de documents concernés
		Set<String> docs = new HashSet<>();
		Map<String, Map<String, Integer>> vector = new HashMap<>();
		for (String term: queryTerms) {
			Map<String, Integer> list = new HashMap<>();
			List<FoofleItem> items = dao.get(term);
			for (FoofleItem item: items) {
				Map<String, Integer> map = vector.get(item.getLink());
				if (map == null) map = new HashMap<>();
				map.put(term, item.getOccur());
				vector.put(item.getLink(), map);
				docs.add(item.getLink());
			}
		}
		
		Map<String, List<Integer>> convertedVector = new HashMap<>();
		Iterator<String> it = docs.iterator();
		while(it.hasNext()) {
			String link = it.next();
			List<Integer> list = new ArrayList<>();
			Map<String, Integer> savedList = vector.get(link);
			for (String term: queryTerms) {
				Integer occur = savedList.get(term);
				if (occur == null) list.add(0);
				else list.add(occur);
			}
			convertedVector.put(link, list);
		}
		return convertedVector;	
	}
	private static void printVector(Map mapVector) {
		for (Object e : mapVector.entrySet()){
			Entry<String, Object> entry = (Entry<String, Object>) e;
			String key = entry.getKey().toString();;
            Object value = entry.getValue();
            System.out.println("key =" + key + " value =" + value.toString() ); 
		}
	}
	public static void main(String[] args) {
		FoofleMatching foo = new FoofleMatching();
		Map<String, Double> res = foo.mesureCosinus("Adama Intouchables");
		printVector(res);
		
	}
}
