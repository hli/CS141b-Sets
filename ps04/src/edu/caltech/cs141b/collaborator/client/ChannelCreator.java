package edu.caltech.cs141b.collaborator.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.appengine.channel.client.Channel;
import com.google.gwt.appengine.channel.client.ChannelFactory;
import com.google.gwt.appengine.channel.client.ChannelFactory.ChannelCreatedCallback;
import com.google.gwt.appengine.channel.client.SocketError;
import com.google.gwt.appengine.channel.client.SocketListener;

import edu.caltech.cs141b.collaborator.common.Message;
import edu.caltech.cs141b.collaborator.ui.Editor;
import edu.caltech.cs141b.collaborator.ui.Notification;
import edu.caltech.cs141b.collaborator.client.Main;


/**
 * Used in conjunction with <code>CollaboratorService.createChannel()</code>.
 */
public class ChannelCreator implements AsyncCallback<String>{
	
	public void createChannel(String clientId) {
		Main.service.createChannel(clientId, this);
	}
	
    @Override
    public void onFailure(Throwable caught) {
    	new Notification(caught.getClass() + " Error: " 
    			+ caught.getMessage()).show();    	

        GWT.log("Error creating channel.", caught);
    }

    @Override
    public void onSuccess(String token) {
        ChannelFactory.createChannel(token, new ChannelCreatedCallback() {
            @Override
            public void onChannelCreated(Channel channel) {
                channel.open(new SocketListener() {
                    @Override
                    public void onOpen() {
                        Window.alert("Channel opened!");
                    }
                    @Override
                    public void onMessage(String msgstr) {
                        Message msgobj = Message.buildMessage(msgstr);
                        
                        switch (msgobj.getType()) {
                            case AVAILABLE:
                                Editor editor = Main.chrome.getEditors().get(msgobj.getDocKey());
                                if (editor == null)
                                    new DocCheckouter().checkoutDocument(msgobj.getDocKey(), Main.clientId);
                                else
                                    new DocCheckouter(editor).checkoutDocument(msgobj.getDocKey(), Main.clientId);
                                break;
                                
                            case UNAVAILABLE:
                                new Notification("You are number " + Integer.toString(msgobj.getPosition()) + " in line to receive the lock.").show();
                                break;
                            
                            case EXPIRED:
                                new DocExpiredRefresher(Main.chrome).getDocument(msgobj.getDocKey(), Main.clientId);
                                break;
                        }
                    }
                    @Override
                    public void onError(SocketError error) {
                        Window.alert("Error: " + error.getDescription());
                    }
                    @Override
                    public void onClose() {
                        Window.alert("Channel closed!");
                    }
                });
      	      }
      	    });
        }
}