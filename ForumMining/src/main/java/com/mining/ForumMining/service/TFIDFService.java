package com.mining.ForumMining.service;

import java.util.Map;

import com.mining.ForumMining.core.CorpusValue;
import com.mining.ForumMining.exception.ClusterServiceException;

public interface TFIDFService {

	public void TFCalculation(Map<String,CorpusValue> keyValueMap) throws ClusterServiceException;
	
	public void IDFCalculation(Map<String,CorpusValue> keyValueMap) throws ClusterServiceException;
	
	public void TFIDFCalculation(Map<String,CorpusValue> keyValueMap) throws ClusterServiceException;
	
}
