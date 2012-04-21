package se.campingwebben.android.stellplats;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class TabsActivity extends TabActivity{

	private String id;
	
	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabs);

		// Get parameters from the Activity before
		Bundle extras = getIntent().getExtras();
		if(extras !=null) {
			// Get id for the item to show more information about
			id = extras.getString("id");
		}

		// TabHost will have Tabs
		TabHost tabHost = (TabHost)findViewById(android.R.id.tabhost);

		/** TabSpec used to create a new tab.
		 * By using TabSpec only we can able to setContent to the tab.
		 * By using TabSpec setIndicator() we can set name to tab. */

		// tid1 is firstTabSpec Id. Its used to access outside
		TabSpec firstTabSpec = tabHost.newTabSpec("tid1");
		TabSpec secondTabSpec = tabHost.newTabSpec("tid1");

		// Prepare the list Activity and send some values to it (must be String!)
		Intent listIntent = new Intent(this,DetailsActivity.class);      	        
		listIntent.putExtra("id", id);

		// Prepare the map Activity and send some values to it (must be String!)
		Intent mapIntent = new Intent(this,MapsActivity.class);      	        
		mapIntent.putExtra("id", id);

		// TabSpec setIndicator() is used to set name for the tab
		// TabSpec setContent() is used to set content for a particular tab
		String tabName1 = getResources().getString(R.string.tabs_tabname_facts);
		firstTabSpec.setIndicator(tabName1).setContent(listIntent);
		String tabName2 = getResources().getString(R.string.tabs_tabname_map);
		secondTabSpec.setIndicator(tabName2).setContent(mapIntent);

		// Add tabSpec to the TabHost to display
		tabHost.addTab(firstTabSpec);
		tabHost.addTab(secondTabSpec);

	}
}
