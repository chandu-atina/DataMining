package com.mining.ForumMining.core;

import java.io.Serializable;

/**
 * @author chandrasekhara
 *
 */
public class CorpusValue implements Comparable<CorpusValue>, Serializable {

	private static final long serialVersionUID = 1L;

	int value = 1; // no.of times term appears in current document
	
	float tf;
	
	float idf;
	
	float tfidf;
	
	int dft; //the number of documents in which term appears
	
	int totalDocCount; //Total no.of docuemnts count
	
	int totalWordCount; //Total no.of words in current document

	
	public CorpusValue() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CorpusValue(int totalDocCount) {
		super();
		this.totalDocCount = totalDocCount;
	}

	public void increment() {
		++value;
	}

	public int get() {
		return value;
	}

	public String toString() {
		
		StringBuilder corpusValue=new StringBuilder("[ value:"+String.valueOf(value));
		if(dft!=0){
			corpusValue.append(" dft:"+dft);
		}
		if(tf!=0){
			corpusValue.append(" docFreqency:"+tf);
		}
		if(idf!=0){
			corpusValue.append(" inverseDocFrequency:"+idf);
		}
		if(tfidf!=0){
			corpusValue.append(" tfidf:"+tfidf);
		}
		corpusValue.append(" ]");
		return corpusValue.toString();
	}

	public int compareTo(CorpusValue o) {
		if (o.get() > this.get())
			return 1;
		else if (o.get() == this.get())
			return 0;
		else
			return -1;
	}
	
	public float getTf() {
		return tf;
	}

	public void setTf(float tf) {
		this.tf = tf;
	}

	public float getIdf() {
		return idf;
	}

	public void setIdf(float idf) {
		this.idf = idf;
	}

	public float getTfidf() {
		return tfidf;
	}

	public void setTfidf(float tfidf) {
		this.tfidf = tfidf;
	}

	public int getDft() {
		return dft;
	}

	public void setDft(int dft) {
		this.dft = dft;
	}

	public int getTotalDocCount() {
		return totalDocCount;
	}

	public void setTotalDocCount(int totalDocCount) {
		this.totalDocCount = totalDocCount;
	}

	public int getTotalWordCount() {
		return totalWordCount;
	}

	public void setTotalWordCount(int totalWordCount) {
		this.totalWordCount = totalWordCount;
	}
	
}
