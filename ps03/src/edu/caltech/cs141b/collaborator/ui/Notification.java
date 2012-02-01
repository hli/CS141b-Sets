package edu.caltech.cs141b.collaborator.ui;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * System Notification
 * 
 * Creates and shows a temporary popup notification.
 * 
 * @author joewang
 *
 */
public class Notification extends PopupPanel {

    /**
     * Class Constructor.
     * 
     * @param text
     *   Text to show in the notification.
     */
    public Notification(String text) {
        super(true);
        this.setStyleName("notification");
        this.setWidget(new Label(text));
    }

    /**
     * Show the notification.
     */
    public void show() {
        super.show();
        this.center();
        
        Timer hide = new Timer() {
            @Override
            public void run() {
                Notification.this.hide();
            }
        };

        // Schedule the timer to close the popup in 3 seconds.
        hide.schedule(3000);
    }
}