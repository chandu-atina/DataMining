package com.mining.ForumMining.service;

import java.util.List;
import java.util.Map;

import com.mining.ForumMining.core.CorpusValue;
import com.mining.ForumMining.exception.ClusterServiceException;

public interface StopWordService {

	//public void removeStopWords() throws ClusterServiceException;

	public void removeStopWords(String docLocation, Map<String, CorpusValue> globalCorpus)
			throws ClusterServiceException;
	
	public List<Map<String, CorpusValue>> removeStopWords(Map<String, CorpusValue> globalCorpus) throws ClusterServiceException;
	
	public Map<String, CorpusValue> sortByValue(Map<String, CorpusValue> map) ;

}
