package apollo.data.sqlitedal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import apollo.core.ApolloApplication;
import apollo.data.model.AutoPost;
import apollo.data.model.Config;
import apollo.data.model.Constants;
import apollo.data.model.Post;
import apollo.data.model.Section;
import apollo.data.model.User;

public class DatabaseHelper extends SQLiteOpenHelper {
 
	public DatabaseHelper(Context context) {
		super(context, Constants.APOLLO_DATABASE_NAME, null, 1);
	}
	
	public static SQLiteDatabase getWriteDatabase() {
		SQLiteDatabase db =  null;
		
		db = (new DatabaseHelper(ApolloApplication.app())).getWritableDatabase();
		return db;
	}
	
	public static SQLiteDatabase getReadDatabase() {
		SQLiteDatabase db =  null;
		
		db = (new DatabaseHelper(ApolloApplication.app())).getReadableDatabase();
		return db;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		StringBuffer buf1 = null;

		// user
		buf1 = new StringBuffer();
		buf1.append("create table if not exists ");
		buf1.append(Constants.APOLLO_DATA_TABLE_USER);
		buf1.append(" (");
		buf1.append(User.Columns.ID);
		buf1.append(" integer primary key,");
		buf1.append(User.Columns.NAME);
		buf1.append(" varchar(16) COLLATE NOCASE,");
		buf1.append(User.Columns.PASSWORD);
		buf1.append(" varchar(64),");
		buf1.append(User.Columns.TICKET);
		buf1.append(" varchar(256),");
		buf1.append(User.Columns.ACTIVE);
		buf1.append(" bit default 0)");
		
		db.execSQL(buf1.toString());
		
		// section
		buf1 = new StringBuffer();
		buf1.append("create table if not exists ");
		buf1.append(Constants.APOLLO_DATA_TABLE_SECTION);
		buf1.append(" (");
		buf1.append(Section.Columns.ID);
		buf1.append(" varchar(8) primary key,");
		buf1.append(Section.Columns.NAME);
		buf1.append(" varchar(16))");

		db.execSQL(buf1.toString());
		
		// post
		buf1 = new StringBuffer();
		buf1.append("create table if not exists ");
		buf1.append(Constants.APOLLO_DATA_TABLE_POST);
		buf1.append(" (");
		buf1.append(Post.Columns.ID);
		buf1.append(" integer primary key autoincrement,");
		buf1.append(Post.Columns.SECTION_ID);
		buf1.append(" varchar(8),");
		buf1.append(Post.Columns.THREAD_ID);
		buf1.append(" integer,");
		buf1.append(Post.Columns.USER_ID);
		buf1.append(" integer,");
		buf1.append(Post.Columns.SUBJECT);
		buf1.append(" varchar(128),");
		buf1.append(Post.Columns.BODY);
		buf1.append(" text,");
		buf1.append(Post.Columns.POST_DATE);
		buf1.append(" integer,");
		buf1.append(Post.Columns.TYPE);
		buf1.append(" integer)");
		
		db.execSQL(buf1.toString());
		
		// bookmark
		buf1 = new StringBuffer();
		buf1.append("create table if not exists ");
		buf1.append(Constants.APOLLO_DATA_TABLE_BOOKMARK);
		buf1.append(" (");		
		buf1.append(Post.Columns.USER_ID);
		buf1.append(" integer,");
		buf1.append(Post.Columns.SECTION_ID);
		buf1.append(" varchar(8),");
		buf1.append(Post.Columns.THREAD_ID);
		buf1.append(" integer key,");
		buf1.append(Post.Columns.FLOOR);
		buf1.append(" integer,PRIMARY KEY (");
		buf1.append(Post.Columns.SECTION_ID);
		buf1.append(",");
		buf1.append(Post.Columns.THREAD_ID);
		buf1.append(",");
		buf1.append(Post.Columns.USER_ID);
		buf1.append("))");
		
		db.execSQL(buf1.toString());
		
		// config
		buf1 = new StringBuffer();
		buf1.append("create table if not exists ");
		buf1.append(Constants.APOLLO_DATA_TABLE_CONFIG);
		buf1.append(" (");
		buf1.append(Config.Columns.ID);
		buf1.append(" integer primary key autoincrement,");
		buf1.append(Config.Columns.REMIND);
		buf1.append(" integer,");
		buf1.append(Config.Columns.FONT_SIZE);
		buf1.append(" integer,");
		buf1.append(Config.Columns.REMIND_ENABLED);
		buf1.append(" integer,");
		buf1.append(Config.Columns.SHOW_IMAGE);
		buf1.append(" integer,");
		buf1.append(Config.Columns.SHOW_HEAD);
		buf1.append(" integer,");
		buf1.append(Config.Columns.SOUND_ENABLED);
		buf1.append(" integer,");
		buf1.append(Config.Columns.VIBRATE_ENABLED);
		buf1.append(" integer)");
		
		db.execSQL(buf1.toString());
		
		buf1 = new StringBuffer("insert into ");
		buf1.append(Constants.APOLLO_DATA_TABLE_CONFIG);
		buf1.append(" (");
		buf1.append(Config.Columns.ID);
		buf1.append(",");
		buf1.append(Config.Columns.REMIND);
		buf1.append(" ,");
		buf1.append(Config.Columns.FONT_SIZE);
		buf1.append(" ,");
		buf1.append(Config.Columns.REMIND_ENABLED);
		buf1.append(" ,");
		buf1.append(Config.Columns.SHOW_IMAGE);
		buf1.append(" ,");
		buf1.append(Config.Columns.SHOW_HEAD);
		buf1.append(" ,");
		buf1.append(Config.Columns.SOUND_ENABLED);
		buf1.append(" ,");
		buf1.append(Config.Columns.VIBRATE_ENABLED);
		buf1.append(" ) VALUES(0,1,14,1,1,1,1,1)");
		
		db.execSQL(buf1.toString());

		// autopost config
		buf1 = new StringBuffer();
		buf1.append("create table if not exists ");
		buf1.append(Constants.APOLLO_DATA_TABLE_AUTOPOST);
		buf1.append(" (");		
		buf1.append(Config.Columns.ID);
		buf1.append(" integer primary key autoincrement,");
		buf1.append(AutoPost.Columns.SECTION_ID);
		buf1.append(" varchar(8),");
		buf1.append(AutoPost.Columns.THREAD_ID);
		buf1.append(" integer,");
		buf1.append(AutoPost.Columns.THREAD_SUBJECT);
		buf1.append(" varchar(128),");
		buf1.append(AutoPost.Columns.FLOOR_NUM);
		buf1.append(" integer,");
		buf1.append(AutoPost.Columns.ACCOUNTS);
		buf1.append(" text,");
		buf1.append(AutoPost.Columns.POST_BODY);
		buf1.append(" text,");
		buf1.append(AutoPost.Columns.START_TIME);
		buf1.append(" varchar(20),");
		buf1.append(AutoPost.Columns.END_TIME);
		buf1.append(" varchar(20))");
		
		db.execSQL(buf1.toString());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

}
