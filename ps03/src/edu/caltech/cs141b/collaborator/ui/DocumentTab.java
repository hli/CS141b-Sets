package edu.caltech.cs141b.collaborator.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PushButton;

import edu.caltech.cs141b.collaborator.client.DocCheckinner;
import edu.caltech.cs141b.collaborator.client.Main;
import edu.caltech.cs141b.collaborator.common.Document;

/**
 * Document Tab
 * 
 * The widget of panel tab that consists of a document title and a close button. 
 * 
 * @author joewang
 *
 */
public class DocumentTab extends Composite {

    /**
     * The document key stored in this tab.
     */
    private String documentKey;
    
    /**
     * The title field for this tab.
     */
    private HTML title;
    
    /**
     * Class Constructor.
     * 
     * @param document
     *   Document to associate with this tab.
     */
    public DocumentTab(Document document) {
        
        // Save the document key.
        this.documentKey = document.getKey();
        
        // Create the panel that holds everything.
        FlowPanel panel = new FlowPanel();
        
        // Add the close button.
        panel.add(new PushButton("x", new ClickHandler() {
            public void onClick(ClickEvent event) {
                new DocCheckinner().checkinDocument(new Document(DocumentTab.this.documentKey, null, null, null));
                Main.chrome.remove(DocumentTab.this.documentKey);
            }
        }));
        
        // Add the document title.
        this.title = new HTML(document.getTitle());
        panel.add(this.title);
        
        // Initialize the widgets.
        this.initWidget(panel);
        
        // Set the tab style.
        this.setStyleName("documentTab");
    }
    
    /**
     * Set tab title.
     * 
     * Sets the tab's title text to the title of the given document.
     * 
     * @param document
     *   Document from which we want to extract the title.
     */
    public void setTitle(Document document) {
        this.title.setHTML(document.getTitle());
    }
}
