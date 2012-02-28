package edu.caltech.cs141b.collaborator.server;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.channel.ChannelPresence;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions.Method;

@SuppressWarnings("serial")
public class ConnectHandler extends HttpServlet {
    
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        ChannelService channelService = ChannelServiceFactory.getChannelService();
        Queue taskQueue = QueueFactory.getDefaultQueue();
        ChannelPresence presence;
        try {
            presence = channelService.parsePresence(req);
            taskQueue.add(withUrl("/Collaborator/tasks").
                    param("Type", "Connect").param("clientId", presence.clientId()).method(Method.POST).countdownMillis(0));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
