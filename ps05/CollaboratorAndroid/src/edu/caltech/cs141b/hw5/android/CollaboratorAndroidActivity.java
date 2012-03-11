package edu.caltech.cs141b.hw5.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import edu.caltech.cs141b.hw5.android.data.DocumentMetadata;
import edu.caltech.cs141b.hw5.android.proto.CollabServiceWrapper;

public class CollaboratorAndroidActivity extends ListActivity {
	
	private static String TAG = "AndroidActivity";
	private static CollabServiceWrapper service = new CollabServiceWrapper();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Log.d(TAG, "starting activity");

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
        List<Map<String,String>> docs = new ArrayList<Map<String, String>>();
        List<DocumentMetadata> docList = service.getDocumentList();
        
        // Populate document index
        if (!docList.isEmpty()) {
            for (DocumentMetadata dm : docList) {
                Map<String,String> entry = new HashMap<String, String>();
                entry.put("docTitle", dm.getTitle());
                entry.put("docKey", dm.getKey());
                docs.add(entry);
            }
        }
        
        this.setListAdapter(new SimpleAdapter(this, docs, R.layout.list_item, 
                new String[] {"docTitle", "docKey"}, 
                new int[] { R.id.docTitle, R.id.docKey }));
    }
    
    public void newDoc() {
        // Create a new document.
        Intent intent = new Intent(CollaboratorAndroidActivity.this, DocumentActivity.class);
        intent.putExtra("docKey", "");
        CollaboratorAndroidActivity.this.startActivity(intent);
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
        
            case R.id.newdoc:
                this.newDoc();
                return true;
                
            case R.id.refresh:
                this.refresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}