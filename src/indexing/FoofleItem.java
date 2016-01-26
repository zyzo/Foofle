package indexing;
public class FoofleItem {
    private String term;
    private String link;
    private int occur;
    private float tfidf;
	private double robertsonTF;
	private double normalizedTF;
	// html pondération
	private double htmlp;
	private double customrobertson;

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getOccur() {
        return occur;
    }

    public void setOccur(int occur) {
        this.occur = occur;
    }

	public float getTfidf() {
		return tfidf;
	}

	public void setTfidf(float tfidf) {
		this.tfidf = tfidf;
	}
	
	public String toString() {
		return "[FoofleItem " + link + ", " + occur + ", " + tfidf + "]";
	}

	public double getRobertsonTF() {
		return robertsonTF;
	}

	public void setRobertsonTF(double robertsonTF) {
		this.robertsonTF = robertsonTF;
	}

	public double getNormalizedTF() {
		return normalizedTF;
	}

	public void setNormalizedTF(double normalizedTF) {
		this.normalizedTF = normalizedTF;
	}

	public Double getHtmlp() {
		return this.htmlp;
	}

	public void setHTMLP(double htmlp) {
		this.htmlp = htmlp;
	}

	public void setCustomRobertsonTF(double customrobertson) {
		this.customrobertson = customrobertson;
	}

	public double getCustomRobertsonTF() {
		return customrobertson;
	}

}
