package edu.caltech.cs141b.collaborator.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.caltech.cs141b.collaborator.common.Document;
import edu.caltech.cs141b.collaborator.ui.Notification;


/**
 * Used in conjunction with <code>CollaboratorService.checkinDocument(String key)</code>.
 */
public class DocCheckinner implements AsyncCallback<Document> {

	private Chrome chrome;
	
    public DocCheckinner(Chrome chrome) {
        this.chrome = chrome;
    }

    public void checkinDocument(Document doc) {
        Main.service.checkinDocument(doc, this);
    }

    @Override
    public void onFailure(Throwable caught) {
    	if (caught.getClass().equals("LockReader")) {
    		new Notification(caught.getMessage()).show();
    	}
    	else if (caught.getClass().equals("JDOObjectNotFoundException")) {
    		new Notification("ERROR: document does not exist.").show();
    	}
    	else {
    		new Notification(caught.getClass() + " Error: "
                    + caught.getMessage()).show();    	
    	}
        GWT.log("Error getting document list.", caught);
    }

    @Override
    public void onSuccess(Document result) {
        this.chrome.remove(result.getKey());
        new Notification("Document \"" + result.getTitle() + "\" checked in.").show();
    }
}

