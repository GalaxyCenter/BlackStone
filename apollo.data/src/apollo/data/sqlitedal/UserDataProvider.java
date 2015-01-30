package apollo.data.sqlitedal;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import apollo.data.idal.IUserDataProvider;
import apollo.data.model.Constants;
import apollo.data.model.User;
import apollo.net.Cookie;
import apollo.util.DataSet;

public class UserDataProvider implements IUserDataProvider {

	@Override
	public Cookie[] validUser(String name, String password, String vcode) {
		throw new UnsupportedOperationException("unsupported getUsers");
	}

	@Override
	public DataSet<User> getUsers(int pageIndex, int pageSize) {
		SQLiteDatabase db = null;
		Cursor cursor = null;
		DataSet<User> datas = null;
		ArrayList<User> users = null;
		User user = null;
		String[] columns = null;
		String orderBy = null;
		String limit = null;
		
		users = new ArrayList<User>();
		datas = new DataSet<User>();
		datas.setObjects(users);
		
		db = DatabaseHelper.getReadDatabase();
		columns = new String[] {User.Columns.ID, User.Columns.NAME, User.Columns.PASSWORD, User.Columns.TICKET, User.Columns.ACTIVE};
		orderBy = User.Columns.ACTIVE + " desc";
		limit = (pageIndex - 1) * pageSize + "," + pageSize;
		cursor = db.query(Constants.APOLLO_DATA_TABLE_USER, columns, null, null, null, null, orderBy, limit);
		if (cursor != null) {
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				user = new User();
				
				user.setUserId(cursor.getInt(0));
				user.setName(cursor.getString(1));
				user.setPassword(cursor.getString(2));
				user.setTicket(cursor.getString(3));
				user.setActive(cursor.getInt(4) == 1);
				
				users.add(user);
			}
			cursor.close();
		}
		
		columns = new String[] {"count(0)"};
		cursor = db.query(Constants.APOLLO_DATA_TABLE_USER, columns, null, null, null, null, null, null);
		if (cursor != null) {
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				datas.setTotalRecords(cursor.getInt(0));
			}
			cursor.close();
		}
		db.close();
		return datas;
	}

	@Override
	public User getUser(int userId) {
		SQLiteDatabase db = null;
		Cursor cursor = null;
		String[] columns = null;
		String selection = null;
		User user = null;
		
		db = DatabaseHelper.getReadDatabase();
		columns = new String[] {User.Columns.ID, User.Columns.NAME, User.Columns.PASSWORD, User.Columns.TICKET, User.Columns.ACTIVE};
		if (userId == -1) {
			selection = User.Columns.ACTIVE + "=1";
		} else {
			selection = User.Columns.ID + "=" + userId;
		}
		
		cursor = db.query(Constants.APOLLO_DATA_TABLE_USER, columns, selection, null, null, null, null);
		if (cursor != null) {
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {			
				user = new User();
				user.setUserId(cursor.getInt(0));
				user.setName(cursor.getString(1));
				user.setPassword(cursor.getString(2));
				user.setTicket(cursor.getString(3));
				user.setActive(cursor.getInt(4) == 1);
			}
			cursor.close();
		}
		db.close();
		return user;
	}

	@Override
	public int add(User user) {
		SQLiteDatabase db = null;
		ContentValues values = null;
		int result = -1;
		
		values = new ContentValues(14);
		values.put(User.Columns.ID, user.getUserId());
		values.put(User.Columns.NAME, user.getName());
		values.put(User.Columns.PASSWORD, user.getPassword());
		values.put(User.Columns.TICKET, user.getTicket());
		values.put(User.Columns.ACTIVE, 0);
		
		db = DatabaseHelper.getWriteDatabase();
		result = (int)db.insert(Constants.APOLLO_DATA_TABLE_USER, null, values);
		db.close();
		return result;
	}

	@Override
	public int update(User user) {
		SQLiteDatabase db = null;
		ContentValues values = null;
		String whereClause = null;
		String[] whereArgs = null;
		
		values = new ContentValues(4);
		values.put(User.Columns.NAME, user.getName());
		values.put(User.Columns.PASSWORD, user.getPassword());
		values.put(User.Columns.TICKET, user.getTicket());
		values.put(User.Columns.ACTIVE, user.isActive());
		
		whereClause = User.Columns.ID + "=?";
		whereArgs = new String[]{Integer.toString(user.getUserId())};
		
		db = DatabaseHelper.getWriteDatabase();
		db.update(Constants.APOLLO_DATA_TABLE_USER, values, whereClause, whereArgs);
		db.close();
		return 1;
	}

	@Override
	public int delete(int userId) {
		SQLiteDatabase db = null;
		String whereClause = null;
		String[] whereArgs = null;
		
		whereClause = User.Columns.ID + "=?";
		whereArgs = new String[]{Integer.toString(userId)};
		db = DatabaseHelper.getWriteDatabase();
		db.delete(Constants.APOLLO_DATA_TABLE_USER, whereClause, whereArgs);
		db.close();
		return 1;
	}

	@Override
	public DataSet<User> getFriends(User user, int pageIndex, int pageSize) {
		throw new UnsupportedOperationException("unsupported getFriends");
	}

	@Override
	public User getUserProfile(User user) {
		throw new UnsupportedOperationException("unsupported getUserProfile");
	}

	@Override
	public boolean removeFriend(User user, int friendUserId) {
		throw new UnsupportedOperationException("unsupported removeFriend");
	}

	@Override
	public int getUserId(String name) {
		// TODO Auto-generated method stub
		return 0;
	}
}
