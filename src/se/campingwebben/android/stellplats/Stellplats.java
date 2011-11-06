package se.campingwebben.android.stellplats;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

public class Stellplats extends Activity {

	long splashTime = 5000;			// Default splash time
	boolean splashPause = false;
	boolean splashActive = true;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		// Draw the splash screen
		setContentView(R.layout.splash);

		// Get value from "strings.xml" and convert to Long
		String temp = getResources().getString(R.string.splash_time);
		splashTime = Long.parseLong(temp);
		
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
