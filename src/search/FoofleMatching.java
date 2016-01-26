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
import select.Evaluation;
import select.FoofleConfig;
import select.Ponderation;



public class FoofleMatching {

	private SqlDAO dao;
	
	public FoofleMatching() {
		dao = SqlDAO.getInstance();
	}
	
	public Map<String, Double> mesure(String[] query) {
		//String[] queryTerms = query.split(" ");
		List<Double> queryWeights = new ArrayList<>(query.length);
		for (int i = 0; i < query.length; i++) queryWeights.add(1.0);
		Map<String, List<Double>> vector = constructVector(query);
		Map<String, Double> res = new HashMap<>();
		
		for (Entry<String, List<Double>> entry: vector.entrySet()) {
			res.put(entry.getKey(), modeleVectoriel(queryWeights, entry.getValue()));
		}
		return res;
	}
	
	private double modeleVectoriel(List<Double> l1, List<Double> l2) {
		switch (FoofleConfig.EVALUATION) {
		case MESURE_COSINUS:
			return modeleCosinus(l1, l2);
		case PRODUIT_SCALAIRE:
			return modeleScalaire(l1, l2);
		case DICE:
			return modeleDice(l1, l2);
		case JACCARD:
			return modeleJaccard(l1,l2);
		default:
			throw new RuntimeException();
		}
	}
	
	private double modeleJaccard(List<Double> l1, List<Double> l2) {
		double result = 0;
		double sumXnY = 0;
		double sqrtX = 0;
		double sqrtY = 0;
		for (int i = 0; i < l1.size(); i++) {
			sumXnY += l1.get(i)*l2.get(i);
			sqrtX += l1.get(i)*l1.get(i);
			sqrtY += l2.get(i)*l2.get(i);
		}
		return sumXnY / (sqrtX + sqrtY - sumXnY);
	}

	private double modeleDice(List<Double> l1, List<Double> l2) {
		double result = 0;
		double sumXnY = 0;
		double sqrtX = 0;
		double sqrtY = 0;
		for (int i = 0; i < l1.size(); i++) {
			sumXnY += l1.get(i)*l2.get(i);
			sqrtX += l1.get(i)*l1.get(i);
			sqrtY += l2.get(i)*l2.get(i);
		}
		return 2*sumXnY/(sqrtX+sqrtY);
	}

	private double modeleScalaire(List<Double> l1, List<Double> l2) {
		double result = 0;
		for (int i = 0; i < l1.size(); i++) {
			result += l1.get(i)*l2.get(i);
		}
		return result;
	}

	// sum(XnY)/(sqrt(X)*sqrt(Y)
	private double modeleCosinus(List<Double> l1, List<Double> l2) {
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
				map.put(term, FoofleUtils.getEvaluation(item));
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
		FoofleConfig.PONDERATION = Ponderation.CUSTOM_ROBERTSON_TF;
		FoofleConfig.EVALUATION = Evaluation.PRODUIT_SCALAIRE;
		FoofleMatching foo = new FoofleMatching();
		Map<String, Double> res = foo.mesure(new String[]{"personnes","Intouchables"});
		FoofleUtils.printVector(res);
	}
}
