package edu.caltech.cs141b.collaborator.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.caltech.cs141b.collaborator.ui.Notification;

import edu.caltech.cs141b.collaborator.common.Document;

/**
 * Used in conjunction with <code>CollaboratorService.newDocument(Document doc)</code>.
 */
public class DocAdder implements AsyncCallback<Document> {

	private Chrome chrome;
	
    public DocAdder(Chrome chrome) {
        this.chrome = chrome;
    }

    public void newDocument(Document doc) {
        Main.service.newDocument(doc, this);
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
        GWT.log("Error making new document.", caught);
    }

    @Override
    public void onSuccess(Document result) {
        this.chrome.add(result);
        this.chrome.show(result);
        new Notification("New document \"" + result.getTitle() + "\" made.").show();
    }
}

