package edu.caltech.cs141b.collaborator.ui;

import java.util.Date;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;

import edu.caltech.cs141b.collaborator.client.CommentAdder;
import edu.caltech.cs141b.collaborator.client.CommentLister;
import edu.caltech.cs141b.collaborator.client.CommentNumGetter;
import edu.caltech.cs141b.collaborator.common.Comment;
import edu.caltech.cs141b.collaborator.common.Document;

public class CommentList extends Composite implements KeyPressHandler {

    private String key;
    private TextArea text;
    private CellList<Comment> comments;
    private AsyncDataProvider<Comment> commentData;
    
    public CommentList(Document document) {
        
        this.key = document.getKey();
        
        // Create the panel that holds everything.
        DockLayoutPanel panel = new DockLayoutPanel(Unit.PX);
        
        // Create the textbox that adds new comments.
        this.text = new TextArea();
        this.text.setStyleName("text");
        this.text.addKeyPressHandler(this);
        panel.addNorth(this.text, 71);
        
        // Create the cell list that holds the rest of the comments.
        this.comments = new CellList<Comment>(new CommentCell());
        this.comments.setPageSize(20);
        new CommentNumGetter(this).getNumComments(this.key);
        
        // Create data provider.
        this.commentData = new AsyncDataProvider<Comment>() {
            @Override
            protected void onRangeChanged(HasData<Comment> display) {

              final Range range = display.getVisibleRange();
              new CommentLister(CommentList.this, range).getComments(CommentList.this.key, range.getStart(), range.getLength());
            }
        };
        this.commentData.addDataDisplay(this.comments);
        
        // Create a ShowMorePagerPanel.
        ShowMorePagerPanel pager = new ShowMorePagerPanel();
        pager.setStyleName("commentPager");
        //SimplePager pager = new SimplePager();

        // Set the cellList as the display.
        pager.setDisplay(this.comments);

        // Add the pager and list to the page.
        panel.add(pager);
        
        // Add the contents to the panel.
        this.initWidget(panel);
        
        // Set the style to be "editor"
        this.setStyleName("commentList");
    }

    public void onKeyPress(KeyPressEvent event) {
        if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
            new CommentAdder().addComment(this.key, this.text.getText());
            this.text.setText("");
        }
    }

    public CellList<Comment> getComments() {
        return this.comments;
    }
    
    public AsyncDataProvider<Comment> getCommentData() {
        return this.commentData;
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