/*
 *  BSD License (http://lemurproject.org/galago-license)
 */
package org.lemurproject.galago.core.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import junit.framework.TestCase;
import org.lemurproject.galago.core.retrieval.ScoredDocument;

/**
 *
 * @author sjh
 */
public class FixedSizeMinHeapTest extends TestCase {
  
  public FixedSizeMinHeapTest(String testName) {
    super(testName);
  }
  
  public void testSomeMethod() {
    
    Comparator<ScoredDocument> cmp = new ScoredDocument.ScoredDocumentComparator();
    ScoredDocument[] all = new ScoredDocument[1000];
    FixedSizeMinHeap<ScoredDocument> heap = new FixedSizeMinHeap(ScoredDocument.class, 100, cmp);
    Random r = new Random(111);
    
    for (int i = 0; i < 1000; i++) {
      ScoredDocument d = new ScoredDocument(i, Math.abs(r.nextDouble()) % 1000);
      //ScoredDocument d = new ScoredDocument(i, i);
      all[i] = d;
      heap.offer(d);
    }
    
    
    ScoredDocument[] heapData = heap.getSortedArray();

    // sort into decreasing order
    Arrays.sort(all, Collections.reverseOrder(cmp));
    
    for (int i = 0; i < heapData.length; i++) {
      assertEquals(heapData[i], all[i]); // by pointer comparison should be fine.
    }
  }
}
