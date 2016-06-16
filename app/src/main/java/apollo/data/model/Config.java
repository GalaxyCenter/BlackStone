package apollo.data.model;

import android.net.Uri;
import android.provider.BaseColumns;

public class Config {

	public static class Columns implements BaseColumns {
		public static final Uri CONTENT_URI = Uri.parse("content://apollo.app.view/item");
		
		public static final String ID = "_id";
		public static final String REMIND = "remind";
		public static final String FONT_SIZE = "font_size";
		public static final String REMIND_ENABLED = "remind_enabled";
		public static final String SHOW_IMAGE = "show_image";
		public static final String SHOW_HEAD = "show_head";
		public static final String SOUND_ENABLED = "sound_enabled";
		public static final String VIBRATE_ENABLED = "vibrate_enabled";

	}
	
	public int id = -1;
	public int remind = 1;
	public int fontSize = 14;
	public boolean remindEnabled = true;
	public boolean showImage = true;
	public boolean showHead = true;
	public boolean soundEnabled = true;
	public boolean vibrateEnabled = true;
	
}
