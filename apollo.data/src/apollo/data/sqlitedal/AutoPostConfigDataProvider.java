package apollo.data.sqlitedal;

import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import apollo.data.idal.IAutoPostConfigDataProvider;
import apollo.data.model.AutoPostConfig;
import apollo.data.model.Constants;
import apollo.data.model.Section;
import apollo.data.model.Thread;
import apollo.data.model.User;
import apollo.util.DateTime;
import apollo.util.StringUtil;

public class AutoPostConfigDataProvider implements IAutoPostConfigDataProvider {

	@Override
	public AutoPostConfig getConfig(Thread thread) {
		SQLiteDatabase db = null;
		Cursor cursor = null;
		String[] columns = null;
		String[] args = null;
		String selection = null;
		String accounts = null;
		AutoPostConfig config = null;

		db = DatabaseHelper.getReadDatabase();
		columns = new String[] {
				AutoPostConfig.Columns.ID,
				AutoPostConfig.Columns.SECTION_ID,
				AutoPostConfig.Columns.THREAD_ID,
				AutoPostConfig.Columns.THREAD_SUBJECT,
				AutoPostConfig.Columns.FLOOR_NUM,
				AutoPostConfig.Columns.ACCOUNTS,
				AutoPostConfig.Columns.POST_BODY,
				AutoPostConfig.Columns.START_TIME,
				AutoPostConfig.Columns.END_TIME };
		selection = AutoPostConfig.Columns.SECTION_ID + "=? AND " + AutoPostConfig.Columns.THREAD_ID + "=?";
		args = new String[]{thread.getSection().getSectionId(), Integer.toString(thread.getThreadId())};
		
		cursor = db.query(Constants.APOLLO_DATA_TABLE_AUTOPOST_CONFIG, columns, selection,
				args, null, null, null);
		if (cursor != null) {
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				config = new AutoPostConfig();
					
				config.id = cursor.getInt(0);
				config.thread = new Thread();
				config.thread.setSection(new Section());
				
				config.thread.getSection().setSectionId(cursor.getString(1));
				config.thread.setThreadId(cursor.getInt(2));
				config.thread.setSubject(cursor.getString(3));
				config.floorNum = cursor.getInt(4);
				config.floorEnable = config.floorNum != 0;
				accounts = cursor.getString(5);
				config.postBody = cursor.getString(6);
				config.start = DateTime.parse(cursor.getString(7)).getDate();
				config.end = DateTime.parse(cursor.getString(8)).getDate();
			}
			cursor.close();
		}
		db.close();
		
		if (TextUtils.isEmpty(accounts) == false) {
			String[] temp = accounts.split(";");
			User u = null;
			config.accounts = new ArrayList<User>();
			
			for(int i=0; i<temp.length; i++) {
				u = new User();
				u.setName(temp[i]);
				
				config.accounts.add(u);
			}
		}
		return config;
	}

	@Override
	public int add(AutoPostConfig config) {
		SQLiteDatabase db = null;
		ContentValues values = null;
		int result = -1;
		String accounts = null;
		String[] temp = null;
		
		if (config.accounts != null) {
			temp = new String[config.accounts.size()];
			for(int i=0; i<temp.length; i++) {
				temp[i] = config.accounts.get(i).getName();
			}
			accounts = StringUtil.join(temp, ";");
		} else {
			accounts = "";
		}
		
		values = new ContentValues(7);
		values.put(AutoPostConfig.Columns.SECTION_ID, config.thread.getSection().getSectionId());
		values.put(AutoPostConfig.Columns.THREAD_ID, config.thread.getThreadId());
		values.put(AutoPostConfig.Columns.THREAD_SUBJECT, config.thread.getSubject());
		values.put(AutoPostConfig.Columns.FLOOR_NUM, config.floorNum);
		values.put(AutoPostConfig.Columns.ACCOUNTS, accounts);
		values.put(AutoPostConfig.Columns.POST_BODY, config.postBody);
		values.put(AutoPostConfig.Columns.START_TIME, (new DateTime(config.start)).toString());
		values.put(AutoPostConfig.Columns.END_TIME, (new DateTime(config.end)).toString());
		
		db = DatabaseHelper.getWriteDatabase();
		result = (int)db.insert(Constants.APOLLO_DATA_TABLE_AUTOPOST_CONFIG, null, values);
		db.close();
		
		return result;
	}

	@Override
	public int update(int id, AutoPostConfig config) {
		SQLiteDatabase db = null;
		ContentValues values = null;
		String whereClause = null;
		String[] whereArgs = null;
		int result = -1;
		String accounts = null;
		String[] temp = null;
		
		if (config.accounts != null) {
			temp = new String[config.accounts.size()];
			for(int i=0; i<temp.length; i++) {
				temp[i] = config.accounts.get(i).getName();
			}
			accounts = StringUtil.join(temp, ";");
		} else {
			accounts = "";
		}
		if (config.floorEnable == false)
			config.floorNum = 0;
		
		values = new ContentValues(7);
		values.put(AutoPostConfig.Columns.SECTION_ID, config.thread.getSection().getSectionId());
		values.put(AutoPostConfig.Columns.THREAD_ID, config.thread.getThreadId());
		values.put(AutoPostConfig.Columns.THREAD_SUBJECT, config.thread.getSubject());
		values.put(AutoPostConfig.Columns.FLOOR_NUM, config.floorNum);
		values.put(AutoPostConfig.Columns.ACCOUNTS, accounts);
		values.put(AutoPostConfig.Columns.POST_BODY, config.postBody);
		values.put(AutoPostConfig.Columns.START_TIME, (new DateTime(config.start)).toString());
		values.put(AutoPostConfig.Columns.END_TIME, (new DateTime(config.end)).toString());
		
		whereClause = AutoPostConfig.Columns.ID + "=?";
		whereArgs = new String[]{Integer.toString(id)};
		
		db = DatabaseHelper.getWriteDatabase();
		result = db.update(Constants.APOLLO_DATA_TABLE_AUTOPOST_CONFIG, values, whereClause, whereArgs);
		db.close();
		return result;
	}

	@Override
	public int delete(AutoPostConfig config) {
		// TODO Auto-generated method stub
		return 0;
	}

}
