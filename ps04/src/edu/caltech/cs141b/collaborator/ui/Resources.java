package edu.caltech.cs141b.collaborator.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;

public interface Resources extends ClientBundle {
    
    public static final Resources INSTANCE = GWT.create(Resources.class);

    @Source("welcome.txt")
    TextResource welcomeMessage();
    
    @Source("archive.png")
    ImageResource documents();
    
    @Source("simulate.png")
    ImageResource simulate();
    
    @Source("document.png")
    ImageResource newDocument();
    
    @Source("document-edit.png")
    ImageResource editTitle();
    
    @Source("bin.png")
    ImageResource deleteDocument();
    
    @Source("button-synchronize.png")
    ImageResource refresh();
    
    @Source("lock-unlock.png")
    ImageResource checkout();
    
    @Source("floppy-disk.png")
    ImageResource commit();
    
    @Source("lock.png")
    ImageResource checkin();
    
    @Source("balloon.png")
    ImageResource comments();
    
    @Source("clock.png")
    ImageResource revisions();
    
    @Source("button-rew.png")
    ImageResource prevRev();
    
    @Source("button-ff.png")
    ImageResource nextRev();
    
}