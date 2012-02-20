package edu.caltech.cs141b.collaborator.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.caltech.cs141b.collaborator.common.Document;
import edu.caltech.cs141b.collaborator.ui.Editor;
import edu.caltech.cs141b.collaborator.ui.Notification;

/**
 * Used in conjunction with <code>CollaboratorService.checkinDocument(String key)</code>.
 */
public class DocCheckinner implements AsyncCallback<Document> {

	private Editor editor;
	
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
        this.editor.refresh(result);
        new Notification("Document \"" + result.getTitle() + "\" checked in.").show();
    }
}

