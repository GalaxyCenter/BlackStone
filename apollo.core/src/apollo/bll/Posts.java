package apollo.bll;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;



import apollo.cache.AppCache;
import apollo.core.ApolloApplication;
import apollo.core.R;
import apollo.data.dalfactory.DataAccess;
import apollo.data.idal.IPostDataProvider;
import apollo.data.model.Post;
import apollo.data.model.User;
import apollo.enums.PostType;
import apollo.enums.SortBy;
import apollo.enums.SortOrder;
import apollo.exceptions.ApplicationException;
import apollo.util.DataSet;
import apollo.util.FileUtil;
import apollo.util.Regex;
import apollo.util.StringUtil;

public class Posts {
	private static IPostDataProvider remoteProvider;
	private static IPostDataProvider localProvider;
	public static String KEY_POSTS = "post:s:{0}_t:{1}_pi:{2}";
	
	static {
		remoteProvider = DataAccess.createRemotePostDataProvider();
		localProvider = DataAccess.createLocalPostDataProvider();
	}
		
	public static List<Post> getIndexOf(String sectinId, int threadId, int fromIndex, int toIndex) {
		return getIndexOf(sectinId, threadId, 0, fromIndex, toIndex);
	}
	
	public static List<Post> getIndexOf(String sectinId, int threadId, int userId, int fromIndex, int toIndex) {
		int pageIndex = 0;
		List<Post> posts = null;
		DataSet<Post> datas = null;
		
		pageIndex = (fromIndex / 100) + 1;
		datas = Posts.getPosts(sectinId, threadId, userId, pageIndex, 100);
		
		fromIndex = (fromIndex % 100);
		toIndex = (toIndex % 100);
		
		if (fromIndex > datas.getObjects().size()) 
			throw new ApplicationException(R.string.error_post_out_of_range);

		if (toIndex < fromIndex) 
			toIndex = datas.getObjects().size();

		if (toIndex > datas.getObjects().size())
			toIndex = datas.getObjects().size();
		
		posts = new ArrayList<Post>();
		posts.addAll(datas.getObjects().subList(fromIndex, toIndex));
		return posts;
	}
		
	public static DataSet<Post> getPosts(String sectinId, int threadId, int userId, int pageIndex, int pageSize) {
		return Posts.getPosts(sectinId, threadId, userId, pageIndex, pageSize, true, false);
	} 
	
	@SuppressWarnings("unchecked")
	public static DataSet<Post> getPosts(String sectionId, int threadId, int userId, int pageIndex, int pageSize, boolean cacheable, boolean flush) {
		DataSet<Post> datas = null;
		List<Post> posts = null;
		String key = null;

		key = MessageFormat.format(KEY_POSTS, sectionId, threadId, pageIndex);
		if (flush) 
			AppCache.remove(key);
		
		datas = (DataSet<Post>)AppCache.get(key);
		if (datas == null) {
			datas = remoteProvider.getPosts(sectionId, threadId, userId, pageIndex, pageSize, SortBy.LAST_REPLY, SortOrder.ASCENDING);
			if (cacheable)
				AppCache.add(key, datas);			
		}
		
		if (userId > 0) {
			Post post = null;
			DataSet<Post> new_dataset = null;
			
			new_dataset = new DataSet<Post>();
			posts = new ArrayList<Post>();
			for(int idx=0; idx<datas.getObjects().size(); idx++) {
				post = datas.getObjects().get(idx);
				if (userId == post.getAuthor().getUserId()) 
					posts.add(post);
			}
			new_dataset.setTotalRecords(datas.getTotalRecords());
			new_dataset.setObjects(posts);
			datas = new_dataset;
		}
		return datas;
	}
	
	public static void add(Post post, User user) {
		String src = null;
		String img = null;
		String tail = null;
		List<Map<String,Object>> list = null;
		Map<String,Object> map = null;
		
		src = post.getBody();
		
		list = Regex.getStartAndEndIndex(post.getBody(),
				Pattern.compile("(?s)\\[img\\](.*?)\\[/img\\]"));
		for(int idx=list.size() - 1; idx>=0; idx--){
			map = list.get(idx);
			
			img = (String) map.get("str1");
			if (FileUtil.isLocalFile(img)) 
				img = Gallerys.add(user, post.getSubject(), post.getSubject(), img).getBigImg();
			
			src = StringUtil.replace(src, (Integer)map.get("startIndex"), (Integer)map.get("endIndex"), img);
		}
		tail = ApolloApplication.app().getResources().getString(R.string.post_body_tail);
		src += tail;
		post.setBody(src);
		remoteProvider.add(post, user);
	}
	
	public static DataSet<Post> getDrafts(String sectinId,int pageIndex, int pageSize) {
		return Posts.getDrafts(sectinId, 0, pageIndex, pageSize);
	}
	
	public static DataSet<Post> getDrafts(String sectionId, int userId, int pageIndex, int pageSize) {
		return Posts.getDrafts(sectionId, 0, userId, pageIndex, pageSize);
	}
	
	public static DataSet<Post> getDrafts(String sectionId, int threadId, int userId, int pageIndex, int pageSize) {
		return Posts.getDrafts(sectionId, threadId, userId, pageIndex, pageSize, SortBy.LAST_REPLY, SortOrder.DESCENDING);
	}
	
	public static DataSet<Post> getDrafts(String sectionId, int threadId, int userId, int pageIndex, int pageSize, SortBy sortBy, SortOrder sortOrder) {
		return localProvider.getPosts(sectionId, threadId, userId, pageIndex, pageSize, sortBy, sortOrder);
	}
	
	public static Post getLastDraft(String sectionId, int threadId, int userId) {
		DataSet<Post> datas = null;
		
		datas = Posts.getDrafts(sectionId, threadId, userId, 1, 1, SortBy.LAST_REPLY, SortOrder.DESCENDING);
		if (datas.getTotalRecords() == 0)
			throw new ApplicationException(R.string.error_draft_not_found);
		else
			return datas.getObjects().get(0);
	}
	
	public static void save(Post post, User user) {
		if (post.getPostType().equals(PostType.REPLY))
			localProvider.add(post, user);
		else
			localProvider.update(post);
	}
	
	public static void removeDraft(int postId) {
		localProvider.delete(postId);
	}
}
