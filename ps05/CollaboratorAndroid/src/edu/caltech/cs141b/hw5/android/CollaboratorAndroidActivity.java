package edu.caltech.cs141b.hw5.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import edu.caltech.cs141b.hw5.android.data.DocumentMetadata;
import edu.caltech.cs141b.hw5.android.data.InvalidRequest;
import edu.caltech.cs141b.hw5.android.data.LockExpired;
import edu.caltech.cs141b.hw5.android.data.LockUnavailable;
import edu.caltech.cs141b.hw5.android.data.LockedDocument;
import edu.caltech.cs141b.hw5.android.proto.CollabServiceWrapper;

public class CollaboratorAndroidActivity extends ListActivity {
	
	private static String TAG = "AndroidActivity";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Log.d(TAG, "starting activity");
        /*
        CollabServiceWrapper service = new CollabServiceWrapper();      
        List<DocumentMetadata> metas = service.getDocumentList();
      
        for (DocumentMetadata meta : metas) {
        	docsInfo += meta.getKey() + ": " + meta.getTitle() + "\n"; 
        }
        
        // Try lock and unlocking a document
        try {
			LockedDocument ld = service.lockDocument(metas.get(0).getKey());
			Log.i(TAG, "locked");
			
			// try modify and save the document
			LockedDocument mld = new LockedDocument(ld.getLockedBy(), 
					ld.getLockedUntil(), ld.getKey(), ld.getTitle() + " mod1", ld.getContents());
			service.saveDocument(mld);
			Log.i(TAG, "saved");
			
			// Should get lock expired here
			service.releaseLock(ld);
			Log.i(TAG, "unlocked");
        } catch (LockExpired e) {
        	Log.i(TAG, "lock expired when attemping release.");
		} catch (LockUnavailable e) {
			Log.i(TAG, "Lock unavailable.");
		} catch (InvalidRequest e) {
			Log.i(TAG, "Invalid request");
		}
        
        TextView tv = new TextView(this);
        tv.setText(docsInfo);
        setContentView(tv);
        //setContentView(R.layout.main);
        */
       
        
        // Refresh the document list.
        this.refresh();
        
        // Configure list view options.
        ListView lv = this.getListView();
        lv.setTextFilterEnabled(true);
        lv.setOnItemClickListener(new OnItemClickListener() {
          public void onItemClick(AdapterView<?> parent, View view,
              int position, long id) {
              
              // Start the document activity.
              Intent intent = new Intent(CollaboratorAndroidActivity.this, DocumentActivity.class);
              intent.putExtra("docKey", ((TextView) ((LinearLayout) view).getChildAt(1)).getText());
              CollaboratorAndroidActivity.this.startActivity(intent);
          }
        });
    }
    
    public void refresh() {       
        /* TOOD: Get document metadata here. */
        /* TODO: Convert metadata to the list of hashmap format below. */
        
        List<Map<String,String>> docs = new ArrayList<Map<String, String>>();
        
        Map<String,String> entry1 = new HashMap<String, String>();
        entry1.put("docTitle", "Document 1 Title");
        entry1.put("docKey", "Document 1 Key");
        docs.add(entry1);
        
        Map<String,String> entry2 = new HashMap<String, String>();
        entry2.put("docTitle", "Document 2 Title");
        entry2.put("docKey", "Document 2 Key");
        docs.add(entry2);
        
        this.setListAdapter(new SimpleAdapter(this, docs, R.layout.list_item, 
                new String[] {"docTitle", "docKey"}, 
                new int[] { R.id.docTitle, R.id.docKey }));
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.refresh:
                this.refresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}