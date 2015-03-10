package apollo.data.sqlitedal;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import apollo.data.idal.IAutoPostDataProvider;
import apollo.data.model.AutoPost;
import apollo.data.model.Constants;
import apollo.data.model.Post;
import apollo.data.model.Section;
import apollo.data.model.Thread;
import apollo.data.model.User;
import apollo.util.DataSet;
import apollo.util.DateTime;
import apollo.util.StringUtil;

public class AutoPostDataProvider implements IAutoPostDataProvider {

	@Override
	public AutoPost getAutoPost(Thread thread) {
		SQLiteDatabase db = null;
		Cursor cursor = null;
		String[] columns = null;
		String[] args = null;
		String selection = null;
		String accounts = null;
		AutoPost post = null;

		db = DatabaseHelper.getReadDatabase();
		columns = new String[] {
				AutoPost.Columns.ID,
				AutoPost.Columns.SECTION_ID,
				AutoPost.Columns.THREAD_ID,
				AutoPost.Columns.THREAD_SUBJECT,
				AutoPost.Columns.FLOOR_NUM,
				AutoPost.Columns.ACCOUNTS,
				AutoPost.Columns.POST_BODY,
				AutoPost.Columns.START_TIME,
				AutoPost.Columns.END_TIME };
		selection = AutoPost.Columns.SECTION_ID + "=? AND " + AutoPost.Columns.THREAD_ID + "=?";
		args = new String[]{thread.getSection().getSectionId(), Integer.toString(thread.getThreadId())};
		
		cursor = db.query(Constants.APOLLO_DATA_TABLE_AUTOPOST, columns, selection,
				args, null, null, null);
		if (cursor != null) {
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				post = new AutoPost();
					
				post.id = cursor.getInt(0);
				post.thread = new Thread();
				post.thread.setSection(new Section());
				
				post.thread.getSection().setSectionId(cursor.getString(1));
				post.thread.setThreadId(cursor.getInt(2));
				post.thread.setSubject(cursor.getString(3));
				post.floorNum = cursor.getInt(4);
				post.floorEnable = post.floorNum != 0;
				accounts = cursor.getString(5);
				post.postBody = cursor.getString(6);
				post.start = DateTime.parse(cursor.getString(7)).getDate();
				post.end = DateTime.parse(cursor.getString(8)).getDate();
			}
			cursor.close();
		}
		db.close();
		
