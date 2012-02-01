package edu.caltech.cs141b.collaborator.ui;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextArea;

import edu.caltech.cs141b.collaborator.common.Comment;
import edu.caltech.cs141b.collaborator.common.Document;

public class CommentList extends Composite implements KeyPressHandler {

    private TextArea text;
    private CellList<Comment> comments;
    
    public CommentList(Document document) {
        
        // Create the panel that holds everything.
        FlowPanel panel = new FlowPanel();
        
        // Create the textbox that adds new comments.
        this.text = new TextArea();
        this.text.setStyleName("text");
        this.text.addKeyPressHandler(this);
        panel.add(this.text);
        
        // Create the cell list that holds the rest of the comments.
        this.comments = new CellList<Comment>(new CommentCell());
        panel.add(this.comments);
        
        // Add the contents to the panel.
        this.initWidget(panel);
        
        // Set the style to be "editor"
        this.setStyleName("commentList");
    }
    
    public void onKeyPress(KeyPressEvent event) {
        if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
            new Notification("Saving comment.").show();
        }
    }
    
    private class CommentCell extends AbstractCell<Comment> {

      @Override
      public void render(Context context, Comment comment, SafeHtmlBuilder sb) {
        /*
         * Always do a null check on the value. Cell widgets can pass null to
         * cells if the underlying data contains a null, or if the data arrives
         * out of order.
         */
        if (comment == null) {
            return;
        }

        // If the value comes from the user, we escape it to avoid XSS attacks.
        SafeHtml user = SafeHtmlUtils.fromString(comment.getCommentBy());
        SafeHtml message = SafeHtmlUtils.fromString(comment.getMessage());

        // Append the comment.
        sb.appendHtmlConstant("<span class=\"commentBy\">");
        sb.append(user);
        sb.appendHtmlConstant("</span>");
        
        sb.appendHtmlConstant("<span class=\"comment\">");
        sb.append(message);
        sb.appendHtmlConstant("</span>");
      }
    }

}