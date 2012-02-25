package edu.caltech.cs141b.collaborator.server;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gdevelop.gwt.syncrpc.SyncProxy;


import edu.caltech.cs141b.collaborator.common.CollaboratorService;


@SuppressWarnings("serial")
public class TaskHandler extends HttpServlet {

    private static CollaboratorService rpcService = (CollaboratorService) SyncProxy.newProxyInstance(CollaboratorService.class,
                  "http://localhost:8888/Collaborator", "collab");
    
    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String docKey = req.getParameter("docKey");
        String clientId = req.getParameter("clientId");
        
        rpcService.handleExpire(docKey, clientId, req.getHeader("X-AppEngine-TaskName"));
    }
}
