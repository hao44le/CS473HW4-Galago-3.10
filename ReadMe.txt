This Lemur project release is for Indri 5.11, Galago 3.10 and RankLib-2.7.

Applications compiled with the Indri API require the following libraries:
   z, iberty, pthread, and m on linux. 

Applications built in Visual Studio require the additional library wsock32.lib.

The java jar files were built with Java 8 (jdk 1.8.0). The java UIs require Java 8.
We have tested using GCC 4.4.7 (CentOS 6.7 linux), 4.8.4 (Ubuntu 14.04 and 16.04
linux), 4.2.1 (OS/X 10.11.3), and Visual Studio 2012 (Windows 7, WIN32 and x86_64).

Note that OS/X Yosemite (10.10) is not supported at this time.


Bug Fixes

  See https://sourceforge.net/p/lemur/bugs/ for the complete ticket listing.

  o BUG# 275  Removed levels variable to correctly handle field terms in 
              Galago #bm25f operations.

  o BUG# 276  Add support for TSV query formats for XFoldLearner in Galago.

  o BUG# 279  Use StringBuffer to load saved models in RankLib.  This greatly
              speeds the loading of large sized models.

  o BUG# 282  Correctly initialize min/max arrays in RankLib linear normalizer.  

  o BUG# 283  Ensure RankLib sparse data representations are used if selected,
              when doing cross fold validation.


Non-Ticket Bug Fixes

  o Fixed JSON parsing error in Galago parameters.

  o Fixed out of memory error in string parsing within Galago JSONParser.

  o Ensure optimization steps in Galago CoordinateAscent learner remain within 
    upper and lower defined limits.

  o Fixes to Galago #prms2, #sdm and #wsdm operators.


Feature Requests

  See https://sourceforge.net/p/lemur/support-requests/ for the complete 
  ticket listing. 

  o FR# 133   Galago term statistics for specified documents or list of documents.

  o FR# 135   Provide Galago version ID and build date to index build manifest.

  o FR# 136   Provide operator description information to Galago operator-help
              function.

  o FR# 139   Combine test results for Galago XFoldLearner.


Non-Ticket Feature Addtions

  o Make Galago #bm25f operator parameters learnable.

  o Support operatorWrap (operator wrapping) for Galago learner.

  o Updates to the Galago #require operator.

  o Added the SMART stopword list to Galago.

  o Add Galago get-docs-jsonl function to output specified indexed raw document
    text in JSON format.

  o Add Galago tokenize-and-grab-stats function to return JSON formatted 
    statistics for all terms in a provided document and specified index.

  o Add a Galago parser for NewsIR JSON document collection.

  o Upgrade Galago TimedBatchSearch to use operatorWrap and queryFormat
    arguments.

  o Add Galago processingModel that ignores zero length documents.

  o Added Galago operator-help function.

  o Added build datetime and galago version and build date to index build 
    manifest.

  o Add a limit argument that restricts Galago eval to use only a specified
    subset of queries from a query set.

  o Added Galago dump-term-stats-ext function to output term frequency and 
    document counts from one or more specified index parts.
