package com.mining.ForumMining.service.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
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
import org.tartarus.snowball.ext.PorterStemmer;

import com.crawl.web.util.ApplicationProperties;
import com.mining.ForumMining.constants.MiningConstants;
import com.mining.ForumMining.exception.ClusterServiceException;
import com.mining.ForumMining.service.StopWordService;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

/* @param <E> the type of elements in this list */
public class StopWordServiceImpl implements StopWordService {

	final static Logger log = Logger.getLogger(StopWordServiceImpl.class);

	@Autowired
	ApplicationProperties appProp;

	PorterStemmer stem = new PorterStemmer();

	public void removeStopWords() throws ClusterServiceException {

		Path path = Paths.get(appProp.getMailLocation());
		List<Path> files = new ArrayList<Path>();
		listFiles(path, files);
		for (Path filePath : files) {
			try {
				removeStopWordfromFile(filePath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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

	public void removeStopWordfromFile(Path filePath) throws IOException {
		BufferedReader reader = Files.newBufferedReader(filePath,
				StandardCharsets.UTF_8);
		Map<String, MutableInt> keyWords = new HashMap<String, MutableInt>();
		StringBuilder content = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			content.append(line);
		}
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
				String stemmedWord = getStemmedWord(s.substring(0,
						s.indexOf("/")).toLowerCase());
				MutableInt count = keyWords.get(stemmedWord);
				if (count == null) {
					keyWords.put(stemmedWord, new MutableInt());
				} else {
					count.increment();
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

		writeListToFile(keyWords, filePath);
		// log.info(keyWords);
	}

	public static void main(String args[]) {

		StopWordServiceImpl sample = new StopWordServiceImpl();
		Path path = Paths.get("/var/tmp/mail");
		List<Path> files = new ArrayList<Path>();
		sample.listFiles(path, files);

		for (Path filePath : files) {
			try {
				sample.removeStopWordfromFile(filePath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	static Map<String, MutableInt> sortByValue(Map<String, MutableInt> map) {
		List<Map<String, MutableInt>> list = new LinkedList(map.entrySet());
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o1)).getValue())
						.compareTo(((Map.Entry) (o2)).getValue());
			}
		});

		Map<String, MutableInt> result = new LinkedHashMap<String, MutableInt>();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			result.put((String) entry.getKey(), (MutableInt) entry.getValue());
		}
		return result;
	}

	public boolean writeListToFile(Map hmap, Path filePath) {

		/*
		 * filePath :
		 * /var/tmp/mail/201407_tagged/<53C7EE0A.9070803@gmx.de>.tagged
		 */

		String filePathString = filePath.toString().replace("tagged",
				"stopword");

		try {
			File file = new File(filePathString);
			file.getParentFile().mkdirs();
			file.createNewFile();

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(hmap.toString());
			bw.close();

			/*
			 * FileOutputStream fos = new FileOutputStream(filePathString);
			 * ObjectOutputStream oos = new ObjectOutputStream(fos);
			 * oos.writeObject(hmap); oos.close(); fos.close();
			 */
		} catch (Exception e) {
			log.info(e.getStackTrace());
			// TODO add proper exception
		}
		return true;
	}

	public String getStemmedWord(String word) {
		stem.setCurrent(word);
		stem.stem();
		String text = stem.getCurrent();
		String lemma = "";

		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props, false);
		// String text = "ran run running update updates updated";
		edu.stanford.nlp.pipeline.Annotation document = pipeline.process(text);

		for (CoreMap sentence : document.get(SentencesAnnotation.class)) {
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				// String word = token.get(TextAnnotation.class);
				lemma = token.get(LemmaAnnotation.class);
				System.out.println("lemmatized version :" + lemma);
			}
		}

		return lemma;
	}
}

class MutableInt implements Comparable<MutableInt>, Serializable {

	private static final long serialVersionUID = 1L;

	int value = 1; // note that we start at 1 since we're counting

	public void increment() {
		++value;
	}

	public int get() {
		return value;
	}

	public String toString() {
		return String.valueOf(value);
	}

	public int compareTo(MutableInt o) {
		if (o.get() > this.get())
			return 1;
		else if (o.get() == this.get())
			return 0;
		else
			return -1;
	}
}
