package judge;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import service.FoofleMatching;
import service.FoofleSearch;

public class FoofleJudge {
	private final NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
	
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
	            Number num = format.parse(split[1]);
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
			System.out.println("Result for '" + qrel + "': " + QRELS_RESULT.get(QRELS_RESULT.size()-1).toString() );
		}
	}

	private double[] judge(int num) throws FoofleJudgeException {
		double[] judgeValues = new double[QRELS.length];
		for (int i = 0; i < QRELS.length; i++) {
			List<String> searchResult = QRELS_RESULT.get(i);
			if (searchResult == null || searchResult.size() < num) {
				throw new FoofleJudgeException("The class search result is not loaded with at least " + num + " items. (" + searchResult.toString() + ")");
			}
			double judgeValue = 0;
			Map<String, Double> currentEval = EVAL.get(i);
			for (int j = 0; j < num; j++) {
				String document = searchResult.get(j);
				System.out.println(i +" "+ j + " " + document);
				if (currentEval.containsKey(document))
					judgeValue += currentEval.get(document);
			}
			judgeValues[i] = judgeValue / num;
		}
		return judgeValues;
	}


	public static void main(String[] args) throws IOException, ParseException, FoofleJudgeException {
		FoofleJudge judge = new FoofleJudge();
		System.out.print(Arrays.toString(judge.judge(3)));
	}
}
