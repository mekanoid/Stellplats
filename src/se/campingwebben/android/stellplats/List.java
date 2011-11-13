package se.campingwebben.android.stellplats;

import java.io.IOException;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.text.SpannableString;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SimpleCursorAdapter.ViewBinder;

public class List extends ListActivity {

	/**
	 *  Constants
	 */
	// For the database
	SQLiteDatabase splDatabase;
	DBmanager myDbHelper;

	// For the list view
	SimpleCursorAdapter splAdapter;
	Cursor splCursor;
	
	// SQLite query fields
	private static final String fields[] = { "namn", "ort", "region", BaseColumns._ID };
	private static final String order = "namn ASC";

	SharedPreferences prefs;
	String prefName = "Preferences";
	final String CHOOSEN_REGION = "0";
	int regionNo;
	
	/**
	 *  Called when the activity is first created.
	 */
	public void onCreate(Bundle savedInstanceState) {
        String select;
		String regionName;

        super.onCreate(savedInstanceState);

		// Set view title
		setTitle(this.getString(R.string.app_name) + " " + this.getString(R.string.sweden));
		
		// Create a new instance of the DBmanger class
        myDbHelper = new DBmanager(this);
 
        // Create a new database if no database exist
        try {
         	myDbHelper.createDataBase();
        } catch (IOException ioe) {
        	throw new Error("Unable to create database");
        }

    	// Load the SharedPreferences object and get last selected region
        prefs = getSharedPreferences(prefName, MODE_PRIVATE);
       	regionNo = prefs.getInt(CHOOSEN_REGION, 0);

		// Get the name of the region from strings.xml
		String[] items = getResources().getStringArray(R.array.region);
		regionName = items[regionNo];
		
		// Special if "all" regions are chosen
        if (regionNo == 0) {
			// Set a new window title
			setTitle(this.getString(R.string.app_name) + " " + this.getString(R.string.sweden));

        // Update the cursor with new data from database
			select = "aktiv='1'";
        } else {
			// Set a new window title
			setTitle(this.getString(R.string.stellplats) + " " + regionName);

			// Make SQL WHERE clause
			select = "region='" + regionNo + "' AND aktiv='1'";
		}

        // Open the database and make a query
        splDatabase = myDbHelper.getReadableDatabase();
        Cursor startCursor = splDatabase.query("platser", fields, select, null, null, null, order);
		startManagingCursor(startCursor);

		// Make a list view with "R.layout.list_item" item layout 
        splAdapter = new SimpleCursorAdapter(this,
        	R.layout.list_listitem,
        	startCursor,
        	fields,
        	new int[] { R.id.list_listitem_labelName, R.id.list_listitem_labelPlace, R.id.list_listitem_labelRegion}
        );

        // Change some of the values with a SetViewBinder
        splAdapter.setViewBinder(new ViewBinder() {
			public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

		      switch (view.getId()) {

		      case R.id.list_listitem_labelRegion:
		    	// Get the value for region (field = 2)
		    	String tmp = cursor.getString(columnIndex);

		    	// Get the array of region names
		    	String[] items = getResources().getStringArray(R.array.region);

		    	// Assign the name to a variable
		    	String region = items[Integer.valueOf(tmp)];

		    	// Show the region name
			    TextView txt = (TextView) view;
		    	txt.setText(region);
		    	return true;			// Remove this row if there are more Case:s!!
		    }
			return false;
			}
		});
        
        // Show the list
        setListAdapter(splAdapter);

