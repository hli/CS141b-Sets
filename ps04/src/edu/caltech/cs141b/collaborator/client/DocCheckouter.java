package edu.caltech.cs141b.collaborator.client;

import com.google.gwt.core.client.GWT;
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

    public void checkoutDocument(String key) {
        Main.service.checkoutDocument(key, this);
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
        }
    }
}