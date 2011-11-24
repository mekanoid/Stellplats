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

		// Prepare new Activity and send some values to it (must be String!)
		Intent myIntent = new Intent(this,Details.class);      	        
		myIntent.putExtra("id", id);

		// TabSpec setIndicator() is used to set name for the tab
		// TabSpec setContent() is used to set content for a particular tab
		// TODO: Variabler för namnen
		firstTabSpec.setIndicator("Fakta").setContent(myIntent);
		secondTabSpec.setIndicator("Karta").setContent(new Intent(this,MapsActivity.class));

		// Add tabSpec to the TabHost to display
		tabHost.addTab(firstTabSpec);
		tabHost.addTab(secondTabSpec);

	}
}
