package com.mining.ForumMining.service.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tartarus.snowball.ext.PorterStemmer;

import com.crawl.web.util.ApplicationProperties;
import com.crawl.web.util.messages.ErrorMessage;
import com.mining.ForumMining.constants.MiningConstants;
import com.mining.ForumMining.core.CorpusValue;
import com.mining.ForumMining.exception.ClusterServiceException;
import com.mining.ForumMining.service.CosineService;
import com.mining.ForumMining.service.StopWordService;
import com.mining.ForumMining.service.TFIDFService;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

/* @param <E> the type of elements in this list */
@Service("StopWordServiceImpl")
public class StopWordServiceImpl implements StopWordService {

	final static Logger log = Logger.getLogger(StopWordServiceImpl.class);

	@Autowired
	ApplicationProperties appProp;

	@Autowired
	TFIDFService tfidfService;

	@Autowired
	CosineService cosineService;

	PorterStemmer stem = new PorterStemmer();

	public void removeStopWords() throws ClusterServiceException {

		Path path = Paths.get(appProp.getMailLocation());
		List<Path> files = new ArrayList<Path>();
		listFiles(path, files);
		Map<String, CorpusValue> globalCorpus = new HashMap<String, CorpusValue>();

		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props, false);

		for (Path filePath : files) {
			try {
				removeStopWordfromFile(globalCorpus, filePath, pipeline,
						files.size());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		log.info("\n\n\n\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n\n\n");

		globalCorpus = sortByValue(globalCorpus);
		// log.info(globalCorpus);
		log.info(globalCorpus.size());
		Set<String> keySet = globalCorpus.keySet();

		List<String> words = new ArrayList<String>();
		try {
			words = FileUtils.readLines(new File(
					"data/stop_word/stopwords_lemmatized"), "utf-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		keySet.removeAll(new HashSet<String>(words));
		log.info(globalCorpus.size());
		// log.info(globalCorpus);
		// writeListToFile(globalCorpus, filePath,"stopword");
		// tfidfService.TFCalculation(globalCorpus);
		// log.info(globalCorpus);
		writeListToFile(globalCorpus, files.get(0), "globalcorpa", true, false);

		calculateTFIDFForAllDocuments(files, globalCorpus);

		calculateCosineSimilarityMatrix(files);

	}

	public void removeStopWords(String docLocation)
			throws ClusterServiceException {

		appProp.setMailLocation(docLocation);
		removeStopWords();
	}

	public ApplicationProperties getAppProp() {
		return appProp;
	}

	public void setAppProp(ApplicationProperties appProp) {
		this.appProp = appProp;
	}

	public void listFiles(Path path, List<Path> files) {
		try {
			DirectoryStream<Path> stream = Files.newDirectoryStream(path);

			for (Path entry : stream) {
				if (Files.isDirectory(entry)) {
					listFiles(entry, files);
				} else {
					if (entry.toString().endsWith(".tagged")) {
						files.add(entry);
					}
				}
			}
			stream.close();
		} catch (IOException e) {

		}
	}

	public void removeStopWordfromFile(Map<String, CorpusValue> globalCorpus,
			Path filePath, StanfordCoreNLP pipeLine, Integer docCount)
			throws IOException {
		BufferedReader reader = Files.newBufferedReader(filePath,
				StandardCharsets.UTF_8);
		Map<String, CorpusValue> keyWords = new HashMap<String, CorpusValue>();
		StringBuilder content = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			content.append(line);
		}
		boolean isCurrentDocCounted = false;
		// log.info(content);
		StringTokenizer st = new StringTokenizer(content.toString());
		while (st.hasMoreTokens()) {
			// log.info(st.nextToken());

			/*
			 * JJ-Adjective;
			 * JJR-Adjective,comparative;JJS-Adjective,superlative;
			 * NN-Noun,singular; NNS-Noun,plural; NNP-Proper noun,singular;
			 * NNPS-Proper noun,plural; VB-Verb, base form ; VBD-Verb, past
			 * tense; VBG-Verb, gerund or present participle; VBN-Verb, past
			 * participle; VBP-Verb, non­3rd person singular present; VBZ-Verb,
			 * 3rd person singular present;
			 */
			String s = st.nextToken();
			if (MiningConstants.POSTAG_LIST
					.contains(s.substring(s.indexOf("/") + 1))) {
				String stemmedWord = getStemmedWord(
						s.substring(0, s.indexOf("/")).toLowerCase(), pipeLine);
				/* Current Document Corpus */
				CorpusValue count = keyWords.get(stemmedWord);
				if (count == null) {
					keyWords.put(stemmedWord, new CorpusValue(docCount));
				} else {
					count.increment();
				}

				/* Global Corpus */
				CorpusValue globalcount = globalCorpus.get(stemmedWord);
				if (globalcount == null) {
					globalCorpus.put(stemmedWord, new CorpusValue(docCount));
					isCurrentDocCounted = true;
				} else {
					globalcount.increment();
					if (!isCurrentDocCounted) {
						globalcount.incrementDFT();
						isCurrentDocCounted = true;
						// if(count !=null)
						// count.setTf(globalcount.getTf());
					}
				}
			}
		}
		keyWords = sortByValue(keyWords);
		log.info(keyWords);
		log.info(keyWords.size());
		Set<String> keySet = keyWords.keySet();

		List<String> words = FileUtils.readLines(new File(
				"data/stop_word/stopwords_lemmatized"), "utf-8");
		keySet.removeAll(new HashSet<String>(words));
		log.info(keyWords.size());
		log.info(keyWords);
		// writeListToFile(keyWords, filePath,"stopword",true,false);
		tfidfService.calculateTF(keyWords);
		log.info(keyWords);
		writeListToFile(keyWords, filePath, "weightage", true, true);

		// log.info(keyWords);
	}

	public static void main(String args[]) {

		StopWordServiceImpl sample = new StopWordServiceImpl();
		Path path = Paths.get("/var/tmp/mail");
		List<Path> files = new ArrayList<Path>();
		// Map<String, CorpusValue> globalCorpus = new HashMap<String,
		// CorpusValue>();
		sample.listFiles(path, files);
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma");
		// StanfordCoreNLP pipeline = new StanfordCoreNLP(props, false);

		/*
		 * for (Path filePath : files) { try {
		 * sample.removeStopWordfromFile(globalCorpus
		 * ,filePath,pipeline,files.size()); } catch (IOException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } }
		 */
		sample.calculateCosineSimilarityMatrix(files);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	static Map<String, CorpusValue> sortByValue(Map<String, CorpusValue> map) {
		List<Map<String, CorpusValue>> list = new LinkedList(map.entrySet());
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o1)).getValue())
						.compareTo(((Map.Entry) (o2)).getValue());
			}
		});

		Map<String, CorpusValue> result = new LinkedHashMap<String, CorpusValue>();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			result.put((String) entry.getKey(), (CorpusValue) entry.getValue());
		}
		return result;
	}

	public boolean writeListToFile(Map<?, ?> hmap, Path filePath,
			String pathKeyword, boolean writeToFileFlag,
			boolean serializeDataFlag) {

		/*
		 * filePath :
		 * /var/tmp/mail/201407_tagged/<53C7EE0A.9070803@gmx.de>.tagged
		 */
		try {
			if (writeToFileFlag) {
				String filePathString = filePath.toString().replace("tagged",
						pathKeyword);
				File file = new File(filePathString);
				file.getParentFile().mkdirs();
				file.createNewFile();

				FileWriter fw = new FileWriter(file.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(hmap.toString());
				bw.close();
			}
			if (serializeDataFlag) {
				String serializationFilePath = filePath.toString().replace(
						"tagged", pathKeyword + "_ser");
				File serFile = new File(serializationFilePath);
				serFile.getParentFile().mkdirs();
				serFile.createNewFile();
				FileOutputStream fileOut = new FileOutputStream(
						serializationFilePath);
				ObjectOutputStream out = new ObjectOutputStream(fileOut);
				out.writeObject(hmap);
				out.close();
				fileOut.close();
			}

		} catch (Exception e) {
			log.info(e.getStackTrace());
			// TODO add proper exception
		}
		return true;
	}

	public String getStemmedWord(String word, StanfordCoreNLP pipeLine) {
		stem.setCurrent(word);
		stem.stem();
		String text = stem.getCurrent();
		String lemma = "";

		/*
		 * Properties props = new Properties(); props.put("annotators",
		 * "tokenize, ssplit, pos, lemma"); StanfordCoreNLP pipeline = new
		 * StanfordCoreNLP(props, false);
		 */
		// String text = "ran run running update updates updated";
		edu.stanford.nlp.pipeline.Annotation document = pipeLine.process(text);

		for (CoreMap sentence : document.get(SentencesAnnotation.class)) {
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				// String word = token.get(TextAnnotation.class);
				lemma = token.get(LemmaAnnotation.class);
				// System.out.println("lemmatized version :" + lemma);
			}
		}

		return lemma;
	}

	@SuppressWarnings("unchecked")
	public void calculateTFIDFForAllDocuments(List<Path> filePaths,
			Map<String, CorpusValue> globalCorpa) {

		for (Path filePath : filePaths) {
			Map<String, CorpusValue> keywords = (LinkedHashMap<String, CorpusValue>) deserializeObject(
					filePath, "weightage");
			Map<String, Double> docVector = tfidfService.calculateTFIDF(
					keywords, globalCorpa);
			writeListToFile(keywords, filePath, "weightage", true, true);
			writeListToFile(docVector, filePath, "vector", true, true);
			// tfidfService.calculateDocumentVector(keywords,globalCorpa);

		}
	}

	public Object deserializeObject(Path filePath, String pathKeyword) {

		Object keywords = null;
		try {
			String filePathString;
			if (pathKeyword != null) {
				filePathString = filePath.toString().replace("tagged",
						pathKeyword + "_ser");
			} else {
				filePathString = filePath.toString().replace("tagged", "ser");
			}

			FileInputStream fileIn = new FileInputStream(filePathString);
			// InputStream fileIn =Files.newInputStream(filePath);
			ObjectInputStream in = new ObjectInputStream(fileIn);

			keywords = in.readObject();
			in.close();
			fileIn.close();
		} catch (IOException i) {
			i.printStackTrace();
			// return keywords
		} catch (ClassNotFoundException c) {
			log.info("Employee class not found");
			// log.info(c.printStackTrace());
			// return;
		}
		return keywords;
	}

	public void calculateCosineSimilarityMatrix(List<Path> filePaths)
			throws ClusterServiceException {
		log.info("Staring calculation of cosine similarity matrix !!!");
		long startTime = Calendar.getInstance().getTimeInMillis();

		Double[][] cosineMatrix = new Double[filePaths.size()][filePaths.size()];
		/* Creating and executor instance */
		ExecutorService es = Executors.newCachedThreadPool();
		try {
			for (int counter = 0; counter < filePaths.size(); counter += MiningConstants.MAX_DOC_PER_THREAD) {
				// cosineMatrix=es.submit(new
				// CosineCalculationThread(cosineMatrix,
				// filePaths,counter)).get();
				es.execute(new CosineCalculationThread(cosineMatrix, filePaths,
						counter));
			}

			es.shutdown();
			boolean finshed = es.awaitTermination(1, TimeUnit.HOURS);

			// log.info("Executor Flag :" + finshed);
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
	public class CosineCalculationThread implements Runnable {

		private Double[][] cosineMatrix;
		private List<Path> filePaths;
		private Integer counter;

		public CosineCalculationThread(Double[][] cosineMatrix,
				List<Path> filePaths, Integer counter) {
			super();
			this.cosineMatrix = cosineMatrix;
			this.filePaths = filePaths;
			this.counter = counter;
		}

		public void run() {
			try {
				log.info(Thread.currentThread().getName() + " Started");
				calculateCosineSimilarity(counter, cosineMatrix, filePaths);
				log.info(Thread.currentThread().getName() + " finished its job");
			} catch (Exception e) {
				throw new ClusterServiceException(new ErrorMessage(
						e.getMessage(), e.getCause()));
			}
		}

		public Double[][] call() throws Exception {
			// TODO Auto-generated method stub
			try {
				calculateCosineSimilarity(counter, cosineMatrix, filePaths);
				return cosineMatrix;

			} catch (Exception e) {
				throw new ClusterServiceException(new ErrorMessage(
						e.getMessage(), e.getCause()));
			}
		}

	}

	@SuppressWarnings("unchecked")
	public void calculateCosineSimilarity(Integer counter,
			Double[][] cosineMatrix, List<Path> filePaths)
			throws ClusterServiceException {
		for (int i = counter; i < counter + MiningConstants.MAX_DOC_PER_THREAD && i < filePaths.size(); i++) {
			Path filePath1 = filePaths.get(i);
			Map<String, Double> docVector1 = (LinkedHashMap<String, Double>) deserializeObject(
					filePath1, "vector");
			List<Double> list1 = new ArrayList<Double>(docVector1.values());

			for (int j = i + 1; j < filePaths.size(); j++) {
				Path filePath2 = filePaths.get(j);
				Map<String, Double> docVector2 = (LinkedHashMap<String, Double>) deserializeObject(
						filePath2, "vector");
				List<Double> list2 = new ArrayList<Double>(docVector2.values());
				// cosineMatrix[i][j]=cosineService.getCosineSimilarity(list1,list2);
				// TODO remove new initialization after test
				cosineMatrix[i][j] = new CosineSimilarityImpl()
						.getCosineSimilarity(list1, list2);
			}
		}
	}
}