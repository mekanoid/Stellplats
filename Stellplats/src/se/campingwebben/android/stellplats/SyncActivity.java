package se.campingwebben.android.stellplats;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SyncActivity extends Activity {
	  
	private static final String TAG = "PB_EXAMPLE";
	private Integer etNumSecondsM;
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
	      Log.d(TAG, "Creating Graphic Interface");
	      setContentView(R.layout.sync);

	      // 
	      etNumSecondsM = 10;
	      etSecondsProgressedM = (TextView) findViewById( R.id.textView1);
	         
	      // Progress Bar
	      pbDefaultM = (ProgressBar) findViewById( R.id.progressBar1);   
	         
	      // Start async task 
	      SyncManager pbTask = new SyncManager( pbDefaultM, etSecondsProgressedM);
	      pbTask.execute(etNumSecondsM);
	    }
}
