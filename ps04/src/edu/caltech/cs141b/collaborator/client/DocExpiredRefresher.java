package edu.caltech.cs141b.collaborator.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.caltech.cs141b.collaborator.common.Document;
import edu.caltech.cs141b.collaborator.ui.Notification;

/**
 * Used in conjunction with <code>CollaboratorService.getDocument(String key)</code>.
 */
public class DocExpiredRefresher implements AsyncCallback<Document>{
    
    private Chrome chrome;
    
    public DocExpiredRefresher(Chrome chrome) {
        this.chrome = chrome;
    }

    public void getDocument(String key) {
        Main.service.getDocument(key, this);
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
        GWT.log("Error refreshing document.", caught);
    }

    @Override
    public void onSuccess(Document result) {
        this.chrome.add(result);
        new Notification("Document \"" + result.getTitle() + "\" lock expired.").show();
    }
}
