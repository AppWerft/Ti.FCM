package ti.fcm;

import android.app.Activity;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import java.io.InputStream;

import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.TiConfig;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiProperties;
import org.appcelerator.titanium.io.TiFileFactory;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import android.app.NotificationManager;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

@Kroll.module(name = "Fcm", id = "ti.fcm")
public class FcmModule extends KrollModule {

	public static final String LCAT = "FCMpush";
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	public static final String INTENT_EXTRA = "tigoosh.notification";
	public static final String TOKEN = "tigoosh.token";

	private static FcmModule module = null;

	private KrollFunction successCallback = null;
	private KrollFunction errorCallback = null;
	private KrollFunction messageCallback = null;
	public GCMParameters gcmParameters;
	private static TiApplication app;
	public KrollDict lastData;
	private boolean dbg = false;

	@Kroll.constant
	final static int SERVICE_SUCCESS = ConnectionResult.SUCCESS;
	@Kroll.constant
	final static int SERVICE_MISSING = ConnectionResult.SERVICE_MISSING;
	@Kroll.constant
	final static int SERVICE_UPDATING = ConnectionResult.SERVICE_UPDATING;
	@Kroll.constant
	final static int SERVICE_VERSION_UPDATE_REQUIRED = ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED;
	@Kroll.constant
	final static int SERVICE_DISABLED = ConnectionResult.SERVICE_DISABLED;
	@Kroll.constant
	final static int SERVICE_INVALID = ConnectionResult.SERVICE_INVALID;

	public FcmModule() {
		super();
		module = this;
	}

	@Kroll.onAppCreate
	public static void onAppCreate(TiApplication _app) {
		app = _app;

	}

	public static FcmModule getModule() {
		if (module != null)
			return module;
		else {
			module = new FcmModule();
			return module;
		}	
		
	}

	public void parseBootIntent() {
		try {
			Bundle extras = TiApplication.getAppRootOrCurrentActivity()
					.getIntent().getExtras();
			String notification = "";

			if (extras != null) {
				notification = extras.getString(FcmModule.INTENT_EXTRA);

			}

			if (notification != null) {
				sendMessage(notification, true);
			} else {
				Log.d(LCAT, "No notification in Intent");
			}
		} catch (Exception ex) {
			Log.e(LCAT, ex.getMessage());
		}
	}


	@Kroll.method
	public void setDebug(boolean dbg) {
		this.dbg=dbg;
	}
	
	public static void log(String msg) {
		Log.d(LCAT, msg);
	}
	
	@Kroll.method
	public boolean checkPlayServices() {
		Activity activity = TiApplication.getAppRootOrCurrentActivity();

		GoogleApiAvailability apiAvailability = GoogleApiAvailability
				.getInstance();
		int resultCode = apiAvailability
				.isGooglePlayServicesAvailable(activity);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (apiAvailability.isUserResolvableError(resultCode)) {
				apiAvailability.getErrorDialog(activity, resultCode,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.e(LCAT, "This device is not supported.");
			}
			return false;
		}
		return true;
	}

	private NotificationManager getNotificationManager() {
		return (NotificationManager) TiApplication.getInstance()
				.getApplicationContext()
				.getSystemService(Context.NOTIFICATION_SERVICE);
	}

	@Kroll.method
	public static String getSenderId() {
		TiProperties props =TiApplication.getInstance().getAppProperties();
		if (props.hasProperty("FCM_SENDERID")) {
			return props.getString("FCM_SENDERID", "");
		}
		Log.e(LCAT,"For working with FCM we need the property from tiapp.xml named 'FCM_SENDERID'");
		return "";
	}

	@Kroll.method
	public int isGooglePlayServicesAvailable() {
		GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
		return googleAPI.isGooglePlayServicesAvailable(TiApplication
				.getAppRootOrCurrentActivity());
	}

