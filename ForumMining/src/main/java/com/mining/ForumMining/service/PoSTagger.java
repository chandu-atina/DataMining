package com.mining.ForumMining.service;

/*
 * ##############################$History Card$###################################
 * ### Latest changes description should be on the top of the history card list###
 * ###############################################################################
 *  Created Date	Updated Date	Author			Change Description
 *  ============	============	============	===================
 *  28/05/2015		29/05/2015		chandu-atina 	Initial basic version
 */
import com.mining.ForumMining.exception.ClusterServiceException;

/**
 * 
 * @author chandrasekhara
 *
 * PoSTagger interface is a service layer which defines services 
 * to apply parts of speech tagging for the documents
 * store on a file system or passed as an input string.
 * 
 */
public interface PoSTagger {
	
	/**
	 * 
	 * @param content - String to be tagged
	 * @return tagged String
	 * @throws ClusterServiceException
	 */
	public String tagContent(String content) throws ClusterServiceException;
	
	/**
	 * 
	 * @param docLocation - Location where the documents are stored
	 * @return true/false based on tagging output
	 * @throws ClusterServiceException
	 * <br/><br/>
	 * @Description The services takes the docLocation as the base location and
	 * search for all documents recursively in all the directories
	 * inside base location and applies POS tags to each and every
	 * word in the document.
	 */
	public boolean tagDocuments(String docLocation) throws ClusterServiceException;

	/**
	 * 
	 * @return true/false based on tagging output
	 * @throws ClusterServiceException
	 * <br/><br/>
	 * @Description The services takes the default location from properties
	 * file as the base location and search for all documents 
	 * recursively in all the directories inside base location
	 * and applies POS tags to each and every
	 * word in the document.
	 */
	public boolean tagDocuments() throws ClusterServiceException;
}
