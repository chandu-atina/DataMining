package com.mining.ForumMining.service.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.crawl.web.util.messages.ErrorMessage;
import com.mining.ForumMining.constants.MiningConstants;
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
		return Math.round(dotProduct * 100000000.0) / 100000000.0;
	}

	public void calculateCosineSimilarityMatrix(
			List<Map<String, Double>> tfidfVectorList)
			throws ClusterServiceException {

		log.info("Staring calculation of cosine similarity matrix !!!");
		long startTime = Calendar.getInstance().getTimeInMillis();
		List<Integer> threadLoadBalance = new ArrayList<Integer>();

		int operationsPerThread = tfidfVectorList.size()
				* tfidfVectorList.size() / 20;

		Double[][] cosineMatrix = new Double[tfidfVectorList.size()][tfidfVectorList
				.size()];

		/* Creating and executor instance */
		ExecutorService es = Executors.newCachedThreadPool();
		try {
			for (int i = MiningConstants.MAX_THREAD_COUNT; i >= 1; i--) {
				threadLoadBalance.add((int) Math.round(Math
						.sqrt(operationsPerThread * 2 * i)
						- Math.sqrt(operationsPerThread * 2 * (i - 1))));
			}
			int prevEnd = -1;
			for (int i = 0; i < MiningConstants.MAX_THREAD_COUNT; i++) {

				// cosineMatrix=es.submit(new
				// CosineCalculationThread(cosineMatrix,
				// filePaths,counter)).get();
				es.execute(new CosineCalculationTask(cosineMatrix,
						tfidfVectorList, prevEnd + 1, prevEnd
								+ threadLoadBalance.get(i)));
				prevEnd += threadLoadBalance.get(i);
			}

			es.shutdown();
			boolean finshed = es.awaitTermination(1, TimeUnit.HOURS);

			log.info("Executor Flag :" + finshed);
			String filePathString = "/var/tmp/cosineMatrix.txt";
			File file = new File(filePathString);
			file.getParentFile().mkdirs();
			file.createNewFile();

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(Arrays.deepToString(cosineMatrix));
			bw.close();
			long endTime = Calendar.getInstance().getTimeInMillis();
			log.info("end of cosine similarity.Time : " + (endTime - startTime));
		} catch (Exception e) {

		}
	}

	/**
	 * Inner class for threa creation
	 */
	public class CosineCalculationTask implements Runnable {

		private Double[][] cosineMatrix;
		private List<Map<String, Double>> tfidfVectorList;
		private Integer startIndex;
		private Integer endIndex;

		public CosineCalculationTask(Double[][] cosineMatrix,
				List<Map<String, Double>> tfidfVectorList, Integer startIndex,
				Integer endIndex) {
			super();
			this.cosineMatrix = cosineMatrix;
			this.tfidfVectorList = tfidfVectorList;
			this.startIndex = startIndex;
			this.endIndex = endIndex;
		}

		public void run() {
			try {
				log.info(Thread.currentThread().getName() + " Started");
				calculateCosineSimilarity(startIndex, endIndex, cosineMatrix,
						tfidfVectorList);
				log.info(Thread.currentThread().getName() + " finished its job");
			} catch (Exception e) {
				throw new ClusterServiceException(new ErrorMessage(
						e.getMessage(), e.getCause()));
			}
		}

		public Double[][] call() throws Exception {
			try {
				calculateCosineSimilarity(startIndex, endIndex, cosineMatrix,
						tfidfVectorList);
				return cosineMatrix;

			} catch (Exception e) {
				throw new ClusterServiceException(new ErrorMessage(
						e.getMessage(), e.getCause()));
			}
		}
	}

	public void calculateCosineSimilarity(Integer startIndex, Integer endIndex,
			Double[][] cosineMatrix, List<Map<String, Double>> tfidfVectorList)
			throws ClusterServiceException {
		for (int i = startIndex; i <= endIndex; i++) {

			Map<String, Double> docVector1 = (LinkedHashMap<String, Double>) tfidfVectorList
					.get(i);

			List<Double> list1 = new ArrayList<Double>(docVector1.values());

			for (int j = i + 1; j < tfidfVectorList.size(); j++) {

				Map<String, Double> docVector2 = (LinkedHashMap<String, Double>) tfidfVectorList
						.get(j);
				List<Double> list2 = new ArrayList<Double>(docVector2.values());
				cosineMatrix[i][j] = this.getCosineSimilarity(list1, list2);
			}
		}
	}
}
