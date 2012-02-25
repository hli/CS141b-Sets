package edu.caltech.cs141b.collaborator.common;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>CollaboratorService</code>.
 */
public interface CollaboratorServiceAsync {

    void getDocuments(AsyncCallback<List<DocumentHeader>> callback);

    void getDocument(String key, String clientId, AsyncCallback<Document> callback);
    
    void checkoutDocument(String key, String clientId, AsyncCallback<Document> callback);

    void commitDocument(Document doc, String clientId, AsyncCallback<Document> callback);
    
    void newDocument(Document doc, String clientId, AsyncCallback<Document> callback);

    void checkinDocument(Document doc, String clientId, AsyncCallback<Document> callback);

    void getComments(String key, int start, int end, AsyncCallback<List<Comment>> callback);
    
    void getNumComments(String key, AsyncCallback<Integer> callback);
    
    void addComment(String key, String comment, AsyncCallback<Void> callback);
    
    void getRevisions(String key, AsyncCallback<List<DocumentRevision>> callback);
    
    void createChannel(String clientId, AsyncCallback<String> callback);
    
    void handleExpire(String key, String clientId, String task, AsyncCallback<Void> callback);
    
    void handleDisconnection(String clientId, AsyncCallback<Void> callback);

}