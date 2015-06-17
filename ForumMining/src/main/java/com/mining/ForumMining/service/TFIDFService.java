package com.mining.ForumMining.service;

/*
 * ##############################$History Card$###################################
 * ### Latest changes description should be on the top of the history card list###
 * ###############################################################################
 *  Created Date	Updated Date	Author			Change Description
 *  ============	============	============	===================
 *  28/05/2015		29/05/2015		chandu-atina 	Initial basic version
 */
import java.util.Map;

import com.mining.ForumMining.core.CorpusValue;
import com.mining.ForumMining.exception.ClusterServiceException;

/**
 * 
 * @author chandrasekhara
 *
 *         TFIDFService interface is a service layer which exposes services to
 *         calculate term frequency, inverse document frequency, tfidf weight,
 *         document vector and dft
 * 
 */
public interface TFIDFService {

	/**
	 * 
	 * @param keyValueMap
	 *            - Key Value Map
	 * @throws ClusterServiceException
	 * <br/>
	 * <br/>
	 * @Description calculates the term frequency for each and every term in the
	 *              map
	 */
	public void calculateTF(Map<String, CorpusValue> keyValueMap)
			throws ClusterServiceException;

	/**
	 * 
	 * @param keyValueMap
	 *            - Key Value Map
	 * @throws ClusterServiceException
	 * <br/>
	 * <br/>
	 * @Description calculates the inverse document frequency for each and every
	 *              term in the map
	 */
	public void calculateIDF(Map<String, CorpusValue> keyValueMap)
			throws ClusterServiceException;

	/**
	 * 
	 * @param keyValueMap
	 *            - Key Value map
	 * @param globalCorpa
	 *            - Key Value map for global corpus
	 * @return tfidf value for each and very term in <em>keyValueMap</em>
	 * @throws ClusterServiceException
	 */
	public Map<String, Double> calculateTFIDF(
			Map<String, CorpusValue> keyValueMap,
			Map<String, CorpusValue> globalCorpa)
			throws ClusterServiceException;

	/**
	 * 
	 * @param keyValueMap
	 *            - Key Value map
	 * @param globalCorpa
	 *            - Key Value map for global corpus
	 * @throws ClusterServiceException
	 * <br/>
	 * <br/>
	 * @Description Calculates dft(no.of documents in which term 't' appears)
	 *              for each and very term in <em>keyValueMap</em>
	 */
	public void calculateDFT(Map<String, CorpusValue> keyValueMap,
			Map<String, CorpusValue> globalCorpa)
			throws ClusterServiceException;

	/**
	 * 
	 * @param keyValueMap
	 *            - Key Value map
	 * @param globalCorpa
	 *            - Key Value map for global corpus
	 * @throws ClusterServiceException
	 * <br/>
	 * <br/>
	 * @Description Calculates the document vector for the given Map
	 */
	public void calculateDocumentVector(Map<String, CorpusValue> keyValueMap,
			Map<String, CorpusValue> globalCorpa)
			throws ClusterServiceException;

}
