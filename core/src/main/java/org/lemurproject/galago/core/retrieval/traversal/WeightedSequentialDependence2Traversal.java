// BSD License (http://lemurproject.org/galago-license)
package org.lemurproject.galago.core.retrieval.traversal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.lemurproject.galago.core.index.stats.AggregateStatistic;
import org.lemurproject.galago.core.index.stats.NodeStatistics;
import org.lemurproject.galago.core.retrieval.GroupRetrieval;
import org.lemurproject.galago.core.retrieval.query.Node;
import org.lemurproject.galago.core.retrieval.query.MalformedQueryException;
import org.lemurproject.galago.core.retrieval.Retrieval;
import org.lemurproject.galago.core.retrieval.query.NodeParameters;
import org.lemurproject.galago.core.util.TextPartAssigner;
import org.lemurproject.galago.tupleflow.Parameters;
import org.lemurproject.galago.tupleflow.Parameters.Type;

/**
 * Weighted Sequential Dependency Model model is structurally similar to the
 * Sequential Dependency Model, however node weights are the linear combination
 * of some node features.
 *
 * (based on bendersky 2012, uses fewer parameters)
 *
 * In particular the weight for a node "term" is determined as a linear
 * combination of features:
 *
 * Feature def for WSDM: <br>
 * { <br>
 * name : "1-gram" <br>
 * tfFeature : [true | false] :: asserts [ tf or df ], (tf default) <br>
 * group : "retrievalGroupName" :: missing or empty = default <br>
 * part : "retrievalPartName" :: missing or empty = default <br>
 * unigram : true|false :: can be used on unigrams <br>
 * bigram : true|false :: can be used on bigrams <br>
 * } <br>
 *
 * @author sjh
 */
public class WeightedSequentialDependence2Traversal extends Traversal {

  private static final Logger logger = Logger.getLogger("WSDM2");
  private Retrieval retrieval;
  private GroupRetrieval gRetrieval;
  private Parameters globalParams;
  private boolean defCombNorm;
  private boolean verbose;
  private List<WSDMFeature> uniFeatures;
  private List<WSDMFeature> biFeatures;
  
  public WeightedSequentialDependence2Traversal(Retrieval retrieval) throws Exception {
    if (retrieval instanceof GroupRetrieval) {
      gRetrieval = (GroupRetrieval) retrieval;
    }
    this.retrieval = retrieval;
    
    this.globalParams = retrieval.getGlobalParameters();
    
    verbose = globalParams.get("verboseWSDM", false);
    defCombNorm = globalParams.get("norm", false);
    
    uniFeatures = new ArrayList();
    biFeatures = new ArrayList();
    
    if (globalParams.isList("wsdmFeatures", Type.MAP)) {
      for (Parameters f : (List<Parameters>) globalParams.getList("wsdmFeatures")) {
        WSDMFeature wf = new WSDMFeature(f);
        if (wf.unigram) {
          uniFeatures.add(wf);
        }
        if (wf.bigram) {
          biFeatures.add(wf);
        }
      }
      
    } else {
      // default list of features: (using target collection only)
      uniFeatures.add(new WSDMFeature("1-const", WSDMFeatureType.CONST));
      uniFeatures.add(new WSDMFeature("1-tf", WSDMFeatureType.TF));
      uniFeatures.add(new WSDMFeature("1-df", WSDMFeatureType.DF));
      
      biFeatures.add(new WSDMFeature("2-const", WSDMFeatureType.CONST));
      biFeatures.add(new WSDMFeature("2-tf", WSDMFeatureType.TF));
      biFeatures.add(new WSDMFeature("2-df", WSDMFeatureType.DF));
    }
  }
  
  @Override
  public void beforeNode(Node original, Parameters queryParameters) throws Exception {
  }
  
