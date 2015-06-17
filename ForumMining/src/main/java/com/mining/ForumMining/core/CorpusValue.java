package com.mining.ForumMining.core;

/*
 * ##############################$History Card$###################################
 * ### Latest changes description should be on the top of the history card list###
 * ###############################################################################
 *  Created Date	Updated Date	Author			Change Description
 *  ============	============	============	===================
 *  28/05/2015		29/05/2015		chandu-atina 	Initial basic version
 */
import java.io.Serializable;

/**
 * 
 * @author chandrasekhara
 * 
 *         CorpusValue holds the core values like word frequency, total no.of
 *         words term frequency, inverse document frequency and other core
 *         values for each and every document.
 *
 */
public class CorpusValue implements Comparable<CorpusValue>, Serializable {

	private static final long serialVersionUID = 1L;

	private int value = 1; // no.of times term appears in current document

	private double tf;

	private double idf;

	private double tfidf;

	private int dft; // the number of documents in which term appears

	private int totalDocCount; // Total no.of documents count

	private int totalWordCount; // Total no.of words in current document

	public CorpusValue() {
		super();
	}

	/**
	 * overloaded constructor which initializes the object with total document
	 * count and default dft
	 */
	public CorpusValue(int totalDocCount) {
		super();
		this.totalDocCount = totalDocCount;
		this.dft = 1;
	}

	/**
	 * increments the value field by 1
	 */
	public void increment() {
		++value;
	}

	/**
	 * @returns the value field
	 */
	public int get() {
		return value;
	}

	/**
	 * @returns the concatenated string of current object snapshot
	 */
	public String toString() {

		StringBuilder corpusValue = new StringBuilder("[ value:"
				+ String.valueOf(value));
		if (dft != 0) {
			corpusValue.append(" dft:" + dft);
		}
		if (tf != 0) {
			corpusValue.append(" docFreqency:" + tf);
		}
		if (idf != 0) {
			corpusValue.append(" inverseDocFrequency:" + idf);
		}
		if (tfidf != 0) {
			corpusValue.append(" tfidf:" + tfidf);
		}
		if (totalDocCount != 0) {
			corpusValue.append(" total_Doc_Count:" + totalDocCount);
		}
		corpusValue.append(" ]");
		return corpusValue.toString();
	}

	/**
	 * @compares the two objects based on the value field
	 */
	public int compareTo(CorpusValue o) {
		if (o.get() > this.get())
			return 1;
		else if (o.get() == this.get())
			return 0;
		else
			return -1;
	}

	/**
	 * @returns term frequency value tf = current word count / no.of words in
	 *          document
	 */
	public double getTf() {
		return tf;
	}

	/**
	 * @param tf
	 *            sets the term frequency value
	 */
	public void setTf(double tf) {
		this.tf = tf;
	}

	/**
	 * @returns inverse document frequency value
	 */
	public double getIdf() {
		return idf;
	}

	/**
	 * @param idf
	 *            sets the inverse document frequency value
	 */
	public void setIdf(double idf) {
		this.idf = idf;
	}

	/**
	 * @returns tfidf value for the term
	 */
	public double getTfidf() {
		return tfidf;
	}

	/**
	 * @param tfidf
	 *            sets the tfidf value for the term
	 */
	public void setTfidf(double tfidf) {
		this.tfidf = tfidf;
	}

	/**
	 * @returns dft value for the term dft- the number of documents in which
	 *          term appears
	 */
	public int getDft() {
		return dft;
	}

	/**
	 * @param dft
	 *            sets the dft value for the term
	 */
	public void setDft(int dft) {
		this.dft = dft;
	}

	/**
	 * @returns totalDocCount value in the global corpus
	 */
	public int getTotalDocCount() {
		return totalDocCount;
	}

	/**
	 * @param totalDocCount
	 *            sets the totalDocCount value
	 */
	public void setTotalDocCount(int totalDocCount) {
		this.totalDocCount = totalDocCount;
	}

	/**
	 * @returns totalWordCount in the document
	 */
	public int getTotalWordCount() {
		return totalWordCount;
	}

	/**
	 * @param totalWordCount
	 *            sets the totalWordCount
	 */
	public void setTotalWordCount(int totalWordCount) {
		this.totalWordCount = totalWordCount;
	}

	/**
	 * increment the dft value by 1
	 */
	public void incrementDFT() {
		this.dft++;
	}
}