        // Close database
//        splDatabase.close();
	}
	
	/**
	 * Create a menu (from res/menu/menu_list.xml)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.list_menu, menu);
	    return true;
	}

	/**
	 * Listen for clicks on Menu
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final SharedPreferences prefs;
		String prefName = "Preferences";
		final String CHOOSEN_REGION = "0";

		switch (item.getItemId()) {
	        case R.id.list_menu_itemChoose:

	    		// Create a new instance of the DBmanger class

	        	// Choose which item to check in dialog box (none at the moment)
	        	int itemToCheck = -1;

	        	// Load the SharedPreferences object and get last selected region
	        	prefs = getSharedPreferences(prefName, MODE_PRIVATE);
	            itemToCheck = prefs.getInt(CHOOSEN_REGION, -1);
	            
	            // Prepare the list dialog box
	            AlertDialog.Builder builder = new AlertDialog.Builder(this);

	            // Set its title (from strings.xml)
	            builder.setTitle(R.string.dialog_region);
	            
	            // Set the list items (with array from strings.xml)
	            // and assign with the click listener
	            builder.setSingleChoiceItems(R.array.region, itemToCheck, new DialogInterface.OnClickListener() {

	            	// Click listener
	            	public void onClick(DialogInterface dialog, int choosenItem) {
	            		// Update the list view
	            		regionListUpdate(choosenItem);

	            		// Get the SharedPreferences object
	                    SharedPreferences.Editor editor = prefs.edit();

	                    // Insert the choosen region number to preferences
	                    editor.putInt(CHOOSEN_REGION, choosenItem);

	                    // Saves the preferences
	                    editor.commit();

	            		// Close the dialog box
	            		dialog.dismiss();
	            	}

	            });
 
	            // Create dialog box
	            AlertDialog alert = builder.create();

	            // Display dialog box
	            alert.show();
	    		
	            break;
	        case R.id.list_menu_itemAbout:
	        	AlertDialog about;
	        	try {
	        	    about = AboutDialogBuilder.create(this);
	        	    about.show();
	        	} catch (NameNotFoundException e) {
	        	    // Auto-generated catch block
	        	    e.printStackTrace();
	        	}
	        	break;
	    }
	    return true;
	}
	
	/**
	 * Listen for clicks on list items
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		super.onListItemClick(l, v, position, id);

		// Prepare to open the Details Activity/View
		Intent myIntent = new Intent(v.getContext(), Details.class);
        
		// Send some values to the new Activity (must be String!)
		String idTemp = Long.toString(id);
		myIntent.putExtra("id", idTemp);

		// Open the new Activity (and don't expect any response)
		startActivity(myIntent);
	}

	
	/**
	 * Update list with stellplatser from a different region = regionNo
	 */
	public void regionListUpdate(Integer regionNo) {
		String select;
		Cursor splCursor;
		
		// Get the name of the region från strings.xml
		String[] items = getResources().getStringArray(R.array.region);
		String regionName = items[regionNo];
		
		// Show which region that was choosen
		Toast.makeText(getApplicationContext(), regionName, Toast.LENGTH_SHORT).show();

		// Open database for reading
        splDatabase = myDbHelper.getReadableDatabase();

        // Special if "all" regions are choosen
        if (regionNo == 0) {
			// Set a new window title
			setTitle(this.getString(R.string.app_name) + " " + this.getString(R.string.sweden));

        // Update the cursor with new data from database
			select = "aktiv='1'";
        } else {
			// Set a new window title
			setTitle(this.getString(R.string.stellplats) + " " + regionName);

			// Make SQL WHERE clause
			select = "region='" + regionNo + "' AND aktiv='1'";
		}
       
		// Update the cursor with new data from database
		splCursor = splDatabase.query("platser", fields, select, null, null, null, order);
		startManagingCursor(splCursor);

		// Update the list by making a new adapter
        splAdapter = new SimpleCursorAdapter(this,
            	R.layout.list_listitem,
            	splCursor,
            	fields,
            	new int[] { R.id.list_listitem_labelName, R.id.list_listitem_labelPlace, R.id.list_listitem_labelRegion}
            );
        
        // Change some of the values with a SetViewBinder
        splAdapter.setViewBinder(new ViewBinder() {
			public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

		      switch (view.getId()) {

		      case R.id.list_listitem_labelRegion:
		    	// Get the value for region (field = 2)
		    	String tmp = cursor.getString(columnIndex);

		    	// Get the array of region names
		    	String[] items = getResources().getStringArray(R.array.region);

		    	// Assign the name to a variable
		    	String region = items[Integer.valueOf(tmp)];

		    	// Show the region name
			    TextView txt = (TextView) view;
		    	txt.setText(region);
		    	return true;			// Remove this row if there are more Case:s!!
		      }
		      // Return without doing anything, if no Case is fulfilled
		      return false;
			}
		});
        
        // Show the list
        setListAdapter(splAdapter);

        // Close database
//        splDatabase.close();
	}

	/**
	 * Make an About dialog
	 */
	public static class AboutDialogBuilder {

	    public static AlertDialog create( Context context ) throws NameNotFoundException {
		    // Make the title text
		    String labelTitle = context.getString(R.string.about_label_about);
		    String aboutTitle = String.format(labelTitle+" %s", context.getString(R.string.app_name));

		    // Make the version text
		    String labelVersion = context.getString(R.string.about_label_version);
		    String aboutVersion = String.format(labelVersion+" %s", context.getString(R.string.app_version));
		    
		    SpannableString www = new SpannableString("www.campingwebben.se");
		    www.setSpan(new URLSpan("http://www.campingwebben.se"), 0, 19, 0);
		    
		    // Make the first paragraph
		    String text00 = context.getString(R.string.about_label_text00);

		    // Make the Thank you paragraph
		    SpannableString head01 = new SpannableString(context.getString(R.string.about_label_head01));
		    String text01a = context.getString(R.string.about_label_text01a);
		    String text01b = context.getString(R.string.about_label_text01b);
		    String text01c = context.getString(R.string.about_label_text01c);
		    String text01 = "- " + text01a + "\n- " + text01b + "\n- " + text01c;
		    
		    // Make the License paragraph
		    SpannableString head02 = new SpannableString(context.getString(R.string.about_label_head02));
		    String text02a = context.getString(R.string.about_label_text02a);
		    String text02b = context.getString(R.string.about_label_text02b);
		    String text02 = text02a + " " + text02b;
		    
		    // Set up the TextView
		    final TextView message = new TextView(context);

		    // We'll use a spannablestring to be able to make links clickable
//		    final SpannableString s = new SpannableString(aboutText);
		 
		    // Set some padding
		    message.setPadding(15, 5, 15, 25);

		    // Set up the final string
		    message.setText(aboutVersion + "\n" + 
		    		www + "\n\n" +
		    		text00 + "\n\n" + 
		    		head01 + "\n" + text01 + "\n\n" + 
		    		head02 + "\n" + text02);

		    // Now linkify the text
		    Linkify.addLinks(message, Linkify.ALL);
		 
		    return new AlertDialog.Builder(context)
		    	.setTitle(aboutTitle)
		    	.setCancelable(true)
		    	.setIcon(R.drawable.ic_launcher_stellplats)
		    	.setPositiveButton(context.getString(android.R.string.ok), null)
		        .setView(message)
		        .create();
		    }
		}
}
