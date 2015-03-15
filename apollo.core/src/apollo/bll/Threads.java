package apollo.bll;

import java.text.MessageFormat;
import java.util.List;

import apollo.cache.AppCache;
import apollo.data.dalfactory.DataAccess;
import apollo.data.idal.IThreadDataProvider;
import apollo.util.DateTime;
import apollo.util.Encoding;
import apollo.data.model.Thread;
import apollo.data.model.User;
import apollo.enums.PostMode;
import apollo.enums.SectionType;
import apollo.enums.SortBy;

public class Threads {
	
	private static IThreadDataProvider provider;
	
	public static String KEY_THREADS = "thread:s:{0}_s:{1}";
	public static String KEY_USER_THREADS = "thread:u:{0}_t:{1}_m:{2}_pi:{3}_ps:{4}";
	
	static {
		provider = DataAccess.createRemoteThreadDataProvider();
	}
	
	public static List<Thread> getThreads(User user, SectionType type, PostMode mode, int pageIndex, int pageSize) {
		return getThreads(user, type, mode, pageIndex, pageSize, true, false);
	}
	
	@SuppressWarnings("unchecked")
	public static List<Thread> getThreads(User user, SectionType type, PostMode mode, int pageIndex, int pageSize, boolean cacheable, boolean flush) {
		List<Thread> lists = null;
		String key = null;
		
		key = MessageFormat.format(KEY_USER_THREADS, type, mode, user.getUserId(), pageIndex, pageSize);
		if (flush) 
			AppCache.remove(key);
		
		lists = (List<Thread>) AppCache.get(key);
		if (lists == null) {
			lists = provider.getThreads(user, type, mode, pageIndex, pageSize);
			
			if (cacheable == true) 
				AppCache.add(key, lists);
		}
		return lists;
	}
	
	public static List<Thread> getThreads(String sectionId, int pageIndex, int pageSize) {
		return Threads.getThreads(sectionId, pageIndex, pageSize, SortBy.LAST_REPLY);
	}

	public static List<Thread> getThreads(String sectionId, int pageIndex, int pageSize, SortBy sortBy) {
		return Threads.getThreads(sectionId, pageIndex, pageSize, sortBy, false);
	}
	
	@SuppressWarnings("unchecked")
	public static List<Thread> getThreads(String sectionId, int pageIndex, int pageSize, SortBy sortBy, boolean flush) {
		int fromIndex = 0;
		int toIndex = 0;
		List<Thread> ori_list = null;
		String key = null;
		DateTime postsOlderThan = null;
		
		fromIndex = (pageIndex - 1) * pageSize;
		toIndex = fromIndex + pageSize;
		pageIndex = (fromIndex / 80) + 1;
		
		key = MessageFormat.format(KEY_THREADS, sectionId, sortBy);
		if (flush) 
			AppCache.remove(key);
		
		ori_list = (List<Thread>)AppCache.get(key);
		if (ori_list == null) {
			postsOlderThan = DateTime.now();
			ori_list = provider.getThreads(sectionId, sortBy, postsOlderThan);
			AppCache.add(key, ori_list);
		}
		
		if (toIndex > ori_list.size()) {
			Thread last_thread = null;
			List<Thread> new_list = null;
			
			last_thread = ori_list.get(ori_list.size() - 1);
			new_list = provider.getThreads(sectionId, sortBy, last_thread.getPostDate());
			ori_list.addAll(new_list);
		}
		
		return ori_list.subList(fromIndex, toIndex);
	}
	
	@SuppressWarnings("unchecked")
	public static List<Thread> search(String sectionId, String searchTerms, int pageIndex) {
		List<Thread> data = null;
		String key = "thread_search_" + sectionId + searchTerms + pageIndex;
		
		searchTerms = Encoding.urlEncode(searchTerms);
		data = (List<Thread>) AppCache.get(key);
		if (data == null) {
			data = provider.search(sectionId, searchTerms, pageIndex);
			AppCache.add(key, data, false);
		}
		return data;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Thread> getRecommendImageThread() {
		List<Thread> threads = null;
		String key = "threads_recommend_imgs";
		
		threads = (List<Thread>) AppCache.get(key);
		if (threads == null) {
			threads = provider.getRecommendImageThread();
			AppCache.add(key, threads);
		}
		
		return threads;
	}
	
	@SuppressWarnings("unchecked")
	public static List<String> getRecommend() {
		List<String> contents = null;
		String key = "threads_recommend";
		
		contents = (List<String>)AppCache.get(key);
		if (contents == null) {
			contents  = provider.getRecommends();
			AppCache.add(key, contents);
		}
		return contents;
 	}
}
