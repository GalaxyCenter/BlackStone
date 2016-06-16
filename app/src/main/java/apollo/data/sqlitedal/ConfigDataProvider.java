package apollo.data.sqlitedal;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import apollo.data.idal.IConfigDataProvider;
import apollo.data.model.Config;
import apollo.data.model.Constants;

public class ConfigDataProvider implements IConfigDataProvider {

	@Override
	public Config getConfig(int id) {
		SQLiteDatabase db = null;
		Cursor cursor = null;
		String[] columns = null;
		String selection = null;
		Config config = null;

		db = DatabaseHelper.getReadDatabase();
		columns = new String[] { Config.Columns.REMIND,
				Config.Columns.FONT_SIZE, Config.Columns.REMIND_ENABLED,
				Config.Columns.SHOW_IMAGE, Config.Columns.SHOW_HEAD,
				Config.Columns.SOUND_ENABLED, Config.Columns.VIBRATE_ENABLED };
		selection = Config.Columns.ID + "=" + id;

		cursor = db.query(Constants.APOLLO_DATA_TABLE_CONFIG, columns, selection,
				null, null, null, null);
		if (cursor != null) {
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
					.moveToNext()) {
				config = new Config();
					
				config.id = id;
				config.remind = cursor.getInt(0);
				config.fontSize = cursor.getInt(1);
				config.remindEnabled = cursor.getInt(2) == 1;
				config.showImage = cursor.getInt(3) == 1;
				config.showHead = cursor.getInt(4) == 1;
				config.soundEnabled = cursor.getInt(5) == 1;
				config.vibrateEnabled = cursor.getInt(6) == 1;
			}
			cursor.close();
		}
		db.close();
		return config;
	}

	@Override
	public int add(Config config) {
		SQLiteDatabase db = null;
		ContentValues values = null;
		int result = -1;
		
		values = new ContentValues(7);
		values.put(Config.Columns.ID, config.id);
		values.put(Config.Columns.REMIND, config.remind);
		values.put(Config.Columns.FONT_SIZE, config.fontSize);
		values.put(Config.Columns.REMIND_ENABLED, config.remindEnabled);
		values.put(Config.Columns.SHOW_IMAGE, config.showImage);
		values.put(Config.Columns.SHOW_HEAD, config.showHead);
		values.put(Config.Columns.SOUND_ENABLED, config.soundEnabled);
		values.put(Config.Columns.VIBRATE_ENABLED, config.vibrateEnabled);
		
		db = DatabaseHelper.getWriteDatabase();
		result = (int)db.insert(Constants.APOLLO_DATA_TABLE_CONFIG, null, values);
		db.close();
		return result;
	}

	@Override
	public int update(int id, Config config) {
		SQLiteDatabase db = null;
		ContentValues values = null;
		String whereClause = null;
		String[] whereArgs = null;
		int result = -1;
		
		values = new ContentValues(7);
		values.put(Config.Columns.REMIND, config.remind);
		values.put(Config.Columns.FONT_SIZE, config.fontSize);
		values.put(Config.Columns.REMIND_ENABLED, config.remindEnabled);
		values.put(Config.Columns.SHOW_IMAGE, config.showImage);
		values.put(Config.Columns.SHOW_HEAD, config.showHead);
		values.put(Config.Columns.SOUND_ENABLED, config.soundEnabled);
		values.put(Config.Columns.VIBRATE_ENABLED, config.vibrateEnabled);
		
		whereClause = Config.Columns.ID + "=?";
		whereArgs = new String[]{Integer.toString(id)};
		
		db = DatabaseHelper.getWriteDatabase();
		result = db.update(Constants.APOLLO_DATA_TABLE_CONFIG, values, whereClause, whereArgs);
		db.close();
		return result;
	}

	@Override
	public int delete(Config config) {
		// TODO Auto-generated method stub
		return 0;
	}

}
