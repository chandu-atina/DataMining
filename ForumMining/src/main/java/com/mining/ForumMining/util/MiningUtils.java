/*
 * ##############################$History Card$###################################
 * ### Latest changes description should be on the top of the history card list###
 * ###############################################################################
 *  Created Date	Updated Date	Author			Change Description
 *  ============	============	============	===================
 *  19/06/2015		29/05/2015		chandu-atina 	Initial basic version
 */
package com.mining.ForumMining.util;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.apporiented.algorithm.clustering.Cluster;
import com.crawl.web.util.messages.ErrorMessage;
import com.mining.ForumMining.exception.ClusterServiceException;

/**
 * 
 * @author chandrasekhara
 *
 *MiningUtils provide a few utility methods useful for clustering
 */
@Component
public class MiningUtils {
	
	final static Logger log = Logger.getLogger(MiningUtils.class);

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
	
    public void printClusterHierarchy(int indent,Cluster c) {
    	String s="";
        for (int i = 0; i < indent; i++) {
            s=s.concat(" ");
        }
        //String name = c.getName() + (c.isLeaf() ? " (leaf)" : "") + (c.getDistance() != null ? "  distance: " + c.getDistance() : "");
        String name=c.getName();
        log.info(s+name);
        for (Cluster child : c.getChildren()) {
            printClusterHierarchy(indent + 2,child);
        }
    }
}
