package search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import indexing.FoofleItem;
import indexing.FoofleUtils;
import indexing.SqlDAO;
import select.FoofleConfig;



public class FoofleMatching {

	private SqlDAO dao;
	
	public FoofleMatching() {
		dao = SqlDAO.getInstance();
	}
	
	public Map<String, Double> mesureCosinus(String query) {
		String[] queryTerms = query.split(" ");
		List<Double> queryWeights = new ArrayList<>(queryTerms.length);
		for (int i = 0; i < queryTerms.length; i++) queryWeights.add(1.0);
		Map<String, List<Double>> vector = constructVector(queryTerms);
		Map<String, Double> res = new HashMap<>();
		
		for (Entry<String, List<Double>> entry: vector.entrySet()) {
			res.put(entry.getKey(), cosinus(queryWeights, entry.getValue()));
		}
		return res;
	}
	
	// sum(XnY)/(sqrt(X)*sqrt(Y)
	private double cosinus(List<Double> l1, List<Double> l2) {
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

	private Map<String, List<Double>> constructVector(String[] queryTerms) {
		// set de documents concernés
		Set<String> docs = new HashSet<>();
		Map<String, Map<String, Double>> vector = new HashMap<>();
		for (String term: queryTerms) {
			Map<String, Double> list = new HashMap<>();
			List<FoofleItem> items = dao.get(term);
			for (FoofleItem item: items) {
				Map<String, Double> map = vector.get(item.getLink());
				if (map == null) map = new HashMap<>();
				switch(FoofleConfig.PONDERATION) {
				case NUM_OCCURS :
					map.put(term, (double) item.getOccur());
					break;
				case TF_IDF:
					map.put(term, (double) item.getTfidf());
					break;
				case ROBERTSON_TF:
					map.put(term, (double) item.getRobertsonTF());
				}
				vector.put(item.getLink(), map);
				docs.add(item.getLink());
			}
		}
		Map<String, List<Double>> convertedVector = new HashMap<>();
		Iterator<String> it = docs.iterator();
		while(it.hasNext()) {
			String link = it.next();
			List<Double> list = new ArrayList<>();
			Map<String, Double> savedList = vector.get(link);
			for (String term: queryTerms) {
				Double occur = savedList.get(term);
				if (occur == null) list.add(0.0);
				else list.add(occur);
			}
			convertedVector.put(link, list);
		}
		return convertedVector;	
	}
	
	public static void main(String[] args) {
		FoofleMatching foo = new FoofleMatching();
		Map<String, Double> res = foo.mesureCosinus("Adama Intouchables");
		FoofleUtils.printVector(res);
	}
}
