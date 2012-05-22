package se.campingwebben.android.stellplats;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;


public class OverlayManager extends BalloonItemizedOverlay<OverlayItem> {

	private Context mContext;
	// TODO Change "mall" to "pitch"
	private ArrayList<OverlayItem> malls = new ArrayList<OverlayItem>();
	private Location currentLocation;

	public OverlayManager(Drawable defaultMarker, MapView mapView) {
		super((defaultMarker),mapView);
		boundCenterBottom(defaultMarker);
		mContext = mapView.getContext();
		}

	@Override
	protected OverlayItem createItem(int i) {
		return malls.get(i);
	}

	@Override
	public int size() {
		return malls.size();
	}

	public void addOverlay(OverlayItem overlay) {
		malls.add(overlay);
		populate();
	}

	public void setCurrentLocation(Location loc){
		this.currentLocation = loc;
	}

	public Location convertGpToLoc(GeoPoint gp){
		Location convertedLocation = new Location("");

		convertedLocation.setLatitude(gp.getLatitudeE6() / 1e6);
		convertedLocation.setLongitude(gp.getLongitudeE6() / 1e6);

		return convertedLocation;
	}

	@Override
	protected boolean onBalloonTap(int index, OverlayItem item) {
		String tmp = malls.get(index).getTitle();

		GeoPoint mallPoint = malls.get(index).getPoint();
		Location tmpLoc = convertGpToLoc(mallPoint);
		double distance = ((currentLocation).distanceTo(tmpLoc))*(0.000621371192);
		DecimalFormat df = new DecimalFormat("#.##");
		tmp = tmp + " is " + String.valueOf(df.format(distance)) + " miles away.";
		Toast.makeText(mContext,tmp,Toast.LENGTH_LONG).show();
		return true;
	}

}