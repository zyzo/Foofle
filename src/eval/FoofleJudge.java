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

public class FoofleJudge {
	public final String[] QRELS = {
			"personnes Intouchables",
			"lieu naissance Omar Sy",
			"personnes récompensées Intouchables",
			"palmarès Globes de Cristal 2012",
			"membre jury Globes de Cristal 2012",
			"prix Omar Sy Globes de Cristal 2012",
			"lieu Globes Cristal 2012",
			"prix Omar Sy",
			"acteurs joué avec Omar Sy"
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
		for (String qrel: QRELS) {
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
				if (currentEval.containsKey(document))
					judgeValue += currentEval.get(document);
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

	public static void main(String[] args) throws IOException, ParseException, FoofleJudgeException {
		FoofleJudge judge = new FoofleJudge();
		System.out.println("\n==== Q5 ===== \n");
		double[] judge5 = judge.judge(5);
		print(judge5);
		System.out.println(FoofleUtils.doubleToString(avg(judge5)));
		System.out.println("\n==== Q10 ===== \n");
		double[] judge10 = judge.judge(10);
		print(judge10);
		System.out.println(FoofleUtils.doubleToString(avg(judge10)));
		System.out.println("\n==== Q25 ===== \n");
		double[] judge25 = judge.judge(25);
		print(judge25);
		System.out.println(FoofleUtils.doubleToString(avg(judge25)));
	}
}
