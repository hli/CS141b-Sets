package edu.caltech.cs141b.collaborator.client;

import java.util.Random;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.caltech.cs141b.collaborator.common.Document;
import edu.caltech.cs141b.collaborator.ui.Editor;
import edu.caltech.cs141b.collaborator.ui.Notification;


/**
 * Used in conjunction with <code>CollaboratorService.simulate()</code>.
 */
public class Simulator implements AsyncCallback<Document> {

    private Chrome chrome;
    
    public Simulator(Chrome chrome) {
        this.chrome = chrome;
    }
    
    public void simulate() {
        Main.service.simulate(this);
    }

    @Override
    public void onFailure(Throwable caught) {
        new Notification(caught.getClass() + " Error: "
                    + caught.getMessage()).show();      
        GWT.log("Error simulating.", caught);
    }

    @Override
    public void onSuccess(Document result) {
        // Think and then get hungry.
        final Document doc = result;
        this.chrome.add(doc);
        this.chrome.show(doc);
        final Editor editor = chrome.getEditors().get(doc.getKey());
        Random randomGenerator = new Random();
        Timer t = new Timer() {;
            public void run() {
                new DocCheckouter(editor).checkoutDocument(doc.getKey(), Main.clientId);
            }
        };
        t.schedule(randomGenerator.nextInt(Main.thinkTime));
        
    }
}