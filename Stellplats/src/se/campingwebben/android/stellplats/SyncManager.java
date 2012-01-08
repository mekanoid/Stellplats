package se.campingwebben.android.stellplats;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * ProgressBarAsyncTask extends from AsyncTask class. It is a template that
 * is defined as follows:
 *
 *      AsyncTask< InitialTaskParamsType, ProgressType, ResultTaskType>     
 *     
 */
public class SyncManager extends AsyncTask<Integer, Integer, Boolean> {
 
   private static final String TAG = "SyncManager";
   private ProgressBar pbM;
   private TextView teSecondsProgressedM;
    
   /**
    * The parameters in the constructor of the class are the controls from the
    * main activity that we will update as the background work is progressing.
    * 
    * @param pb: the progress bar control.
    * @param secondProgressed: an edit text with the percentage of seconds
    *                          progressed.
    */
    public SyncManager(ProgressBar pb, TextView secondProgressed) {
      Log.d(TAG, "Constructor");
   
      pbM = pb;
      teSecondsProgressedM = secondProgressed;
    }
 
    /**
     * This method will be called before the execution of the task. Here we
     * are activating the visibility of the progress controls of the main
     * activity.
     */
    protected void onPreExecute() {
      Log.d(TAG, "Pre-Execute");
 
      super.onPreExecute();
      pbM.setVisibility(View.VISIBLE);
      teSecondsProgressedM.setVisibility(View.VISIBLE);
    }
 
    /**
     * This method will be called after the invocation of the
     * publishProgress( progress) method in the background task. Here is where
     * we update the progress controls.
     *
     * @param progress: the amount of progress of the background task
     */
    protected void onProgressUpdate(Integer... progress) {
      Log.d(TAG, "Progress Update: " + progress[0].toString());
 
      super.onProgressUpdate(progress[0]); 
      pbM.setProgress(progress[0]);
      teSecondsProgressedM.setText(progress[0].toString());
    }
  
    /**
     * This method is called after the execution of the background task. Here
     * we reset the progress controls and set their visible property to
     * invisible again, to hide them.
     *
     * @param result: is the result of the background task, and it is passed to
     *                this method with de result returned in the
     *                doInBackGroundMethod()
     */
    protected void onPostExecute(Boolean result) {
       Log.d(TAG, "Post-Execute: " + result);
   
       super.onPostExecute(result);
       pbM.setVisibility(View.INVISIBLE);
//       teSecondsProgressedM.setVisibility(View.INVISIBLE);
       teSecondsProgressedM.setText("Klart!");
       pbM.setProgress(0);
    }
 
    /**
     * This method is called for executing the background task in the AsyncTask.
     * For this tutorial we are only sleeping the thread for the number of
     * seconds passed as parameter of the function.
     *
     * @param numSeconds: life of the task
     * @return the result of the background task
     */
    protected Boolean doInBackground(Integer... numSeconds) {
      Log.d(TAG, "doInBackground: " + numSeconds[0]);
   
      try { 
        int totalSecs = numSeconds[0].intValue();
        Log.d(TAG, "Total SECS: " + totalSecs);
    
        for (int i = 1; i <= totalSecs; i++) {
           Log.d(TAG, "Sleeping " + i);
           Thread.sleep(1000);
     
           float percentage = ((float)i / (float)totalSecs) * 100;
           Log.d(TAG, "Percentage of progress: " + percentage);
     
           publishProgress( new Float( percentage).intValue());
        } 
      } catch (InterruptedException e) {
          e.printStackTrace();
          return false;
      }
 
      return true;
   }
}