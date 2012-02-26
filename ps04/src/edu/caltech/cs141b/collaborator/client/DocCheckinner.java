package edu.caltech.cs141b.collaborator.client;

import java.util.Random;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.caltech.cs141b.collaborator.common.Document;
import edu.caltech.cs141b.collaborator.ui.Editor;
import edu.caltech.cs141b.collaborator.ui.Notification;

/**
 * Used in conjunction with <code>CollaboratorService.checkinDocument(String key)</code>.
 */
public class DocCheckinner implements AsyncCallback<Document> {

	private Editor editor;
	
	public DocCheckinner() {
	    this.editor = null;
	}
	
    public DocCheckinner(Editor editor) {
        this.editor = editor;
    }

    public void checkinDocument(Document doc, String clientId) {
        Main.service.checkinDocument(doc, clientId, this);
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
        if (this.editor != null) {
            this.editor.refresh(result);
        }
        new Notification("Document \"" + result.getTitle() + "\" checked in.").show();
        // If it's in the simulation, here is where we think and then get hungry.
        if (result.getSimulate()) {
            final Document doc = result;
            final Editor editor = this.editor;
            Random randomGenerator = new Random();
            Timer t = new Timer() {;
                public void run() {
                    if (editor != null) {
                        new DocCheckouter(editor).checkoutDocument(doc.getKey(), Main.clientId);
                    }
                    else {
                        new DocCheckouter().checkoutDocument(doc.getKey(), Main.clientId);
                    }
                }
            };
            t.schedule(randomGenerator.nextInt(Main.thinkTime));
        }
    }
}

