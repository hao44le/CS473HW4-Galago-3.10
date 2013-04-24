// BSD License (http://lemurproject.org/galago-license)
package org.lemurproject.galago.core.retrieval.traversal;

import java.io.File;
import junit.framework.TestCase;
import org.lemurproject.galago.core.retrieval.LocalRetrieval;
import org.lemurproject.galago.core.retrieval.LocalRetrievalTest;
import org.lemurproject.galago.core.retrieval.RetrievalFactory;
import org.lemurproject.galago.core.retrieval.query.Node;
import org.lemurproject.galago.core.retrieval.query.StructuredQuery;
import org.lemurproject.galago.tupleflow.Parameters;
import org.lemurproject.galago.tupleflow.Utility;

/**
 * This test is seriously a pain so all traversals that make use of 2 rounds of
 * retrieval should use the testing infrastructure set up here.
 *
 * If you want to print the various statistics, uncomment some of the print
 * calls below.
 *
 * TODO: Make stronger tests to increase confidence
 *
 * @author irmarc, sjh, dietz
 */
public class RelevanceFeedbackTraversalTest extends TestCase {

  File relsFile = null;
  File queryFile = null;
  File scoresFile = null;
  File trecCorpusFile = null;
  File corpusFile = null;
  File indexFile = null;

  public RelevanceFeedbackTraversalTest(String testName) {
    super(testName);
  }

  // Build an index based on 10 short docs
  @Override
  public void setUp() throws Exception {
    File[] files = LocalRetrievalTest.make10DocIndex();
    trecCorpusFile = files[0];
    corpusFile = files[1];
    indexFile = files[2];
  }

  public void testRelevanceModelTraversal() throws Exception {
    // Create a retrieval object for use by the traversal
    Parameters p = new Parameters();
    p.set("index", indexFile.getAbsolutePath());
    p.set("stemmedPostings", false);
    p.set("fbOrigWt", 0.5);

    LocalRetrieval retrieval = (LocalRetrieval) RetrievalFactory.instance(p);
    RelevanceModelTraversal traversal = new RelevanceModelTraversal(retrieval, new Parameters());

    Node parsedTree = StructuredQuery.parse("#rm:fbTerms=3:fbDocs=2( #feature:dirichlet( #extents:fits:part=postings() ) )");
    Node transformed = StructuredQuery.copy(traversal, parsedTree);
    // truth data
    StringBuilder correct = new StringBuilder();
    correct.append("#combine:0=0.5:1=0.5( ");
    correct.append("#combine:w=1.0( #feature:dirichlet( #extents:fits:part=postings() ) ) ");
    correct.append("#combine:0=0.12516622340425526:1=0.04161125886524822:2=0.04161125886524822( ");
    correct.append("#feature:dirichlet( #lengths:document:part=lengths() #extents:program:part=postings() ) ");
    correct.append("#feature:dirichlet( #lengths:document:part=lengths() #extents:shoe:part=postings() ) ");
    correct.append("#feature:dirichlet( #lengths:document:part=lengths() #extents:ugly:part=postings() ) ) )");

    assertEquals(correct.toString(), transformed.toString());
    retrieval.close();
  }

