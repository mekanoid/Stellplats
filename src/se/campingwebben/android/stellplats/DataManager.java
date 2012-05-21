 package se.campingwebben.android.stellplats;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DataManager extends SQLiteOpenHelper {
	 
	    // The Android's default system path of your application database
	    private static String DB_PATH = "/data/data/se.campingwebben.android.stellplats/databases/";
	    // The name of your application database
	    private static String DB_NAME = "SplDB.sqlite";
	    // The version of your application database
	    private static int DB_VERSION = 1;
	    // The name of your database table
//	    private static String TB_NAME = "platser";

	    private SQLiteDatabase myDataBase; 
	    private final Context myContext;
	    public static final String KEY_CONTENT = "namn";

		// For debugging
		private static final String TAG = "DataMgr";


	    /**
	     * Constructor
	     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
	     * @param context
	     */
	    public DataManager(Context context) {
	    	super(context, DB_NAME, null, DB_VERSION);
	        this.myContext = context;
	    }	
	    
	    /**
	     * Creates a empty database on the system and rewrites it with your own database.
	     * */ 
	    public void createDataBase() throws IOException{
	 
	    	boolean dbExist = checkDataBase();
	 
			if(dbExist){
	    		//do nothing - database already exist
	    	}else{
	 
	    		//By calling this method an empty database will be created into the default system path
	    		//of your application so we are gonna be able to overwrite that database with our database.
	        	this.getReadableDatabase();

	        	// Close database here to avoid "SQLiteDatabase created and never closed" error
	        	this.close();
	 
	        	try {
	    			copyDataBase();
	    		} catch (IOException e) {
	        		throw new Error("Error copying database");
	        	}
	    	}
	    }
	 
	    /**
	     * Check if the database already exist to avoid re-copying the file each time you open the application.
	     * @return true if it exists, false if it doesn't
	     */
	    private boolean checkDataBase(){
	 
	    	SQLiteDatabase checkDB = null;
	 
	    	try{
	    		String myPath = DB_PATH + DB_NAME;
	    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
	    	}catch(SQLiteException e){
	    		//database does't exist yet.
	    	}
	 
	    	if(checkDB != null){
	    		checkDB.close();
	    	}
	 
	    	return checkDB != null ? true : false;
	    }
	 
	    /**
	     * Copies your database from your local assets-folder to the just created empty database in the
	     * system folder, from where it can be accessed and handled. Done by transferring byte stream.
	     */
	    private void copyDataBase() throws IOException{
	 
	    	// Open your local db as the input stream
	    	InputStream myInput = myContext.getAssets().open(DB_NAME);
	 
	    	// Path to the just created empty db
	    	String outFileName = DB_PATH + DB_NAME;
	 
	    	// Open the empty db as the output stream
	    	OutputStream myOutput = new FileOutputStream(outFileName);
	 
	    	// Transfer bytes from the inputfile to the outputfile
	    	byte[] buffer = new byte[1024];
	    	int length;
	    	while ((length = myInput.read(buffer))>0){
	    		myOutput.write(buffer, 0, length);
	    	}
	 
	    	// Close the streams
	    	myOutput.flush();
	    	myOutput.close();
	    	myInput.close();
	    }

	    /**
	     * This method is used to delete the old/current database
	     * @param context
	     * @return
	     */
//	    public boolean deleteDatabase(Context context) {
//	        return context.deleteDatabase(DB_NAME);
//	    }


	    /**
	     * Open database for read-only
	     * @throws SQLException
	     */
	    public void openDataBase() throws SQLException{
	        String myPath = DB_PATH + DB_NAME;
	    	myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
	    }

	    
	    /**
	     * Close database
	     */
	    @Override
	    public synchronized void close() {
	    	if(myDataBase != null)
	    		myDataBase.close();
	    	super.close();
		}

	    
	    /**
	     * This method is called during the creation of the class(?)
	     */
	    @Override
		public void onCreate(SQLiteDatabase db) {

		}

	    
	    /**
		 * This method is called during an upgrade of the database, 
		 * e.g. if you increase the database version
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.d(TAG, "Upgrading database from v" + oldVersion + " to v"+ newVersion);

			// For checking upgrade success
			int upgradeVersion = oldVersion;

			// Upgrade from v1 to v2
			if (upgradeVersion == 1) {

				// Delete current database
				Boolean chk = myContext.deleteDatabase(DB_NAME);
				if (chk) {
					Log.d(TAG, "Deleted old database");
				} else {
					Log.d(TAG, "Unsuccessfully tried to delete old database");
				}
		 
		        // Create a new database if no database exist
		        try {
		        	Log.d(TAG, "Trying to create new database");
		        	Log.d(TAG, "Copying database");
		        	copyDataBase();
		         	Log.d(TAG, "New database created!");
					upgradeVersion = 2;
		        } catch (IOException ioe) {
					upgradeVersion = 1;
		        	throw new Error("Unable to create database");
		        }
		        
			}
			// Check if upgrade was successful or not
			if (upgradeVersion != newVersion) {
				Log.d(TAG, "Upgrade not successful");
			}
		}
	 
		// Add your public helper methods to access and get content from the database.
		// You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
		// to you to create adapters for your views.

}
