package edu.caltech.cs141b.collaborator.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.caltech.cs141b.collaborator.ui.CommentList;
import edu.caltech.cs141b.collaborator.ui.Notification;


/**
 * Used in conjunction with <code>CollaboratorService.getDocument(String key)</code>.
 */
public class CommentNumGetter implements AsyncCallback<Integer> {

    private CommentList commentList;
    
    public CommentNumGetter(CommentList commentList) {
        this.commentList = commentList;
    }

    public void getNumComments(String key) {
        Main.service.getNumComments(key, this);
    }

    @Override
    public void onFailure(Throwable caught) {
        if (caught.getClass().equals("JDOObjectNotFoundException")) {
            new Notification("ERROR: document does not exist.").show();
        }
        else {
            new Notification(caught.getClass() + " Error: "
                    + caught.getMessage()).show();      
        }
        GWT.log("Error getting comment list.", caught);
    }

    @Override
    public void onSuccess(Integer result) {
        if (this.commentList.getComments().getRowCount() < result)
        {
            Integer rows = this.commentList.getComments().getRowCount();
            this.commentList.getCommentData().updateRowCount(result, true);
            this.commentList.getComments().setVisibleRange(0, rows + Math.min(20, result - rows));
        }
        
    }
}
