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
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
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

import smile.clustering.KMeans;

import com.apporiented.algorithm.clustering.AverageLinkageStrategy;
import com.apporiented.algorithm.clustering.Cluster;
import com.apporiented.algorithm.clustering.ClusteringAlgorithm;
import com.apporiented.algorithm.clustering.DefaultClusteringAlgorithm;
import com.apporiented.algorithm.clustering.visualization.DendrogramPanel;
import com.crawl.web.exception.WebCrawlerServiceException;
import com.crawl.web.service.CrawlerService;
import com.crawl.web.util.ApplicationProperties;
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

	@Autowired
	ApplicationProperties appProp;

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
			// webCrawler.processRequest(true);

			/* Applies PosTagging for the data */
			//posTagger.tagDocuments();

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
			double[][] cosinematrix = cosineService
					.calculateCosineSimilarityMatrix(tfidfVectorList);

			/*
			 * Applying Clustering on cosine similarity matrix
			 */

			List<Path> files = new ArrayList<Path>();
			String[] str = new String[cosinematrix[0].length];
			Path path = Paths.get(appProp.getMailLocation());

			this.listFiles(path, files);
			int i = 0;
			for (Path filePath : files) {
				str[i] = filePath.toString();
				i++;
			}
			Map<String,String> name= new LinkedHashMap<String, String>();
			
			for(int j=0;j<str.length;j++){
				name.put("doc"+(j+1), str[j]);
				str[j] = "doc"+(j+1);
			}
			log.info(name);
			ClusteringAlgorithm alg = new DefaultClusteringAlgorithm();
			Cluster cluster = alg.performClustering(cosinematrix, str,
					new AverageLinkageStrategy());
			DendrogramPanel dp = new DendrogramPanel();
			dp.setModel(cluster);
			//cluster.toConsole(30);
			this.toConsole(2, cluster);

			log.info(cluster.getName());
			
			//KMeans k = new KMeans(cosinematrix, 20, 5, 2);
		//	log.info(k);
			
			/*
			 * Labelling of clusters
			 */
			// TODO: Applying labels to clusters based on the keywords in the
			// document vectors

		} catch (IOException e) {
			throw new ClusterServiceException(new ErrorMessage(e.getMessage(),
					e.getCause()));
		}
		return true;
	}

	public void listFiles(Path path, List<Path> files) {
		try {
			DirectoryStream<Path> stream = Files.newDirectoryStream(path);
			/*
			 * recursively checks for directories and lists all files In-Order
			 * Tree Traversal
			 */
			for (Path entry : stream) {
				if (Files.isDirectory(entry)) {
					listFiles(entry, files);
				} else {
					if (entry.toString().endsWith(".tagged")) {
						files.add(entry.getFileName());
					}
				}
			}
			stream.close();
		} catch (IOException e) {
			throw new ClusterServiceException(new ErrorMessage(
					"Exception while listing files at : " + path, e.getCause()));
		}
	}
	
    public void toConsole(int indent,Cluster c) {
    	String s="";
        for (int i = 0; i < indent; i++) {
            s=s.concat(" ");
        }
        //String name = c.getName() + (c.isLeaf() ? " (leaf)" : "") + (c.getDistance() != null ? "  distance: " + c.getDistance() : "");
        String name=c.getName();
        log.info(s+name);
        for (Cluster child : c.getChildren()) {
            this.toConsole(indent + 2,child);
        }
    }
}