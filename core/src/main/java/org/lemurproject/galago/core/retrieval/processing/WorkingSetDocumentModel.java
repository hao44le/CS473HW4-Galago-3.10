// BSD License (http://lemurproject.org/galago-license)
package org.lemurproject.galago.core.retrieval.processing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import org.lemurproject.galago.core.index.Index;
import org.lemurproject.galago.core.retrieval.LocalRetrieval;
import org.lemurproject.galago.core.retrieval.ScoredDocument;
import org.lemurproject.galago.core.retrieval.iterator.MovableScoreIterator;
import org.lemurproject.galago.core.retrieval.query.Node;
import org.lemurproject.galago.tupleflow.Parameters;

/**
 * Performs straightforward document-at-a-time (daat) processing of a fully
 * annotated query, processing scores over documents.
 *
 * @author irmarc
 */
public class WorkingSetDocumentModel extends ProcessingModel {

  LocalRetrieval retrieval;
  Index index;

  public WorkingSetDocumentModel(LocalRetrieval lr) {
    retrieval = lr;
    this.index = retrieval.getIndex();
  }

  @Override
  public ScoredDocument[] execute(Node queryTree, Parameters queryParams) throws Exception {
    // This model uses the simplest ScoringContext
    ScoringContext context = new ScoringContext();

    // There should be a whitelist to deal with
    List l = queryParams.getList("working");
    if (l == null) {
      throw new IllegalArgumentException("Parameters must contain a 'working' parameter specifying the working set");
    }

    Class containedType = l.get(0).getClass();
    List<Integer> whitelist;
    if (Integer.class.isAssignableFrom(containedType)) {
      whitelist = (List<Integer>) l;
    } else if (Long.class.isAssignableFrom(containedType)) {
      // Sadly this will not directly translate for now - maybe when/if we move
      // Galago to using longs instead of ints...
      whitelist = new ArrayList<Integer>();
      for (Long docid : (List<Long>)l) {
        whitelist.add(docid.intValue());
      }
    } else if (String.class.isAssignableFrom(containedType)) {
      whitelist = retrieval.getDocumentIds((List<String>) l);
    } else {
      throw new IllegalArgumentException(
              String.format("Parameter 'working' must be a list of longs or a list of strings. Found type %s\n.",
              containedType.toString()));
    }
    Collections.sort(whitelist);

    // construct the query iterators
    MovableScoreIterator iterator =
            (MovableScoreIterator) retrieval.createIterator(queryParams, queryTree, context);
    int requested = (int) queryParams.get("requested", 1000);
    boolean annotate = queryParams.get("annotate", false);

    // now there should be an iterator at the root of this tree
    PriorityQueue<ScoredDocument> queue = new PriorityQueue<ScoredDocument>();

    for (int i = 0; i < whitelist.size(); i++) {
      int document = whitelist.get(i);
      iterator.syncTo(document);
      context.document = document;

      // This context is shared among all scorers
      double score = iterator.score();
      if (requested < 0 || queue.size() <= requested || queue.peek().score < score) {
        ScoredDocument scoredDocument = new ScoredDocument(document, score);
        if (annotate) {
          scoredDocument.annotation = iterator.getAnnotatedNode();
        }
        queue.add(scoredDocument);
        if (requested > 0 && queue.size() > requested) {
          queue.poll();
        }
      }
    }
    return toReversedArray(queue);
  }
}
