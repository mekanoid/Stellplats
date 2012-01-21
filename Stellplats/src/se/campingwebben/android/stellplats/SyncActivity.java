package se.campingwebben.android.stellplats;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SyncActivity extends Activity {
	  
	private static final String TAG = "PB_EXAMPLE";
	private Integer requestType;
	private TextView etSecondsProgressedM;
	private ProgressBar pbDefaultM;
	     
	/** Called when the activity is first created. */
	   @Override
	   public void onCreate(Bundle savedInstanceState) {
	     super.onCreate(savedInstanceState);
	     setContentView(R.layout.main);

	     drawGUI();
	   }
	     
	   public void drawGUI()
	   {
	      setContentView(R.layout.sync);

	      // 
	      requestType = 2;	// Status request
	      etSecondsProgressedM = (TextView) findViewById( R.id.textView1);
	         
	      // Progress Bar
	      pbDefaultM = (ProgressBar) findViewById( R.id.progressBar1);   
	         
	      // Start async task 
	      Log.d(TAG, "Start ASYNC task");
	      SyncManager pbTask = new SyncManager( pbDefaultM, etSecondsProgressedM);
	      pbTask.execute(requestType);
	    }
}
