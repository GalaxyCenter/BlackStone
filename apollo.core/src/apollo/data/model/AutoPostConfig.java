package apollo.data.model;

import java.util.Date;
import java.util.List;

import android.provider.BaseColumns;
import apollo.util.DateTime;

public class AutoPostConfig {

	public static class Columns implements BaseColumns {
		public static final String ID = "_id";
		public static final String SECTION_ID = "section_id";
		public static final String THREAD_ID = "thread_id";
		public static final String THREAD_SUBJECT = "thread_subject";
		public static final String FLOOR_NUM = "floor_num";
		public static final String ACCOUNTS = "accounts";
		public static final String POST_BODY = "post_body";
		public static final String START_TIME = "start_time";
		public static final String END_TIME = "end_time";
	}
	
	public int id = -1;
	public int floorNum = 0;
	public boolean floorEnable = false;
	public List<User> accounts = null;
	public String postBody = null;
	public Date start = DateTime.now().getDate();
	public Date end = DateTime.now().addDays(10).getDate();
	public Thread thread = null;
}
