package edu.caltech.cs141b.collaborator.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import com.google.appengine.api.datastore.KeyFactory;

import edu.caltech.cs141b.collaborator.common.Document;
import edu.caltech.cs141b.collaborator.common.DocumentHeader;
import edu.caltech.cs141b.collaborator.common.PMF;
import edu.caltech.cs141b.collaborator.server.data.DocumentData;
import edu.caltech.cs141b.collaborator.server.data.DocumentRevisionData;
import edu.caltech.cs141b.hw2.gwt.collab.server.CollaboratorServiceImpl;
import edu.caltech.cs141b.hw2.gwt.collab.shared.LockExpired;
import edu.caltech.cs141b.hw2.gwt.collab.shared.LockUnavailable;

public class CollaboratorServer {

    private static final Logger log = Logger
            .getLogger(CollaboratorServiceImpl.class.toString());
    
    private static final long DELTA = 60 * 60 * 1000;
    
    private CollaboratorServiceImpl servlet;
    
    /**
     * Get User ID.
     * 
     * Gets a unique identifier for the current user.
     * 
     * @return
     *   Current user identifier.
     */
    private String getUserId() {
        return this.servlet.getUserId();
    }
    
    /**
     * Class Constructor.
     * 
     * @param servlet
     *   Servlet that uses this server.
     */
    public CollaboratorServer(CollaboratorServiceImpl servlet) {
        this.servlet = servlet;
    }
    
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

        try {
            DocumentData result = pm.getObjectById(DocumentData.class,
                    KeyFactory.stringToKey(key));
            
            doc = new Document(result.getKey(), result.getTitle(),
                    result.getContents(), false);
        } catch (JDOObjectNotFoundException e) {
        }

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
    public Document checkoutDocument(String key) throws LockUnavailable {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Document doc = null;
        Date currentTime = new Date();

        try {
            tx.begin();
            
            try {
                DocumentData result = pm.getObjectById(DocumentData.class,
                        KeyFactory.stringToKey(key));
    
                if (result.getLockedUntil() == null ||
                        result.getLockedUntil().before(currentTime)) {
                    doc = new Document(result.getKey(), result.getTitle(),
                            result.getContents(), true);
                    result.lock(this.getUserId(), 
                            new Date(currentTime.getTime() + DELTA));
                    pm.makePersistent(result);
                    
                    tx.commit();
                } else {
                    throw new LockUnavailable("Available at : " +  
                         (new Date(currentTime.getTime() + DELTA)).toString());
                }      
            } catch (JDOObjectNotFoundException e) {
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
    public Document commitDocument(Document doc) throws LockExpired {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        DocumentData result = null;
        Date currentTime = new Date();

        try {
            tx.begin();
            
            try {
                if (doc.getKey() == null) {
                    
                    result = this.newDocument(doc);
                    
                } else {
                    
                    result = pm.getObjectById(DocumentData.class,
                            KeyFactory.stringToKey(doc.getKey()));
                    if (result.getLockedUntil().after(currentTime) &&
                            result.getLockedBy().equals(this.getUserId())) {
                        
                        result.setTitle(doc.getTitle());
                        result.setContents(doc.getContents(), this.getUserId());
                        
                    } else {
                        throw new LockExpired();
                    }
                    
                }
            } catch (JDOObjectNotFoundException e) {
                result = this.newDocument(doc);
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
    public void checkinDocument(Document doc) throws LockExpired {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Date currentTime = new Date();

        try {
            tx.begin();
            
            try {
                DocumentData result = pm.getObjectById(DocumentData.class,
                        KeyFactory.stringToKey(doc.getKey()));
                
                if (result.getLockedUntil().after(currentTime) &&
                        result.getLockedBy().equals(this.getUserId())) {
                    
                    result.unlock();
                    pm.makePersistent(result);
                    
                    tx.commit();
                } else {
                    throw new LockExpired();
                }
                    
            } catch (JDOObjectNotFoundException e) {
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
    private DocumentData newDocument(Document doc) {
        
        Date currentTime = new Date();
        
        return new DocumentData(doc.getTitle(), 
                doc.getContents(), this.getUserId(), this.getUserId(), 
                new Date(currentTime.getTime() + DELTA));
    }
}
