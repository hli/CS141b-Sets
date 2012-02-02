package edu.caltech.cs141b.collaborator.common;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>CollaboratorService</code>.
 */
public interface CollaboratorServiceAsync {

    void getDocuments(AsyncCallback<List<DocumentHeader>> callback);

    void getDocument(String key, AsyncCallback<Document> callback);
    
    void checkoutDocument(String key, AsyncCallback<Document> callback);

    void commitDocument(Document doc, AsyncCallback<Document> callback);
    
    void newDocument(Document doc, AsyncCallback<Document> callback);

    void checkinDocument(Document doc, AsyncCallback<Document> callback);

    void getComments(String key, int start, int end, AsyncCallback<List<Comment>> callback);
    
    void addComment(String key, Comment comment, AsyncCallback<Void> callback);
    
    void getRevisions(String key, AsyncCallback<List<DocumentRevision>> callback);
}