package edu.caltech.cs141b.collaborator.server;

import java.util.Date;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.gson.Gson;

import edu.caltech.cs141b.collaborator.common.Document;
import edu.caltech.cs141b.collaborator.common.LockExpired;
import edu.caltech.cs141b.collaborator.common.Message;
import edu.caltech.cs141b.collaborator.common.PMF;
import edu.caltech.cs141b.collaborator.server.CollaboratorServer;
import edu.caltech.cs141b.collaborator.server.data.DocumentData;

@SuppressWarnings("serial")
public class TaskHandler extends HttpServlet {
    
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String docKey = req.getParameter("docKey");
        String clientId = req.getParameter("clientId");
        this.handleExpire(docKey, clientId);
    }
    
    public void handleExpire(String key, String clientId) {
        Gson gson = new Gson();
        ChannelService channelService = CollaboratorServer.getChannelService();
        List<String> clientIds = CollaboratorServer.getClientIds(); 
        if (clientIds.contains(clientId)) {
            Message msgobj = new Message(Message.MessageType.EXPIRED, key, -1);
            channelService.sendMessage(
                    new ChannelMessage(clientId, gson.toJson(msgobj)));
            Document doc = this.getDocument(key, clientId);
            this.checkinDocument(doc, clientId);
        }
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
        Gson gson = new Gson();
        ChannelService channelService = CollaboratorServer.getChannelService();
        
        try {
            tx.begin();
            
            result = pm.getObjectById(DocumentData.class,
                    KeyFactory.stringToKey(doc.getKey()));

            if (result.getLockedUntil().after(currentTime) &&
                    result.getLockedBy().equals(clientId)) {

                result.unlock();
                
            }
            
            result.popFromQueue();
            pm.makePersistent(result);

            //If there is a client in the queue, tell them that the document 
            //is now available to them.
            if (!result.queueIsEmpty()) {
                Message msgobj = new Message(Message.MessageType.AVAILABLE, result.getKey(), -1);
                channelService.sendMessage(
                        new ChannelMessage(result.peekAtQueue(), gson.toJson(msgobj)));
            }
            tx.commit();

        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
        }
        return new Document(result.getKey(), result.getTitle(), 
                result.getContents(), false);
    }
}
