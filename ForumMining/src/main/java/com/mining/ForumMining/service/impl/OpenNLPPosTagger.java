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
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.crawl.web.util.ApplicationProperties;
import com.crawl.web.util.messages.ErrorMessage;
import com.mining.ForumMining.exception.ClusterServiceException;
import com.mining.ForumMining.service.PoSTagger;

/**
 * OpenNLPPosTagger class has a few service methods that are helpful to tag the
 * document with their appropriate parts of speech. The class internally uses
 * OpenNLP PoS Tagger implementation.
 */
@Service("OpenNLPPoSTagger")
public class OpenNLPPosTagger implements PoSTagger {

	final static Logger log = Logger.getLogger(OpenNLPPosTagger.class);

	@Autowired
	ApplicationProperties appProp;

	/**
	 * Tags the input string with it's appropriate POS tag and returns the
	 * tagged String.
	 */
	@SuppressWarnings("deprecation")
	public String tagContent(String content) throws ClusterServiceException {

		InputStream modelIn = null;

		try {
			modelIn = new FileInputStream(
					"data/openNLPModels/en-pos-maxent.bin");
			POSModel model = new POSModel(modelIn);
			POSTaggerME tagger = new POSTaggerME(model);
			log.info(tagger.tag(content));
		} catch (IOException e) {
			throw new ClusterServiceException(new ErrorMessage(
					"Model loading failed !!", e.getCause()));
		} finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				} catch (IOException e) {
					throw new ClusterServiceException(
							new ErrorMessage(
									"Exception while cloising FileInoutStream : modelIn !!",
									e.getCause()));
				}
			}
		}
		return new String("OpenNLPPoSTagger");
	}

	public boolean tagDocuments(String docLocation)
			throws ClusterServiceException {

		appProp.setMailLocation(docLocation);
		tagDocuments();
		return true;
	}

	public boolean tagDocuments() throws ClusterServiceException {

		Path path = Paths.get(appProp.getMailLocation());
		List<Path> files = new ArrayList<Path>();
		listFiles(path, files);
		InputStream modelIn = null;
		try {
			modelIn = new FileInputStream(
					"data/openNLPModels/en-pos-maxent.bin");
			POSModel model = new POSModel(modelIn);
			POSTaggerME tagger = new POSTaggerME(model);

			for (Path filePath : files) {
				try {
					tagFile(filePath, tagger);
				} catch (IOException e) {
					throw new ClusterServiceException(new ErrorMessage(
							"Exception while taging file. File Path : "
									+ filePath, e.getCause()));
				}
			}
		} catch (IOException e) {
			throw new ClusterServiceException(new ErrorMessage(
					"Exception while instantiating modeIn Object !! ",
					e.getCause()));
		} finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				} catch (IOException e) {
					throw new ClusterServiceException(
							new ErrorMessage(
									"Exception while cloising FileInoutStream : modelIn !!",
									e.getCause()));
				}
			}
		}
		return true;
	}

	/**
	 * 
	 * @param filePath
	 *            - Location of input file
	 * @param tagger
	 *            - OpenNLP POS Tagger Object
	 * @return status - true/false
	 * @throws IOException
	 * <br/>
	 * <br/>
	 * @Description Tags the file mentioned in filepath and write the tagged
	 *              file to file system.
	 */
	@SuppressWarnings("deprecation")
	public boolean tagFile(Path filePath, POSTaggerME tagger)
			throws IOException {
		BufferedReader reader = Files.newBufferedReader(filePath,
				StandardCharsets.UTF_8);
		StringBuilder content = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			content.append(line);
		}

		try {

			String taggedContent = tagger.tag(content.toString());
			log.info(taggedContent);

			log.info("Testing ....!!!" + filePath.getParent());

			File file = new File(filePath.getParent() + "_tagged/"
					+ filePath.getFileName() + ".tagged");
			file.getParentFile().mkdirs();
			file.createNewFile();

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(taggedContent);
			bw.close();
		} catch (IOException e) {
			throw new ClusterServiceException(
					new ErrorMessage(
							"Excepiton while writing tagged content to file system !! ",
							e.getCause()));
		}
		return true;
	}

	/**
	 * 
	 * @return appProp
	 */
	public ApplicationProperties getAppProp() {
		return appProp;
	}

	/**
	 * 
	 * @param appProp
	 *            set the appprop object through Auto wiring
	 */
	public void setAppProp(ApplicationProperties appProp) {
		this.appProp = appProp;
	}

	/**
	 * 
	 * @param path
	 *            - File system locataion
	 * @param files
	 *            - stores all the files names mentioned in path
	 */
	public void listFiles(Path path, List<Path> files) {
		try {
			DirectoryStream<Path> stream = Files.newDirectoryStream(path);

			for (Path entry : stream) {
				if (Files.isDirectory(entry)) {
					listFiles(entry, files);
				} else {
					files.add(entry);
				}
			}
			stream.close();
		} catch (IOException e) {
			throw new ClusterServiceException(new ErrorMessage(
					"Excepiton while listings file in input path !! " + path,
					e.getCause()));
		}
	}

	/**
	 * 
	 * @param args
	 *            main method for test purpose
	 */
	public static void main(String args[]) {
		OpenNLPPosTagger oTag = new OpenNLPPosTagger();
		Path path = Paths.get("/var/tmp/mail");
		List<Path> files = new ArrayList<Path>();
		oTag.listFiles(path, files);
		log.info(files.size());
	}
}