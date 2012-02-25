/*
 * BSD License (http://lemurproject.org/galago-license)

 */
package org.lemurproject.galago.core.retrieval.iterator;

import java.text.ParseException;
import java.text.DateFormat;
import org.lemurproject.galago.core.index.disk.FieldIndexReader;
import org.lemurproject.galago.core.retrieval.query.NodeParameters;

/**
 *
 * @author irmarc
 */
public class EqualityIterator extends FieldComparisonIterator {

  public EqualityIterator(NodeParameters p, FieldIndexReader.ListIterator fieldIterator) {
    super(p, fieldIterator);
    parseField(p);
  }

  public boolean hasMatch(int identifier) {
    if (currentCandidate() != identifier) {
      return false;
    } else if (format.equals("string")) {
      return (iterator.stringValue().equals(strValue));
    } else if (format.equals("int")) {
      return (iterator.intValue() == intValue);
    } else if (format.equals("long")) {
      return (iterator.longValue() == longValue);
    } else if (format.equals("float")) {
      return (iterator.floatValue() == floatValue);
    } else if (format.equals("double")) {
      return (iterator.doubleValue() == doubleValue);
    } else if (format.equals("date")) {
      return (iterator.dateValue() == dateValue);
    } else {
      throw new RuntimeException(String.format("Don't have any plausible format for tag %s\n",
              format));
    }
  }
}
