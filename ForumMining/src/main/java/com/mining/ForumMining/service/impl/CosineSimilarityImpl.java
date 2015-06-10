package com.mining.ForumMining.service.impl;

import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.mining.ForumMining.exception.ClusterServiceException;
import com.mining.ForumMining.service.CosineService;

@Service
public class CosineSimilarityImpl implements CosineService {

	final static Logger log = Logger.getLogger(StopWordServiceImpl.class);

	public Double getCosineSimilarity(List<Double> doc1, List<Double> doc2)
			throws ClusterServiceException {
		Double vectorProduct = 0.0;
		Double doc1SquaredSum = 0.0;
		Double doc2SquaredSum = 0.0;
		Double dotProduct = 0.0;
		//long startTime = Calendar.getInstance().getTimeInMillis();
		if (doc1.size() != doc2.size()) {

			throw new ClusterServiceException("Irregular Vector Size. Doc1 : "
					+ doc1.size() + " Doc2 : " + doc2.size());
		}
		for (int i = 0; i < doc1.size(); ++i) {
			vectorProduct += doc1.get(i) * doc2.get(i);
			doc1SquaredSum += doc1.get(i) * doc1.get(i);
			doc2SquaredSum += doc2.get(i) * doc2.get(i);
		}
		dotProduct = vectorProduct
				/ (Math.sqrt(doc1SquaredSum) * Math.sqrt(doc2SquaredSum));
		//long endTime = Calendar.getInstance().getTimeInMillis();
		// log.info("Time for each cal: "+(endTime-startTime));
		return Math.round(dotProduct * 100000000.0) / 100000000.0;
	}
}
