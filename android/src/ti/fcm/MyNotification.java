package ti.fcm;

import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.util.TiRHelper;
import org.appcelerator.titanium.util.TiRHelper.ResourceNotFoundException;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

public class MyNotification {
	private static final AtomicInteger atomic = new AtomicInteger(0);

	public static void create(JSONObject message) {
		Context ctx = TiApplication.getInstance().getApplicationContext();
		FcmModule
				.log("Content of gcm.defaults.json\n===========================");
		FcmModule.log(message.toString());

		String title = "NO TITLE";
		String alert = "NO ALERT";
		try {
			title = message.has("title") ? message.getString("title") : title;
			alert = message.has("alert") ? message.getString("alert") : alert;
		} catch (JSONException e1) {

			e1.printStackTrace();
		}

		FcmModule.log("Show Notification: TRUE " + title);
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

		// small icon for status bar or as content of icon if bigicon is missing
		String smallIconName = "notificationicon";
		if (message != null && message.has("smallIcon")) {
			FcmModule.log("message has props 'smallicon'");
			try {
				smallIconName = message.getString("smallIcon");
				int smallIcon = TiRHelper.getApplicationResource("drawable."
						+ smallIconName);
				if (smallIcon > 0) {
					builder.setSmallIcon(smallIcon);
				} else {
					FcmModule.log("no icon found");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (ResourceNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			FcmModule.log("message has no props 'smallicon', take default");
			int smallIcon;
			try {
				smallIcon = TiRHelper.getApplicationResource("drawable.notificationicon");
				if (smallIcon > 0) 
					builder.setSmallIcon(smallIcon);
			} catch (ResourceNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		
		}

		// Large icon
		if (message != null && message.has("bigIcon")) {
			String iconName;
			try {
				iconName = message.getString("bigIcon");
				Bitmap icon = getBitmapFromURL(iconName);
				builder.setLargeIcon(icon);
			} catch (Exception ex) {
				FcmModule.log("Icon exception: " + ex.getMessage());
			}
		}
		// Large image
		if (message != null && message.has("bigImage")) {
			NotificationCompat.BigPictureStyle bigPictureNotification = new NotificationCompat.BigPictureStyle();
			try {
				bigPictureNotification.bigPicture(getBitmapFromURL(message
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
				FcmModule.log("Color exception: " + ex.getMessage());
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
					builder_defaults |= android.app.Notification.DEFAULT_SOUND;
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
				FcmModule.log("Ongoing exception: " + ex.getMessage());
			}
		} else {
			builder_defaults |= android.app.Notification.DEFAULT_LIGHTS;
		}
		// Only alert once
		if (message != null && message.has("only_alert_once")) {
			try {
				Boolean oaoJson = message.getBoolean("only_alert_once");
			} catch (Exception ex) {
				FcmModule.log("Only alert once exception: " + ex.getMessage());
			}
		} else {
			builder_defaults |= android.app.Notification.DEFAULT_LIGHTS;
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

	private static Bitmap getBitmapFromURL(String src) throws Exception {
		HttpURLConnection connection = (HttpURLConnection) (new URL(src))
				.openConnection();
		connection.setDoInput(true);
		connection.setUseCaches(false); // Android BUG
		connection.connect();
		return BitmapFactory.decodeStream(new BufferedInputStream(connection
				.getInputStream()));
	}

	private static int getResource(String type, String name) {
		int icon = 0;
		if (name != null) {
			int index = name.lastIndexOf(".");
			if (index > 0)
				name = name.substring(0, index);
			try {
				icon = TiRHelper.getApplicationResource(type + "." + name);
			} catch (TiRHelper.ResourceNotFoundException ex) {
				FcmModule.log(type + "." + name
						+ " not found; make sure it's in platform/android/res/"
						+ type);
			}
		}

		return icon;
	}
}
