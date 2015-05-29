package com.mining.ForumMining.service;

import com.mining.ForumMining.exception.ClusterServiceException;

public interface PoSTagger {
	
	public String tagContent(String content) throws ClusterServiceException;
	
	public boolean tagDocuments(String docLocation) throws ClusterServiceException;

	public boolean tagDocuments() throws ClusterServiceException;
}
