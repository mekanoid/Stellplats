package se.campingwebben.android.stellplats;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.widget.ImageView;
import android.widget.TextView;

public class Details extends Activity{

	private SQLiteDatabase database;

	// SQLite query fields
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
	private String wgs84_lat;
	private String wgs84_long;
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

		// Create a new instance of the DBmanger class
		DBmanager myDbHelper = new DBmanager(null);
        myDbHelper = new DBmanager(this);

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
			region = items[Integer.valueOf(tmp)];
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

		// TODO: Ta bort när flikarna fungerar bra...
		// Get reference to the title bar text
//		TextView title = new TextView(this); 
//        title = (TextView)findViewById(R.id.titleText); 

        // Set the title bar text
//        title.setText(this.getString(R.string.stellplats) + " " + place);
        
		/**
		 *  Basic information
		 */
		// Name
		TextView txt = new TextView(this); 
        txt = (TextView)findViewById(R.id.details_label_name); 
        txt.setText(name);

        // Place (town, village etc)
        txt = (TextView)findViewById(R.id.details_label_place); 
        txt.setText(place);

        // Region
        txt = (TextView)findViewById(R.id.details_label_region); 
        txt.setText(region);

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
        txt = (TextView)findViewById(R.id.text_wgs84); 
        txt.setText(wgs84);
	
	}
}
