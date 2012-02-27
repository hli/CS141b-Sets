package edu.caltech.cs141b.collaborator.server;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.gson.Gson;

import edu.caltech.cs141b.collaborator.common.Document;
import edu.caltech.cs141b.collaborator.common.LockUnavailable;
import edu.caltech.cs141b.collaborator.common.Message;
import edu.caltech.cs141b.collaborator.common.PMF;
import edu.caltech.cs141b.collaborator.server.CollaboratorServer;
import edu.caltech.cs141b.collaborator.server.data.DocumentData;

@SuppressWarnings("serial")
public class CheckoutHandler extends HttpServlet {
    
    private static final Logger log = Logger.getLogger(TaskHandler.class.getName());

    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String docKey = req.getParameter("docKey");
        String clientId = req.getParameter("clientId");
        this.checkoutDocument(docKey, clientId);
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
        Gson gson = new Gson();
        ChannelService channelService = CollaboratorServer.getChannelService();
        Queue taskqueue = CollaboratorServer.getTaskQueue();
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Document doc = null;
        Date currentTime = new Date();

        try {
            tx.begin();

            DocumentData result = pm.getObjectById(DocumentData.class,
                    KeyFactory.stringToKey(key));
            
            if (!result.queueContains(clientId)) {
                result.addToQueue(clientId);
                pm.makePersistent(result);
            }
            String head = result.peekAtQueue();

            if (result.getLockedUntil() == null ||
                    result.getLockedUntil().before(currentTime) ||
                    // If the top of the queue for the document is the user, give the user the document.
                    head.equals(clientId)) {
                result.lock(clientId, 
                        new Date(currentTime.getTime() + CollaboratorServer.DELTA));
                pm.makePersistent(result);
                
                taskqueue.add(withUrl("/Collaborator/tasks").
                        param("docKey", result.getKey()).param("clientId", clientId).method(Method.POST).countdownMillis(CollaboratorServer.DELTA));
                Message msgobj = new Message(result.getKey(), result.getTitle(), result.getContents(), result.getSimulate());
                String msgstr = gson.toJson(msgobj);
                channelService.sendMessage(
                        new ChannelMessage(clientId, msgstr));
            } else {
                Message msgobj = new Message(Message.MessageType.UNAVAILABLE, result.getKey(), result.indexInQueue(clientId));
                String msgstr = gson.toJson(msgobj);
                channelService.sendMessage(
                        new ChannelMessage(clientId, msgstr));
            }      
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
        }
    }
}