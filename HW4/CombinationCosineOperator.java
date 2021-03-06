// BSD License (http://lemurproject.org/galago-license)
package org.lemurproject.galago.core.retrieval.iterator.scoring;
import org.lemurproject.galago.core.retrieval.RequiredParameters;
import org.lemurproject.galago.core.retrieval.processing.ScoringContext;
import org.lemurproject.galago.core.retrieval.query.AnnotatedNode;
import org.lemurproject.galago.core.retrieval.query.NodeParameters;
import org.lemurproject.galago.core.retrieval.iterator.*;
import org.lemurproject.galago.core.retrieval.ann.OperatorDescription;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;



public class CombinationCosineOperator extends DisjunctionIterator implements ScoreIterator {

  NodeParameters np;
  protected ScoreIterator[] scoreIterators;
  //set to identify duplicate elements
  private HashSet<String> set;

  public CombinationCosineOperator(NodeParameters parameters,
    ScoreIterator[] childIterators) {
    super(childIterators);

    assert (childIterators.length > 0) : "#combine nodes must have more than 1 child.";

    this.np = parameters;
    this.set = new HashSet<String>();
    this.scoreIterators = childIterators;
  }

  @Override
  public double score(ScoringContext c) {
    double total_top = 0;
    double total_bottom = 0;

    //table contains frequency of word in a query.
    Hashtable<String,Integer> table = new Hashtable<String,Integer>();
    for (int i = 0; i < scoreIterators.length; i++) {
      try{
        String query = scoreIterators[i].getAnnotatedNode(new ScoringContext()).children.get(1).parameters;
        if(!table.containsKey(query)){
          table.put(query,1);
        }else{
          table.put(query,table.get(query)+1);
        }
      }catch (IOException e){
        System.err.println("Caught IOException: " + e.getMessage());
      }
    }

    //loop through every score again to calculate final score for the whole query.
    for (int i = 0; i < scoreIterators.length; i++) {
      double freqOfWordInQuery = 1.0;
      try{
        String query = scoreIterators[i].getAnnotatedNode(new ScoringContext()).children.get(1).parameters;
        if(set.contains(query)||query.equals("")){
          continue;
        }else{
          set.add(query);
          freqOfWordInQuery = table.get(query);
          double score_top = scoreIterators[i].score(c) * freqOfWordInQuery;
          double score_bottom= scoreIterators[i].score(c)*scoreIterators[i].score(c)*freqOfWordInQuery*freqOfWordInQuery;
          total_top += score_top;
          total_bottom += score_bottom;
        }
        
      }catch (IOException e){
        System.err.println("Caught IOException: " + e.getMessage());
      }
      
      
    }
    this.set.clear();
    return total_top / Math.sqrt(total_bottom);
  }

  @Override
  public double minimumScore() {
    double min = 0;
    for (int i = 0; i < scoreIterators.length; i++) {
      min += scoreIterators[i].minimumScore();
    }
    return min;
  }

  @Override
  public double maximumScore() {
    double max = 0;
    for (int i = 0; i < scoreIterators.length; i++) {
      max += scoreIterators[i].maximumScore();
    }
    return max;
  }

  @Override
  public String getValueString(ScoringContext c) throws IOException {
    return this.currentCandidate() + " " + this.score(c);
  }

  @Override
  public AnnotatedNode getAnnotatedNode(ScoringContext c) throws IOException {
    String type = "score";
    String className = this.getClass().getSimpleName();
    String parameters = np.toString();
    long document = currentCandidate();
    boolean atCandidate = hasMatch(c);
    String returnValue = Double.toString(score(c));
    List<AnnotatedNode> children = new ArrayList<>();
    for (BaseIterator child : this.iterators) {
      children.add(child.getAnnotatedNode(c));
    }

    return new AnnotatedNode(type, className, parameters, document, atCandidate, returnValue, children);
  }
}
