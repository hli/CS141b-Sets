package edu.caltech.cs141b.collaborator.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;

import edu.caltech.cs141b.collaborator.common.Document;
import edu.caltech.cs141b.collaborator.common.DocumentRevision;

public class Revisions extends PopupPanel {

    private String key;
    private List<DocumentRevision> revisions;
    private int cursor = 0;
    
    private Toolbar toolbar;
    private Label revisionTitle;
    private Label revisionBy;
    private Label revisionTime;
    private TextArea revisionText;
    
    public Revisions(Document document) {
        super(true, true);
        this.key = document.getKey();
        
        // Create the panel that holds everything.
        DockLayoutPanel panel = new DockLayoutPanel(Unit.PX);
        
        // Setup toolbar.
        this.toolbar = new Toolbar();
        this.toolbar.add(this.btnPrevRev());
        this.toolbar.add(this.btnNextRev());
        panel.addNorth(this.toolbar, 50);
        
        // Setup the revision information area.
        FlowPanel revInfo = new FlowPanel();
        revInfo.setStyleName("revInfo");
        
        this.revisionTitle = new Label();
        this.revisionTitle.setStyleName("revisionTitle");
        revInfo.add(this.revisionTitle);
        
        this.revisionBy = new Label();
        this.revisionBy.setStyleName("revisionBy");
        revInfo.add(this.revisionBy);
        
        this.revisionTime = new Label();
        this.revisionTime.setStyleName("revisionTime");
        revInfo.add(this.revisionTime);
        
        panel.addNorth(revInfo, 40);
        
        // Setup the revision text area.
        this.revisionText = new TextArea();
        this.revisionText.setEnabled(false);
        panel.add(this.revisionText);
        
        this.setHeight("600px");
        this.setWidth("600px");
        
        this.setStyleName("revisions");
        this.setGlassEnabled(true);
        this.setWidget(panel);
        
        
        /* Temp stuff. */
        this.revisions = new ArrayList<DocumentRevision>();
        this.revisions.add(new DocumentRevision("key1", "title1", "text1",
            new Date(), "me"));
        this.revisions.add(new DocumentRevision("key2", "title2", "text2",
                new Date(), "me"));
    }
    
    public void show() {
        /* TODO: get the list of revisions for this document. */
        
        this.cursor = 0;
        this.update(this.revisions.get(this.cursor));
        super.show();
        this.center();
    }
    
    private void update(DocumentRevision revision) {
        this.revisionTitle.setText(revision.getTitle());
        this.revisionBy.setText(revision.getUpdatedBy());
        this.revisionTime.setText(revision.getUpdatedTime().toString());
        this.revisionText.setText(revision.getContents());
    }
    
    private ToolbarButton btnNextRev() {
        return new ToolbarButton(new Image(Resources.INSTANCE.nextRev()),
                "Next Revision", new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (Revisions.this.cursor == 0) {
                    new Notification("You're viewing the latest revision.").show();
                } else {
                    Revisions.this.cursor -= 1;
                    Revisions.this.update(Revisions.this.revisions.get(Revisions.this.cursor));
                }
                
            }
        });
    }
    
    private ToolbarButton btnPrevRev() {
        return new ToolbarButton(new Image(Resources.INSTANCE.prevRev()),
                "Previous Revision", new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (Revisions.this.cursor == Revisions.this.revisions.size() - 1) {
                    new Notification("You're viewing the earliest revision.").show();
                } else {
                    Revisions.this.cursor += 1;
                    Revisions.this.update(Revisions.this.revisions.get(Revisions.this.cursor));
                }
            }
        });
    }
}
