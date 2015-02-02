package apollo.data.model;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.BaseColumns;
import apollo.core.ApolloApplication;

public class Constants {
	
	public static final String APOLLO_DATABASE_NAME= "apollo.db";
	public static final String APOLLO_DATA_TABLE_USER = "users";
	public static final String APOLLO_DATA_TABLE_SECTION = "sections";
	public static final String APOLLO_DATA_TABLE_POST = "posts";
	public static final String APOLLO_DATA_TABLE_BOOKMARK = "book_marks";
	public static final String APOLLO_DATA_TABLE_CONFIG = "configs";
	public static final String APOLLO_DATA_TABLE_AUTOPOST = "autopost_configs";
	
	public static String APOLLO_FOLDER_TEMP = null;
	public static String APOLLO_CAMERA_TEMP = null;
	public static String VERSION = "";
	public static final String SETTINGFILE = "settings";
	public static int THREAD_IMAGE_MAX_WIDTH = 640;
	public static int THREAD_GIF_MIN_USE_MEMORY = 0x600000;
	public static int ALBUM_COLUMN_SIZE = 3;
	public static int PAGE_SIZE = 30;
	public static int BitmapQuality = 0;
	public static Bitmap.Config BitmapConfig;
	
	public static class Settings {
		public static final String BLANK_SUMMARY = " ";
		public static String KEY_REMIND_MODE = "remind_mode";
		public static String KEY_MANAGE_ACCOUNT = "account_manage";
		public static String KEY_MANAGE_AUTOPOST = "autopost_manage";
		
		public static String KEY_REMIND_ENABLE = "remind_enable";
		public static String KEY_SOUND = "sound_enable";
		public static String KEY_VIBRATE = "vibrate_enable";
		public static String KEY_FONT_SIZE = "font_size";
		public static String KEY_SHOW_IMG = "show_image";
		public static String KEY_SHOW_HEAD_IMG = "show_head_img";
		public static String KEY_CLEAR_CACHE = "clear_cache";
	}
	
	public static class AutoPostSettings {
		public static String KEY_START_TIME = "start_time";
		public static String KEY_END_TIME = "end_time";
		public static String KEY_FLOOR_ENABLE = "floor_enable";
		public static String KEY_FLOOR_NUM = "floor_num";
		public static String KEY_ACCOUNTS = "accounts";
		public static String KEY_POST_BODY = "post_body";
	}
		
	static {
		BitmapQuality = 80;
		BitmapConfig = Bitmap.Config.RGB_565;
		
		APOLLO_FOLDER_TEMP = Environment
				.getExternalStorageDirectory() + "/"
				+ ApolloApplication.app().getPackageName() + "/temp";
		
		APOLLO_CAMERA_TEMP = APOLLO_FOLDER_TEMP + "/camara.img";
	}

	
	public static int getContentSize() {
		return 8;
	}

	public static void setThreadImageWidth(int i) {
		if (i >= 300 && i <= 1200)
			THREAD_IMAGE_MAX_WIDTH = i;
	}
}
