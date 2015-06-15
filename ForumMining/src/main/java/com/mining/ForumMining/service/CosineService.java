package com.mining.ForumMining.service;

import java.util.List;
import java.util.Map;

import com.mining.ForumMining.exception.ClusterServiceException;

public interface CosineService {

	public Double getCosineSimilarity(List<Double> doc1, List<Double> d2)
			throws ClusterServiceException;

	public void calculateCosineSimilarityMatrix(
			List<Map<String, Double>> tfidfVectorList)
			throws ClusterServiceException;

}
