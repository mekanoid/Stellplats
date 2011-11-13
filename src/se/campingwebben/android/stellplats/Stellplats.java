package se.campingwebben.android.stellplats;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

public class Stellplats extends Activity {

	long splashTime = 8000;			// Default splash time
	boolean splashPause = false;
	boolean splashActive = true;

	SharedPreferences prefs;
	String prefName = "Preferences";
	String SPLASH_COUNT;
	int splashCount;
	
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		// Draw the splash screen
		setContentView(R.layout.splash);

    	// Load the SharedPreferences object and get last count
        prefs = getSharedPreferences(prefName, MODE_PRIVATE);
        splashCount = prefs.getInt(SPLASH_COUNT, 1);
       	
    	// Calculate splash time to a minimum around 3s
    	splashTime = (splashTime/splashCount) + 3000;

    	// Add +1 to count the first few times to get decreasing splash times
    	if (splashCount<5) {
        	// Add one to splashCount
        	splashCount = splashCount+1;
        	
        	// Get the SharedPreferences object
        	SharedPreferences.Editor editor = prefs.edit();

            // Insert the choosen region number to preferences
            editor.putInt(SPLASH_COUNT, splashCount);

            // Saves the preferences
            editor.commit();
        }

        // Very simple timer thread
		Thread splashTimer = new Thread() {
			public void run() {
				try {
					// Wait loop
					long ms = 0;
					while(splashActive && ms < splashTime) {
						sleep(100);

						// Advance the timer only if we're running
						if(!splashPause)
							ms += 100;
					}

					// Advance to the next screen
					startActivity(new Intent("se.campingwebben.android.stellplats.CLEARSPLASH"));
				}
				catch(Exception e) {
					Log.e("Splash", e.toString());
				}
				finally	{
					finish();
				}
			}
		};
		splashTimer.start();
	}

	// Clear the splash screen after pushing any key
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		super.onKeyDown(keyCode, event);
		splashActive = false;
		return true;
	}

	// Pausing the splash timer when activity is suspended by any interruption
	protected void onPause() {
		super.onPause();
		splashPause = true;
	}

	// Start timer again when application is resumed
	protected void onResume() {
		super.onResume();
		splashPause = false;
	}

}
