package edu.caltech.cs141b.hw2.server;

import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.caltech.cs141b.hw2.gwt.collab.client.CollaboratorService;
import edu.caltech.cs141b.hw2.gwt.collab.shared.DocumentMetadata;
import edu.caltech.cs141b.hw2.gwt.collab.shared.LockExpired;
import edu.caltech.cs141b.hw2.gwt.collab.shared.LockUnavailable;
import edu.caltech.cs141b.hw2.gwt.collab.shared.LockedDocument;
import edu.caltech.cs141b.hw2.gwt.collab.shared.UnlockedDocument;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class CollaboratorServiceImpl extends RemoteServiceServlet implements
		CollaboratorService {
	
	private static final Logger log = Logger.getLogger(CollaboratorServiceImpl.class.toString());

	@Override
	public List<DocumentMetadata> getDocumentList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LockedDocument lockDocument(String documentKey)
			throws LockUnavailable {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UnlockedDocument getDocument(String documentKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UnlockedDocument saveDocument(LockedDocument doc) throws LockExpired {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void releaseLock(LockedDocument doc) throws LockExpired {
		// TODO Auto-generated method stub

	}

}
