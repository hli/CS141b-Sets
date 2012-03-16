package edu.caltech.cs141b.collaborator.server;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.gson.Gson;

import edu.caltech.cs141b.collaborator.common.Message;
import edu.caltech.cs141b.collaborator.common.PMF;
import edu.caltech.cs141b.collaborator.server.data.ClientIds;
import edu.caltech.cs141b.collaborator.server.data.DocumentData;

@SuppressWarnings("serial")
public class TaskHandler extends HttpServlet {
    
    public static final long DELTA = 60000;
    
    Logger log = Logger.getLogger(TaskHandler.class.getName());
        
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String type = req.getParameter("Type");
        log.info(type);
        String clientId = req.getParameter("clientId");
        if (type.equals("Connect")) {
            this.handleConnection(clientId);
        }
        else if (type.equals("Checkout")) {
            String docKey = req.getParameter("docKey");
            this.checkoutDocument(docKey, clientId);
        }
        else if (type.equals("Expire")) {
            String docKey = req.getParameter("docKey");
            this.handleExpire(docKey, clientId);
        }
        else {
            this.handleDisconnection(clientId);
        }
    }
    
    public void handleConnection(String clientId) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        try {
            tx.begin();
            ClientIds clientIds;
            Query clientIdsQuery = pm.newQuery(ClientIds.class);
            try {
                List<ClientIds> result = (List<ClientIds>) clientIdsQuery.execute();
                if (result.isEmpty()) { clientIds = new ClientIds(); }
                else { clientIds = result.get(0); }
                clientIds.addClient(clientId);
                pm.makePersistent(clientIds);
                tx.commit();
            } finally {
                clientIdsQuery.closeAll();
            }
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
        }
    }
    
    public void checkoutDocument(String key, String clientId) {
        Gson gson = new Gson();
        ChannelService channelService = ChannelServiceFactory.getChannelService();
        Queue taskQueue = QueueFactory.getDefaultQueue();
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Transaction tx = pm.currentTransaction();
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

            // If the top of the queue for the document is the user, give the user the document.
            if (head.equals(clientId)) {
                result.lock(clientId, 
                        new Date(currentTime.getTime() + DELTA));
                pm.makePersistent(result);
                
                taskQueue.add(withUrl("/Collaborator/tasks").
                         param("docKey", result.getKey()).param("clientId", clientId).param("Type", "Expire").method(Method.POST).countdownMillis(DELTA));
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
    
    public void handleExpire(String key, String clientId) {
        Gson gson = new Gson();
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        DocumentData result = null;
        ChannelService channelService = ChannelServiceFactory.getChannelService();
        Date currentTime = new Date();
        ClientIds clientIds;
        
        Query clientIdsQuery = pm.newQuery(ClientIds.class);
        
        try {
            List<ClientIds> clientIdsList = (List<ClientIds>) clientIdsQuery.execute();

            clientIds = clientIdsList.get(0);         
        } finally {
            clientIdsQuery.closeAll();
        }
        
        if (clientIds.contains(clientId)) {
            try {
                tx.begin();
                result = pm.getObjectById(DocumentData.class,
                        KeyFactory.stringToKey(key));
                if (result.getLockedUntil() != null 
                        && result.getLockedBy().equals(clientId) 
                        && result.getLockedUntil().before(currentTime) 
                        && !result.queueIsEmpty()) {
                    Message msgobj = new Message(Message.MessageType.EXPIRED, key, -1);
                    channelService.sendMessage(
                            new ChannelMessage(clientId, gson.toJson(msgobj)));
                    result.popFromQueue();
                    pm.makePersistent(result);
    
                    //If there is a client in the queue, tell them that the document 
                    //is now available to them.
                    if (!result.queueIsEmpty()) {
                        Message msgobj2 = new Message(Message.MessageType.AVAILABLE, result.getKey(), -1);
                        channelService.sendMessage(
                                new ChannelMessage(result.peekAtQueue(), gson.toJson(msgobj2)));
                    }
                }
                tx.commit();

            } finally {
                if (tx.isActive()) {
                    tx.rollback();
                }
            }
        }
    }
    
    public void handleDisconnection(String clientId) {
        Gson gson = new Gson();
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        ChannelService channelService = ChannelServiceFactory.getChannelService();
        
        Query clientIdsQuery = pm.newQuery(ClientIds.class);
        
        try {
            List<ClientIds> clientIdsList = (List<ClientIds>) clientIdsQuery.execute();
        
            ClientIds clientIds = clientIdsList.get(0);
            try {
                tx.begin();
                clientIds.removeClient(clientId);
                pm.makePersistent(clientIds);
                tx.commit();
            } finally {
                if (tx.isActive()) {
                    tx.rollback();
                }
            }
            
        } finally {
            clientIdsQuery.closeAll();
        }
        
        tx = pm.currentTransaction();
        Query docQuery = pm.newQuery(DocumentData.class);

        try {
            List<DocumentData> results = (List<DocumentData>) docQuery.execute();
            if (!results.isEmpty()) {
                try {
                    for (DocumentData d : results) {
                        tx.begin();
                        if (!d.queueIsEmpty() && d.peekAtQueue().equals(clientId)) {
                            d.popFromQueue();
                            if (!d.queueIsEmpty()) {
                                Message msgobj = new Message(Message.MessageType.AVAILABLE, d.getKey(), -1);
                                channelService.sendMessage(
                                        new ChannelMessage(d.peekAtQueue(), gson.toJson(msgobj)));
                            }
                        }
                        else {
                            d.removeFromQueue(clientId);
                        }
                        pm.makePersistent(d);
                        tx.commit();
                    }
                } finally {
                    if (tx.isActive()) {
                        tx.rollback();
                    }
                }
            }
        } finally {
            docQuery.closeAll();
        }
    }
}
