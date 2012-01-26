package edu.caltech.cs141b.hw2.gwt.collab.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;

import edu.caltech.cs141b.collaborator.common.Document;
import edu.caltech.cs141b.collaborator.common.DocumentHeader;
import edu.caltech.cs141b.collaborator.common.PMF;
import edu.caltech.cs141b.collaborator.server.CollaboratorServer;
import edu.caltech.cs141b.collaborator.server.data.DocumentData;
import edu.caltech.cs141b.hw2.gwt.collab.client.CollaboratorService;
import edu.caltech.cs141b.hw2.gwt.collab.shared.DocumentMetadata;
import edu.caltech.cs141b.hw2.gwt.collab.shared.LockExpired;
import edu.caltech.cs141b.hw2.gwt.collab.shared.LockUnavailable;
import edu.caltech.cs141b.hw2.gwt.collab.shared.LockedDocument;
import edu.caltech.cs141b.hw2.gwt.collab.shared.UnlockedDocument;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class CollaboratorServiceImpl extends RemoteServiceServlet implements
        CollaboratorService {

    private static final Logger log = Logger
            .getLogger(CollaboratorServiceImpl.class.toString());
    private static CollaboratorServer server = new CollaboratorServer();

    @Override
    public List<DocumentMetadata> getDocumentList() {

        List<DocumentMetadata> metadata = new ArrayList<DocumentMetadata>();
        List<DocumentHeader> headers = server.getDocuments();

        if (!headers.isEmpty()) {
            for (DocumentHeader dh : headers) {
                metadata.add(new DocumentMetadata(dh.getKey(), dh.getTitle()));
            }
        }

        return metadata;
    }

    @Override
    public LockedDocument lockDocument(String documentKey)
            throws LockUnavailable {
        /*
         * TODO: Call the CollaboratorServer.checkoutDocument() method and
         * transform Document to LockedDocument.
         */
        Document doc = server.checkoutDocument(documentKey);
        PersistenceManager pm = PMF.get().getPersistenceManager();
        DocumentData result = pm.getObjectById(DocumentData.class, 
                KeyFactory.stringToKey(doc.getKey()));
        return new LockedDocument(result.getLockedBy(), result.getLockedUntil(),
                doc.getKey(), doc.getTitle(), doc.getContents());
    }

    @Override
    public UnlockedDocument getDocument(String documentKey) {
        /*
         * TODO: Call the CollaboratorServer.getDocument() method and transform
         * Document to UnlockedDocument.
         */
        Document doc = server.getDocument(documentKey);

        return new UnlockedDocument(doc.getKey(), doc.getTitle(), 
                doc.getContents());
    }

    @Override
    public UnlockedDocument saveDocument(LockedDocument doc) throws LockExpired {
        /*
         * TODO: Transform LockedDocument to Document, call the
         * CollaboratorServer.commitDocument() method, call the
         * CollaboratorServer.checkinDocument() method. Transform resulting
         * Document to UnlockedDocument.
         */
        Document plainDoc = new Document(doc.getKey(), doc.getTitle(), 
                doc.getContents(), true);
        server.commitDocument(plainDoc);
        server.checkinDocument(plainDoc);
        
        return new UnlockedDocument(plainDoc.getKey(), plainDoc.getTitle(), 
                plainDoc.getContents());
    }

    @Override
    public void releaseLock(LockedDocument doc) throws LockExpired {
        /*
         * TODO: Transform LockedDocument to Document, call the
         * CollaboratorServer.checkinDocument() method.
         */
        Document plainDoc = new Document(doc.getKey(), doc.getTitle(),
                doc.getContents(), true);
        server.checkinDocument(plainDoc);
    }

}