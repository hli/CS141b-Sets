package edu.caltech.cs141b.hw5.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import edu.caltech.cs141b.hw5.android.data.InvalidRequest;
import edu.caltech.cs141b.hw5.android.data.LockExpired;
import edu.caltech.cs141b.hw5.android.data.LockUnavailable;
import edu.caltech.cs141b.hw5.android.data.LockedDocument;
import edu.caltech.cs141b.hw5.android.data.UnlockedDocument;
import edu.caltech.cs141b.hw5.android.proto.CollabServiceWrapper;

public class DocumentActivity extends Activity {
	
	private static String TAG = "AndroidActivity";
	private static CollabServiceWrapper service = new CollabServiceWrapper();
	
	private String docKey;
	private LockedDocument lockedDoc;
	private Boolean isLocked = true;
    private EditText title;
	private EditText contents;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Get the docKey
        this.docKey = this.getIntent().getExtras().getString("docKey");
        this.setContentView(R.layout.doc);
        
        // Set the controls
        this.title = (EditText) this.findViewById(R.id.title);
        this.contents = (EditText) this.findViewById(R.id.contents);

        // Refresh the existing document.
        if (!this.docKey.equals("")) {
            this.isLocked = false;
            this.title.setEnabled(false);
            this.contents.setEnabled(false);
            this.refresh();
        }
    }
    
    /** Called when the activity is paused. */
    @Override
    protected void onPause() {
        super.onPause();
        
        // If document is locked, we unlock.
        if (this.isLocked){
            try {
                service.releaseLock(this.lockedDoc);
                this.isLocked = false;
                this.lockedDoc = null;
                this.title.setEnabled(false);
                this.contents.setEnabled(false);
            } catch (InvalidRequest e) {
                Toast.makeText(getApplicationContext(), "Invalid request.", Toast.LENGTH_SHORT).show();
            } catch (LockExpired e) {
                Toast.makeText(getApplicationContext(), "Lock has expired.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    public void refresh(){
        // Only refresh when the document is unlocked.
        if (!this.isLocked) {
            try {
                UnlockedDocument doc = service.getDocument(this.docKey);
                this.contents.setText(doc.getContents());
                this.title.setText(doc.getTitle());
            } catch (InvalidRequest e) {
                Toast.makeText(getApplicationContext(), "Invalid request.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "You can only refresh when the document is unlocked.", Toast.LENGTH_SHORT).show();
        }
    }
    
    public void lock() {
        // Only lock when the document is unlocked.
        if (!this.isLocked) {
            try {
                this.lockedDoc = service.lockDocument(this.docKey);
                this.isLocked = true;
                this.title.setEnabled(true);
                this.contents.setEnabled(true);
            } catch (LockUnavailable e) {
                Toast.makeText(getApplicationContext(), "Lock is unavailable.", Toast.LENGTH_SHORT).show();
            } catch (InvalidRequest e) {
                Toast.makeText(getApplicationContext(), "Invalid request.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "You can only lock when the document is unlocked.", Toast.LENGTH_SHORT).show();
        }
    }
    
    public void save() {
        // Only save when the document is locked.
        if (this.isLocked) {
            try {
                // Refresh the existing document.
                if (!this.docKey.equals("")) {
                    this.lockedDoc.setTitle(this.title.getText().toString());
                    this.lockedDoc.setContents(this.contents.getText().toString());
                    service.saveDocument(this.lockedDoc);
                }
                // Create new document.
                else {
                    LockedDocument newDoc = new LockedDocument(null, null, null, this.title.getText().toString(), this.contents.getText().toString());
                    UnlockedDocument result = service.saveDocument(newDoc);
                    this.docKey = result.getKey();
                }
                this.isLocked = false;
                this.title.setEnabled(false);
                this.contents.setEnabled(false);
            } catch (LockExpired e) {
                Toast.makeText(getApplicationContext(), "Lock has expired.", Toast.LENGTH_SHORT).show();
            } catch (InvalidRequest e) {
                Toast.makeText(getApplicationContext(), "Invalid request.", Toast.LENGTH_SHORT).show();
            }
            
        } else {
            Toast.makeText(getApplicationContext(), "You can only save when the document is locked.", Toast.LENGTH_SHORT).show();
        }
        
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.doc_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.refresh:
                this.refresh();
                return true;
                
            case R.id.lock:
                this.lock();
                return true;
                
            case R.id.save:
                this.save();
                return true;
                
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}