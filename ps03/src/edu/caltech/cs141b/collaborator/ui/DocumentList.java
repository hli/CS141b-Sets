package edu.caltech.cs141b.collaborator.ui;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;

import edu.caltech.cs141b.collaborator.client.Main;
import edu.caltech.cs141b.collaborator.common.DocumentHeader;

public class DocumentList extends PopupPanel {

    private Toolbar toolbar;
    private CellList<DocumentHeader> headers;
    
    public DocumentList() {
        
        // Call the parent constructor.
        super(true);
        
        // Create the panel that holds everything.
        FlowPanel panel = new FlowPanel();
        
        // Create the toolbar for managing documents.
        this.toolbar = new Toolbar();
        
        toolbar.add(Main.chrome.btnNewDocument());
        
        toolbar.add(new ToolbarButton(new Image(Resources.INSTANCE.editTitle()),
                new ClickHandler() {
            public void onClick(ClickEvent event) {
                Window.alert("Edit Title");
            }
        }));
        
        toolbar.add(new ToolbarButton(new Image(Resources.INSTANCE.deleteDocument()),
                new ClickHandler() {
            public void onClick(ClickEvent event) {
                Window.alert("Delete Document");
            }
        }));
        
        panel.add(this.toolbar);
        
        // Create the list of document headers.
        this.headers = new CellList<DocumentHeader>(new DocumentHeaderCell());
        panel.add(this.headers);
        
        // Add the contents to the popup panel.
        this.setWidget(panel);
        
        // Position the popup panel.
        this.setPopupPosition(5, 50);
        
        // Set the style to be "editor"
        this.setStyleName("documentList");
    }
    
    private static class DocumentHeaderCell extends AbstractCell<DocumentHeader> {

        @Override
        public void render(Context context, DocumentHeader header, SafeHtmlBuilder sb) {
          /*
           * Always do a null check on the value. Cell widgets can pass null to
           * cells if the underlying data contains a null, or if the data arrives
           * out of order.
           */
          if (header == null) {
              return;
          }

          // If the value comes from the user, we escape it to avoid XSS attacks.
          SafeHtml title = SafeHtmlUtils.fromString(header.getTitle());

          // Append the comment.
          sb.appendHtmlConstant("<span class=\"title\">");
          sb.append(title);
          sb.appendHtmlConstant("</span>");
        }
      }
}
