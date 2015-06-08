package com.mining.ForumMining.core;

import java.io.Serializable;

/**
 * @author chandrasekhara
 *
 */
public class CorpusValue implements Comparable<CorpusValue>, Serializable {

	private static final long serialVersionUID = 1L;

	private int value = 1; // no.of times term appears in current document
	
	private double tf;
	
	private double idf;
	
	private double tfidf;
	
	private int dft; //the number of documents in which term appears
	
	private int totalDocCount; //Total no.of docuemnts count
	
	private int totalWordCount; //Total no.of words in current document

	
	public CorpusValue() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CorpusValue(int totalDocCount) {
		super();
		this.totalDocCount = totalDocCount;
		this.dft=1;
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
		if(totalDocCount !=0){
			corpusValue.append(" total_Doc_Count:"+totalDocCount);
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
	
	public double getTf() {
		return tf;
	}

	public void setTf(double tf) {
		this.tf = tf;
	}

	public double getIdf() {
		return idf;
	}

	public void setIdf(double idf) {
		this.idf = idf;
	}

	public double getTfidf() {
		return tfidf;
	}

	public void setTfidf(double tfidf) {
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
	public void incrementDFT(){
		this.dft++;
	}
}
