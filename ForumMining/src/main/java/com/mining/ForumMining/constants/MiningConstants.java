package com.mining.ForumMining.constants;

/*
 * ##############################$History Card$###################################
 * ### Latest changes description should be on the top of the history card list###
 * ###############################################################################
 *  Created Date	Updated Date	Author			Change Description
 *  ============	============	============	===================
 *  28/05/2015		29/05/2015		chandu-atina 	Initial basic version
 */
import java.util.ArrayList;
import java.util.List;

/*
 * CC Coordinating conjunction
 * CD Cardinal number
 * DT Determiner
 * EX Existential there
 * FW Foreign word
 * IN Preposition or subordinating conjunction
 * JJ Adjective
 * JJR Adjective, comparative
 * JJS Adjective, superlative
 * LS List item marker
 * MD Modal
 * NN Noun, singular or mass
 * NNS Noun, plural
 * NNP Proper noun, singular
 * NNPS Proper noun, plural
 * PDT Predeterminer
 * POS Possessive ending
 * PRP Personal pronoun
 * PRP$ Possessive pronoun
 * RB Adverb
 * RBR Adverb, comparative
 * RBS Adverb, superlative
 * RP Particle
 * SYM Symbol
 * TO to
 * UH Interjection
 * VB Verb, base form
 * VBD Verb, past tense
 * VBG Verb, gerund or present participle
 * VBN Verb, past participle
 * VBP Verb, non­3rd person singular present
 * VBZ Verb, 3rd person singular present
 * WDT Wh­determiner
 * WP Wh­pronoun
 * WP$ Possessive wh­pronoun
 * WRB Wh­adverb
 */
/**
 * Mining constants class holds the set of constants used across the clustering
 * process.
 *
 */
public class MiningConstants {

	public static List<String> POSTAG_LIST = new ArrayList<String>();

	public static Integer TERM_THRESHOLD_COUNT = 100;

	public static Double COSINE_SIMILARITY_THRESHOLD = 0.5;

	public static Integer MAX_THREAD_COUNT = 10;

	static {
		POSTAG_LIST.add("JJ"); // Adjective
		POSTAG_LIST.add("JJR"); // Adjective Comparitive
		POSTAG_LIST.add("JJS"); // Adjective Superlative

		POSTAG_LIST.add("NN"); // Noun, singular or mass
		POSTAG_LIST.add("NNS"); // Noun, plural
		POSTAG_LIST.add("NNP"); // Proper noun, singular
		POSTAG_LIST.add("NNPS"); // Proper noun, plural

		POSTAG_LIST.add("VB"); // Verb, base form
		POSTAG_LIST.add("VBG"); // Verb, past tense
		POSTAG_LIST.add("VBD"); // Verb, gerund or present participle
		POSTAG_LIST.add("VBN"); // Verb, past participle
		POSTAG_LIST.add("VBP"); // Verb, non­3rd person singular present
		POSTAG_LIST.add("VBZ"); // Verb, 3rd person singular present
	}

}
