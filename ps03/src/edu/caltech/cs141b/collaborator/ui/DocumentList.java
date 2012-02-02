package edu.caltech.cs141b.collaborator.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
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
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

import edu.caltech.cs141b.collaborator.client.DocAdder;
import edu.caltech.cs141b.collaborator.client.DocGetter;
import edu.caltech.cs141b.collaborator.client.Main;
import edu.caltech.cs141b.collaborator.common.Document;
import edu.caltech.cs141b.collaborator.common.DocumentHeader;

/**
 * Document Library
 * 
 * List of available documents to consume.
 * 
 * @author joewang
 *
 */
public class DocumentList extends PopupPanel {

    /**
     * Data provider for the document library.
     */
    private ListDataProvider<DocumentHeader> headerData;
    
    /**
     * The list widget being displayed.
     */
    private CellList<DocumentHeader> headerList;
    
    /**
     * The currently selected document.
     */
    private DocumentHeader headerSelected;

    /**
     * Class Constructor.
     */
    public DocumentList() {

        // Call the parent constructor.
        super(true);

        // Create the panel that holds everything.
        FlowPanel panel = new FlowPanel();

        // Create the toolbar for managing documents.
        Toolbar toolbar = new Toolbar();
        toolbar.add(this.btnNewDocument);
        panel.add(toolbar);

        // Create the list of document headers.
        this.headerList = new CellList<DocumentHeader>(new DocumentHeaderCell());
        this.headerList.setStyleName("headerList");
        this.headerData = new ListDataProvider<DocumentHeader>();
        this.headerData.addDataDisplay(this.headerList);

        // Add a selection model to handle user selection.
        final SingleSelectionModel<DocumentHeader> selectionModel = new SingleSelectionModel<DocumentHeader>();
        this.headerList.setSelectionModel(selectionModel);
        selectionModel
                .addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
                    public void onSelectionChange(SelectionChangeEvent event) {
                        DocumentList.this.headerSelected = selectionModel
                                .getSelectedObject();
                    }
                });

        panel.add(this.headerList);

        // Add the contents to the popup panel.
        this.setWidget(panel);

        // Position the popup panel.
        this.setPopupPosition(5, 50);

        // Set the style to be "editor"
        this.setStyleName("documentList");
    }

    /**
     * Refresh Document Library.
     * 
     * Given a list of documents, repopulate the library.
     * 
     * @param headers
     *   List of document headers to use.
     */
    public void refresh(List<DocumentHeader> headers) {
        this.headerList.setVisibleRange(0, headers.size());
        this.headerData.setList(headers);
    }

    /**
     * New Document Toolbar Button.
     */
    private ToolbarButton btnNewDocument = new ToolbarButton(new Image(
            Resources.INSTANCE.newDocument()), "New Document",
            new ClickHandler() {
                public void onClick(ClickEvent event) {

                    String name = Window.prompt(
                            "Enter the new document title:", "Untitled");
                    if (name != null && name.equals("")) {
                        new Notification("Please enter a valid document title.")
                                .show();
                    } else if (name != null) {
                        new DocAdder(Main.chrome).newDocument(new Document(null, name, "", true));
                        DocumentList.this.hide();
                    }

                }
            });

    /**
     * Document Header Cell
     * 
     * Cell that handles and renders the document library.
     * 
     * @author joewang
     *
     */
    private class DocumentHeaderCell extends AbstractCell<DocumentHeader> {

        /**
         * Class Constructor.
         */
        public DocumentHeaderCell() {
            super("dblclick");
        }

        /**
         * Browser Event Handler.
         * 
         * Handles double click events.
         */
        @Override
        public void onBrowserEvent(Context context, Element parent,
                DocumentHeader value, NativeEvent event,
                ValueUpdater<DocumentHeader> valueUpdater) {

            super.onBrowserEvent(context, parent, value, event, valueUpdater);

            // Handle the click event.
            if ("dblclick".equals(event.getType())) {
                // Ignore clicks that occur outside of the outermost element.
                EventTarget eventTarget = event.getEventTarget();
                if (parent.getFirstChildElement().isOrHasChild(
                        Element.as(eventTarget))) {
                    new DocGetter(Main.chrome).getDocument(value.getKey());
                    DocumentList.this.hide();
                }
            }
        }

        /**
         * Render document header cell.
         */
        @Override
        public void render(Context context, DocumentHeader header,
                SafeHtmlBuilder sb) {
            /*
             * Always do a null check on the value. Cell widgets can pass null
             * to cells if the underlying data contains a null, or if the data
             * arrives out of order.
             */
            if (header == null) {
                return;
            }

            // If the value comes from the user, we escape it to avoid XSS
            // attacks.
            SafeHtml title = SafeHtmlUtils.fromString(header.getTitle());

            // Append the comment.
            sb.appendHtmlConstant("<div class=\"title\">");
            sb.append(title);
            sb.appendHtmlConstant("</div>");
        }
    }
}
