/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.lemurproject.galago.core.retrieval.traversal;

import org.lemurproject.galago.core.retrieval.processing.FilteredStatisticsScoringContext;
import org.lemurproject.galago.core.retrieval.query.Node;
import org.lemurproject.galago.core.retrieval.query.NodeParameters;

/**
 *
 * @author irmarc
 */
public class AdjustAnnotationsTraversal extends Traversal {

  FilteredStatisticsScoringContext context;

  public AdjustAnnotationsTraversal(FilteredStatisticsScoringContext c) {
    this.context = c;
  }

  @Override
  public Node afterNode(Node original) throws Exception {
    return original;
  }

  @Override
  public void beforeNode(Node object) throws Exception {
    if (object.numChildren() > 0 && object.getOperator().equals("feature")) {
      Node key = object.getChild(0);
      // Update statistics in place.
      NodeParameters np = object.getNodeParameters();
      if (np.containsKey("nodeFrequency")) {
        np.set("nodeFrequency",
                context.tfs.get(key));
      }
      if (np.containsKey("nodeDocumentCount")) {
        np.set("nodeDocumentCount",
                context.dfs.get(key));
      }
      if (np.containsKey("collectionLength")) {
        np.set("collectionLength", context.collectionLength);
      }
      if (np.containsKey("documentCount")) {
        np.set("documentCount", context.documentCount);
      }
      if (np.containsKey("collectionProbability")) {
        int collectionCount = context.tfs.get(key);
        if (collectionCount > 0) {
          np.set("collectionProbability",
                  ((double) collectionCount) / context.collectionLength);
        } else {
          np.set("collectionProbability", 0.5 / (double) context.collectionLength);
        }
      }
    }
  }
}