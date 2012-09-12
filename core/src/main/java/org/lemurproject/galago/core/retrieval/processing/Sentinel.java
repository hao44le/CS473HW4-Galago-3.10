/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.lemurproject.galago.core.retrieval.processing;

import org.lemurproject.galago.core.retrieval.iterator.DeltaScoringIterator;

/**
 *
 * @author irmarc
 */
public class Sentinel {

  public double score;
  public DeltaScoringIterator iterator;

  public Sentinel(DeltaScoringIterator it, double s) {
    this.score = s;
    this.iterator = it;
  }
}