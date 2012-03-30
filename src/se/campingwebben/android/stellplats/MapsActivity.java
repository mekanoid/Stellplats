
package se.campingwebben.android.stellplats;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

public class MapsActivity extends MapActivity {

	// SQL variables
	private SQLiteDatabase database;
	private static final String fields[] = {
		"namn", "ort", "region", "beskrivning", "uppdaterad", "wgs84_lat", "wgs84_long",
		"plats_husvagn", "vinter",  "typ", "service_toalett", "service_dusch",
		"plats_el", "service_vatten", "service_latrin", "avgift", 
		BaseColumns._ID };

	// SQL Fields/variables
	private String id, where;
	private String wgs84_lat, wgs84_long;
	
	SharedPreferences prefs;
	String prefName = "Preferences";
	String MAP_ZOOM = "12";
	int zoom;
	
	MapView mapView;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		
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
	        wgs84_lat = "61.30";
	        wgs84_long = "17.00";
	    } else {
	    	data.moveToFirst();
	        wgs84_lat = data.getString(5);
	        wgs84_long = data.getString(6);
	    }

		// Close database
		database.close();

		// View map zoom controls
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.setBuiltInZoomControls(true);

        // Set location on map
        MapController mc = mapView.getController();
        String coordinates[] = {wgs84_lat, wgs84_long};
        double lat = Double.parseDouble(coordinates[0]);
        double lng = Double.parseDouble(coordinates[1]);
 
        final GeoPoint point = new GeoPoint(
            (int) (lat * 1E6), 
            (int) (lng * 1E6));
 
        mc.animateTo(point);

        // Get zoom level from preferences
    	prefs = getSharedPreferences(prefName, MODE_PRIVATE);
        zoom = prefs.getInt(MAP_ZOOM, 12);

        // Set zoom level
        mc.setZoom(zoom); 

        // Prepare to show a marker at the selected location
        class MapOverlay extends com.google.android.maps.Overlay {
            public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
                super.draw(canvas, mapView, shadow);                   
     
                // Translate the GeoPoint to screen pixels
                Point screenPts = new Point();
                mapView.getProjection().toPixels(point, screenPts);
 
                // Add the marker with offset
                Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_spl_mapsign);            

                // Set marker to the right position
                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                switch(metrics.densityDpi){
                     case DisplayMetrics.DENSITY_LOW:
                         canvas.drawBitmap(bmp, screenPts.x-18, screenPts.y-36, null);         
                         break;
                     case DisplayMetrics.DENSITY_MEDIUM:
                         canvas.drawBitmap(bmp, screenPts.x-24, screenPts.y-48, null);         
                         break;
                     case DisplayMetrics.DENSITY_HIGH:
                         canvas.drawBitmap(bmp, screenPts.x-36, screenPts.y-72, null);         
                         break;
                }
                return true;
            }

            // Actions to take when the user touches the screen
            @Override
            public boolean onTouchEvent (MotionEvent event, MapView mapView) {   
            	// When user lifts his/her finger
            	if (event.getAction() == 1) {
            		// Get zoom level
            		zoom = mapView.getZoomLevel();

            		// Get the SharedPreferences object
            		prefs = getSharedPreferences(prefName, MODE_PRIVATE);
            		SharedPreferences.Editor editor = prefs.edit();

            		// Insert the chosen region number to preferences
            		editor.putInt(MAP_ZOOM, zoom);

            		// Saves the preferences
            		editor.commit();
            	}                            
            	return false;
            }        
        }
        
        // Add the location marker
        MapOverlay mapOverlay = new MapOverlay();
        java.util.List<Overlay> listOfOverlays = mapView.getOverlays();
        listOfOverlays.clear();
        listOfOverlays.add(mapOverlay);        
 
        mapView.invalidate();
    }

	@Override
    protected boolean isRouteDisplayed() {
        return false;
    }

    // Actions when Activity pauses
    protected void onPause()
    {
        super.onPause();

        // Get current zoom level 
        zoom = mapView.getZoomLevel();

        // Get the SharedPreferences object
        prefs = getSharedPreferences(prefName, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Insert the chosen region number to preferences
        editor.putInt(MAP_ZOOM, zoom);

        // Saves the preferences
        editor.commit();
    }
}
