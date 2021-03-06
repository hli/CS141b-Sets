package edu.caltech.cs141b.collaborator.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.Queue;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.*;

import com.google.gson.Gson;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.caltech.cs141b.collaborator.common.CollaboratorService;
import edu.caltech.cs141b.collaborator.common.Comment;
import edu.caltech.cs141b.collaborator.common.Document;
import edu.caltech.cs141b.collaborator.common.DocumentHeader;
import edu.caltech.cs141b.collaborator.common.DocumentRevision;
import edu.caltech.cs141b.collaborator.common.LockExpired;
import edu.caltech.cs141b.collaborator.common.LockUnavailable;
import edu.caltech.cs141b.collaborator.common.Message;
import edu.caltech.cs141b.collaborator.common.PMF;
import edu.caltech.cs141b.collaborator.server.data.CommentData;
import edu.caltech.cs141b.collaborator.server.data.DocumentData;
import edu.caltech.cs141b.collaborator.server.data.DocumentRevisionData;


@SuppressWarnings("serial")
public class CollaboratorServer extends RemoteServiceServlet implements
    CollaboratorService {
        
    public static final long DELTA = 60000;
    
    private static ChannelService channelService = ChannelServiceFactory.getChannelService();
    private static Queue taskQueue = QueueFactory.getDefaultQueue();
    
    private static Gson gson = new Gson();
        
    /**
     * Get client's email address.
     * 
     * Gets the email address of the client using this thread.
     * 
     * @return
     *   Current client's email address.
     */
    private String getClientSignature() {
        return this.getThreadLocalRequest().getUserPrincipal().getName(); 
    }
    
    public String createChannel(String clientId) {
    	String token = channelService.createChannel(clientId);
    	return token;
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
    public Document getDocument(String key, String clientId) {

    	PersistenceManager pm = PMF.get().getPersistenceManager();
    	Document doc = null;
    	Date currentTime = new Date();

    	DocumentData result = pm.getObjectById(DocumentData.class,
    			KeyFactory.stringToKey(key));

    	Boolean isLocked = result.getLockedUntil() != null && 
    	        result.getLockedUntil().after(currentTime) &&
                result.getLockedBy().equals(clientId);
    	
    	doc = new Document(result.getKey(), result.getTitle(),
    			result.getContents(), isLocked);

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
    public void checkoutDocument(String key, String clientId) {
        taskQueue.add(withUrl("/Collaborator/tasks").param("Type", "Checkout").
                param("docKey", key).param("clientId", clientId).method(Method.POST).countdownMillis(0));
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
    public Document commitDocument(Document doc, String clientId) throws LockExpired {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        DocumentData result = null;
        Date currentTime = new Date();

        try {
            tx.begin();
               
            result = pm.getObjectById(DocumentData.class,
            		KeyFactory.stringToKey(doc.getKey()));

            if (result.getLockedUntil().after(currentTime) &&
            		result.getLockedBy().equals(clientId)) {

                if (doc.getTitle() != null) {
                    result.setTitle(doc.getTitle());
                }
            	if (doc.getContents() != null) {
            	    result.setContents(doc.getContents(), clientId);
            	}

            } else {
            	throw new LockExpired("No longer have write access.");
            }

            pm.makePersistent(result);
            
            tx.commit();
            
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
        }
        
        Document returnDoc = new Document(result.getKey(), result.getTitle(), 
                result.getContents(), true);
        if (result.getSimulate()) {
            returnDoc.setSimulate(true);
        }
        return returnDoc;
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
    public Document checkinDocument(Document doc, String clientId) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Date currentTime = new Date();
        DocumentData result = null;
        
        try {
            tx.begin();
            
            result = pm.getObjectById(DocumentData.class,
            		KeyFactory.stringToKey(doc.getKey()));

            if (result.getLockedUntil().after(currentTime) &&
            		result.getLockedBy().equals(clientId)) {

            	result.unlock();
                result.popFromQueue();
                pm.makePersistent(result);
                tx.commit();
                
                //If there is a client in the queue, tell them that the document 
                //is now available to them.
                if (!result.queueIsEmpty()) {
                    Message msgobj = new Message(Message.MessageType.AVAILABLE, result.getKey(), -1);
                    channelService.sendMessage(
                            new ChannelMessage(result.peekAtQueue(), gson.toJson(msgobj)));
                }	
            } else {
                tx.commit();
            }
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
        }
        Document returnDoc = new Document(result.getKey(), result.getTitle(), 
                result.getContents(), false);
        if (result.getSimulate()) {
            returnDoc.setSimulate(true);
        }
        return returnDoc;
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
    public Document newDocument(Document doc, String clientId) {
    	PersistenceManager pm = PMF.get().getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        DocumentData result = null;
        Date currentTime = new Date();

        try {
        	tx.begin();

        	result = new DocumentData(doc.getTitle(), 
        			doc.getContents(), clientId, clientId, 
        			new Date(currentTime.getTime() + DELTA));
        	result.addToQueue(clientId);
            pm.makePersistent(result);
            tx.commit();
            
            taskQueue.add(withUrl("/Collaborator/tasks").
                    param("docKey", result.getKey()).param("clientId", clientId).param("Type", "Expired").method(Method.POST).countdownMillis(DELTA));
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
        }
        
        return new Document(result.getKey(), result.getTitle(), 
                result.getContents(), true);
    }
    
    /**
     * Get comments for document.
     * 
     * @param key
     *   The key of the document to get the comments for.
     * @param start
     *   Start index of the comments range.
     * @param end
     *   End index of the comments range.
     * @return
     *   List of comments.
     */
    public List<Comment> getComments(String key, int start, int end) {
    	PersistenceManager pm = PMF.get().getPersistenceManager();
    	
        List<Comment> comments = new ArrayList<Comment>();

        DocumentData result = pm.getObjectById(DocumentData.class,
        		KeyFactory.stringToKey(key));

        List<CommentData> commentdata = result.getComments(start, end);
        if (!commentdata.isEmpty()) {
        	for (CommentData c : commentdata) {
        		comments.add(new Comment(c.getCommentTime(), c.getCommentBy(), c.getMessage()));
        	}
        }

        return comments;
        
    }
    
    public Integer getNumComments(String key) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        
            DocumentData result = pm.getObjectById(DocumentData.class,
            KeyFactory.stringToKey(key));
            
            return result.getNumComments();
    }
    
    /**
     * Add comment to document.
     * @param key
     *   The key of the document to add the comment to.
     * @param comment
     *   The comment to add.
     */
    public void addComment(String key, String comment){
    	PersistenceManager pm = PMF.get().getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try {
        	tx.begin();
        	DocumentData result = pm.getObjectById(DocumentData.class,
        			KeyFactory.stringToKey(key));
        	result.addComment(comment, this.getClientSignature());
            pm.makePersistent(result);
            tx.commit();
        } finally {
        	if (tx.isActive()) {
        		tx.rollback();
        	}
        }
    }

    /**
     * Get document revisions.
     * 
     * @param key
     *   The key of the document to get revisions for.
     * @return
     *   A list of document revisions.
     */
    public List<DocumentRevision> getRevisions(String key) {
    	PersistenceManager pm = PMF.get().getPersistenceManager();
    	
        List<DocumentRevision> revisions = new ArrayList<DocumentRevision>();

        DocumentData result = pm.getObjectById(DocumentData.class,
                KeyFactory.stringToKey(key));
        
        List<DocumentRevisionData> revisiondata = result.getRevisions();
        if (!revisiondata.isEmpty()) {
            for (DocumentRevisionData r : revisiondata) {
                revisions.add(new DocumentRevision(r.getKey(), r.getDocument().getTitle(), r.getContents(), r.getUpdatedTime(), r.getUpdatedBy()));
            }
        }  
        return revisions;
    }
    
    public Document simulate() {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        DocumentData result;

        Query query = pm.newQuery(DocumentData.class);
        query.setFilter("isSimulate == true");
        
        try {
            List<DocumentData> results = (List<DocumentData>) query.execute();
            if (!results.isEmpty()) {
                result = results.get(0);
            } else {
                try {
                    tx.begin();
                    
                    result = new DocumentData("Simulation Document", "Simulating...", null, null, null);
                    result.setSimulate();
                    pm.makePersistent(result);
                    tx.commit();
                    
                } finally {
                    if (tx.isActive()) {
                        tx.rollback();
                    }
                }
            }
        } finally {
            query.closeAll();
        }
        Document returnDoc = new Document(result.getKey(), result.getTitle(), 
                result.getContents(), false);
        return returnDoc;
    }
}
