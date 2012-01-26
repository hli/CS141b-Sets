package edu.caltech.cs141b.hw2.gwt.collab.server;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import edu.caltech.cs141b.collaborator.common.DocumentHeader;
import edu.caltech.cs141b.collaborator.server.CollaboratorServer;
import edu.caltech.cs141b.hw2.gwt.collab.client.CollaboratorService;
import edu.caltech.cs141b.hw2.gwt.collab.shared.DocumentMetadata;
import edu.caltech.cs141b.hw2.gwt.collab.shared.LockExpired;
import edu.caltech.cs141b.hw2.gwt.collab.shared.LockUnavailable;
import edu.caltech.cs141b.hw2.gwt.collab.shared.LockedDocument;
import edu.caltech.cs141b.hw2.gwt.collab.shared.UnlockedDocument;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class CollaboratorServiceImpl extends RemoteServiceServlet implements
		CollaboratorService {
	
	private static final Logger log = Logger.getLogger(CollaboratorServiceImpl.class.toString());
	private static CollaboratorServer server = new CollaboratorServer();
	
	@Override
	public List<DocumentMetadata> getDocumentList() {
		
		List<DocumentMetadata> metadata = new ArrayList<DocumentMetadata>();
		List<DocumentHeader> headers = server.getDocuments();
		
		if (!headers.isEmpty()) {
			for (DocumentHeader dh : headers)
			{
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
		return null;
	}

	@Override
	public UnlockedDocument getDocument(String documentKey) {
		/*
		 * TODO: Call the CollaboratorServer.getDocument() method and
		 * transform Document to UnlockedDocument.
		 */
		return null;
	}

	@Override
	public UnlockedDocument saveDocument(LockedDocument doc)
			throws LockExpired {
		/*
		 * TODO: Transform LockedDocument to Document, call the
		 * CollaboratorServer.commitDocument() method, call the
		 * CollaboratorServer.checkinDocument() method. Transform resulting
		 * Document to UnlockedDocument.
		 */
		return null;
	}
	
	@Override
	public void releaseLock(LockedDocument doc) throws LockExpired {
		/*
		 * TODO: Transform LockedDocument to Document, call the 
		 * CollaboratorServer.checkinDocument() method.
		 */
	}

}