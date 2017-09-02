package ti.goosh;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.io.TiFileFactory;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiRHelper;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;

public class GCMParameters {
	final static String FILENAME = "gcm.defaults.json";
	private String subject = "Subject";
	private String alert = "Alert";
	private String title = "Title";
	private String priority = "high";
	private String tag = "";
	private String sound;
	private String channel = "default";
	private String icon = "";
	private int notificationicon;

	private int color = Color.GRAY;
	private Boolean ongoing = false;
	private Boolean only_alert_once = false;
	private Boolean vibrate = true;
	private Boolean force_show_in_foreground = true;
	private String LCAT = TiGooshModule.LCAT;
	JSONObject defaults;

	public GCMParameters() throws JSONException {
		defaults = loadJSONFromAsset();
		if (defaults == null) {
			defaults = new JSONObject();
			Log.e(LCAT, "not gcm.defaults.json found");
			defaults.put("subject", subject);
			defaults.put("alert", alert);
			defaults.put("tag", tag);
			defaults.put("sound", sound);
			defaults.put("channel", channel);

		}
		if (defaults.has("subject")) {
			this.subject = defaults.getString("subject");
		}
		if (defaults.has("alert")) {
			this.alert = defaults.getString("alert");
		}
		if (defaults.has("tag")) {
			this.tag = defaults.getString("tag");
		}
		if (defaults.has("sound")) {
			this.sound = defaults.getString("sound");
		}

		if (defaults.has(TiC.PROPERTY_TITLE)) {
			this.title = defaults.getString(TiC.PROPERTY_TITLE);
		}
		if (defaults.has("channel")) {
			this.channel = defaults.getString("channel");
		}
		if (defaults.has("priority")) {
			this.priority = defaults.getString("priority");
		}
		if (defaults.has("vibrate")) {
			this.vibrate = defaults.getBoolean("vibrate");
		}
		if (defaults.has("icon")) {
			this.icon = defaults.getString("icon");
		}
		if (defaults.has("color")) {
			this.color = TiConvert.toColor(defaults.getString("color"));
		}
		if (defaults.has("ongoing")) {
			this.ongoing = defaults.getBoolean("ongoing");
		}
		if (defaults.has("only_alert_once")) {
			this.only_alert_once = defaults.getBoolean("only_alert_once");
		}
		if (defaults.has("force_show_in_foreground")) {
			this.force_show_in_foreground = defaults
					.getBoolean("force_show_in_foreground");
		}
	}

	public void handleOptions(KrollDict options) throws JSONException {
		if (options.containsKeyAndNotNull("priority")) {
			this.priority = options.getString("priority");
		}
		if (options.containsKeyAndNotNull("subject")) {
			this.subject = options.getString("subject");
		}
		if (options.containsKeyAndNotNull("tag")) {
			this.tag = options.getString("tag");
		}
		if (options.containsKeyAndNotNull("alert")) {
			this.alert = options.getString("alert");
		}
		if (options.containsKeyAndNotNull(TiC.PROPERTY_TITLE)) {
			this.title = options.getString(TiC.PROPERTY_TITLE);
		}

		if (options.containsKeyAndNotNull("channel")) {
			this.channel = options.getString("channel");
		}

		if (options.containsKeyAndNotNull("sound")) {
			this.sound = options.getString("sound");
		}

		if (options.containsKeyAndNotNull("icon")) {
			this.icon = options.getString("icon");
		}

		if (options.containsKeyAndNotNull("color")) {
			this.color = TiConvert.toColor(options.getString("color"));
		}

		if (options.containsKeyAndNotNull("ongoing")) {
			this.ongoing = defaults.getBoolean("ongoing");
		}

		if (options.containsKeyAndNotNull("only_alert_once")) {
			this.only_alert_once = defaults.getBoolean("only_alert_once");
		}

		if (options.containsKeyAndNotNull("vibrate")) {
			this.vibrate = defaults.getBoolean("vibrate");
		}

		if (options.containsKeyAndNotNull("force_show_in_foreground")) {
			this.force_show_in_foreground = defaults
					.getBoolean("force_show_in_foreground");
		}

		if (options.containsKeyAndNotNull("notificationicon")) {
			this.notificationicon = defaults.getInt("notificationicon");
		}

	}

	public String toString() {
		return defaults.toString();
	}

	public String getSubject() {
		return subject;
	}

	public String getAlert() {
		return alert;
	}

	public String getTitle() {
		return title != null ? title : "";
	}

	public String getPriority() {
		return priority;
	}

	public Boolean getOngoing() {
		return ongoing;
	}

	public void setOngoing(Boolean ongoing) {
		this.ongoing = ongoing;
	}

	public int getColor(int _color) {

		return (color != 0) ? color : _color;
	}

	public Boolean getOnly_alert_once() {
		return only_alert_once;
	}

	public void setOnly_alert_once(Boolean only_alert_once) {
		this.only_alert_once = only_alert_once;
	}

	public Boolean getVibrate() {
		return vibrate;
	}

	public void setVibrate(Boolean vibrate) {
		this.vibrate = vibrate;
	}

	public String getSound() {
		return sound;
	}

	public String getIcon(String _icon) {
		return (_icon != null) ? _icon : icon;
	}

	public Boolean getForce_show_in_foreground() {
		return force_show_in_foreground;
	}

	public void setForce_show_in_foreground(Boolean force_show_in_foreground) {
		this.force_show_in_foreground = force_show_in_foreground;
	}

	public String getChannel() {
		return channel;
	}

	public String getTag(String _tag) {
		return (_tag != null) ? _tag : tag;
	}

	public JSONObject loadJSONFromAsset() {
		String json = null;
		try {
			String url = TiGooshModule.getModule().resolveUrl(null, FILENAME);
			InputStream inStream = TiFileFactory.createTitaniumFile(
					new String[] { url }, false).getInputStream();
			byte[] buffer = new byte[inStream.available()];
			inStream.read(buffer);
			inStream.close();
			json = new String(buffer, "UTF-8");
			Log.d(LCAT, json);

		} catch (IOException ex) {
			Log.d(LCAT, "file not found: " + FILENAME);
			ex.printStackTrace();
			return null;
		}
		try {
			return new JSONObject(json);
		} catch (JSONException e) {
			Log.d(LCAT, FILENAME + " is not valif JSON, cannot parse");
			e.printStackTrace();
			return null;
		}
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

	private int getResource(String type, String name) {
		int icon = 0;
		if (name != null) {
			int index = name.lastIndexOf(".");
			if (index > 0)
				name = name.substring(0, index);
			try {
				icon = TiRHelper.getApplicationResource(type + "." + name);
			} catch (TiRHelper.ResourceNotFoundException ex) {

			}
		}

		return icon;
	}
}
