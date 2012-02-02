package edu.caltech.cs141b.collaborator.client;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.caltech.cs141b.collaborator.ui.Editor;
import edu.caltech.cs141b.collaborator.ui.Notification;

import edu.caltech.cs141b.collaborator.common.DocumentRevision;

/**
 * Used in conjunction with <code>CollaboratorService.getRevisions()</code>.
 */
public class RevisionLister implements AsyncCallback<List<DocumentRevision>> {

    private Editor editor;
    
    public RevisionLister(Editor editor) {
        this.editor = editor;
    }

    public void getRevisions(String key) {
        Main.service.getRevisions(key, this);
    }

    @Override
    public void onFailure(Throwable caught) {
        new Notification(caught.getClass() + " Error: "
                + caught.getMessage()).show();
        GWT.log("Error getting revision list.", caught);
    }

    @Override
    public void onSuccess(List<DocumentRevision> result) {
        if (result == null || result.size() == 0) {
            new Notification("No revisions available.").show();
        } 
        else {
            this.editor.getRevisionList().refresh(result);
            this.editor.getRevisionList().show();
            GWT.log("Got " + result.size() + " revisions.");
        }
    }
}