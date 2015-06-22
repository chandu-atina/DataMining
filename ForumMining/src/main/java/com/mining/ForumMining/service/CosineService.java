package com.mining.ForumMining.service;

/*
 * ##############################$History Card$###################################
 * ### Latest changes description should be on the top of the history card list###
 * ###############################################################################
 *  Created Date	Updated Date	Author			Change Description
 *  ============	============	============	===================
 *  28/05/2015		29/05/2015		chandu-atina 	Initial basic version
 */
import java.util.List;
import java.util.Map;

import com.mining.ForumMining.exception.ClusterServiceException;

/**
 * 
 * @author chandrasekhara
 *
 *         CosineService interface is a service layer which defines services to
 *         calculate the cosine similarity matrix for a set of documents
 * 
 */
public interface CosineService {

	/**
	 * 
	 * @param doc1
	 *            - document vector List of Double values
	 * @param doc2
	 *            - document vector List of Double values
	 * @return Double - Cosine similarity between documents
	 * @throws ClusterServiceException
	 * <br/>
	 * <br/>
	 *             @Description calculates the cosine similarity between two
	 *             document vectors
	 */
	public Double getCosineSimilarity(List<Double> doc1, List<Double> doc2)
			throws ClusterServiceException;

	/**
	 * 
	 * @param tfidfVectorList
	 *            - Document vector list
	 * @param matrixType
	 * 				i)  SIMILAR indicates similarity matrix
	 * 				ii) DISSIMILAR indicates dissimilarity matrix
	 * @throws ClusterServiceException
	 */
	public double[][] calculateCosineSimilarityMatrix(
			List<Map<String, Double>> tfidfVectorList, String matrixType)
			throws ClusterServiceException;

}
