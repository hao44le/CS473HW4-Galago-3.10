// BSD License (http://lemurproject.org/galago-license)
package org.lemurproject.galago.core.retrieval.iterator.disk;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.lemurproject.galago.core.index.disk.StreamCountSource;
import org.lemurproject.galago.core.index.stats.NodeAggregateIterator;
import org.lemurproject.galago.core.index.stats.NodeStatistics;
import org.lemurproject.galago.core.retrieval.iterator.CountIterator;
import org.lemurproject.galago.core.retrieval.iterator.SourceIterator;
import org.lemurproject.galago.core.retrieval.query.AnnotatedNode;

/**
 *
 * @author jfoley
 */
public class StreamCountIterator extends SourceIterator<StreamCountSource>
  implements NodeAggregateIterator, CountIterator {
  
  public StreamCountIterator(StreamCountSource src) {
    super(src);
  }

  @Override
  public String getValueString() throws IOException {
    return String.format("%s,%d,%d", getKeyString(), currentCandidate(), count());
  }

  @Override
  public AnnotatedNode getAnnotatedNode() throws IOException {
    String type = "counts";
    String className = this.getClass().getSimpleName();
    String parameters = this.getKeyString();
    int document = currentCandidate();
    boolean atCandidate = hasMatch(this.context.document);
    String returnValue = Integer.toString(count());
    List<AnnotatedNode> children = Collections.EMPTY_LIST;
    return new AnnotatedNode(type, className, parameters, document, atCandidate, returnValue, children);
  }

  @Override
  public NodeStatistics getStatistics() {
    NodeStatistics stats = new NodeStatistics();
    stats.node = getKeyString();
    stats.nodeFrequency = source.collectionCount;
    stats.nodeDocumentCount = source.documentCount;
    stats.maximumCount = source.maximumPositionCount;
    return stats;
  }

  @Override
  public int count() {
    return (int) source.count(context.document);
  }

  @Override
  public int maximumCount() {
    return source.maximumPositionCount;
  }
  
}
