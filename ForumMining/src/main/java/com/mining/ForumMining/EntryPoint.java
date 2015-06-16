package com.mining.ForumMining;

/*
 * ##############################$History Card$###################################
 * ### Latest changes description should be on the top of the history card list###
 * ###############################################################################
 *  Created Date	Updated Date	Author			Change Description
 *  ============	============	============	===================
 *  28/05/2015		29/05/2015		chandu-atina 	Initial basic version
 */

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.crawl.web.exception.WebCrawlerServiceException;
import com.crawl.web.service.CrawlerService;
import com.crawl.web.util.messages.ErrorMessage;
import com.mining.ForumMining.core.CorpusValue;
import com.mining.ForumMining.exception.ClusterServiceException;
import com.mining.ForumMining.service.CosineService;
import com.mining.ForumMining.service.PoSTagger;
import com.mining.ForumMining.service.StopWordService;
import com.mining.ForumMining.service.TFIDFService;

/**
 * EntryPoint is a entry point for clustering of forum data This is a main class
 * which is automatically picked from jar when executed from command prompt
 *
 */
@Component
public class EntryPoint {
	final static Logger log = Logger.getLogger(EntryPoint.class);

	private static final String CONFIG_PATH = "classpath:Application-Config-Mining.xml";

	@Autowired
	@Qualifier("OpenNLPPoSTagger")
	PoSTagger posTagger;

	@Autowired
	@Qualifier("MultiThreadedWebCrawlerServiceImpl")
	CrawlerService webCrawler;

	@Autowired
	@Qualifier("StopWordNewServiceImpl")
	StopWordService stopWordService;
	

	@Autowired
	TFIDFService tfidfService;
	
	@Autowired
	CosineService cosineService;
	
	/**
	 * main method is the starting point of clustering
	 */
	public static void main(String args[]) {
		try {
			final ApplicationContext context = new ClassPathXmlApplicationContext(
					CONFIG_PATH);
			final EntryPoint entryPoint = context.getBean(EntryPoint.class);
			log.info("Staring Clustering !!!");
			long startTime = Calendar.getInstance().getTimeInMillis();
			boolean flag = entryPoint.cluster();
			long endTime = Calendar.getInstance().getTimeInMillis();
			log.info("Clustering completed in " + (endTime - startTime)
					+ " seconds");
		} catch (WebCrawlerServiceException e) {
			log.error(e.toString());
			log.info("Exception in Crawling data. Process Terminated !!!");
		} catch (ClusterServiceException e) {
			log.error(e.toString());
			log.info("Exception in Cluster data. Process Terminated !!!");
		}
	}

	/**
	 * print is a sample method to verify logger and spring IOC
	 */
	public void print() {
		log.info(posTagger.tagContent("Hi This is a sample text from india"));
	}

	public boolean cluster() {
		Map<String, CorpusValue> globalCorpus = new HashMap<String, CorpusValue>();
		List<Map<String, Double>> tfidfVectorList = new ArrayList<Map<String, Double>>();

		try {
			/* Crawls data from maven forum */
			webCrawler.processRequest(true);

			/* Applies PosTagging for the data */
			// posTagger.tagDocuments();

			/* Removes stop words, stemming and lemmatization */
			List<Map<String, CorpusValue>> docList = stopWordService
					.removeStopWords(globalCorpus);

			globalCorpus = stopWordService.sortByValue(globalCorpus);
			log.info(globalCorpus.size());
			Set<String> keySet = globalCorpus.keySet();

			List<String> words = new ArrayList<String>();
			words = FileUtils.readLines(new File(
					"data/stop_word/stopwords_lemmatized"), "utf-8");

			keySet.removeAll(new HashSet<String>(words));
			log.info(globalCorpus.size());
			
			/*
			 * Calculate tfidf value for each and every document
			 */
			for (Map<String, CorpusValue> doc : docList) {
				Map<String, Double> docVector = tfidfService.calculateTFIDF(
						doc, globalCorpus);
				tfidfVectorList.add(docVector);
			}
			
			/*
			 * Calcualte cosine similarity matrix from the document vectors
			 */
			cosineService.calculateCosineSimilarityMatrix(tfidfVectorList);
			
			/*
			 * Applying Clustering on cosine similarity matrix
			 */
			//TODO: CLustering of documents based on cosine similarity matrix
			
			/*
			 * Labelling of clusters
			 */
			//TODO: Applying labels to clusters based on the keywords in the document vectors
			
		} catch (IOException e) {
			throw new ClusterServiceException(new ErrorMessage(e.getMessage(),
					e.getCause()));
		}
		return true;
	}
}