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

import edu.caltech.cs141b.collaborator.client.RevisionLister;
import edu.caltech.cs141b.collaborator.common.Document;
import edu.caltech.cs141b.collaborator.common.DocumentRevision;

public class Revisions extends PopupPanel {

    private List<DocumentRevision> revisions;
    private int cursor = 0;
    
    private Label revisionTitle;
    private Label revisionBy;
    private Label revisionTime;
    private TextArea revisionText;
    
    public Revisions() {
        super(true, true);
        
        // Create the panel that holds everything.
        DockLayoutPanel panel = new DockLayoutPanel(Unit.PX);
        
        // Setup toolbar.
        Toolbar toolbar = new Toolbar();
        toolbar.add(this.btnPrevRev);
        toolbar.add(this.btnNextRev);
        panel.addNorth(toolbar, 50);
        
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
        
    }
    
    public void show() {
        this.cursor = 0;
        this.update(this.revisions.get(this.cursor));
        super.show();
        this.center();
    }

    public void refresh(List<DocumentRevision> revisions) {
        this.revisions = revisions;
    }
    
    private void update(DocumentRevision revision) {
        this.revisionTitle.setText(revision.getTitle());
        this.revisionBy.setText(revision.getUpdatedBy());
        this.revisionTime.setText(revision.getUpdatedTime().toString());
        this.revisionText.setText(revision.getContents());
    }
    
    private ToolbarButton btnNextRev =
        new ToolbarButton(new Image(Resources.INSTANCE.nextRev()),
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
    
    private ToolbarButton btnPrevRev =
        new ToolbarButton(new Image(Resources.INSTANCE.prevRev()),
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
