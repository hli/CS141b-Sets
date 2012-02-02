package edu.caltech.cs141b.collaborator.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.caltech.cs141b.collaborator.ui.CommentList;
import edu.caltech.cs141b.collaborator.ui.Notification;

import edu.caltech.cs141b.collaborator.common.Comment;

/**
 * Used in conjunction with <code>CollaboratorService.addComment()</code>.
 */
public class CommentAdder implements AsyncCallback<Void> {

    private CommentList commentList;

    public CommentAdder(CommentList commentList) {
        this.commentList = commentList;
    }
    
    public void addComment(String key, String com) {
        Main.service.addComment(key, com, this);
    }

    @Override
    public void onFailure(Throwable caught) {
        if (caught.getClass().equals("JDOObjectNotFoundException")) {
            new Notification("ERROR: comment does not exist.").show();
        }
        else {
            new Notification(caught.getClass() + " Error: "
                    + caught.getMessage()).show();      
        }
        GWT.log("Error making new comment.", caught);
    }

    @Override
    public void onSuccess(Void _) {
        new Notification("Comment added.").show();
        new CommentNumGetter(this.commentList).getNumComments(this.commentList.getKey());
    }
}
