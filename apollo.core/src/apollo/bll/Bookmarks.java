package apollo.bll;

import java.text.MessageFormat;
import java.util.List;

import apollo.cache.AppCache;
import apollo.data.dalfactory.DataAccess;
import apollo.data.idal.IBookmarkDataProvider;
import apollo.data.model.Section;
import apollo.data.model.Thread;
import apollo.data.model.User;
import apollo.util.DataSet;

public class Bookmarks {

	private static IBookmarkDataProvider remoteProvider;
	private static IBookmarkDataProvider localProvider;
	public static String KEY_BOOKMARKS = "bm:u:{0}_pi:{1}_ps:{2}";
	
	static {
		remoteProvider = DataAccess.createRemoteBookmarkDataProvider();
		localProvider = DataAccess.createLocalBookmarkDataProvider();
	}
	
	public static List<Section> getSections(User user) {
		return  remoteProvider.getSections(user);
	}
	
	public static void add(Thread thread, User user) {
		remoteProvider.add(thread, user);
		
		localProvider.delete(thread, user);
		localProvider.add(thread, user);
	}
	
	public static void delete(Thread thread, User user) {
		remoteProvider.delete(thread, user);
		localProvider.delete(thread, user);
	}
	
	public static int getPosition(Thread thread, User user) {
		return localProvider.getPosition(thread, user);
	}
	
	public static DataSet<Thread> getThreads(User user, int pageIndex, int pageSize) {
		return Bookmarks.getThreads(user, pageIndex, pageSize, true, false);
	}
	
	@SuppressWarnings("unchecked")
	public static DataSet<Thread> getThreads(User user, int pageIndex, int pageSize, boolean cacheable, boolean flush) {
		String key = null;
		DataSet<Thread> datas = null;
		
		key = MessageFormat.format(KEY_BOOKMARKS, user.getUserId(), pageIndex, pageSize);
		if (flush) 
			AppCache.remove(key);
		
		datas = (DataSet<Thread>) AppCache.get(key);
		if (datas == null) {
			datas = remoteProvider.getThreads(user, pageIndex, pageSize);
			if (cacheable) 
				AppCache.add(key, datas, false);
		}
		
		return datas;
	}
}
