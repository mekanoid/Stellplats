package se.campingwebben.android.stellplats;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailsActivity extends Activity{

	// SQL variables
	private SQLiteDatabase database;
	// TODO: Byt till "plats_vinter" & "plats_avgift"
	private static final String fields[] = {
		"namn", "ort", "region", "beskrivning", "uppdaterad", "wgs84_lat", "wgs84_long",
		"plats_husvagn", "vinter",  "typ", "service_toalett", "service_dusch",
		"plats_el", "service_vatten", "service_latrin", "avgift", 
		BaseColumns._ID };

	// Fields to show on screen
	private String tmp;
	private String where;
	private String id;
	private String name;
	private String place;
	private String region;
	private String description;
	private String updated;
	String wgs84_lat;
	String wgs84_long;
	// Facts
	private Integer caravan;
	private Integer winter;
	private Integer stay;
	// Service
	private Integer toilet;
	private Integer shower;
	private Integer electric;
	private Integer water;
	private Integer waste;
	private Integer fee;
	
	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details);
		
		// Get parameters from the Activity before
		Bundle extras = getIntent().getExtras();
		if(extras !=null) {
			// Get id for the item to show more information about and form a WHERE clause
			id = extras.getString("id");
			where = "_id='" + id + "'";				// Don't forget the '' even if its a number!
		}

		// Get reference to the action bar title and set title text
//		TextView actionbarTitle = new TextView(this); 
//        actionbarTitle = (TextView)findViewById(R.id.actionbarTitle); 
//        actionbarTitle.setText(this.getString(R.string.stellplats));
        
		// Get reference to the action bar text
		TextView actionbarText = new TextView(this); 
        actionbarText = (TextView)findViewById(R.id.titleText); 
//        actionbarText.setText(this.getString(R.string.details_actionbar_text));

       	// Get reference to the icons
        ImageView iconBack = (ImageView)findViewById(R.id.actionBack);
        ImageView iconMap = (ImageView)findViewById(R.id.actionIcon02);
        ImageView iconNavigate = (ImageView)findViewById(R.id.actionIcon01);

        // Call private method gpsListener
        iconBack.setOnClickListener(backListener);
        iconMap.setOnClickListener(mapListener);
        iconNavigate.setOnClickListener(navigateListener);

		// Create a new instance of the DBmanger class
		DataManager myDbHelper = new DataManager(null);
        myDbHelper = new DataManager(this);

        // Open the database and make a query
        database = myDbHelper.getReadableDatabase();
		Cursor data = database.query("platser", fields, where, null, null, null, null);

		// Get the result and put it in variables
		if (data.getCount() == 0){
	    	name = "Fel";
	    } else {
			
	    	data.moveToFirst();
	        name = data.getString(0);
	        place = data.getString(1);
	        // Get the name of the region
	        tmp = data.getString(2);
			String[] items = getResources().getStringArray(R.array.region);
			region = items[(int) Integer.valueOf(tmp)];
	        description = data.getString(3);
	        updated = data.getString(4);
	        wgs84_lat = data.getString(5);
	        wgs84_long = data.getString(6);
	        tmp = data.getString(7);
	        caravan = Integer.valueOf(tmp);
	        tmp = data.getString(8);
	        if (tmp.length() == 1) { winter = Integer.valueOf(tmp); } else { winter = 0;}	// Check for NULL
	        tmp = data.getString(9);
	        stay = Integer.valueOf(tmp);

	        // Service
	        tmp = data.getString(10);
	        if (tmp.length() == 1) { toilet = Integer.valueOf(tmp); } else { toilet = 0;}	// Check for NULL
	        tmp = data.getString(11);
	        if (tmp.length() == 1) { shower = Integer.valueOf(tmp); } else { shower = 0;}	// Check for NULL
	        tmp = data.getString(12);
	        if (tmp.length() == 1) { electric = Integer.valueOf(tmp); } else { electric = 0;}	// Check for NULL
	        tmp = data.getString(13);
	        if (tmp.length() == 1) { water = Integer.valueOf(tmp); } else { water = 0;}	// Check for NULL
	        tmp = data.getString(14);
	        if (tmp.length() == 1) { waste = Integer.valueOf(tmp); } else { waste = 0;}	// Check for NULL
	        tmp = data.getString(15);
	        if (tmp.length() == 1) { fee = Integer.valueOf(tmp); } else { fee = 0;}	// Check for NULL
	    }

		// Close database
		database.close();

        actionbarText.setText(name);
		/**
		 *  Basic information
		 */
		// Name
		TextView txt = new TextView(this); 
//        txt = (TextView)findViewById(R.id.details_label_name); 
        //txt.setText(name);
        txt.setText("");

        // Place (town, village etc)
//        txt = (TextView)findViewById(R.id.details_label_place); 
//        txt.setText(place);
        txt.setText("");

        // Region
