package com.mining.ForumMining.service.impl;

/*
 * ##############################$History Card$###################################
 * ### Latest changes description should be on the top of the history card list###
 * ###############################################################################
 *  Created Date	Updated Date	Author			Change Description
 *  ============	============	============	===================
 *  28/05/2015		29/05/2015		chandu-atina 	Initial skeleton
 */
import org.springframework.stereotype.Service;

import com.mining.ForumMining.exception.ClusterServiceException;
import com.mining.ForumMining.service.PoSTagger;

/**
 * StanfordPoSTagger class has a few service methods that are helpful to tag the
 * document with their appropriate parts of speech. The class internally uses
 * Stanford PoS Tagger implementation.
 */
@Service("StanfordPoSTagger")
public class StanfordPoSTagger implements PoSTagger {

	public String tagContent(String content) throws ClusterServiceException {
		return new String("StanfordPoSTagger");
	}

	public boolean tagDocuments(String docLocation)
			throws ClusterServiceException {
		return true;
	}

	public boolean tagDocuments() throws ClusterServiceException {
		return true;
	}
}