  public void testRelevance2ModelTraversal() throws Exception {
    // Create a retrieval object for use by the traversal
    Parameters p = new Parameters();
    p.set("index", indexFile.getAbsolutePath());
    p.set("stemmedPostings", false);
    p.set("fb2Pass", false); // passage retrieval switched to off!
    p.set("fbOrigWt", 0.5);
    
    // these parameters should not be used
    Parameters rmParams2Pass = new Parameters();
    rmParams2Pass.set("passageQuery", true);
    rmParams2Pass.set("passageSize", 3);
    rmParams2Pass.set("passageShift", 1);
    p.set("fbParams2Pass", rmParams2Pass);

    LocalRetrieval retrieval = (LocalRetrieval) RetrievalFactory.instance(p);
    RelevanceModelTraversal traversal = new RelevanceModelTraversal(retrieval, new Parameters());

    Node parsedTree = StructuredQuery.parse("#rm:fbTerms=5:fbDocs=10( #feature:dirichlet( #extents:jumped:part=postings() ) )");
    Node transformed = StructuredQuery.copy(traversal, parsedTree);
    // truth data
    StringBuilder correct = new StringBuilder();
    correct.append("#combine:0=0.5:1=0.5( #combine:w=1.0( #feature:dirichlet( #extents:jumped:part=postings() ) ) ");
    correct.append("#combine:0=0.05001660577881102:1=0.05001660577881102:2=0.04165282851765748:3=0.04165282851765748( ");
    correct.append("#feature:dirichlet( #lengths:document:part=lengths() #extents:sample:part=postings() ) ");
    correct.append("#feature:dirichlet( #lengths:document:part=lengths() #extents:ugly:part=postings() ) ");
    correct.append("#feature:dirichlet( #lengths:document:part=lengths() #extents:cat:part=postings() ) ");
    correct.append("#feature:dirichlet( #lengths:document:part=lengths() #extents:moon:part=postings() ) ) )");
    
    assertEquals(correct.toString(), transformed.toString());
    
    retrieval.close();
  }

  public void testPassageRelevanceModelTraversal() throws Exception {
    // Create a retrieval object for use by the traversal
    Parameters p = new Parameters();
    p.set("index", indexFile.getAbsolutePath());
    p.set("stemmedPostings", false);
    p.set("fb2Pass", true);
    p.set("fbOrigWt", 0.5);

    // these parameters should not be used
    Parameters rmParams2Pass = new Parameters();
    rmParams2Pass.set("passageQuery", true);
    rmParams2Pass.set("passageSize", 3);
    rmParams2Pass.set("passageShift", 1);
    p.set("fbParams2Pass", rmParams2Pass);

    System.err.println("Here1");
    
    LocalRetrieval retrieval = (LocalRetrieval) RetrievalFactory.instance(p);
    RelevanceModelTraversal traversal = new RelevanceModelTraversal(retrieval, new Parameters());

    System.err.println("Here2");

    Node parsedTree = StructuredQuery.parse("#rm:fbTerms=5:fbDocs=10( #feature:dirichlet( #extents:jumped:part=postings() ) )");
    Node transformed = StructuredQuery.copy(traversal, parsedTree);

    System.err.println("Here3");
    
    // truth data
    StringBuilder correct = new StringBuilder();
    correct.append("#combine:0=0.5:1=0.5( #combine:w=1.0( #feature:dirichlet( #extents:jumped:part=postings() ) ) ");
    correct.append("#combine:0=0.018518518518518517:1=0.018518518518518517:2=0.009259259259259259( ");
    correct.append("#feature:dirichlet( #lengths:document:part=lengths() #extents:cat:part=postings() ) ");
    correct.append("#feature:dirichlet( #lengths:document:part=lengths() #extents:sample:part=postings() ) ");
    correct.append("#feature:dirichlet( #lengths:document:part=lengths() #extents:ugly:part=postings() ) ) )");

    System.err.println(transformed.toString());
    System.err.println(correct.toString());
    
    assertEquals(correct.toString(), transformed.toString());
    retrieval.close();
  }

  public void testIllSpecifiedPassageRelevanceModelTraversalShouldNotHang() throws Exception {
    // Create a retrieval object for use by the traversal
    Parameters p = new Parameters();
    p.set("index", indexFile.getAbsolutePath());
    p.set("corpus", corpusFile.getAbsolutePath());
    p.set("stemmedPostings", false);
    p.set("fb2Pass", true);
    p.set("fbOrigWt", 0.5);

    Parameters fbParams = new Parameters();
    fbParams.set("passageQuery", true);
    fbParams.set("passageSize", 3);
    fbParams.set("passageShift", 1);
//    p.set("fbParams2Pass",rmParams);
    
    LocalRetrieval retrieval = (LocalRetrieval) RetrievalFactory.instance(p);
    RelevanceModelTraversal traversal = new RelevanceModelTraversal(retrieval, new Parameters());

    Node parsedTree = StructuredQuery.parse("#rm:fbTerms=5:fbDocs=10( #feature:dirichlet( #extents:jumped:part=postings() ) )");
    Node transformed = StructuredQuery.copy(traversal, parsedTree);
    
    retrieval.close();
  }

