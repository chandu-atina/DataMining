package com.mining.ForumMining.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.mining.ForumMining.core.CorpusValue;
import com.mining.ForumMining.exception.ClusterServiceException;
import com.mining.ForumMining.service.TFIDFService;

@Service
public class TFIDFServiceImpl implements TFIDFService{

	public void TFCalculation(Map<String, CorpusValue> keyValueMap)
			throws ClusterServiceException {
		
		int sum=0;
		for (CorpusValue corpus : keyValueMap.values()) {
		    sum += corpus.get();
		}
		
		for (CorpusValue corpus : keyValueMap.values()) {
		    corpus.setTf((float)corpus.get()/sum);
		    corpus.setTotalWordCount(sum);
		}
		
	}

	public void IDFCalculation(Map<String, CorpusValue> keyValueMap)
			throws ClusterServiceException {
		// TODO Auto-generated method stub
		
	}

	public void TFIDFCalculation(Map<String, CorpusValue> keyValueMap)
			throws ClusterServiceException {
		// TODO Auto-generated method stub
		
	}
	
	public static void main(String args[]){
		Map<String, CorpusValue> keyValueMap=new HashMap<String, CorpusValue>();
		keyValueMap.put("apple", new CorpusValue());
		keyValueMap.put("ball", new CorpusValue());
		keyValueMap.put("cat", new CorpusValue());
		keyValueMap.put("dog", new CorpusValue());
		
		TFIDFServiceImpl tf= new TFIDFServiceImpl();
		
		tf.TFCalculation(keyValueMap);
		
		System.out.println(keyValueMap);
	}

}
