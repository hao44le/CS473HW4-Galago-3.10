// BSD License (http://lemurproject.org/galago-license)
package org.lemurproject.galago.core.parse;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.regex.Pattern;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.util.StreamReaderDelegate;
import org.lemurproject.galago.core.types.DocumentSplit;

/**
 * Generic superclass for dealing with events.
 * You can instantiate this and run it, but it will do nothing.
 * Subclass and use the provided methods to set actions to 
 * process start/end and character events in the XML stream.
 * Attributes should be handled while handling the start event
 * that the attributes are inside.
 *
 * Actions are checked/removed by their regular expression patterns.
 * It is suggested that you simply pass in the Pattern if you have
 * it, as this will result in faster checking. However passing in
 * just the String may be more convenient.
 *
 * Methods that can serve as actions must fit one of the following
 * signatures:
 * 
 * - public void methodName(int eventType);
 * - public void methodName(int eventType, Pattern matchingPattern);
 *
 * For character actions only the first form is valid, since the match
 * is only on the event. Yes, they have to be public. DO NOT FORGET THIS.
 * Anything else creates uninformative null pointer bugs.
 *
 * @author irmarc
 */
class MBTEIParserBase implements DocumentStreamParser {
    // For XML stream processing
    protected StreamReaderDelegate reader;
    protected XMLInputFactory factory;

    protected DocumentSplit split;

    // Using these directly is either tedious or stupid to
    // do. Use the functions provided.
    class Action {
	public Action(Pattern p, Method m) {
	    labelRE = p;
	    todo = m;
	    arity = todo.getParameterTypes().length;
	}
	public Pattern labelRE;
	public Method todo;
	public int arity;
    }
    protected LinkedList<Action> startElementActions;
    protected LinkedList<Action> endElementActions;
    protected Method charactersAction;

    protected Document parsedDocument;
    protected StringBuilder buffer;
 
    public MBTEIParserBase(DocumentSplit split, InputStream is) {
	try {
	    this.split = split;
	    startElementActions = new LinkedList<Action>();
	    endElementActions = new LinkedList<Action>();
	    factory = XMLInputFactory.newInstance();
	    factory.setProperty(XMLInputFactory.IS_COALESCING, true);
	    reader = new StreamReaderDelegate(factory.createXMLStreamReader(is));
	} catch (Exception e) {
	    System.err.printf("SKIPPING %s: Caught exception %s\n", split.fileName, e.getMessage());
	    reader = null;
	}
    }

    protected void addStartElementAction(String labelPattern, String actionName) {
	for (Action a : startElementActions) {
	    if (a.labelRE.pattern().equals(labelPattern)) {
		throw new RuntimeException(String.format("Already have start action for %s: %s\n",
							 labelPattern,
							 a.todo.getName()));
	    }
	}
	try {
	    Method action = getClass().getMethod(actionName, int.class);
	    Pattern p = Pattern.compile(labelPattern);
	    startElementActions.add(new Action(p, action));
	} catch (Exception e) {
	    throw new IllegalArgumentException(e);
	}
    }

    protected void addStartElementAction(Pattern p, String actionName) {
	for (Action a : startElementActions) {
	    if (a.labelRE.equals(p)) {
		throw new RuntimeException(String.format("Already have start action for %s: %s\n",
							 p.pattern(),
							 a.todo.getName()));
	    }
	}
	try {
	    Method action = getClass().getMethod(actionName, int.class);
	    startElementActions.add(new Action(p, action));
	} catch (Exception e) {
	    throw new IllegalArgumentException(e);
	}
    }
    
    protected void clearStartElementActions() {
	startElementActions.clear();
    }

    protected void removeStartElementAction(String labelPattern) {
	int i = 0;
	for (Action a : startElementActions) {
	    if (a.labelRE.pattern().equals(labelPattern)) {
		break;
	    }
	    ++i;
	}
	if (i < startElementActions.size()) {
	    startElementActions.remove(i);
	}
    }

    protected void removeStartElementAction(Pattern p) {
	int i = 0;
	for (Action a : startElementActions) {
	    if (a.labelRE.equals(p)) {
		break;
	    }
	    ++i;
	}
	if (i < startElementActions.size()) {
	    startElementActions.remove(i);
	}
    }

    protected void addEndElementAction(String labelPattern, String actionName) {
	for (Action a : endElementActions) {
	    if (a.labelRE.pattern().equals(labelPattern)) {
		throw new RuntimeException(String.format("Already have end action for %s: %s\n",
							 labelPattern,
							 a.todo.getName()));
	    }
	}
	try {
	    Method action = getClass().getMethod(actionName, int.class);
	Pattern p = Pattern.compile(labelPattern);
	endElementActions.add(new Action(p, action));
	} catch (Exception e) {
	    throw new IllegalArgumentException(e);
	}
    }

