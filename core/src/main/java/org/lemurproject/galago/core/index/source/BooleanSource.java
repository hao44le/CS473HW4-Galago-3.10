// BSD License (http://lemurproject.org/galago-license)
package org.lemurproject.galago.core.index.source;

/**
 *
 * @author jfoley
 */
public interface BooleanSource extends DiskSource {
  public boolean indicator(long id);
}
