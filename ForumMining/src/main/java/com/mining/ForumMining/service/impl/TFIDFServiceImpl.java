package com.mining.ForumMining.service.impl;

/*
 * ##############################$History Card$###################################
 * ### Latest changes description should be on the top of the history card list###
 * ###############################################################################
 *  Created Date	Updated Date	Author			Change Description
 *  ============	============	============	===================
 *  28/05/2015		29/05/2015		chandu-atina 	Initial basic version
 */
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.mining.ForumMining.constants.MiningConstants;
import com.mining.ForumMining.core.CorpusValue;
import com.mining.ForumMining.exception.ClusterServiceException;
import com.mining.ForumMining.service.TFIDFService;

/**
 * 
 * @author chandrasekhara
 *
 * TFIDFService class is a service layer which implements services 
 * define in TDIDFService interface like term frequency, 
 * inverse document frequency, tfidf weight, document vector and dft
 * 
 */
@Service
public class TFIDFServiceImpl implements TFIDFService {
	
	final static Logger log = Logger.getLogger(TFIDFServiceImpl.class);

	/**
	 * calculates the term frequency for each and every term in the map
	 */
	public void calculateTF(Map<String, CorpusValue> keyValueMap)
			throws ClusterServiceException {

		int sum = 0;
		for (CorpusValue corpus : keyValueMap.values()) {
			sum += corpus.get();
		}
		for (CorpusValue corpus : keyValueMap.values()) {
			corpus.setTf((float) corpus.get() / sum);
			corpus.setTotalWordCount(sum);
		}
	}

	/**
	 * calculates the inverse document frequency for each and 
	 * every term in the map
	 */
	public void calculateIDF(Map<String, CorpusValue> keyValueMap)
			throws ClusterServiceException {
		// TODO Auto-generated method stub

	}

	/**
	 * Calculates dft(no.of documents in which term 't' appears)
	 * for each and very term in <em>keyValueMap</em>
	 */
	public void calculateDFT(Map<String, CorpusValue> keyValueMap,
			Map<String, CorpusValue> globalCorpa)
			throws ClusterServiceException {

		for (CorpusValue corpus : keyValueMap.values()) {
			corpus.setTfidf(corpus.getTf()
					* (1 + Math.log(corpus.getTotalDocCount() - corpus.getDft())));
		}
	}
	/*
	 * (non-Javadoc)
	 * @see com.mining.ForumMining.service.TFIDFService#calculateTFIDF(java.util.Map, java.util.Map)
	 * Calculate tf*idf value for the document and returns the document vector
	 */
	public Map<String,Double> calculateTFIDF(Map<String, CorpusValue> keyValueMap,
			Map<String, CorpusValue> globalCorpa)
			throws ClusterServiceException {
		Iterator<Map.Entry<String, CorpusValue>> it = keyValueMap.entrySet()
				.iterator();
		while (it.hasNext()) {
			Entry<String, CorpusValue> entry = it.next();
			entry.getValue().setDft(globalCorpa.get(entry.getKey()).getDft());
			entry.getValue().setTfidf(entry.getValue().getTf()
					* (1 + Math.log(entry.getValue().getTotalDocCount() /entry.getValue().getDft())));
			//TODO: Remove if block after testing
			if(entry.getValue().getTfidf()<-10){
				log.info(entry.getValue().getTfidf());
			}
			
		}
		Map<String,Double> docVector=new LinkedHashMap<String, Double>();
		
		Iterator<Map.Entry<String, CorpusValue>> globalIterator = globalCorpa.entrySet()
				.iterator();
		while (globalIterator.hasNext()) {
			Entry<String, CorpusValue> entry = globalIterator.next();
			if (entry.getValue().get() <= MiningConstants.TERM_THRESHOLD_COUNT) {
				break;
			} else {
				docVector.put(entry.getKey(),
						keyValueMap.get(entry.getKey()) == null ? 0
								: keyValueMap.get(entry.getKey()).getTfidf());
			}
		}
		return docVector;
	}

	/**
	 * 
	 * @param args
	 * main method for test purpose
	 */
	public static void main(String args[]) {
		Map<String, CorpusValue> keyValueMap = new HashMap<String, CorpusValue>();
		keyValueMap.put("apple", new CorpusValue());
		keyValueMap.put("ball", new CorpusValue());
		keyValueMap.put("cat", new CorpusValue());
		keyValueMap.put("dog", new CorpusValue());

		TFIDFServiceImpl tf = new TFIDFServiceImpl();

		tf.calculateTF(keyValueMap);

		System.out.println(keyValueMap);
	}
	
	/**
	 * To be implemented
	 */
	public void calculateDocumentVector(Map<String, CorpusValue> keyValueMap,
			Map<String, CorpusValue> globalCorpa)
			throws ClusterServiceException{
		
	}
}
