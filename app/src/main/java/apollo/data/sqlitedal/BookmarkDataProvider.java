package apollo.data.sqlitedal;

import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import apollo.data.idal.IBookmarkDataProvider;
import apollo.data.model.Constants;
import apollo.data.model.Section;
import apollo.data.model.Thread;
import apollo.data.model.User;
import apollo.util.DataSet;

public class BookmarkDataProvider implements IBookmarkDataProvider {

	@Override
	public void add(Thread thread, User user) {
		SQLiteDatabase db = null;
		ContentValues values = null;
		
		values = new ContentValues(3);
		values.put(Thread.Columns.SECTION_ID, thread.getSection().getSectionId());
		values.put(Thread.Columns.THREAD_ID, thread.getThreadId());
		values.put(Thread.Columns.FLOOR, thread.getFloor());
		values.put(Thread.Columns.USER_ID, user.getUserId());
		
		db = DatabaseHelper.getWriteDatabase();
		db.insert(Constants.APOLLO_DATA_TABLE_BOOKMARK, null, values);
		db.close();
	}

	@Override
	public void delete(Thread thread, User user) {
		SQLiteDatabase db = null;
		String whereClause = null;
		String[] whereArgs = null;
		
		whereClause = Thread.Columns.USER_ID + "=? AND " + Thread.Columns.SECTION_ID + "=? AND " + Thread.Columns.THREAD_ID + "=?";
		whereArgs = new String[]{Integer.toString(user.getUserId()), thread.getSection().getSectionId(), Integer.toString(thread.getThreadId())};
		db = DatabaseHelper.getWriteDatabase();
		db.delete(Constants.APOLLO_DATA_TABLE_BOOKMARK, whereClause, whereArgs);
		db.close();
	}

	@Override
	public DataSet<Thread> getThreads(User user, int pageIndex, int pageSize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Section> getSections(User user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getPosition(Thread thread, User user) {
		SQLiteDatabase db = null;
		Cursor cursor = null;
		String[] columns = null;
		String selection = null;
		int pos = 0;
		String[] args = null;
		
		db = DatabaseHelper.getReadDatabase();
		columns = new String[] {Thread.Columns.FLOOR };
		selection = Thread.Columns.USER_ID + "=? AND " + Thread.Columns.SECTION_ID + "=? AND " + Thread.Columns.THREAD_ID + "=?";
		args = new String[]{Integer.toString(user.getUserId()), thread.getSection().getSectionId(), Integer.toString(thread.getThreadId())};
		
		cursor = db.query(Constants.APOLLO_DATA_TABLE_BOOKMARK, columns, selection, args, null, null, null);
		if (cursor != null) {
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {			
				pos = cursor.getInt(0);
			}
			cursor.close();
		}
		db.close();
		return pos;
	}

}
