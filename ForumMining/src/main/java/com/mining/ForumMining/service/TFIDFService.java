package com.mining.ForumMining.service;

import java.util.Map;

import com.mining.ForumMining.core.CorpusValue;
import com.mining.ForumMining.exception.ClusterServiceException;

public interface TFIDFService {

	public void calculateTF(Map<String, CorpusValue> keyValueMap)
			throws ClusterServiceException;

	public void calculateIDF(Map<String, CorpusValue> keyValueMap)
			throws ClusterServiceException;

	public void calculateTFIDF(Map<String, CorpusValue> keyValueMap,
			Map<String, CorpusValue> globalCorpa)
			throws ClusterServiceException;

	public void calculateDFT(Map<String, CorpusValue> keyValueMap,
			Map<String, CorpusValue> globalCorpa)
			throws ClusterServiceException;

}
