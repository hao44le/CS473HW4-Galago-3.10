// BSD License (http://lemurproject.org/galago-license)
package org.lemurproject.galago.core.retrieval.iterator.scoring;

import java.io.IOException;
import org.lemurproject.galago.core.retrieval.query.NodeParameters;
import org.lemurproject.galago.core.retrieval.structured.RequiredParameters;
import org.lemurproject.galago.core.retrieval.structured.RequiredStatistics;

/**
 * Smoothes raw counts according to the BM25 scoring model, as described by
 * "Experimentation as a way of life: Okapi at TREC" by Robertson, Walker, and Beaulieu.
 * (http://www.sciencedirect.com/science/article/pii/S0306457399000461)
 *
 * @author irmarc
 * @deprecated 
 */
@RequiredStatistics(statistics = {"nodeDocumentCount", "collectionLength", "documentCount"})
@RequiredParameters(parameters = {"b", "k"})
public class BM25Scorer implements ScoringFunction {

  double b;
  double k;
  double avgDocLength;
  double idf;
  long documentCount;
  
  public BM25Scorer(NodeParameters parameters) throws IOException {
    b = parameters.get("b", 0.75);
    k = parameters.get("k", 1.2);

    double collectionLength = parameters.getLong("collectionLength");
    documentCount = parameters.getLong("documentCount");
    avgDocLength = (collectionLength + 0.0) / (documentCount + 0.0);

    // now get idf
    long df = parameters.getLong("nodeDocumentCount");
    // I'm not convinced this is the correct idf formulation -- MAC
    //idf = Math.log((documentCount - df + 0.5) / (df + 0.5));
    
    idf = Math.log(documentCount / (df + 0.5));
  }

  public double score(int count, int length) {
    double numerator = count * (k + 1);
    double denominator = count + (k * (1 - b + (b * length / avgDocLength)));
    return idf * numerator / denominator;
  }

  public double score(int count, int length, double externalIDF) {
    double numerator = count * (k + 1);
    double denominator = count + (k * (1 - b + (b * length / avgDocLength)));
    return externalIDF * numerator / denominator;
  }
    
  public double getIDF() {
      return idf;
  }
  public void setIDF(double newIDF) {
      idf = newIDF;
  }
  
  public long getDocumentCount() {
      return documentCount;
  }
  
  public void setDocumentCount(int dc) {
      documentCount = dc;
  }
}