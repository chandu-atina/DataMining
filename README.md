# DataMining
=============

This project has various phases which involves the sequence of steps.

1) Web Crawling
  >> Crawling the web forums to get the appropriate data and store them in flat files

2) Pre-Processing the data
  >> Data Pre-Processing involves organizing the noisey data and inappropriate data into appropriate 
     format for Pos Tagging

3) PoS Tagging
  >> Parts of Speech Tagging is done to the processed data using a few standard Pos Taggers like 
    Stanford PoS Tagger, OpenNLP Tagger, LTAG-Spinal etc

4) Stop-Word Removal
  >> Stop-Word removal includes removal of unmeaning full words, common words etc

5) Stemming & Lemmatization
  >> Stemming includes removal of similar words and base line them to a single meaningful word.
     For e.g. running,run can be stemmed to single word "run".
  >> Lemmatisation (or lemmatization) is the process of grouping together the different inflected forms of a word so        they can be analysed as a single item includes removal of similar words and base line them to a single meaningful      word.
     For e.g. running, ran, run can be lemmatized to single word "run".

6) Pruning
  >> Low frequency words are removed from word list.
  
7) Weighting
  >> Weightage is given to each and every term inside the document by calculating "tfidf". It is the product of term        frequency and inverse document frequency.
      tf idft = tf · (log 2 n − log 2 dft + 1)
      
      tf  - term frequency
      dft - the number of documents in which term 't' appears
      n   - no.of documents
      
8) Cosine Similarity
  >> COsine distnace between two document vectors
      s(d i , d j ) = cos( ( d i , d j )) =   di·dj
                                          
                                          ________________      
                                          
                                             |di|·|dj|
    Cosine Similarity(Doc1,Doc2) = Dot product(Doc1,Doc2) / ||Doc1||*||Doc2||
  
9) Clustering
  >> Apply clustering algorithm to form Clusters.
  
Note
======
You need to checkout web-crawler project as well in order to work with DataMining. DataMining project has internally dependencies on web-crawler project.

First build web-crawler project and then build DataMining project.
