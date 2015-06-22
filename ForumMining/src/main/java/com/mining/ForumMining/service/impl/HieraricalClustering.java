/*
 * ##############################$History Card$###################################
 * ### Latest changes description should be on the top of the history card list###
 * ###############################################################################
 *  Created Date	Updated Date	Author			Change Description
 *  ============	============	============	===================
 *  19/06/2015		29/05/2015		chandu-atina 	Initial basic version
 */
package com.mining.ForumMining.service.impl;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apporiented.algorithm.clustering.AverageLinkageStrategy;
import com.apporiented.algorithm.clustering.Cluster;
import com.apporiented.algorithm.clustering.ClusteringAlgorithm;
import com.apporiented.algorithm.clustering.DefaultClusteringAlgorithm;
import com.crawl.web.util.ApplicationProperties;
import com.mining.ForumMining.exception.ClusterServiceException;
import com.mining.ForumMining.service.ClusterService;
import com.mining.ForumMining.util.MiningUtils;

/**
 * 
 * @author chandrasekhara
 *
 *         HieraricalClustering provides service to perform hierarical
 *         clustering of dissimilarity matrix
 */
@Service("HieraricalClustering")
public class HieraricalClustering implements ClusterService {

	final static Logger log = Logger.getLogger(HieraricalClustering.class);

	@Autowired
	ApplicationProperties appProp;

	@Autowired
	MiningUtils miningUtils;

	/**
	 * Perform clustering on input matrix
	 */
	public void doCluster(double[][] cosineMatrix)
			throws ClusterServiceException {

		List<Path> files = new ArrayList<Path>();
		String[] str = new String[cosineMatrix[0].length];
		Path path = Paths.get(appProp.getMailLocation());

		miningUtils.listFiles(path, files);
		int i = 0;
		for (Path filePath : files) {
			str[i] = filePath.toString();
			i++;
		}
		Map<String, String> name = new LinkedHashMap<String, String>();

		for (int j = 0; j < str.length; j++) {
			name.put("doc" + (j + 1), str[j]);
			str[j] = "doc" + (j + 1);
		}
		log.info(name);
		ClusteringAlgorithm alg = new DefaultClusteringAlgorithm();
		Cluster cluster = alg.performClustering(cosineMatrix, str,
				new AverageLinkageStrategy());

		miningUtils.printClusterHierarchy(2, cluster);
	}

}
