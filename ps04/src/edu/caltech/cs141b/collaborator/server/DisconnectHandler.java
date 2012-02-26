package edu.caltech.cs141b.collaborator.server;

import java.io.IOException;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.channel.ChannelPresence;
import com.google.appengine.api.channel.ChannelService;

import edu.caltech.cs141b.collaborator.common.PMF;
import edu.caltech.cs141b.collaborator.server.CollaboratorServer;
import edu.caltech.cs141b.collaborator.server.data.DocumentData;

@SuppressWarnings("serial")
public class DisconnectHandler extends HttpServlet {

    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        ChannelService channelService = CollaboratorServer.getChannelService();
        ChannelPresence presence;
        try {
            presence = channelService.parsePresence(req);
            this.handleDisconnection(presence.clientId());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void handleDisconnection(String clientId) {
        List<String> clientIds = CollaboratorServer.getClientIds(); 
        clientIds.remove(clientId);
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Transaction tx = pm.currentTransaction();
        Query query = pm.newQuery(DocumentData.class);

        try {
            List<DocumentData> results = (List<DocumentData>) query.execute();
            if (!results.isEmpty()) {
                try {
                    for (DocumentData d : results) {
                        tx.begin();
                        d.removeFromQueue(clientId);
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
            query.closeAll();
        }
    }
}