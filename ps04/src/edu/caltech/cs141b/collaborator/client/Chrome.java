package edu.caltech.cs141b.collaborator.client;

import java.util.HashMap;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TabLayoutPanel;

import edu.caltech.cs141b.collaborator.common.Document;
import edu.caltech.cs141b.collaborator.ui.DocumentList;
import edu.caltech.cs141b.collaborator.ui.DocumentTab;
import edu.caltech.cs141b.collaborator.ui.Editor;
import edu.caltech.cs141b.collaborator.ui.Resources;
import edu.caltech.cs141b.collaborator.ui.ToolbarButton;

/**
 * Chrome makes up the outer UI of the Collaborator.
 * 
 * @author joewang
 *
 */
public class Chrome extends ResizeComposite {

    /**
     * Tabs that line the top of the interface.
     */
    private TabLayoutPanel tabPanel;
    
    /**
     * The widget that lists the documents available to be opened.
     */
    private DocumentList documentList;
    
    /**
     * Editors currently open in the Collaborator.
     */
    private HashMap<String, Editor> editors;
    
    /**
     * Class Constructor.
     */
    public Chrome() {
               
        // Setup the editors.
        this.editors = new HashMap<String, Editor>();
        
        // Setup the editor tabs.
        this.tabPanel = new TabLayoutPanel(50, Unit.PX);
        this.tabPanel.setHeight("100%");
        this.tabPanel.setStyleName("tabPanel");
        
        // Initialize the widgets.
        this.initWidget(this.tabPanel);
    }
    
    /**
     * Initialize Default Content
     * 
     * We add content outside of the constructor to avoid recursive
     * dependencies.
     */
    public void init() {
        
        // Setup the document list.
        this.documentList = new DocumentList();

        // Add a welcome tab.
        this.tabPanel.add(new Welcome(), "Welcome");
    }
    
    /**
     * Add Document
     * 
     * Opens a new editor to view the given document.
     * @param document
     *   Document to render in the editor.
     */
    public void add(Document document) {
        if (!this.editors.containsKey(document.getKey()))
        {
            DocumentTab t = new DocumentTab(document);
            Editor e = new Editor(document, t);
            this.editors.put(document.getKey(), e);
            this.tabPanel.add(e, t);
        }
        this.editors.get(document.getKey()).refresh(document);
    }
    
    /**
     * Remove Document
     * 
     * Closes and removes the editor associated with the given document key.
     * 
     * @param documentKey
     *   Document key to identify the document.
     * @return
     *   True if document existed and was successfully removed, false otherwise.
     */
    public boolean remove(String documentKey) {
        if (this.editors.containsKey(documentKey)) {
            this.tabPanel.remove(this.editors.get(documentKey));
            this.editors.remove(documentKey);
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Show Document
     * 
     * Brings the given document to the front.
     * 
     * @param document
     *   Document to show.
     * @return
     *   Returns whether the operation was successful.
     */
    public boolean show(Document document) {
        if (this.editors.containsKey(document.getKey()))
        {
            this.tabPanel.selectTab(this.editors.get(document.getKey()));
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Document Library Toolbar Button.
     */
    public ToolbarButton btnDocumentLibrary() {
        return new ToolbarButton(new Image(Resources.INSTANCE.documents()),
            "Document Library", new ClickHandler() {
            public void onClick(ClickEvent event) {
            
                new DocLister(Chrome.this.documentList).getDocuments();
            
            }
        });
    }
    
    /**
     * Simulate Toolbar Button.
     */
    public ToolbarButton btnSimulate() {
        final Chrome chromium = this;
        return new ToolbarButton(new Image(Resources.INSTANCE.simulate()),
            "Simulate", new ClickHandler() {
            public void onClick(ClickEvent event) {
                
                new Simulator(chromium).simulate();
            
            }
        });
    }
    
    /**
     * Accessor for editors.
     */
    public HashMap<String, Editor> getEditors() {
        return this.editors;
    }
}