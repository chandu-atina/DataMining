package com.mining.ForumMining.service;

import com.mining.ForumMining.exception.ClusterServiceException;

public interface ClusterService {

	public void doCluster(double[][] cosineMatrix)
			throws ClusterServiceException;

}
