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

5) Stemming
  >> Stemming includes removal of similar words and base line them to a single meaningful word.
     For e.g. running, ran, run can be stemmed to single word "run".

6) Pruning
  >> Low frequency words are removed from word list.

7) Clustering
  >> Apply clustering algorithm to form Clusters.
  
# Note
======
You need to checkout web-crawler project as well in order to work with DataMining. DataMining project has internally dependencies on web-crawler project.

First build web-crawler project and then build DataMining project.
