package edu.caltech.cs141b.collaborator.client;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;

import edu.caltech.cs141b.collaborator.ui.Resources;
import edu.caltech.cs141b.collaborator.ui.Toolbar;

/**
 * Welcome Tab
 * 
 * @author joewang
 *
 */
public class Welcome extends Composite {

    /**
     * Class Constructor.
     */
    public Welcome() {
        
        // Create the panel that holds everything.
        DockLayoutPanel panel = new DockLayoutPanel(Unit.PX);
        
        // Setup toolbars.
        Toolbar toolbar = new Toolbar();
        toolbar.add(Main.chrome.btnDocumentLibrary());
        toolbar.add(Main.chrome.btnSimulate());
        panel.addNorth(toolbar, 50);

        // Construct the welcome message.
        HTML welcomeHtml = new HTML(Resources.INSTANCE.welcomeMessage().getText());
        welcomeHtml.setStyleName("welcomeHtml");
        panel.add(welcomeHtml);

        // Initialize the widgets.
        this.initWidget(panel);
        
        // Set the style to be "editor"
        this.setStyleName("welcome");
    }
}
