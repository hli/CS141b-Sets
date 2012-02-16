package edu.caltech.cs141b.collaborator.ui;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;

/**
 * ToolbarButton Widget
 * 
 * A push button that can be placed in a Toolbar.
 * 
 * @author joewang
 *
 */
public class ToolbarButton extends ComplexPanel {

    /**
     * PushButton associated with this ToolbarButton.
     */
    private PushButton button;
    
    /**
     * Class Constructor
     * 
     * Constructor for ToolbarButton that takes a static image and a handler.
     * 
     * @param upImage
     *   Image to display on the button.
     * @param handler
     *   Action to perform when clicking on the button.
     */
    public ToolbarButton(Image upImage, ClickHandler handler) {
        
        // Create the list element.
        this.setElement(Document.get().createLIElement());
        
        // Add the button to the list element.
        this.button = new PushButton(upImage, handler);
        super.add(this.button, getElement());
    }
    
    /**
     * Class Constructor
     * 
     * Constructor for ToolbarButton that takes an up image, title, and
     * a handler.
     * 
     * @param upImage
     *   Image to display on the button.
     * @param title
     *   Title to display when hovering over this button.
     * @param handler
     *   Action to perform when clicking on the button.
     */
    public ToolbarButton(Image upImage, String title, ClickHandler handler) {
        
        // Create the list element.
        this.setElement(Document.get().createLIElement());
        
        // Add the button to the list element.
        this.button = new PushButton(upImage, handler);
        this.button.setTitle(title);
        super.add(button, getElement());
    }

}