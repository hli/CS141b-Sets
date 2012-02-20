package edu.caltech.cs141b.collaborator.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.caltech.cs141b.collaborator.common.Document;
import edu.caltech.cs141b.collaborator.ui.Notification;


/**
 * Used in conjunction with <code>CollaboratorService.getDocument(String key)</code>.
 */
public class DocGetter implements AsyncCallback<Document> {

	private Chrome chrome;
	
    public DocGetter(Chrome chrome) {
        this.chrome = chrome;
    }

    public void getDocument(String key, String clientId) {
        Main.service.getDocument(key, clientId, this);
    }

    @Override
    public void onFailure(Throwable caught) {
    	if (caught.getClass().equals("JDOObjectNotFoundException")) {
    		new Notification("ERROR: document does not exist.").show();
    	}
    	else {
    		new Notification(caught.getClass() + " Error: "
                    + caught.getMessage()).show();    	
    	}
        GWT.log("Error getting document.", caught);
    }

    @Override
    public void onSuccess(Document result) {
        this.chrome.add(result);
        this.chrome.show(result);
    }
}
