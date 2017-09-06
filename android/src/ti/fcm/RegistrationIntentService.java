package ti.fcm;

import java.io.IOException;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiProperties;
import org.appcelerator.titanium.util.TiRHelper;

public class RegistrationIntentService extends IntentService {

	private static final String LCAT = "FCMpush.RegistrationIntentService";
	 private static final String[] TOPICS = {"global"};
	 
	public RegistrationIntentService() {
		super(LCAT);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		TiProperties props =TiApplication.getInstance().getAppProperties();
		if (!props.hasProperty("FCM_SENDERID")) {
			Log.e(LCAT,"FCM_SENDERID is missing in tiapp.xml, RTFM!");
			return;
		}
		try {
			// retreiving senderid from module 
			String senderId = FcmModule.getSenderId();
			InstanceID instanceID = InstanceID.getInstance(this);
			String token = instanceID.getToken(senderId, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
			Log.i(LCAT, "Sender ID: " + senderId);
			Log.i(LCAT, "Device Token: " + token);
			FcmModule.getModule().sendSuccess(token);
		} catch (Exception ex) {
			Log.e(LCAT, "Failed to get GCM Registration Token: " + ex.getMessage());
			FcmModule.getModule().sendError(ex);
		}
	}
}
