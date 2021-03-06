package edu.caltech.cs141b.collaborator.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;

import edu.caltech.cs141b.collaborator.common.CollaboratorService;
import edu.caltech.cs141b.collaborator.common.CollaboratorServiceAsync;

public class Main implements EntryPoint {

    /**
     * Singleton that maintains the Collaborator chrome.
     */
    public static final Chrome chrome = new Chrome();
    
    /**
     * Create a remote service proxy to talk to the server-side service.
     */
    public static final CollaboratorServiceAsync service = GWT
            .create(CollaboratorService.class);
    
    public static final int mealTime = 1000;
    
    public static final int thinkTime = 1000;
    
    public static String clientId = Integer.toString(Random.nextInt());
    
    public void onModuleLoad() {
        
        // Add the default chrome contents.
        Main.chrome.init();
        
        // Make the loading display invisible and the application visible.
        RootLayoutPanel.get().add(Main.chrome);
        RootPanel.get("loading").setVisible(false);

        // Initialize channel between server and client.
        new ChannelCreator().createChannel(clientId);
    }
}