    protected void addEndElementAction(Pattern p, String actionName) {
	for (Action a : endElementActions) {
	    if (a.labelRE.equals(p)) {
		throw new RuntimeException(String.format("Already have end action for %s: %s\n",
							 p.pattern(),
							 a.todo.getName()));
	    }
	}
	try {
	    Method action = getClass().getMethod(actionName, int.class);
	endElementActions.add(new Action(p, action));
	} catch (Exception e) {
	    throw new IllegalArgumentException(e);
	}
    }

    protected void removeEndElementAction(String labelPattern) {
	int i = 0;
	for (Action a : endElementActions) {
	    if (a.labelRE.pattern().equals(labelPattern)) {
		break;
	    }
	    ++i;
	}
	if (i < endElementActions.size()) {
	    endElementActions.remove(i);
	}
    }

    protected void removeEndElementAction(Pattern p) {
	int i = 0;
	for (Action a : endElementActions) {
	    if (a.labelRE.equals(p)) {
		break;
	    }
	    ++i;
	}
	if (i < endElementActions.size()) {
	    endElementActions.remove(i);
	}
    }

    protected void clearEndElementActions() {
	endElementActions.clear();
    }

    protected void setCharactersAction(String actionName) {
	try {
	    Method action = getClass().getMethod(actionName, int.class);
	    charactersAction = action;
	} catch (Exception e) {
	    throw new IllegalArgumentException(e);
	}
    }

    protected void unsetCharactersAction() {
	charactersAction = null;
    }

    @Override
    public Document nextDocument() throws IOException {
	if (reader == null) {
	    return null;
	}
	int status;
	parsedDocument = null;
	buffer = new StringBuilder();
      
	try {
	    while (reader.hasNext() && parsedDocument == null) {
		status = reader.next();
		switch (status) {
		case XMLStreamConstants.START_ELEMENT: {
		    String label = reader.getLocalName();
		    for (Action a : startElementActions) {
			if (a.labelRE.matcher(label).matches()) {
			    switch (a.arity) {
			    case 1: 
				a.todo.invoke(this, status); 
				break;
			    case 2: 
				a.todo.invoke(this, status, a.labelRE); 
				break;
			    }
			    break;
			}
		    }
		}
		    break;		
		case XMLStreamConstants.END_ELEMENT: {
		    String label = reader.getLocalName();
		    for (Action a : endElementActions) {
			if (a.labelRE.matcher(label).matches()) {
			    switch (a.arity) {
			    case 1: 
				a.todo.invoke(this, status); 
				break;
			    case 2: 
				a.todo.invoke(this, status, a.labelRE); 
				break;
			    }
			    break;
			}
		    }
		}
		    break;		    
		case XMLStreamConstants.CHARACTERS: {
		    if (charactersAction != null) {
			charactersAction.invoke(this, status);
		    }
		}  // case
		}  // switch
	    }

	    // Either no more tokens or have a document
	    // If we have a document send it up
	    if (parsedDocument != null) {
		return parsedDocument;
	    }

	    // If we're out of tokens, just return null.
	    return null;
	} catch (Exception e) {
	    System.err.printf("EXCEPTION [%s,%s]: %s\n", 
			      getArchiveIdentifier(), 
			      buffer.toString(),
			      e.getMessage());
	    return null;
	}
    }
    
    @Override
    public void close() throws IOException {
	try {
	    reader.close();
	} catch (XMLStreamException ex) {
	    System.err.printf("EXCEPTION CLOSING [%s]: %s\n", getArchiveIdentifier(), ex.getMessage());
	}
    }

    // UTILITY FUNCTIONS
    public void echo(int eventType) {
	switch(eventType) {
	case XMLStreamConstants.START_ELEMENT:
            buffer.append("<").append(reader.getLocalName()).append(">");
	    break;
	case XMLStreamConstants.END_ELEMENT:
	    buffer.append("</").append(reader.getLocalName()).append(">");
	    break;
	case XMLStreamConstants.CHARACTERS:
	    buffer.append(reader.getText());
	    break;
	}
    }

    public void echoWithAttributes(int eventType) {
	assert eventType == XMLStreamConstants.START_ELEMENT;
	buffer.append("<").append(reader.getLocalName());
	for (int i = 0; i < reader.getAttributeCount(); ++i) {
	    buffer.append(reader.getAttributeLocalName(i));
	    buffer.append("=\"");
	    buffer.append(reader.getAttributeValue(i));
	    buffer.append("\"");
	}
	buffer.append(">");
    }

    public String scrub(String dirty) {
	String cleaned = dirty.replaceAll("&apos;", "'");
	cleaned = cleaned.replaceAll("&quot;", "\"");
	cleaned = cleaned.replaceAll("&amp;", "&");
	cleaned = cleaned.replaceAll("[ ]+", " ");
	cleaned = cleaned.replaceAll("(-LRB-|-RRB-)", "");
	return cleaned.trim();
    }
    
    public String getArchiveIdentifier() {
	File f = new File(split.fileName);
	String basename = f.getName();
	String[] parts = basename.split("_");
	return parts[0];
    }

}