	@Kroll.method
	public void init() {
		// db init:
		GCMQueue.init();
        
		try {
			gcmParameters = new GCMParameters();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Kroll.method
	public void registerForPushNotifications(KrollDict options) {
		if (gcmParameters != null)
			try {
				gcmParameters.handleOptions(options);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		// getting activity
		Activity activity = TiApplication.getAppRootOrCurrentActivity();
		if (false == options.containsKeyAndNotNull("callback")) {
			Log.e(LCAT,
					"You have to specify a callback attribute when calling registerForPushNotifications");
			return;
		}
		messageCallback = (KrollFunction) options.get("callback");
		successCallback = options.containsKeyAndNotNull("success") ? (KrollFunction) options
				.get("success") : null;
		errorCallback = options.containsKeyAndNotNull("error") ? (KrollFunction) options
				.get("error") : null;
		parseBootIntent();
		if (isGooglePlayServicesAvailable() == ConnectionResult.SUCCESS) {
			activity.startService(new Intent(activity,
					RegistrationIntentService.class));
		}
	}

	@Kroll.method
	public void unregisterForPushNotifications() {
		final String senderId = getSenderId();
		final Context context = TiApplication.getInstance()
				.getApplicationContext();

		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				try {
					InstanceID.getInstance(context).deleteToken(senderId,
							GoogleCloudMessaging.INSTANCE_ID_SCOPE);
					Log.d(LCAT, "delete instanceid succeeded");
				} catch (final IOException e) {
					Log.e(LCAT,
							"remove token failed - error: " + e.getMessage());
				}
				return null;
			}
		}.execute();
	}

	@Kroll.method
	public void cancelAll() {
		getNotificationManager().cancelAll();
	}

	@Kroll.method
	public void cancelWithTag(String tag, int id) {
		getNotificationManager().cancel(tag, -1 * id);
	}

	@Kroll.method
	public void cancel(int id) {
		getNotificationManager().cancel(-1 * id);
	}

	@Kroll.method
	@Kroll.getProperty
	public Boolean isRemoteNotificationsEnabled() {
		return (getRemoteDeviceUUID() != null);
	}

	@Kroll.method
	@Kroll.getProperty
	public String getRemoteDeviceUUID() {
		return getDefaultSharedPreferences().getString(TOKEN, "");
	}

	@Kroll.method
	public void setAppBadge(int count) {
		BadgeUtils.setBadge(
				TiApplication.getInstance().getApplicationContext(), count);
	}

	@Kroll.method
	public int getAppBadge() {
		return 0;
	}

	// Privates

	private SharedPreferences getDefaultSharedPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(TiApplication
				.getInstance().getApplicationContext());
	}

	private void saveToken(String token) {
		SharedPreferences preferences = getDefaultSharedPreferences();
		preferences.edit().putString(TOKEN, token).apply();
	}

	// Public

	public void sendSuccess(String token) {
		if (successCallback == null) {
			Log.e(LCAT, "sendSuccess invoked but no successCallback defined");
			return;
		}

		saveToken(token);

		HashMap<String, Object> e = new HashMap<String, Object>();
		e.put("deviceToken", token);

		successCallback.callAsync(getKrollObject(), e);

	}

	public void sendError(Exception ex) {
		if (errorCallback == null) {
			Log.e(LCAT, "sendError invoked but no errorCallback defined");
			return;
		}

		HashMap<String, Object> e = new HashMap<String, Object>();
		e.put("error", ex.getMessage());

		errorCallback.callAsync(getKrollObject(), e);
	}

	public void sendMessage(String data, Boolean inBackground) {
		HashMap<String, Object> e = new HashMap<String, Object>();
		e.put("data", data); // to parse on reverse on JS side
		e.put("inBackground", inBackground);
		// app.fireAppEvent("gcm",new KrollDict(e));
		if (messageCallback != null)
			messageCallback.callAsync(getKrollObject(), e);
		if (hasListeners("onCallback")) {
			fireEvent("onCallback", e);
		}
	}
}
