package se.campingwebben.android.stellplats;

import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class GpsMapActivity extends MapActivity implements LocationListener{
    /** Called when the activity is first created. */
	
	private MapController mapController;
	private MapView mapView;
	private LocationManager locationManager;
	private GeoPoint currentPoint;
	private Location currentLocation = null;
	private OverlayManager currPos;

	// För debugging
	private static final String TAG = "GpsMap";

	// Data base variables
	DBmanager myDbHelper = new DBmanager(this);
	private static final String fields[] = { "namn", "wgs84_lat", "wgs84_long", "beskrivning", "typ", "vinter", "plats_husvagn",
											"service_toalett", "service_vatten",  "service_dusch",  "service_latrin",  
											"avgift",  "plats_el", BaseColumns._ID };
	private static final String order = "namn ASC";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        mapView = (MapView)findViewById(R.id.mapView);
		mapView.setBuiltInZoomControls(true);
		mapView.setSatellite(false);
		mapView.setStreetView(true);
		mapController = mapView.getController();
		mapController.setZoom(12);
		getLastLocation();
		drawCurrPositionOverlay();
		drawMalls();
		animateToCurrentLocation();
		
	//	Toast.makeText(this, "Click on the displayed coordinates to center map around your current location", Toast.LENGTH_LONG).show();
    }
    
    
    public void getLastLocation(){
    	String provider = getBestProvider();
    	currentLocation = locationManager.getLastKnownLocation(provider);
    	
    	/*The next 4 lines are used to hardcode our location
    	 * If you wish to get your current location remember to
    	 * comment or remove them
    	 */
    	
/*    	currentPoint = new GeoPoint(29647929,-82352486);
    	currentLocation = new Location("");
    	currentLocation.setLatitude(currentPoint.getLatitudeE6() / 1e6);
    	currentLocation.setLongitude(currentPoint.getLongitudeE6() / 1e6);
*/    	
    	if(currentLocation != null){
    		setCurrentLocation(currentLocation);
    	}
    	else
    	{
    		Toast.makeText(this, "Location not yet acquired", Toast.LENGTH_LONG).show();
    	}
    	
    }
    
    public void animateToCurrentLocation(){
    	if(currentPoint!=null){
    		mapController.animateTo(currentPoint);
    	}
    }
    
    public void centerToCurrentLocation(View view){
    	animateToCurrentLocation();
    }
    
    public String getBestProvider(){
    	locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    	Criteria criteria = new Criteria();
    	criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
    	criteria.setAccuracy(Criteria.NO_REQUIREMENT);
    	String bestProvider = locationManager.getBestProvider(criteria, true);
    	return bestProvider;
    }
    
   public void setCurrentLocation(Location location){
     	int currLatitude = (int) (location.getLatitude()*1E6);
    	int currLongitude = (int) (location.getLongitude()*1E6);
//    	currentPoint = new GeoPoint(currLatitude,currLongitude); 
    	
    	/*========================================================================================
    	/*The Above Code displays your correct current location, but for the sake of the demo
    	I will be hard coding your current location to the University of Florida, to get your real
    	current location, comment or delete the line of code below and uncomment the code above. */
    	currentPoint = new GeoPoint(56765697,12933076);

    	currentLocation = new Location("");
    	currentLocation.setLatitude(currentPoint.getLatitudeE6() / 1e6);
    	currentLocation.setLongitude(currentPoint.getLongitudeE6() / 1e6);
    	
    	drawCurrPositionOverlay();
    	
    }
    
    public void drawCurrPositionOverlay(){
    	List<Overlay> overlays = mapView.getOverlays();
    	overlays.remove(currPos);
    	Drawable marker = getResources().getDrawable(R.drawable.me);
    	currPos = new OverlayManager(marker,mapView);
    	if(currentPoint!=null){
			OverlayItem overlayitem = new OverlayItem(currentPoint, "Aktuell position", "Här befinner du dig nu!");
			currPos.addOverlay(overlayitem);
			overlays.add(currPos);
			currPos.setCurrentLocation(currentLocation);
    	}
    }
    
    
    public void drawMalls(){
    	Drawable marker = getResources().getDrawable(R.drawable.ic_spl_mapsign);
    	OverlayManager mallsPos = new OverlayManager(marker,mapView);
    	List<Overlay> overlays = mapView.getOverlays();
    	OverlayItem overlayItem;
 	
    	// Manage places
    	// TODO: Make dynamic no of GeoPoint
    	GeoPoint[] splCoords = new GeoPoint[600];
    	Cursor cursor = getEvents();

    	// Iterate through all places
    	Integer no = 0;
       	cursor.moveToFirst();
    	while (cursor.moveToNext()) {

    		// Get the name
    		String name = cursor.getString(0);

    		// Get latitude
    		double latTmp = cursor.getDouble(1);
    		latTmp = (latTmp * 1e6);
    		Integer lat = (int) latTmp;
//    		Log.d(TAG, lat.toString());

    		// Get longitude
    		double lonTmp = cursor.getDouble(2);
    		lonTmp = (lonTmp * 1e6);
    		Integer lon = (int) lonTmp;
//    		Log.d(TAG, lon.toString());

    		String description = cursor.getString(3);
    		Log.d(TAG, description);

    		String r2a = "", r2b = "", r2c = "", r2d = "", r2e = "";
    		String r3a = "", r3b = "", r3c = "", r3d = "", r3e = "";

    		// If toilet
    		Integer s_toilet = cursor.getInt(7);
    		switch (s_toilet) {
    			case 1: r2a = "toalett, "; break;
    			case 2: r3a = "toalett, "; break;
    		}

    		Integer s_water = cursor.getInt(8);
    		switch (s_water) {
    			case 1: r2b = "vatten, "; break;
    			case 2: r3b = "vatten, "; break;
    		}

    		Integer s_shower = cursor.getInt(9);
    		switch (s_shower) {
    			case 1: r2c = "dusch, "; break;
    			case 2: r3c = "dusch, "; break;
    		}

    		Integer s_waste = cursor.getInt(9);
    		switch (s_waste) {
    			case 1: r2d = "latrin, "; break;
    			case 2: r3d = "latrin, "; break;
    		}

    		Integer s_electric = cursor.getInt(10);
    		switch (s_electric) {
    			case 1: r2e = "el, "; break;
    			case 2: r3e = "el, "; break;
    		}


    		String r3 = r3a + r3b + r3c + r3d + r3e;
    		if (r3.length()!=0) {
    			r3 = r3.substring(0, 1).toUpperCase() + r3.substring(1).toLowerCase();
    			r3 = r3.substring(0, r3.length()-2);
        		description = "Saknar: " + r3 + "\n" + description;
    		}

    		// Remove trailing ", "
    		String r2 = r2a + r2b + r2c + r2d + r2e;
    		if (r2.length()!=0) {
    			r2 = r2.substring(0, 1).toUpperCase() + r2.substring(1).toLowerCase();
    			r2 = r2.substring(0, r2.length()-2);
        		description = "Service: " + r2 + "\n" + description;
    		}


    		/*" "avgift",  "plats_el */
//    		Integer s_fee = cursor.getInt(10);
// 			String rAa = "";
//    		switch (s_fee) {
//    			case 1: r3a = "Avgift, "; break;
//    			case 2: r3a = "Gratis, "; break;
//    		}

    		
    		// Type of pitch
    		Integer p_type = cursor.getInt(4);
    		String r1a = "";
    		switch (p_type) {
    			case 1: r1a = "rastplats, "; break;
    			case 2: r1a = "ställplats, "; break;
    		}

    		// Open or closed during winter
    		Integer p_winter = cursor.getInt(5);
    		String r1b = "";
    		switch (p_winter) {
    			case 1: r1b = "vinteröppet, "; break;
    			case 2: r1b = "vinterstängt, "; break;
    		}

    		// If caravans allowed
    		Integer p_caravan = cursor.getInt(6);
    		String r1c = "";
    		switch (p_caravan) {
    			case 1: r1c = "plats för husvagn, "; break;
    			case 2: r1c = "ej husvagnar, "; break;
    		}

    		// Remove trailing ", "
    		String r1 = r1a + r1b + r1c;
    		if (r1.length()!=0) {
    			r1 = r1.substring(0, 1).toUpperCase() + r1.substring(1).toLowerCase();
    			r1 = r1.substring(0, r1.length()-2);
        		description = r1 + "\n" + description;
    		}

    		// Make a Point Of Interest
        	splCoords[no] = new GeoPoint(lat,lon);	// Skapa en koordinat
        	overlayItem = new OverlayItem(splCoords[no], name, description);
    		mallsPos.addOverlay(overlayItem);
    		
    		no++;
   	    }

		overlays.add(mallsPos);	
		mallsPos.setCurrentLocation(currentLocation);
    }

    /**
     * Get pitches from SQLite database
     *
     * @return
     */
    // TODO: Move to DBmanager class
    private Cursor getEvents() {
    	// Make an square area around current position
    	double tmpMinLat = (currentPoint.getLatitudeE6()-650000)/1e6;
    	double tmpMaxLat = (currentPoint.getLatitudeE6()+650000)/1e6;
    	double tmpMinLon = (currentPoint.getLongitudeE6()-950000)/1e6;
    	double tmpMaxLon = (currentPoint.getLongitudeE6()+950000)/1e6;

    	// Calculate max and min latitude
    	String minLat = String.valueOf(tmpMinLat);
    	minLat = minLat.substring(0, 4);
    	String maxLat = String.valueOf(tmpMaxLat);
    	maxLat = maxLat.substring(0, 4);

    	// Calculate max and min longitude
    	String minLon = String.valueOf(tmpMinLon);
    	minLon = minLon.substring(0, 4);
    	String maxLon = String.valueOf(tmpMaxLon);
    	maxLon = maxLon.substring(0, 4);

    	String select = "aktiv='1'";
    	select = select + "AND wgs84_lat<'"+maxLat+"' ";
    	select = select + "AND wgs84_lat>'"+minLat+"' ";
    	select = select + "AND wgs84_long<'"+maxLon+"' ";
    	select = select + "AND wgs84_long>'"+minLon+"' ";

    	Log.d(TAG, "Select: "+select);

    	// Get coordinates from database
    	SQLiteDatabase db = myDbHelper.getReadableDatabase();
        Cursor cursor = db.query("platser", fields, select, null, null, null, order);

        // Return Cursor with relevant pitches
        startManagingCursor(cursor);
        return cursor;
      }

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}


	public void onLocationChanged(Location newLocation) {
		// TODO Auto-generated method stub
		setCurrentLocation(newLocation);
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		locationManager.requestLocationUpdates(getBestProvider(), 1000, 1, this);
	}


	@Override
	protected void onPause() {
		super.onPause();
		locationManager.removeUpdates(this);
	}



	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
		Toast.makeText(this, "Provider Disabled", Toast.LENGTH_SHORT).show();
		
	}


	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Provider Enabled", Toast.LENGTH_SHORT).show();
		
	}


	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "Staus Changed", Toast.LENGTH_SHORT).show();
		
	}
}