package com.mining.ForumMining.service.impl;

/*
 * ##############################$History Card$###################################
 * ### Latest changes description should be on the top of the history card list###
 * ###############################################################################
 *  Created Date	Updated Date	Author			Change Description
 *  ============	============	============	===================
 *  28/05/2015		29/05/2015		chandu-atina 	Initial basic version
 */

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

/**
 * 
 * @author chandrasekhara
 *
 *         StopWordService class is a service layer implementing StopWordService
 *         Interface.It exposes services to remove stop words from the list of
 *         documents.
 * 
 */
@Service("StopWordNewServiceImpl")
public class StopWordNewServiceImpl implements StopWordService {

	final static Logger log = Logger.getLogger(StopWordServiceImpl.class);

	@Autowired
	ApplicationProperties appProp;

	@Autowired
	TFIDFService tfidfService;

	@Autowired
	CosineService cosineService;

	PorterStemmer stem = new PorterStemmer();

	List<Map<String, Double>> tfidfVectorList = new ArrayList<Map<String, Double>>();

	/**
	 * Implements removeStopWords method define in StopWordService Interface.
	 */
	public List<Map<String, CorpusValue>> removeStopWords(
			Map<String, CorpusValue> globalCorpus)
			throws ClusterServiceException {

		Path path = Paths.get(appProp.getMailLocation());
		List<Path> files = new ArrayList<Path>();
		listFiles(path, files);
		List<Map<String, CorpusValue>> docList = new ArrayList<Map<String, CorpusValue>>();

		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props, false);

