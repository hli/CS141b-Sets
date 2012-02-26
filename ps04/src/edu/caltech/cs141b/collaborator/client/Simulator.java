package edu.caltech.cs141b.collaborator.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.caltech.cs141b.collaborator.ui.Notification;


/**
 * Used in conjunction with <code>CollaboratorService.simulate()</code>.
 */
public class Simulator implements AsyncCallback<String> {

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
    public void onSuccess(String key) {

    }
}