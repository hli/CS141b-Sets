package edu.caltech.cs141b.collaborator.client;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.caltech.cs141b.collaborator.ui.DocumentList;
import edu.caltech.cs141b.collaborator.ui.Notification;

import edu.caltech.cs141b.collaborator.common.DocumentHeader;

/**
 * Used in conjunction with <code>CollaboratorService.getDocuments()</code>.
 */
public class DocLister implements AsyncCallback<List<DocumentHeader>> {

	private DocumentList documentList;
	
    public DocLister(DocumentList documentList) {
        this.documentList = documentList;
    }

    public void getDocuments() {
        Main.service.getDocuments(this);
    }

    @Override
    public void onFailure(Throwable caught) {
    	new Notification(caught.getClass() + " Error: "
                + caught.getMessage()).show();
        GWT.log("Error getting document list.", caught);
    }

    @Override
    public void onSuccess(List<DocumentHeader> result) {
        if (result == null || result.size() == 0) {
            new Notification("No documents available.").show();
        } 
        else {
        	this.documentList.refresh(result);
        	this.documentList.show();
            GWT.log("Got " + result.size() + " documents.");
        }
    }
}


