package edu.caltech.cs141b.collaborator.ui;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.ComplexPanel;

/**
 * Toolbar Widget
 * 
 * A toolbar consists of one or more ToolbarButtons that renders as an unordered
 * list.
 *  
 * @author joewang
 *
 */
public class Toolbar extends ComplexPanel {
    
    /**
     * Class Constructor.
     */
    public Toolbar() {
        
        // Create the unordered list.
        this.setElement(Document.get().createULElement());
        
        // Set the class to be a toolbar.
        this.setStyleName("toolbar");
    }

    /**
     * Add Button
     * 
     * Adds the given button to the toolbar.
     * 
     * @param b
     *   Button to add to the toolbar.
     */
    public void add(ToolbarButton b) {
        
        // ComplexPanel requires the two-arg add() method
        super.add(b, getElement());
    }

}