		/*
		 * Processing each and every document at "path"
		 */
		for (Path filePath : files) {
			try {
				docList.add(removeStopWordfromFile(globalCorpus, filePath,
						pipeline, files.size()));
			} catch (IOException e) {
				throw new ClusterServiceException(new ErrorMessage(
						"Exception while processing file at : " + filePath,
						e.getCause()));
			}
		}
		log.info("\n\n\n\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n\n\n");
		return docList;
	}

	/**
	 * Implements removeStopWords method define in StopWordService Interface.
	 */
	public void removeStopWords(String docLocation,
			Map<String, CorpusValue> globalCorpus)
			throws ClusterServiceException {

		appProp.setMailLocation(docLocation);
		removeStopWords(globalCorpus);
	}

	/**
	 * 
	 * @return appProp - Application Properties
	 */
	public ApplicationProperties getAppProp() {
		return appProp;
	}

	/**
	 * 
	 * @param appProp
	 *            - sets appProp through Auto wiring
	 */
	public void setAppProp(ApplicationProperties appProp) {
		this.appProp = appProp;
	}

	/**
	 * 
	 * @param path
	 *            - File System Path
	 * @param files
	 *            - Stores all file name in the input path
	 */
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
						files.add(entry);
					}
				}
			}
			stream.close();
		} catch (IOException e) {
			throw new ClusterServiceException(new ErrorMessage(
					"Exception while listing files at : " + path, e.getCause()));
		}
	}

	/**
	 * 
	 * @param globalCorpus
	 *            - Global set of words from all documents
	 * @param filePath
	 *            - Absolute file path
	 * @param pipeLine
	 *            - pipeLine Object for stemming
	 * @param docCount
	 *            - total documents count
	 * @return - Map containing keywords in the document along with their core
	 *         values used for clustering
	 * @throws IOException
	 * <br/>
	 * <br/>
	 * @Description The methods takes filePath as input and applies the
	 *              following set of actions to document in filePath. <br/>
	 *              <em>1. Removes all words expect words mentioned
	 * in static POSTAG_LIST</em> <br/>
	 *              <em>2. Apply Stemming & Lemmatization for the 
	 * remaining set of words in each and every document</em> <br/>
	 *              <em>3. Calculate the word count for each lemma for 
	 * all documents</em> <br/>
	 *              <em>4. Update the Global Corpus from all the documents</em>
	 */
	public Map<String, CorpusValue> removeStopWordfromFile(
			Map<String, CorpusValue> globalCorpus, Path filePath,
			StanfordCoreNLP pipeLine, Integer docCount) throws IOException {
		BufferedReader reader = Files.newBufferedReader(filePath,
				StandardCharsets.UTF_8);
		Map<String, CorpusValue> keyWords = new HashMap<String, CorpusValue>();
		StringBuilder content = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			content.append(line);
		}

		StringTokenizer st = new StringTokenizer(content.toString());
		while (st.hasMoreTokens()) {

			/*
			 * JJ-Adjective; JJR-Adjective,comparative;
			 * JJS-Adjective,superlative; NN-Noun,singular; NNS-Noun,plural;
			 * NNP-Proper noun,singular; NNPS-Proper noun,plural; VB-Verb, base
			 * form ; VBD-Verb, past tense; VBG-Verb, gerund or present
			 * participle; VBN-Verb, past participle; VBP-Verb, non­3rd person
			 * singular present; VBZ-Verb, 3rd person singular present;
			 */
			String s = st.nextToken();
			/*
			 * Process tokens that has any one of the tags listed in
			 * POSTAG_LIST. Remaining tokens are skipped
			 */
			if (MiningConstants.POSTAG_LIST
					.contains(s.substring(s.indexOf("/") + 1))) {
				/*
				 * Get stemmed & lemmatized word for the token
				 */
				String stemmedWord = getStemmedWord(
						s.substring(0, s.indexOf("/")).toLowerCase(), pipeLine);
				/*
				 * Current Document Corpus Check for keyword in list, increment
				 * value if already present add to list if not present
				 */
				CorpusValue count = keyWords.get(stemmedWord);
				if (count == null) {
					keyWords.put(stemmedWord, new CorpusValue(docCount));
				} else {
					count.increment();
				}
				/*
				 * Global Corpus Check for keyword in list, increment value if
				 * already present add to list if not present. Increment dft
				 * value only once per keyword per document
				 */
				CorpusValue globalcount = globalCorpus.get(stemmedWord);
				if (globalcount == null) {
					globalCorpus.put(stemmedWord, new CorpusValue(docCount));
				} else {
					globalcount.increment();
					if (keyWords.get(stemmedWord).get() == 1) {
						globalcount.incrementDFT();
					}
				}
			}
		}
		/* Sort keywords in ascending order of value field. */
		keyWords = sortByValue(keyWords);
		log.info(keyWords);
		log.info(keyWords.size());
		Set<String> keySet = keyWords.keySet();

		/*
		 * Remove static list of stopwords defined in "stopwords_lemmatized"
		 * file
		 */
		List<String> words = FileUtils.readLines(new File(
				"data/stop_word/stopwords_lemmatized"), "utf-8");
		keySet.removeAll(new HashSet<String>(words));
		log.info(keyWords.size());
		log.info(keyWords);
		writeListToFile(keyWords, filePath, "stopword", true, false);

		/* Calculate termfrequency for each term in the document */
		tfidfService.calculateTF(keyWords);
		return keyWords;
	}

	/**
	 * 
	 * @param args
	 *            main method foe testing purpose
	 */
	public static void main(String args[]) {

		StopWordServiceImpl sample = new StopWordServiceImpl();
		Path path = Paths.get("/var/tmp/mail");
		List<Path> files = new ArrayList<Path>();
		// Map<String, CorpusValue> globalCorpus = new HashMap<String,
		// CorpusValue>();
		sample.listFiles(path, files);
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props, false);

		System.out.println(sample.getStemmedWord("desk", pipeline));
		System.out.println(sample.getStemmedWord("table", pipeline));

		/*
		 * for (Path filePath : files) { try {
		 * sample.removeStopWordfromFile(globalCorpus
		 * ,filePath,pipeline,files.size()); } catch (IOException e) {
		 * Auto-generated catch block e.printStackTrace(); } }
		 */
		// sample.calculateCosineSimilarityMatrix(files);
	}

	/**
	 * sorts the values in the map by "value in CorpusValue Object"
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, CorpusValue> sortByValue(Map<String, CorpusValue> map) {
		List<Map<String, CorpusValue>> list = new LinkedList(map.entrySet());

		/* Sort List using comparator */
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o1)).getValue())
						.compareTo(((Map.Entry) (o2)).getValue());
			}
		});

		/* Add element to LinkedHashMap to preserve the insertion order */
		Map<String, CorpusValue> result = new LinkedHashMap<String, CorpusValue>();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			result.put((String) entry.getKey(), (CorpusValue) entry.getValue());
		}
		return result;
	}

	/**
	 * 
	 * @param hmap
	 *            - Map values to be stored to file system
	 * @param filePath
	 *            - filePath where the values to be stored
	 * @param pathKeyword
	 *            - key file Path
	 * @param writeToFileFlag
	 *            - "true" indicates that values are to be stored to file path
	 *            as plain text(human readable).
	 * @param serializeDataFlag
	 *            = "true" indicates that the object to be Serialized(non-human
	 *            readable), easy to read the content back to it's appropriate
	 *            data structure.
	 * @return
	 */
	public boolean writeListToFile(Map<?, ?> hmap, Path filePath,
			String pathKeyword, boolean writeToFileFlag,
			boolean serializeDataFlag) {

		/*
		 * filePath :
		 * /var/tmp/mail/201407_tagged/<53C7EE0A.9070803@gmx.de>.tagged
		 */
		try {

			/* Writes to file as plain text if 'writeToFileFlag' is true */
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

			/* Serializes Object if the 'serializeDataFlag' is set to true */
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
			throw new ClusterServiceException(new ErrorMessage(
					"Exception while saving data to file system : ",
					e.getCause()));
		}
		return true;
	}

	/**
	 * 
	 * @param word
	 *            - input word on which stemming to be applied
	 * @param pipeLine
	 *            - pipeLine Object to perform stemming
	 * @return - stemmed word.
	 */
	public String getStemmedWord(String word, StanfordCoreNLP pipeLine) {

		/* Stemming of input word */
		stem.setCurrent(word);
		stem.stem();
		String text = stem.getCurrent();
		String lemma = "";

		edu.stanford.nlp.pipeline.Annotation document = pipeLine.process(text);

		/* Lemmatizing the stemmed word */
		for (CoreMap sentence : document.get(SentencesAnnotation.class)) {
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				lemma = token.get(LemmaAnnotation.class);
			}
		}

		return lemma;
	}

	/**
	 * 
	 * @param filePath
	 *            - file path where serialized object is stored
	 * @param pathKeyword
	 *            - key file path
	 * @return - deserialized object
	 */
	public Object deserializeObject(Path filePath, String pathKeyword) {

		Object keywords = null;
		String filePathString = null;
		try {
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
			throw new ClusterServiceException(new ErrorMessage(
					"Exception while deserializing object at : "
							+ filePathString, i.getCause()));
		} catch (ClassNotFoundException c) {
			throw new ClusterServiceException(new ErrorMessage(
					"Exception in typecast during deserialization : "
							+ filePathString, c.getCause()));
		}
		return keywords;
	}
}