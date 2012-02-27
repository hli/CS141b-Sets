package edu.caltech.cs141b.collaborator.common;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("collab")
public interface CollaboratorService extends RemoteService {

    /**
     * Get Available Documents.
     * 
     * @return 
     *   A list of the metadata of the currently available documents.
     */
    List<DocumentHeader> getDocuments();

    /**
     * Get a document in read-only mode.
     * 
     * @param key
     *   The key of the document to read.
     * @return 
     *   An unlocked Document object.
     */
    Document getDocument(String key, String clientId);
    
    /**
     * Checkout (lock) an existing document.
     * 
     * @param key
     *   The key of the document to lock.
     * @return
     *   A locked Document object.
     * @throws LockUnavailable
     *   If a lock cannot be obtained
     */
    void checkoutDocument(String key, String clientId) throws LockUnavailable;

    /**
     * Save the locked document.
     * 
     * @param doc
     *   A locked Document object to be saved.
     * @return 
     *   An updated locked Document.
     * @throws LockExpired
     *   If the lock given to the document has expired.
     */
    Document commitDocument(Document doc, String clientId) throws LockExpired;

    /**
     * Check in (unlock) a locked document.
     * 
     * @param doc
     *   A locked Document object to checkin.
     * @throws LockExpired
     *   If the lock given to the document has expired.
     */
    Document checkinDocument(Document doc, String clientId) throws LockExpired;
    
    /**
     * Creates new document.
     * 
     * Initializes a new document with the title and contents of the given
     * document, the user's identifier, and the current time for the lock.
     * @param doc
     *    new document
     * @return document
     */
    Document newDocument(Document doc, String clientId);
    
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
    List<Comment> getComments(String key, int start, int end);
    
    /**
     * Get number of comments.
     * @param key
     * @return Integer
     */
    Integer getNumComments(String key);
    
    /**
     * Add comment to document.
     * @param key
     *   The key of the document to add the comment to.
     * @param comment
     *   The comment to add.
     */
    void addComment(String key, String comment);
    
    /**
     * Get document revisions.
     * 
     * @param key
     *   The key of the document to get revisions for.
     * @return
     *   A list of document revisions.
     */
    List<DocumentRevision> getRevisions(String key);
    
    /**
     * Create channel and get channel token.
     *
     * @return
     *   Channel token string.
     */
    String createChannel(String clientId);
    
    /**
     * Creates or finds pre-existing simulation document.
     *
     * @return
     *   Simulation document.
     */
    Document simulate();
}
