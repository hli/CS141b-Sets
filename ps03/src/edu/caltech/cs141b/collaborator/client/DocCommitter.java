package edu.caltech.cs141b.collaborator.client;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.caltech.cs141b.collaborator.ui.Notification;
import edu.caltech.cs141b.collaborator.ui.Editor;

import edu.caltech.cs141b.collaborator.common.Document;
import edu.caltech.cs141b.collaborator.common.DocumentHeader;

/**
 * Used in conjunction with <code>CollaboratorService.commitDocument()</code>.
 */
public class DocCommitter implements AsyncCallback<Document> {

	private Editor editor;
	
    public DocCommitter(Editor editor) {
        this.editor = editor;
    }

    public void commitDocument(Document doc) {
        Main.service.commitDocument(doc, this);
    }

    @Override
    public void onFailure(Throwable caught) {
        new Notification("Error commiting document"
                + "; caught exception " + caught.getClass() + " with message: "
                + caught.getMessage()).show();
        GWT.log("Error commiting document.", caught);
    }

    @Override
    public void onSuccess(Document result) {
    	new Notification("Document saved.").show();
    	GWT.log("Document " + result + " saved.");
        this.editor.refresh(result);
        
    }
}

