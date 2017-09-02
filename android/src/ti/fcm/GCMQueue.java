package ti.fcm;

import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class GCMQueue {
	private static Context ctx = TiApplication.getInstance().getApplicationContext();
	private String LCAT = FcmModule.LCAT;

	public GCMQueue() {
		init();

	}
     public static void init() {
    	 SQLiteDatabase mydatabase = ctx.openOrCreateDatabase("GCMQUEUE",
 				Context.MODE_PRIVATE, null);
 		mydatabase
 				.execSQL("CREATE TABLE IF NOT EXISTS queue(id TEXT,ctime INTEGER, payload TEXT,done INTEGER);");
 		mydatabase.close();
    	 
     }
	public static void insertMessage(String id, long ctime, JSONObject message) {
		SQLiteDatabase mydatabase = ctx.openOrCreateDatabase("GCMQUEUE",
				Context.MODE_PRIVATE, null);

		String sql = "INSERT INTO queue VALUES('" + id + "'," + ctime + ",'"
				+ message.toString() + "',0);";
	
		mydatabase.execSQL(sql);
		mydatabase.close();
	}
}
