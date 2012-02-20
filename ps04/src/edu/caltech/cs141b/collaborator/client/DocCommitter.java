package edu.caltech.cs141b.collaborator.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.caltech.cs141b.collaborator.ui.Notification;
import edu.caltech.cs141b.collaborator.ui.Editor;

import edu.caltech.cs141b.collaborator.common.Document;

/**
 * Used in conjunction with <code>CollaboratorService.commitDocument(Document doc)</code>.
 */
public class DocCommitter implements AsyncCallback<Document> {

	private Editor editor;
	
    public DocCommitter(Editor editor) {
        this.editor = editor;
    }

    public void commitDocument(Document doc, String clientId) {
        Main.service.commitDocument(doc, clientId, this);
    }

    @Override
    public void onFailure(Throwable caught) {
    	if (caught.getClass().equals("LockExpired")) {
    		new Notification(caught.getMessage()).show();
    	}
    	else if (caught.getClass().equals("JDOObjectNotFoundException")) {
    		new Notification("ERROR: document does not exist.").show();
    	}
    	else {
    		new Notification(caught.getClass() + " Error: "
                    + caught.getMessage()).show();    	
    	}
        GWT.log("Error commiting document.", caught);
    }

    @Override
    public void onSuccess(Document result) {
        
        this.editor.refresh(result);
        new Notification("Document \"" + result.getTitle() + "\" saved.").show();
        
    	GWT.log("Document " + result + " saved.");        
    }
}

