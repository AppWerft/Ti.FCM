package ti.fcm;

import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.util.TiRHelper;
import org.appcelerator.titanium.util.TiRHelper.ResourceNotFoundException;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

public class IntentService extends GcmListenerService {
	private static final String LCAT = "tigoosh.IntentService";
	private static final AtomicInteger atomic = new AtomicInteger(0);
	private FcmModule module = FcmModule.getModule();

	@Override
	public void onMessageReceived(String from, Bundle bundle) {
		Log.d(LCAT, "Push notification received from ~~~~~~~~~~: " + from);
		for (String key : bundle.keySet()) {
			Object value = bundle.get(key);
			Log.d(LCAT,
					String.format("key: %s => Value: %s (%s)", key,
							value.toString(), value.getClass().getName()));
		}

		JSONObject message;
		try {
			// AWS:
			if (bundle.containsKey("default")) {
				message = (new JSONObject(bundle.getString("default")));
				Log.d(LCAT, message.toString());
				GCMQueue db = new GCMQueue();
				db.insertMessage(bundle.getString("google.message_id"),
						bundle.getLong("google.sent_time"), message);
				parseNotification(message);

			} else {
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private int getResource(String type, String name) {
		int icon = 0;
		if (name != null) {
			int index = name.lastIndexOf(".");
			if (index > 0)
				name = name.substring(0, index);
			try {
				icon = TiRHelper.getApplicationResource(type + "." + name);
			} catch (TiRHelper.ResourceNotFoundException ex) {
				Log.e(LCAT, type + "." + name
						+ " not found; make sure it's in platform/android/res/"
						+ type);
			}
		}

		return icon;
	}

	private Bitmap getBitmapFromURL(String src) throws Exception {
		HttpURLConnection connection = (HttpURLConnection) (new URL(src))
				.openConnection();
		connection.setDoInput(true);
		connection.setUseCaches(false); // Android BUG
		connection.connect();
		return BitmapFactory.decodeStream(new BufferedInputStream(connection
				.getInputStream()));
	}

	private void showNotification(Context ctx, JSONObject message) {
		FcmModule module = FcmModule.getModule();
		Log.d(LCAT, "Content of gcm.defaults.json\n===========================");
		// Log.d(LCAT, module.gcmParameters.toString());
		String title = "";// module.gcmParameters.getTitle();
		String alert = "";// module.gcmParameters.getAlert();
		try {
			title = message.getString("title");
			alert = message.getString("alert");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.w(LCAT, "Show Notification: TRUE " + title);
		/* Create intent to (re)start the app's root activity (from gcmpush) */
		String pkg = ctx.getPackageName();
		Intent launcherIntent = ctx.getPackageManager()
				.getLaunchIntentForPackage(pkg);
		launcherIntent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0,
				launcherIntent, Intent.FLAG_ACTIVITY_NEW_TASK);

		// build the manager:
		NotificationManager notificationManager = (NotificationManager) TiApplication
				.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
		launcherIntent.putExtra(FcmModule.INTENT_EXTRA, message.toString());

		// Start building notification

		NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx);
		int builder_defaults = 0;
		// adding pendingIntent
		builder.setContentIntent(pendingIntent);

		builder.setAutoCancel(false);
		builder.setPriority(NotificationCompat.PRIORITY_HIGH);
		builder.setContentTitle(title);
		builder.setContentText(alert);
		builder.setTicker(alert);

		// BigText
		if (message != null && message.has("bigText")) {
			NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
			try {
				builder.setContentTitle(message.getString("contentTitle"));
				builder.setContentText(message.getString("contentText"));
				bigTextStyle.bigText(message.getString("bigText"));
				bigTextStyle.setSummaryText(message.getString("summaryText"));
				bigTextStyle.setBigContentTitle(message
						.getString("bigContentTitle"));
				builder.setStyle(bigTextStyle);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

		// Icons
		String smallIconName = "notificationicon";
		if (message != null && message.has("smallIcon")) {
			try {
				smallIconName = message.getString("smallIcon");
				int smallIcon = TiRHelper.getApplicationResource("drawable."
						+ smallIconName);
				if (smallIcon > 0) {
					builder.setSmallIcon(smallIcon);
				} else {
					Log.d(LCAT, "no icon found");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (ResourceNotFoundException e) {
				e.printStackTrace();
			}
		}

		// Large icon
		if (message != null && message.has("bigIcon")) {
			String iconName;
			try {
				iconName = message.getString("bigIcon");
				Bitmap icon = this.getBitmapFromURL(iconName);
				builder.setLargeIcon(icon);
			} catch (Exception ex) {
				Log.e(LCAT, "Icon exception: " + ex.getMessage());
			}
		}
		// Large image
		if (message != null && message.has("bigImage")) {
			NotificationCompat.BigPictureStyle bigPictureNotification = new NotificationCompat.BigPictureStyle();
			try {
				bigPictureNotification.bigPicture(this.getBitmapFromURL(message
						.getString("bigImage")));
				if (message.has("bigContentTitle")) {
					bigPictureNotification.setBigContentTitle(message
							.getString("bigContentTitle"));
				}
				if (message.has("contentText")) {
					builder.setContentText(message.getString("contentText"));
				}
				builder.setStyle(bigPictureNotification);

			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		// Color
		if (message != null && message.has("color")) {
			try {
				int color = Color.parseColor(message.getString("color"));
				builder.setColor(color);
			} catch (Exception ex) {
				Log.e(LCAT, "Color exception: " + ex.getMessage());
			}
		}

		// Badge
		if (message != null && message.has("badge")) {
			int badge;
			try {
				badge = message.getInt("badge");
				BadgeUtils.setBadge(ctx, badge);
				builder.setNumber(badge);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		// Sound
		if (message != null && message.has("sound")) {
			Object sound;
			try {
				sound = message.get("sound");
				if (sound instanceof Boolean && (((Boolean) sound) == true)
						|| (sound instanceof String)
						&& (((String) sound).equals("default"))) {
					builder_defaults |= Notification.DEFAULT_SOUND;
				} else if (sound instanceof String) {
					int resource = getResource("raw", (String) sound);
					builder.setSound(Uri.parse("android.resource://"
							+ ctx.getPackageName() + "/" + resource));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		// Ongoing
		// builder.setOngoing(module.gcmParameters.getOngoing());
		if (message != null && message.has("ongoing")) {
			try {
				Boolean ongoing = message.getBoolean("ongoing");
				builder.setOngoing(ongoing);
			} catch (Exception ex) {
				Log.e(LCAT, "Ongoing exception: " + ex.getMessage());
			}
		} else {
			builder_defaults |= Notification.DEFAULT_LIGHTS;
		}
		// Only alert once
		if (message != null && message.has("only_alert_once")) {
			try {
				Boolean oaoJson = message.getBoolean("only_alert_once");
			} catch (Exception ex) {
				Log.e(LCAT, "Only alert once exception: " + ex.getMessage());
			}
		} else {
			builder_defaults |= Notification.DEFAULT_LIGHTS;
		}

		// Builder defaults OR
		builder.setDefaults(builder_defaults);

		// Tag
		String tag = "";
		if (message != null && message.has("tag")) {
			try {
				tag = message.getString("tag");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Nid
		int id = 0;

		id = atomic.getAndIncrement();

		// Send
		notificationManager.notify(tag, id, builder.build());

	}

	private void parseNotification(JSONObject message) {
		Context ctx = TiApplication.getInstance().getApplicationContext();
		Boolean isAppInBackground = !testIfActivityIsTopInList()
				.getIsForeground();
		Log.d(LCAT, "~~~~~~~~ background=" + isAppInBackground);
		// Flag that determine if the message should be broadcasted to
		// TiGooshModule and call the callback
		Boolean sendMessage = !isAppInBackground;
		// Flag to show the system alert
		Boolean showNotification = isAppInBackground;

		// the title and alert

		// here wer have title, alert and data
		if (!isAppInBackground) {
			Log.d(LCAT,
					"!isAppInBackground  => depending on force_show_in_foreground: ");
			if (message != null && message.has("force_show_in_foreground")) {
				Boolean forceShowInForeground = false;
				try {
					forceShowInForeground = message
							.getBoolean("force_show_in_foreground");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				showNotification = (forceShowInForeground == true);
			} else {
				showNotification = false;
			}
		}

		if (sendMessage && module != null) {
			Log.d(LCAT, " IntentServioce tries to sendback to JS via module");
			module.sendMessage(message.toString(), isAppInBackground);
		}

		if (showNotification) {
			Log.d(LCAT, "showNotification will call");
			showNotification(ctx, message);

		} else {
			Log.w(LCAT, "Show Notification: FALSE");
		}
	}

	static public TaskTestResult testIfActivityIsTopInList() {
		try {
			TaskTestResult result = new ForegroundCheck().execute(
					TiApplication.getInstance().getApplicationContext()).get();
			return result;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		} catch (ExecutionException e) {
			e.printStackTrace();
			return null;
		}
	}

	private String getCountlyId(Bundle bundle) {
		String id = bundle.getString("c.i");
		return "{\"c.i\": \"" + id + "\"}";
	}
}
