package edu.caltech.cs141b.collaborator.client;

import java.util.Random;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.caltech.cs141b.collaborator.ui.Editor;
import edu.caltech.cs141b.collaborator.ui.Notification;

import edu.caltech.cs141b.collaborator.common.Document;

/**
 * Used in conjunction with <code>CollaboratorService.checkoutDocument(String key)</code>.
 */
public class DocCheckouter implements AsyncCallback<Document> {

    private Editor editor;

    public DocCheckouter() {
        this.editor = null;
    }
    
    public DocCheckouter(Editor editor) {
        this.editor = editor;
    }

    public void checkoutDocument(String key, String clientId) {
        Main.service.checkoutDocument(key, clientId, this);
    }

    @Override
    public void onFailure(Throwable caught) {
    	if (caught.getClass().equals("LockUnavailable")) {
    		new Notification(caught.getMessage()).show();
    	}
    	else if (caught.getClass().equals("JDOObjectNotFoundException")) {
    		new Notification("ERROR: document does not exist.").show();
    	}
    	else {
    		new Notification(caught.getClass() + " Error: "
                    + caught.getMessage()).show();    	
    	}
        GWT.log("Error checking out document.", caught);
    }

    @Override
    public void onSuccess(Document result) {
        if (result != null) {
            if (this.editor != null)
                this.editor.refresh(result);
            new Notification("Document \"" + result.getTitle() + "\" checked out.").show();
            //If it's in the simulation, here is where we eat.
            if (result.getSimulate()) {
                final Document doc = result;
                final Editor editor = this.editor;
                Random randomGenerator = new Random();
                Timer t = new Timer() {;
                    public void run() {
                        if (editor != null) {
                            doc.setContents(doc.getContents() + "\n" + Main.clientId);
                            new DocCommitter(editor).commitDocument(doc, Main.clientId);
                            new DocCheckinner(editor).checkinDocument(doc, Main.clientId);
                        }
                        else {
                            new DocCommitter().commitDocument(doc, Main.clientId);
                            new DocCheckinner().checkinDocument(doc, Main.clientId);
                        }
                    }
                };
                t.schedule(randomGenerator.nextInt(Main.mealTime));
            }
        }
    }
}