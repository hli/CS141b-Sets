package edu.caltech.cs141b.collaborator.server;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.datastore.KeyFactory;

import edu.caltech.cs141b.collaborator.common.Document;
import edu.caltech.cs141b.collaborator.common.DocumentHeader;
import edu.caltech.cs141b.collaborator.common.PMF;
import edu.caltech.cs141b.collaborator.server.data.DocumentData;
import edu.caltech.cs141b.hw2.gwt.collab.shared.LockExpired;
import edu.caltech.cs141b.hw2.gwt.collab.shared.LockUnavailable;

public class CollaboratorServer {

	public List<DocumentHeader> getDocuments() {

		PersistenceManager pm = PMF.get().getPersistenceManager();
		List<DocumentHeader> headers = new ArrayList<DocumentHeader>();
		
	    Query query = pm.newQuery(DocumentData.class);
	    query.setOrdering("title asc");

	    try {
	        List<Document> results = (List<Document>) query.execute();
	        if (!results.isEmpty()) {
	            for (Document d : results) {
	                headers.add(new DocumentHeader(d.getKey(), d.getTitle()));
	            }
	        }
	    } finally {
	        query.closeAll();
	    }
	    
	    return headers;
	}
	
	public Document getDocument(String key) {
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Document doc = null;
		
		DocumentData result = pm.getObjectById(DocumentData.class, 
				KeyFactory.stringToKey(key));
		
		if (result != null)
		{
			doc = new Document(result.getKey(), result.getTitle(), 
					result.getContents(), true);
		}
				
		return doc;
	}
	
	public Document checkoutDocument(String key) throws LockUnavailable {
		/* 
		 * TODO: Use JDO to checkout the Document specified by key.
		 * Check out means retrieve the document with locked = false.
		 * Throw LockUnavailable exception if the document is already
		 * locked in the datastore.
		 */
	}
	
	public Document commitDocument(Document doc) throws LockExpired {
		/*
		 * TODO: Use JDO to save the passed Document. Verify that the client
		 * still has a lock on the document before committing. Throw
		 * LockExpired exception if the client lock had already expired.
		 */
	}
	
	public void checkinDocument(Document doc) throws LockExpired {
		/*
		 * TODO: Release the client lock. Throw LockExpired exception if the
		 * client lock had already expired.
		 */
	}
	
}