		if (TextUtils.isEmpty(accounts) == false) {
			String[] temp = accounts.split(";");
			User u = null;
			post.accounts = new ArrayList<User>();
			
			for(int i=0; i<temp.length; i++) {
				u = new User();
				u.setName(temp[i]);
				
				post.accounts.add(u);
			}
		}
		return post;
	}
	
	@Override
	public DataSet<AutoPost> getAutoPosts(int pageIndex, int pageSize) {
		SQLiteDatabase db = null;
		Cursor cursor = null;
		DataSet<AutoPost> datas = null;
		List<AutoPost> posts = null;
		List<String> post_accounts = null;
		String[] columns = null;
		String limit = null;
		String accounts = null;
		AutoPost post = null;
		
		posts = new ArrayList<AutoPost>();
		datas = new DataSet<AutoPost>();
		datas.setObjects(posts);
		
		post_accounts = new ArrayList<String>();
		
		columns = new String[] {
				AutoPost.Columns.ID,
				AutoPost.Columns.SECTION_ID,
				AutoPost.Columns.THREAD_ID,
				AutoPost.Columns.THREAD_SUBJECT,
				AutoPost.Columns.FLOOR_NUM,
				AutoPost.Columns.ACCOUNTS,
				AutoPost.Columns.POST_BODY,
				AutoPost.Columns.START_TIME,
				AutoPost.Columns.END_TIME };
		limit = (pageIndex - 1) * pageSize + "," + pageSize;
		
		db = DatabaseHelper.getReadDatabase();
		cursor = db.query(Constants.APOLLO_DATA_TABLE_AUTOPOST, columns, null, null, null, null, null, limit);
		if (cursor != null) {
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				post = new AutoPost();
				
				post.id = cursor.getInt(0);
				post.thread = new Thread();
				post.thread.setSection(new Section());
				
				post.thread.getSection().setSectionId(cursor.getString(1));
				post.thread.setThreadId(cursor.getInt(2));
				post.thread.setSubject(cursor.getString(3));
				post.floorNum = cursor.getInt(4);
				post.floorEnable = post.floorNum != 0;
				post_accounts.add(cursor.getString(5));
				post.postBody = cursor.getString(6);
				post.start = DateTime.parse(cursor.getString(7)).getDate();
				post.end = DateTime.parse(cursor.getString(8)).getDate();
				
				posts.add(post);
			}
			cursor.close();
		}
		
		columns = new String[] {"count(0)"};
		cursor = db.query(Constants.APOLLO_DATA_TABLE_AUTOPOST, columns, null, null, null, null, null, null);
		if (cursor != null) {
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				datas.setTotalRecords(cursor.getInt(0));
			}
			cursor.close();
		}
		
		db.close();
		
		for(int i=0; i<posts.size(); i++) {
			accounts = post_accounts.get(i);
			post = posts.get(i);
			if ("".equals(accounts) == false) {
				String[] temp = accounts.split(";");
				User u = null;
				post.accounts = new ArrayList<User>();
				
				for(int j=0; j<temp.length; j++) {
					u = new User();
					u.setName(temp[j]);
					
					post.accounts.add(u);
				}
			}
		}
		return datas;
	}

	@Override
	public int add(AutoPost config) {
		SQLiteDatabase db = null;
		ContentValues values = null;
		int result = -1;
		String accounts = null;
		String[] temp = null;
		
		if (config.accounts != null) {
			// 处理选中的特定用户
			temp = new String[config.accounts.size()];
			for(int i=0; i<temp.length; i++) {
				temp[i] = config.accounts.get(i).getName();
			}
			accounts = StringUtil.join(temp, ";");
		} else {
			// 当全选用户过多时会造成数据字段过长，所以当accounts为“”时既是选中所有用户 
			accounts = "";
		}
		
		values = new ContentValues(7);
		values.put(AutoPost.Columns.SECTION_ID, config.thread.getSection().getSectionId());
		values.put(AutoPost.Columns.THREAD_ID, config.thread.getThreadId());
		values.put(AutoPost.Columns.THREAD_SUBJECT, config.thread.getSubject());
		values.put(AutoPost.Columns.FLOOR_NUM, config.floorNum);
		values.put(AutoPost.Columns.ACCOUNTS, accounts);
		values.put(AutoPost.Columns.POST_BODY, config.postBody);
		values.put(AutoPost.Columns.START_TIME, (new DateTime(config.start)).toString());
		values.put(AutoPost.Columns.END_TIME, (new DateTime(config.end)).toString());
		
		db = DatabaseHelper.getWriteDatabase();
		result = (int)db.insert(Constants.APOLLO_DATA_TABLE_AUTOPOST, null, values);
		db.close();
		
		return result;
	}

	@Override
	public int update(int id, AutoPost config) {
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
		values.put(AutoPost.Columns.SECTION_ID, config.thread.getSection().getSectionId());
		values.put(AutoPost.Columns.THREAD_ID, config.thread.getThreadId());
		values.put(AutoPost.Columns.THREAD_SUBJECT, config.thread.getSubject());
		values.put(AutoPost.Columns.FLOOR_NUM, config.floorNum);
		values.put(AutoPost.Columns.ACCOUNTS, accounts);
		values.put(AutoPost.Columns.POST_BODY, config.postBody);
		values.put(AutoPost.Columns.START_TIME, (new DateTime(config.start)).toString());
		values.put(AutoPost.Columns.END_TIME, (new DateTime(config.end)).toString());
		
		whereClause = AutoPost.Columns.ID + "=?";
		whereArgs = new String[]{Integer.toString(id)};
		
		db = DatabaseHelper.getWriteDatabase();
		result = db.update(Constants.APOLLO_DATA_TABLE_AUTOPOST, values, whereClause, whereArgs);
		db.close();
		return result;
	}

	@Override
	public int delete(int id) {
		SQLiteDatabase db = null;
		String whereClause = null;
		String[] whereArgs = null;
		int result = -1;
		
		whereClause = AutoPost.Columns.ID + "=?";
		whereArgs = new String[]{Integer.toString(id)};
		db = DatabaseHelper.getWriteDatabase();
		result = db.delete(Constants.APOLLO_DATA_TABLE_AUTOPOST, whereClause, whereArgs);
		db.close();
		return result;
	}
}
