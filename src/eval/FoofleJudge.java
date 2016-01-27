package eval;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import indexing.FoofleUtils;
import search.FoofleSearch;
import select.Evaluation;
import select.FoofleConfig;
import select.Ponderation;
import sparqlclient.FoofleReformulate;
import sparqlclient.FoofleReformulate2;
import sparqlclient.FoofleReformulate2_n;

public class FoofleJudge {
	public final String[][] QRELS = {
			{"récompense", "Intouchables"}
	};
	public List<Map<String, Double>> EVAL = new ArrayList<>();

	private List<List<String>> QRELS_RESULT;
	
	public FoofleJudge() {
		try {
			initEval();
		} catch (IOException | ParseException e) {
			System.err.println("Problem inititiating EVAL");
			e.printStackTrace();
			return;
		}
		initResult();
	}
	
	private void initEval() throws FileNotFoundException, IOException, ParseException {
		for (int i = 1; i <= QRELS.length; i++) {
			BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(
                                new File("qrels/qrelQ" + i + ".txt"))));
	        String line;
	        Map<String, Double> map = new HashMap<>();
	        while((line = reader.readLine()) != null) {
	            String[] split = line.split("\t");
	            Number num = FoofleUtils.stringToNum(split[1]);
	            map.put(split[0], num.doubleValue());
	        }
	        EVAL.add(i-1, map);
	        reader.close();
		}
	}

	private void initResult() {
		QRELS_RESULT = new ArrayList<>();

		// reformulate queries
		String[][] converts = new String[QRELS.length][];
		if (FoofleConfig.WITH_REFORMULATE) {
			for (int i = 0; i < QRELS.length; i++) {
				String[] qrel = QRELS[i];
				List<String> enriched_qrels = FoofleReformulate2.strategy2(qrel);
				String[] convert = new String[enriched_qrels.size()];
				enriched_qrels.toArray(convert);
				enriched_qrels = FoofleReformulate.strategy1(convert);
				convert = new String[enriched_qrels.size()];
				enriched_qrels.toArray(convert);/*
				enriched_qrels = FoofleReformulate2_n.strategy3(convert);
				convert = new String[enriched_qrels.size()];
				enriched_qrels.toArray(convert);
				enriched_qrels = FoofleReformulate.strategy1(convert);
				convert = new String[enriched_qrels.size()];
				enriched_qrels.toArray(convert);*/
				//System.out.println(enriched_qrels.toString());
				converts[i] = convert;
			}
		} else {
			converts = QRELS;
		}
		for (String[] qrel: converts) {
			QRELS_RESULT.add(FoofleSearch.search(qrel));
		}
	}

	private double[] judge(int num) throws FoofleJudgeException {
		double[] judgeValues = new double[QRELS.length];
		for (int i = 0; i < QRELS.length; i++) {
			List<String> searchResult = QRELS_RESULT.get(i);
			if (searchResult == null || searchResult.size() < num) {
				System.err.println("The class search result is not loaded with at least " + num + " items. (" + searchResult.toString() + ")");
			}
			double judgeValue = 0;
			Map<String, Double> currentEval = EVAL.get(i);
			for (int j = 0; j < searchResult.size() && j < num; j++) {
				String document = searchResult.get(j);
				if (currentEval.containsKey(document) && currentEval.get(document) >= 0.5)
					judgeValue += 1;
			}
			judgeValues[i] = judgeValue / num;
		}
		return judgeValues;
	}
	
	public static double avg(double[] judge5) {
		double result = 0;
		for (Double d: judge5) {
			result += d;
		}
		return result/judge5.length;
	}

	public static void print(double[] array) {
		for (Double d: array) {
			System.out.println(FoofleUtils.doubleToString(d));
		}
	}

	public static void runJudge() throws IOException, ParseException, FoofleJudgeException {
		FoofleJudge judge = new FoofleJudge();
		//System.out.println("\n==== Q5 ===== \n");
		double[] judge5 = judge.judge(5);
		//print(judge5);
		//System.out.println(FoofleUtils.doubleToString(avg(judge5)));
		//System.out.println("\n==== Q10 ===== \n");
		double[] judge10 = judge.judge(10);
		//print(judge10);
		//System.out.println(FoofleUtils.doubleToString(avg(judge10)));
		//System.out.println("\n==== Q25 ===== \n");
		double[] judge25 = judge.judge(25);
		//print(judge25);
		//System.out.println(FoofleUtils.doubleToString(avg(judge25)));
		//System.out.println("Max étudiants   : 0.89  0.74  0.72");
		System.out.println(
				FoofleUtils.doubleToString(avg(judge5)) + " " +
				FoofleUtils.doubleToString(avg(judge10)) + " " +
				FoofleUtils.doubleToString(avg(judge25)));
	}
	
	public static void main(String[] args) throws IOException, ParseException, FoofleJudgeException {
		int size = 50;
		for (Ponderation p : Ponderation.values()) {
			for (Evaluation e: Evaluation.values()) 
			{
				FoofleConfig.PONDERATION = p;
				FoofleConfig.EVALUATION = e;
				int restant = size - FoofleConfig.PONDERATION.name().length() - FoofleConfig.EVALUATION.name().length();
				String toInsert = "";
				for (int i = 0; i < restant; i++) toInsert += " ";
				System.out.print(FoofleConfig.PONDERATION.name() + "-" + FoofleConfig.EVALUATION.name() + toInsert);
				runJudge();
			}
		}
	}
}
