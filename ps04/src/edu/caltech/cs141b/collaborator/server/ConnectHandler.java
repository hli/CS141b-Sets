package edu.caltech.cs141b.collaborator.server;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.channel.ChannelPresence;
import com.google.appengine.api.channel.ChannelService;

@SuppressWarnings("serial")
public class ConnectHandler extends HttpServlet {
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        ChannelService channelService = CollaboratorServer.getChannelService();
        ChannelPresence presence;
        try {
            presence = channelService.parsePresence(req);
            CollaboratorServer.addClientId(presence.clientId());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