  public void testClassloaderRelevanceModelTraversal() throws Exception {
    // Create a retrieval object for use by the traversal
    Parameters p = new Parameters();
    p.set("index", indexFile.getAbsolutePath());
    p.set("corpus", corpusFile.getAbsolutePath());
    p.set("stemmedPostings", false);
    p.set("fb2Pass", true);
    p.set("fbOrigWt", 0.5);

    Parameters rmParams = new Parameters();
    rmParams.set("passageQuery", true);
    rmParams.set("passageSize", 3);
    rmParams.set("passageShift", 1);
    p.set("fbParams2Pass", rmParams);
    p.set("relevanceModel", "org.lemurproject.galago.core.scoring.RelevanceModel");
    
    LocalRetrieval retrieval = (LocalRetrieval) RetrievalFactory.instance(p);
    RelevanceModelTraversal traversal = new RelevanceModelTraversal(retrieval, new Parameters());

    Node parsedTree = StructuredQuery.parse("#rm:fbTerms=5:fbDocs=10( #feature:dirichlet( #extents:jumped:part=postings() ) )");
    Node transformed = StructuredQuery.copy(traversal, parsedTree);

    // truth data
    StringBuilder correct = new StringBuilder();
    correct.append("#combine:0=0.5:1=0.5( #combine:w=1.0( #feature:dirichlet( #extents:jumped:part=postings() ) ) ");
    correct.append("#combine:0=0.018518518518518517:1=0.018518518518518517:2=0.009259259259259259( ");
    correct.append("#feature:dirichlet( #lengths:document:part=lengths() #extents:cat:part=postings() ) ");
    correct.append("#feature:dirichlet( #lengths:document:part=lengths() #extents:sample:part=postings() ) ");
    correct.append("#feature:dirichlet( #lengths:document:part=lengths() #extents:ugly:part=postings() ) ) )");


    assertEquals(correct.toString(), transformed.toString());
    retrieval.close();
  }

  public void testBM25RelevanceFeedbackTraversal() throws Exception {
    // Create a retrieval object for use by the traversal
    Parameters p = new Parameters();
    p.set("retrievalGroup", "all");
    p.set("index", indexFile.getAbsolutePath());
    p.set("corpus", corpusFile.getAbsolutePath());
    LocalRetrieval retrieval = (LocalRetrieval) RetrievalFactory.instance(p);
    BM25RelevanceFeedbackTraversal traversal = new BM25RelevanceFeedbackTraversal(retrieval);
    Node parsedTree = StructuredQuery.parse("#bm25rf:fbDocs=3:fbTerms=2( #feature:bm25( #extents:cat:part=postings() ) )");
    Node transformed = StructuredQuery.copy(traversal, parsedTree);
    //truth data
    StringBuilder correct = new StringBuilder();
    correct.append("#combine( #feature:bm25( #extents:cat:part=postings() ) ");
    correct.append("#feature:bm25rf:R=3:rt=1( #extents:jumped:part=postings() ) ");
    correct.append("#feature:bm25rf:R=3:rt=2( #extents:moon:part=postings() ) )");

    assertEquals(correct.toString(), transformed.toString());

    retrieval.close();
  }

  @Override
  public void tearDown() throws Exception {
    if (relsFile != null) {
      relsFile.delete();
    }
    if (queryFile != null) {
      queryFile.delete();
    }
    if (scoresFile != null) {
      scoresFile.delete();
    }
    if (trecCorpusFile != null) {
      trecCorpusFile.delete();
    }
    if (corpusFile != null) {
      Utility.deleteDirectory(corpusFile);
    }
    if (indexFile != null) {
      Utility.deleteDirectory(indexFile);
    }
  }
}
