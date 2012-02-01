package edu.caltech.cs141b.collaborator.client;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.caltech.cs141b.collaborator.ui.Notification;

import edu.caltech.cs141b.collaborator.common.Document;
import edu.caltech.cs141b.collaborator.common.DocumentHeader;

/**
 * Used in conjunction with <code>CollaboratorService.getDocumentList()</code>.
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
        new Notification("Error making new document"
                + "; caught exception " + caught.getClass() + " with message: "
                + caught.getMessage()).show();
        GWT.log("Error making new document.", caught);
    }

    @Override
    public void onSuccess(Document result) {
        chrome.add(result);
        new DocLister(chrome).getDocuments();
    }
}

