package edu.caltech.cs141b.collaborator.server.data;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;


@PersistenceCapable
public class ClientIds {
    
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
    
    @Persistent
    private List<String> clientIds = new ArrayList<String>();
    
    public ClientIds() {
    }

    public void setKey(Key key) {
        this.key = key;
    }
    
    public Key getKey() {
        return this.key;
    }
    
    public void addClient(String clientId) {
        this.clientIds.add(clientId);
    }
    
    public void removeClient(String clientId) {
        this.clientIds.remove(clientId);
    }
    
    public List<String> getClientIds() {
        return this.clientIds;
    }
    
    public Boolean contains(String clientId) {
        return this.clientIds.contains(clientId);
    }
}