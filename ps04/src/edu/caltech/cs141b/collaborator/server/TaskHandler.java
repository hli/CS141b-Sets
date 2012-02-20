package edu.caltech.cs141b.collaborator.server;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.gson.Gson;

import edu.caltech.cs141b.collaborator.common.Document;
import edu.caltech.cs141b.collaborator.common.Message;
import edu.caltech.cs141b.collaborator.server.CollaboratorServer;

public class TaskHandler extends HttpServlet {
    
    public void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws IOException{
    }
}