  @Override
  public Node afterNode(Node original, Parameters queryParams) throws Exception {
    if (original.getOperator().equals("wsdm2")) {
      
      NodeParameters np = original.getNodeParameters();

      // First check format - should only contain text node children
      List<Node> children = original.getInternalNodes();
      for (Node child : children) {
        if (child.getOperator().equals("text") == false) {
          throw new MalformedQueryException("wsdm operator requires text-only children");
        }
      }

      // formatting is ok - now reassemble
      ArrayList<Node> newChildren = new ArrayList();
      NodeParameters newWeights = new NodeParameters();
      // i don't want normalization -- even though michael used some.
      newWeights.set("norm", defCombNorm);
      
      
      for (Node child : children) {
        String term = child.getDefaultParameter();
        
        double weight = computeWeight(term, np, queryParams);
        newWeights.set(Integer.toString(newChildren.size()), weight);
        newChildren.add(child.clone());
      }
      
      for (int i = 0; i < (children.size() - 1); i++) {
        ArrayList<Node> pair = new ArrayList();
        pair.add(new Node("extents", children.get(i).getDefaultParameter()));
        pair.add(new Node("extents", children.get(i + 1).getDefaultParameter()));
        
        double weight = computeWeight(pair.get(0).getDefaultParameter(), pair.get(1).getDefaultParameter(), np, queryParams);
        
        newWeights.set(Integer.toString(newChildren.size()), weight);
        newChildren.add(new Node("od", new NodeParameters(1), Node.cloneNodeList(pair)));
        
        newWeights.set(Integer.toString(newChildren.size()), weight);
        newChildren.add(new Node("uw", new NodeParameters(8), Node.cloneNodeList(pair)));
      }
      
      Node wsdm = new Node("combine", newWeights, newChildren, original.getPosition());
      
      if (verbose) {
        System.err.println(wsdm.toPrettyString());
      }
      
      return wsdm;
    } else {
      return original;
    }
  }
  
  private double computeWeight(String term, NodeParameters np, Parameters queryParams) throws Exception {

    // we will probably need this for several features : 
    Node t = new Node("counts", term);
    t = TextPartAssigner.assignPart(t, queryParams, retrieval.getAvailableParts());

    // feature value store
    Map<WSDMFeature, Double> featureValues = new HashMap();

    // tf/df comes from the same object - can be used  twice
    Map<String, AggregateStatistic> localCache = new HashMap();

    // NOW : collect some feature values
    Node node;
    NodeStatistics featureStats;
    String cacheString;
    
    for (WSDMFeature f : uniFeatures) {
      switch (f.type) {
        case CONST:
          assert (!featureValues.containsKey(f));
          featureValues.put(f, 1.0);
          break;
        
        case TF:
          assert (!featureValues.containsKey(f));
          node = t;
          if (!f.part.isEmpty()) {
            node = t.clone();
            node.getNodeParameters().set("part", f.part);
          }
          cacheString = node.toString() + "-" + f.group;
          
          if (localCache.containsKey(cacheString)) {
            featureStats = (NodeStatistics) localCache.get(cacheString);
          } else if (!f.group.isEmpty()) {
            featureStats = gRetrieval.getNodeStatistics(node, f.group);
            localCache.put(cacheString, featureStats);
          } else {
            featureStats = this.retrieval.getNodeStatistics(node);
            localCache.put(cacheString, featureStats);
          }
          
          featureValues.put(f, Math.log(featureStats.nodeFrequency));
          
          break;
        
        case DF:
          assert (!featureValues.containsKey(f));
          node = t;
          if (!f.part.isEmpty()) {
            node = t.clone();
            node.getNodeParameters().set("part", f.part);
          }
          cacheString = node.toString() + "-" + f.group;
          
          if (localCache.containsKey(cacheString)) {
            featureStats = (NodeStatistics) localCache.get(cacheString);
          } else if (!f.group.isEmpty()) {
            featureStats = gRetrieval.getNodeStatistics(node, f.group);
            localCache.put(cacheString, featureStats);
          } else {
            featureStats = this.retrieval.getNodeStatistics(node);
            localCache.put(cacheString, featureStats);
          }
          
          featureValues.put(f, Math.log(featureStats.nodeDocumentCount));
          
          break;
      }
    }
    
    double weight = 0.0;
    for (WSDMFeature f : uniFeatures) {
      double lambda = np.get(f.name, queryParams.get(f.name, f.defLambda));
      weight += lambda * featureValues.get(f);
    }
    
    return weight;
  }
  
