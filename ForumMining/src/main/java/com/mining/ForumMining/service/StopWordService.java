package com.mining.ForumMining.service;

import com.mining.ForumMining.exception.ClusterServiceException;

public interface StopWordService {

	public void removeStopWords() throws ClusterServiceException;

	public void removeStopWords(String docLocation)
			throws ClusterServiceException;

}
