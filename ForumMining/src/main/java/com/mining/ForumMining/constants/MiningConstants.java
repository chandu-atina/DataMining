package com.mining.ForumMining.constants;

import java.util.ArrayList;
import java.util.List;

public class MiningConstants {

	public static List<String> POSTAG_LIST = new ArrayList<String>();

	static {
		POSTAG_LIST.add("JJ");
		POSTAG_LIST.add("JJR");
		POSTAG_LIST.add("JJS");

		POSTAG_LIST.add("NN");
		POSTAG_LIST.add("NNS");
		POSTAG_LIST.add("NNP");
		POSTAG_LIST.add("NNPS");

		POSTAG_LIST.add("VB");
		POSTAG_LIST.add("VBG");
		POSTAG_LIST.add("VBD");
		POSTAG_LIST.add("VBN");
		POSTAG_LIST.add("VBP");
		POSTAG_LIST.add("VBZ");
	}

}