  private double computeWeight(String term1, String term2, NodeParameters np, Parameters queryParams) throws Exception {

    // prepare nodes (will be used several times)
    Node t1 = new Node("extents", term1);
    t1 = TextPartAssigner.assignPart(t1, queryParams, retrieval.getAvailableParts());
    Node t2 = new Node("extents", term2);
    t2 = TextPartAssigner.assignPart(t2, queryParams, retrieval.getAvailableParts());
    
    Node od1 = new Node("ordered");
    od1.getNodeParameters().set("default", 1);
    od1.addChild(t1);
    od1.addChild(t2);

    // feature value store
    Map<WSDMFeature, Double> featureValues = new HashMap();

    // tf/df comes from the same object - can be used  twice
    Map<String, AggregateStatistic> localCache = new HashMap();

    // NOW : collect some feature values
    Node node;
    NodeStatistics featureStats;
    String cacheString;
    
    for (WSDMFeature f : uniFeatures) {
      switch (f.type) {
        case CONST:
          assert (!featureValues.containsKey(f));
          featureValues.put(f, 1.0);
          break;
        
        case TF:
          assert (!featureValues.containsKey(f));
          node = od1;
          if (!f.part.isEmpty()) {
            node = od1.clone();
            node.getChild(0).getNodeParameters().set("part", f.part);
            node.getChild(1).getNodeParameters().set("part", f.part);
          }
          // f.group is "" or some particular group
          cacheString = node.toString() + "-" + f.group;

          // first check if we have already done this node.
          if (localCache.containsKey(cacheString)) {
            featureStats = (NodeStatistics) localCache.get(cacheString);
          } else if (!f.group.isEmpty()) {
            featureStats = gRetrieval.getNodeStatistics(node, f.group);
            localCache.put(cacheString, featureStats);
          } else {
            featureStats = this.retrieval.getNodeStatistics(node);
            localCache.put(cacheString, featureStats);
          }
          
          featureValues.put(f, Math.log(featureStats.nodeFrequency));
          
          break;
        
        case DF:
          assert (!featureValues.containsKey(f));
          node = od1;
          if (!f.part.isEmpty()) {
            node = od1.clone();
            node.getChild(0).getNodeParameters().set("part", f.part);
            node.getChild(1).getNodeParameters().set("part", f.part);
          }
          cacheString = node.toString() + "-" + f.group;
          
          if (localCache.containsKey(cacheString)) {
            featureStats = (NodeStatistics) localCache.get(cacheString);
          } else if (!f.group.isEmpty()) {
            featureStats = gRetrieval.getNodeStatistics(node, f.group);
            localCache.put(cacheString, featureStats);
          } else {
            featureStats = this.retrieval.getNodeStatistics(node);
            localCache.put(cacheString, featureStats);
          }
          
          featureValues.put(f, Math.log(featureStats.nodeDocumentCount));
          
          break;
      }
    }
    
    double weight = 0.0;
    for (WSDMFeature f : uniFeatures) {
      double lambda = np.get(f.name, queryParams.get(f.name, f.defLambda));
      weight += lambda * featureValues.get(f);
    }
    
    return weight;
  }
  
  public static enum WSDMFeatureType {
    
    TF, DF, CONST;
  }

  /*
   * Features for WSDM: 
   *  name : "1-gram" 
   *  tfFeature : [true | false] :: asserts [ tf or df ], (tf default)
   *  group : "retrievalGroupName" :: missing or empty = default 
   *  part : "retrievalPartName" :: missing or empty = default
   *  unigram : true|false :: can be used on unigrams
   *  bigram : true|false :: can be used on bigrams
   */
  public static class WSDMFeature {
    
    public String name;
    public WSDMFeatureType type; // [tf | df | const] -- others may be supported later
    public String group;
    public String part;
    public double defLambda;
    // mutually exclusive unigram/bigram
    public boolean unigram;
    public boolean bigram;
    
    public WSDMFeature(Parameters p) {
      this.name = p.getString("name");
      this.type = WSDMFeatureType.valueOf(p.get("type", "tf").toUpperCase());
      this.defLambda = p.get("lambda", 0.0);
      this.group = p.get("group", "");
      this.part = p.get("part", "");
      this.unigram = p.get("unigram", true);
      this.bigram = p.get("bigram", false);
      assert (this.unigram ^ this.bigram) : "either unigram or bigram, but not both.";
    }

    /*
     * Constructor to allow default list of features
     */
    public WSDMFeature(String name, WSDMFeatureType type) {
      this.name = name;
      this.type = type;
    }
  }
}