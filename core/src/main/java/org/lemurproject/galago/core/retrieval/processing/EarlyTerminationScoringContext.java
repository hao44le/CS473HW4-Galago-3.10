// BSD License (http://lemurproject.org/galago-license)
package org.lemurproject.galago.core.retrieval.processing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import org.lemurproject.galago.core.retrieval.iterator.DeltaScoringIterator;
import org.lemurproject.galago.core.retrieval.iterator.BaseIterator;
import org.lemurproject.galago.core.retrieval.query.Node;

/**
 * The scoring context needed for early-termination style processing.
 * 
 * @author irmarc
 */
public class EarlyTerminationScoringContext extends ScoringContext {


  public EarlyTerminationScoringContext() {
    scorers = new ArrayList<DeltaScoringIterator>();
    members = new HashSet<BaseIterator>();
    sentinelIndex = 0;
  }
  public ArrayList<DeltaScoringIterator> scorers;
  public HashSet<BaseIterator> members;
  public double[] startingPotentials;
  public double[] potentials;
  public double runningScore;
  public double startingPotential;
  public int sentinelIndex;
  public double minCandidateScore;
}
