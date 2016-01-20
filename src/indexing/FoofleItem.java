package indexing;
public class FoofleItem {
    private String term;
    private String link;
    private int occur;
    private float tfidf;
	private double robertsonTF;

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
}
