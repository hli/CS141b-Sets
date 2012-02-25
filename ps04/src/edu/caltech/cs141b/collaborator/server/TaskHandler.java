package edu.caltech.cs141b.collaborator.server;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gdevelop.gwt.syncrpc.SyncProxy;
import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.gson.Gson;

import edu.caltech.cs141b.collaborator.common.CollaboratorService;
import edu.caltech.cs141b.collaborator.common.CollaboratorServiceAsync;
import edu.caltech.cs141b.collaborator.common.Document;
import edu.caltech.cs141b.collaborator.common.Message;
import edu.caltech.cs141b.collaborator.server.CollaboratorServer;

public class TaskHandler extends HttpServlet {

    private static CollaboratorService rpcService = (CollaboratorService) SyncProxy.newProxyInstance(CollaboratorService.class,
                  "http://localhost/Collaborator", "collab");
    
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String docKey = req.getParameter("docKey");
        String clientId = req.getParameter("clientId");
        
        rpcService.handleExpire(docKey, clientId, req.getHeader("X-AppEngine-TaskName"));
    }
}
