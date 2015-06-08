package com.mining.ForumMining.service.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.stereotype.Service;

import com.mining.ForumMining.core.CorpusValue;
import com.mining.ForumMining.exception.ClusterServiceException;
import com.mining.ForumMining.service.TFIDFService;

@Service
public class TFIDFServiceImpl implements TFIDFService {

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

	public void calculateIDF(Map<String, CorpusValue> keyValueMap)
			throws ClusterServiceException {
		// TODO Auto-generated method stub

	}

	public void calculateDFT(Map<String, CorpusValue> keyValueMap,
			Map<String, CorpusValue> globalCorpa)
			throws ClusterServiceException {

		for (CorpusValue corpus : keyValueMap.values()) {
			corpus.setTfidf(corpus.getTf()
					* (1 + Math.log(corpus.getTotalDocCount() - corpus.getDft())));
		}
	}

	public void calculateTFIDF(Map<String, CorpusValue> keyValueMap,
			Map<String, CorpusValue> globalCorpa)
			throws ClusterServiceException {
		Iterator<Map.Entry<String, CorpusValue>> it = keyValueMap.entrySet()
				.iterator();
		while (it.hasNext()) {
			Entry<String, CorpusValue> entry = it.next();
			entry.getValue().setDft(globalCorpa.get(entry.getKey()).getDft());
			entry.getValue().setTfidf(entry.getValue().getTf()
					* (1 + Math.log(entry.getValue().getTotalDocCount() /entry.getValue().getDft())));
			
		}
	}

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

}
