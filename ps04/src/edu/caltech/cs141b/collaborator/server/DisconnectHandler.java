package edu.caltech.cs141b.collaborator.server;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelPresence;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.gson.Gson;

import edu.caltech.cs141b.collaborator.common.CollaboratorService;
import edu.caltech.cs141b.collaborator.common.CollaboratorServiceAsync;
import edu.caltech.cs141b.collaborator.common.Document;
import edu.caltech.cs141b.collaborator.common.Message;
import edu.caltech.cs141b.collaborator.common.PMF;
import edu.caltech.cs141b.collaborator.server.CollaboratorServer;
import edu.caltech.cs141b.collaborator.server.data.DocumentData;

public class DisconnectHandler extends HttpServlet {
    private static final Logger log = Logger.getLogger(DisconnectHandler.class.getName());

    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        log.info("An informational message. SHOOOOOOOOOOOOT");
        
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
        Gson gson = new Gson();
        ChannelService channelService = CollaboratorServer.getChannelService();
        List<String> clientIds = CollaboratorServer.getClientIds(); 
        clientIds.remove(clientId);
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Query query = pm.newQuery(DocumentData.class);

        try {
            List<DocumentData> results = (List<DocumentData>) query.execute();
            if (!results.isEmpty()) {
                for (DocumentData d : results) {
                    d.removeFromQueue(clientId);
                }
            }
        } finally {
            query.closeAll();
        }
        Message msgobj = new Message(Message.MessageType.EXPIRED, null, -1);
        channelService.sendMessage(
                new ChannelMessage(clientId, gson.toJson(msgobj)));
    }
}