// TODO        txt = (TextView)findViewById(R.id.details_label_region); 
//        txt.setText(region);
        txt.setText("");

        /**
         *  Facts
         */
        // Caravans welcome or not
        ImageView image_caravan = (ImageView) findViewById(R.id.details_image_caravan);
        switch(caravan) {
        case 1:
            image_caravan.setImageResource(R.drawable.ic_spl_button_ok); break;
        case 2:
            image_caravan.setImageResource(R.drawable.ic_spl_button_nok); break;
        }

        // Open in the winter
        ImageView image_winter = (ImageView) findViewById(R.id.details_image_winter);
        switch(winter) {
        case 1:
            image_winter.setImageResource(R.drawable.ic_spl_button_ok); break;
        case 2:
            image_winter.setImageResource(R.drawable.ic_spl_button_nok); break;
        }
        
        // Stay one or more nights (type of place)
        ImageView image_stay = (ImageView) findViewById(R.id.details_image_stay);
        TextView txt_stay = (TextView)findViewById(R.id.details_label_stay); 
        switch(stay) {
        case 1:
            txt_stay.setText(R.string.details_label_shortstay);
            image_stay.setImageResource(R.drawable.ic_spl_button_ok); break;
        case 2:
            txt_stay.setText(R.string.details_label_longstay);
            image_stay.setImageResource(R.drawable.ic_spl_button_ok); break;
        }

        /**
         * Service
         */
        // Toilets
        ImageView image = (ImageView) findViewById(R.id.details_image_toilet);
        switch(toilet) {
        case 1:
            image.setImageResource(R.drawable.ic_spl_button_ok); break;
        case 2:
            image.setImageResource(R.drawable.ic_spl_button_nok); break;
        }

        // Shower
        image = (ImageView) findViewById(R.id.details_image_shower);
        switch(shower) {
        case 1:
            image.setImageResource(R.drawable.ic_spl_button_ok); break;
        case 2:
            image.setImageResource(R.drawable.ic_spl_button_nok); break;
        }
        
        // Electricity
        image = (ImageView) findViewById(R.id.details_image_electric);
        switch(electric) {
        case 1:
            image.setImageResource(R.drawable.ic_spl_button_ok); break;
        case 2:
            image.setImageResource(R.drawable.ic_spl_button_nok); break;
        }
        
        // Water
        image = (ImageView) findViewById(R.id.details_image_water);
        switch(water) {
        case 1:
            image.setImageResource(R.drawable.ic_spl_button_ok); break;
        case 2:
            image.setImageResource(R.drawable.ic_spl_button_nok); break;
        }
        
        // Waste
        image = (ImageView) findViewById(R.id.details_image_waste);
        switch(waste) {
        case 1:
            image.setImageResource(R.drawable.ic_spl_button_ok); break;
        case 2:
            image.setImageResource(R.drawable.ic_spl_button_nok); break;
        }
        
        // Fee
        image = (ImageView) findViewById(R.id.details_image_fee);
        switch(fee) {
        case 1:
            image.setImageResource(R.drawable.ic_spl_button_ok); break;
        case 2:
            image.setImageResource(R.drawable.ic_spl_button_nok); break;
        }
        
        /**
         *  Description
         */
        txt = (TextView)findViewById(R.id.text_description); 
        txt.setText(description);
        
        /**
         *  Other
         */
        // Updated date
        txt = (TextView)findViewById(R.id.text_updated); 
        txt.setText(updated);

        // GPS coordinates
    	String wgs84 = wgs84_lat + ", " + wgs84_long;
    	TextView gps = new TextView(this);
    	gps = (TextView)findViewById(R.id.text_wgs84); 
        gps.setText(wgs84);
        gps.setOnClickListener(navigateListener);
	}

	/**
	 *  Create an OnClickListener for the back icon
	 */
	private OnClickListener backListener = new OnClickListener() {
		public void onClick(View view) {
			// Change map view to current location
			finish();
		}
	};

	/**
	 *  Create an OnClickListener for the navigate icon
	 */
	private OnClickListener navigateListener = new OnClickListener() {
		public void onClick(View view) {
			// Go to external navigation software when the text is clicked
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW, 
					Uri.parse("google.navigation:q="+wgs84_lat+","+wgs84_long));
			startActivity(intent);
	    }
	};

	/**
	 *  Create an OnClickListener for the map icon
	 */
	private OnClickListener mapListener = new OnClickListener() {
		public void onClick(View view) {
    		// Prepare to open the Details Activity/View
//    		Intent myIntent = new Intent(view.getContext(), TabsActivity.class);      	        
    		Intent myIntent = new Intent(view.getContext(), GpsMapActivity.class);      	        

    		// Send some values to the new Activity (must be String!)
    		myIntent.putExtra("lat", wgs84_lat);
    		myIntent.putExtra("lon", wgs84_long);
    		myIntent.putExtra("name", name);

    		// Open the new Activity (and don't expect any response)
    		startActivity(myIntent);
	    }
	};
}
