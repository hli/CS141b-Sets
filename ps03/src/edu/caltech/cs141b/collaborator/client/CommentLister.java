package edu.caltech.cs141b.collaborator.client;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.Range;

import edu.caltech.cs141b.collaborator.common.Comment;
import edu.caltech.cs141b.collaborator.ui.CommentList;
import edu.caltech.cs141b.collaborator.ui.Notification;


/**
 * Used in conjunction with <code>CollaboratorService.getComments()</code>.
 */
public class CommentLister implements AsyncCallback<List<Comment>> {

    private CommentList commentList;
    private Range range;
    
    public CommentLister(CommentList commentList, Range range) {
        this.commentList = commentList;
        this.range = range;
    }

    public void getComments(String key, int start, int end) {
        Main.service.getComments(key, start, end, this);
    }

    @Override
    public void onFailure(Throwable caught) {
        new Notification(caught.getClass() + " Error: "
                + caught.getMessage()).show();
        GWT.log("Error getting comment list.", caught);
    }

    @Override
    public void onSuccess(List<Comment> result) {
        this.commentList.getCommentData().updateRowData(this.range.getStart(), result);
        //this.commentList.refresh(result);
        GWT.log("Got " + result.size() + " comments.");
    }
}
