package se.campingwebben.android.stellplats;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

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
public class SyncManager extends AsyncTask<Integer, String, Boolean> {
 
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
//      Log.d(TAG, "Constructor");
   
      pbM = pb;
      teSecondsProgressedM = secondProgressed;
    }
 
    /**
     * This method will be called before the execution of the task. Here we
     * are activating the visibility of the progress controls of the main
     * activity.
     */
    protected void onPreExecute() {
//      Log.d(TAG, "Pre-Execute");
 
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
    protected void onProgressUpdate(String... progress) {
//      Log.d(TAG, "Progress Update: " + progress[0].toString());
 
    	String test = progress[0].toString();
//      super.onProgressUpdate(progress[0]); 
//      pbM.setProgress(progress[0]);
      teSecondsProgressedM.setText(test);
    }
  
	/**
     * This method is called for executing the background task in the AsyncTask.
     *
     * @param requestType: 
     * @return the result of the background task
     */
	protected Boolean doInBackground(Integer... requestType) {
//		Log.d(TAG, "doInBackground: " + numSeconds[0]);


		String result = null;
		InputStream is = null;
		StringBuilder sb=null;
		String idParameter = "67";
		String dateParameter = "20120114";

		String typeParameter = requestType[0].toString();
//		String typeParameter = "2";

		Log.d(TAG, "Request type: " + typeParameter);

		/**
		 * Set parameters for the request
		 */
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("id", idParameter));
		nameValuePairs.add(new BasicNameValuePair("type", typeParameter));
		nameValuePairs.add(new BasicNameValuePair("date", dateParameter));

		/**
		 * Get data from web site
		 */
		try{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://www.campingwebben.se/api01.php");
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
			publishProgress("Connection ok");
			Log.d(TAG, "Connection ok");
		}catch(Exception e){
			Log.e(TAG, "Error in http connection"+e.toString());
		}

		/**
		 * Convert response to string
		 */
		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
			sb = new StringBuilder();
			sb.append(reader.readLine() + "\n");
			String line="0";

			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			             
			is.close();
			result=sb.toString();
			publishProgress("Conversion ok");
			Log.d(TAG, "Conversion ok: "+result);
			             
		}catch(Exception e){
			Log.e(TAG, "Error converting result "+e.toString());
		}

		/**
		 * Act on response
		 */
		if ("2".equals(typeParameter)) {
			try{
				JSONObject jObject = new JSONObject(result);

				// Get Status response
				String status = jObject.getString("Status");
				Log.d(TAG, "Status: "+status);
				
				// Get Value response
				String value = jObject.getString("Value");
				Log.d(TAG, "Value: "+value);

				// Get Service response
				String service = jObject.getString("Service");
				Log.d(TAG, "Service: "+service);

				// Get Information response
				String info = jObject.getString("Info");
				Log.d(TAG, "Info: "+info);

				// Get Data response
				String data = jObject.getString("Data");
				Log.d(TAG, "Data: "+data);

				// TODO Extend
				publishProgress("Status: "+status+", Data: "+data);

			}catch(JSONException e1){
				publishProgress("Error parsing JSON data");
				Log.d(TAG, "No data found i response: "+result);
//			}catch (ParseException e1){
//				e1.printStackTrace();
			}
			
		}
 
      return true;
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
  //  	Log.d(TAG, "Post-Execute: " + result);

    	super.onPostExecute(result);
    	pbM.setVisibility(View.INVISIBLE);
//       teSecondsProgressedM.setVisibility(View.INVISIBLE);
//    	teSecondsProgressedM.setText("Klart!");
    	pbM.setProgress(0);

    }
}