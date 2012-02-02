package edu.caltech.cs141b.collaborator.ui;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TextArea;

import edu.caltech.cs141b.collaborator.client.DocCheckinner;
import edu.caltech.cs141b.collaborator.client.DocCheckouter;
import edu.caltech.cs141b.collaborator.client.DocCommitter;
import edu.caltech.cs141b.collaborator.client.DocGetter;
import edu.caltech.cs141b.collaborator.client.Main;
import edu.caltech.cs141b.collaborator.common.Document;

public class Editor extends Composite {

    private Document document;
    private DocumentTab tab;
    private SimplePanel toolbarPanel;
    private DockLayoutPanel textCommentsPanel;
    private TextArea textarea;
    private CommentList comments;
    private Revisions revisions;
    
    public Editor(Document document, DocumentTab tab) {
        
        // Associate with document.
        this.document = document;
        
        // Associate with chrome document tab.
        this.tab = tab;
        
        // Create the panel that holds everything.
        DockLayoutPanel panel = new DockLayoutPanel(Unit.PX);
        
        // Setup toolbars.
        this.toolbarPanel = new SimplePanel();
        panel.addNorth(this.toolbarPanel, 50);
        
        // Setup the split panel to hold the text area and the comments list.
        this.textCommentsPanel = new DockLayoutPanel(Unit.PX);
       
        // Setup the comments list, hidden by default.
        this.comments = new CommentList(document);
        this.textCommentsPanel.addEast(this.comments, 0);
        this.comments.setVisible(false);
        
        // Setup the document revisions popup.
        this.revisions = new Revisions(document);
        
        // Setup the text area.
        this.textarea = new TextArea();
        if (!document.isLocked()) {
            this.textarea.setEnabled(false);
        }
        this.textCommentsPanel.add(this.textarea);
        panel.add(this.textCommentsPanel);
        
        // Call refresh routine to setup editor contents.
        this.refresh(document);
        
        // Initialize the widgets.
        this.initWidget(panel);
        
        // Set the style to be "editor"
        this.setStyleName("editor");
    }
    
    public void refresh(Document document) {
        
        // Add the toolbar based on document state.
        if (document.isLocked()) {
            this.toolbarPanel.setWidget(this.getLockedToolbar());
        } else {
            this.toolbarPanel.setWidget(this.getUnlockedToolbar());
        }
        
        // Update the local document.
        this.document = document;
        
        // Update the title.
        this.tab.setTitle(document);
        
        // Update the document contents.
        this.textarea.setText(document.getContents());
    }
    
    private Toolbar getUnlockedToolbar() {
        
        // Setup the buttons for the toolbar of an unlocked document:
        Toolbar toolbar = new Toolbar();
        toolbar.add(Main.chrome.btnDocumentLibrary());
        toolbar.add(this.btnRefresh);
        toolbar.add(this.btnCheckout);
        toolbar.add(this.btnComments);
        toolbar.add(this.btnRevisions);
        
        return toolbar;
    }
    
    private Toolbar getLockedToolbar() {

        Toolbar toolbar = new Toolbar();
        toolbar.add(Main.chrome.btnDocumentLibrary());
        toolbar.add(this.btnEditTitle);
        toolbar.add(this.btnCommit);
        toolbar.add(this.btnCheckin);
        toolbar.add(this.btnComments);
        toolbar.add(this.btnRevisions);
        
        return toolbar;
    }
    
    private ToolbarButton btnRefresh = 
        new ToolbarButton(new Image(Resources.INSTANCE.refresh()), 
                "Refresh Document", new ClickHandler() { 
            public void onClick(ClickEvent event) {
                
                new DocGetter(Main.chrome).getDocument(Editor.this.document.getKey());
                
            }
        });
    
    private ToolbarButton btnCheckout = 
        new ToolbarButton(new Image(Resources.INSTANCE.checkout()),
                "Lock Document", new ClickHandler() {
            public void onClick(ClickEvent event) {
                
                new DocCheckouter(Editor.this).checkoutDocument(Editor.this.document.getKey());
                Editor.this.textarea.setEnabled(true);
                
            }
        });
    
    /**
     * Edit Title Toolbar Button.
     */
    private ToolbarButton btnEditTitle = new ToolbarButton(new Image(
            Resources.INSTANCE.editTitle()), "Edit Title", new ClickHandler() {
        public void onClick(ClickEvent event) {

            String name = Window.prompt("Enter a new document title:",
                    Editor.this.document.getTitle());
            if (name != null && name.equals("")) {
                new Notification("Please enter a valid document title.")
                        .show();
            } else if (name != null) {
                Editor.this.document.setTitle(name);
                new DocCommitter(Editor.this).commitDocument(Editor.this.document);
            }

        }
    });
    
    private ToolbarButton btnCommit = 
        new ToolbarButton(new Image(Resources.INSTANCE.commit()),
                "Save Document", new ClickHandler() {
            public void onClick(ClickEvent event) {
                
                Editor.this.document.setContents(Editor.this.textarea.getText());
                new DocCommitter(Editor.this).commitDocument(Editor.this.document);
                Editor.this.textarea.setEnabled(false);
                
            }
        });
    
    private ToolbarButton btnCheckin = 
        new ToolbarButton(new Image(Resources.INSTANCE.checkin()),
                "Unlock Document", new ClickHandler() {
            public void onClick(ClickEvent event) {
                
                new DocCheckinner(Editor.this).checkinDocument(Editor.this.document);
                
            }
        });
    
    private ToolbarButton btnComments =
        new ToolbarButton(new Image(Resources.INSTANCE.comments()),
                "Comments", new ClickHandler() {
            public void onClick(ClickEvent event) {
                
                if (comments.isVisible()) {
                    Editor.this.textCommentsPanel.setWidgetSize(comments, 0);
                    Editor.this.comments.setVisible(false);
                } else {
                    Editor.this.comments.setVisible(true);
                    Editor.this.textCommentsPanel.setWidgetSize(comments, 300);
                }
            }
            
        });
    
    private ToolbarButton btnRevisions =
        new ToolbarButton(new Image(Resources.INSTANCE.revisions()),
                "Revisions", new ClickHandler() {
            public void onClick(ClickEvent event) {
                
                Editor.this.revisions.show();
                
            }
        });
}
