package edu.caltech.cs141b.collaborator.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import com.google.appengine.api.datastore.KeyFactory;

import edu.caltech.cs141b.collaborator.common.Document;
import edu.caltech.cs141b.collaborator.common.DocumentHeader;
import edu.caltech.cs141b.collaborator.common.PMF;
import edu.caltech.cs141b.collaborator.server.data.DocumentData;
import edu.caltech.cs141b.hw5.gwt.collab.shared.LockExpired;
import edu.caltech.cs141b.hw5.gwt.collab.shared.LockUnavailable;

public class CollaboratorServer {
    
    private static final long DELTA = 60 * 60 * 1000;
    
    /**
     * Get list of document headers.
     * 
     * Retrieves the key and title for all the documents.
     * @return list of document headers
     */
    public List<DocumentHeader> getDocuments() {

        PersistenceManager pm = PMF.get().getPersistenceManager();
        List<DocumentHeader> headers = new ArrayList<DocumentHeader>();

        Query query = pm.newQuery(DocumentData.class);
        query.setOrdering("title asc");

        try {
            List<DocumentData> results = (List<DocumentData>) query.execute();
            if (!results.isEmpty()) {
                for (DocumentData d : results) {
                    headers.add(new DocumentHeader(d.getKey(), d.getTitle()));
                }
            }
        } finally {
            query.closeAll();
        }

        return headers;
    }

    /**
     * Gets document given key.
     * 
     * Retrieves the contents of a document specified by the given key.
     * @param key
     *    document key
     * @return document
     */
    public Document getDocument(String key) {

        PersistenceManager pm = PMF.get().getPersistenceManager();
        Document doc = null;

        DocumentData result = pm.getObjectById(DocumentData.class,
                KeyFactory.stringToKey(key));
        
        doc = new Document(result.getKey(), result.getTitle(),
                result.getContents(), false);

        return doc;
    }
  
    /**
     * Gets document and lock.
     * 
     * Gives the client exclusive access to a document with the given key if the document
     * is available.
     * @param key
     *    document key
     * @return document
     * @throws LockUnavailable
     *    if another client has the lock
     */
    public Document checkoutDocument(String user, String key) throws LockUnavailable {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Document doc = null;
        Date currentTime = new Date();

        try {
            tx.begin();
            
            DocumentData result = pm.getObjectById(DocumentData.class,
                    KeyFactory.stringToKey(key));

            if (result.getLockedUntil() == null ||
                    result.getLockedUntil().before(currentTime) ||
                    result.getLockedBy().equals(user)) {
                doc = new Document(result.getKey(), result.getTitle(),
                        result.getContents(), true);
                result.lock(user, new Date(currentTime.getTime() + DELTA));
                pm.makePersistent(result);
                
                tx.commit();
            } else {
                throw new LockUnavailable("Available at : " +  
                     (new Date(currentTime.getTime() + DELTA)).toString());
            }      
            
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
        }
        
        return doc;
    }

    /**
     * Stores document changes.
     * 
     * Stores the documents changes if the client still has the lock.
     * @param doc
     *    document
     * @return document
     * @throws LockExpired
     *    if the client no longer has access to the document.
     */
    public Document commitDocument(String user, Document doc) throws LockExpired {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        DocumentData result = null;
        Date currentTime = new Date();

        try {
            tx.begin();
            
            if (doc.getKey() == null) {
                
                result = this.newDocument(user, doc);
                
            } else {
                
                result = pm.getObjectById(DocumentData.class,
                        KeyFactory.stringToKey(doc.getKey()));
                if (result.getLockedUntil().after(currentTime) &&
                        result.getLockedBy().equals(user)) {
                    
                    result.setTitle(doc.getTitle());
                    result.setContents(doc.getContents(), user);
                    
                } else {
                    throw new LockExpired();
                }
            }

            pm.makePersistent(result);
            
            tx.commit();
            
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
        }

        return new Document(result.getKey(), result.getTitle(), 
                result.getContents(), true);
    }

    /**
     * Gives up access to document.
     * 
     * Client returns the given document's lock to the server.
     * @param doc
     *    document
     * @throws LockExpired
     *    if the client no longer has access to the document.
     */
    public void checkinDocument(String user, Document doc) throws LockExpired {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Date currentTime = new Date();

        try {
            tx.begin();
            
            DocumentData result = pm.getObjectById(DocumentData.class,
                    KeyFactory.stringToKey(doc.getKey()));
            
            if (result.getLockedUntil().after(currentTime) &&
                    result.getLockedBy().equals(user)) {
                
                result.unlock();
                pm.makePersistent(result);
                
                tx.commit();
            } else {
                throw new LockExpired();
            }
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
        }
    }
    
    /**
     * Creates new document.
     * 
     * Initializes a new document with the title and contents of the given
     * document, the user's identifier, and the current time for the lock.
     * @param doc
     *    new document
     * @return document
     */
    private DocumentData newDocument(String user, Document doc) {
        
        Date currentTime = new Date();
        
        return new DocumentData(doc.getTitle(), 
                doc.getContents(), user, user, 
                new Date(currentTime.getTime() + DELTA));
    }
}
