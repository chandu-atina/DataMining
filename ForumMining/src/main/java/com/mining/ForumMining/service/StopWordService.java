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

import com.mining.ForumMining.core.CorpusValue;
import com.mining.ForumMining.exception.ClusterServiceException;

/**
 * 
 * @author chandrasekhara
 *
 * StopWordService class is a service layer which exposes services 
 * to remove stop words from the list of documents.
 * 
 */
public interface StopWordService {

	/**
	 * 
	 * @param docLocation - Location where the documents are stored
	 * @param globalCorpus - Global set of words from all documents
	 * @throws ClusterServiceException
	 * <br/><br/>
	 * @Description The methods takes docLocation as input and applies the 
	 * following set of actions to each and every document.
	 * <br/><em>1. Removes all words expect words mentioned
	 * in static POSTAG_LIST</em>
	 * <br/><em>2. Apply Stemming & Lemmatization for the 
	 * remaining set of words in each and every document</em>
	 * <br/><em>3. Calculate the word count for each lemma for 
	 * all documents</em>
	 * <br/> <em>4. Update the Global Corpus from all the documents</em>
	 * 
	 */
	public void removeStopWords(String docLocation, Map<String, CorpusValue> globalCorpus)
			throws ClusterServiceException;
	
	/**
	 * 
	 * @param globalCorpus - Global set of words from all documents
	 * @throws ClusterServiceException
	 * <br/><br/>
	 * @Description The methods takes default docLocation from properties file
	 *  as input and applies the 
	 * following set of actions to each and every document.
	 * <br/><em>1. Removes all words expect words mentioned
	 * in static POSTAG_LIST</em>
	 * <br/><em>2. Apply Stemming & Lemmatization for the 
	 * remaining set of words in each and every document</em>
	 * <br/><em>3. Calculate the word count for each lemma for 
	 * all documents</em>
	 * <br/> <em>4. Update the Global Corpus from all the documents</em>
	 * 
	 */
	public List<Map<String, CorpusValue>> removeStopWords(Map<String, CorpusValue> globalCorpus) throws ClusterServiceException;
	
	public Map<String, CorpusValue> sortByValue(Map<String, CorpusValue> map) ;

}
