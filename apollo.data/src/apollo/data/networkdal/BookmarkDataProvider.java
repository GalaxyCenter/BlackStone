package apollo.data.networkdal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import apollo.data.R;
import apollo.data.idal.IBookmarkDataProvider;
import apollo.data.model.Thread;
import apollo.data.model.Section;
import apollo.data.model.User;
import apollo.exceptions.ApplicationException;
import apollo.exceptions.SystemException;
import apollo.net.RequestMethod;
import apollo.util.DataSet;
import apollo.util.DateTime;

public class BookmarkDataProvider extends DataProvider implements
		IBookmarkDataProvider {

	@Override
	public void add(Thread thread, User user) {
		HashMap<String, String> params = null;
		String body = null;
		String url = null;

		url = "http://www.tianya.cn/api/tw";
		params = new HashMap<String, String>();
		params.put("method", "bbsArticleMark.insert");
		params.put("params.blockId", thread.getSection().getSectionId());
		params.put("params.articleId", Integer.toString(thread.getThreadId()));
		params.put("params.title", thread.getSubject());
		params.put("params.authorId", Integer.toString(thread.getAuthor().getUserId()));
		params.put("params.authorName", thread.getAuthor().getName());
		params.put("params.markResId", Integer.toString(thread.getThreadId()));
		params.put("params.markFloorId", Integer.toString(thread.getFloor()));
		params.put("params.grade", "0");
		
		body = super.getContent(url, RequestMethod.GET, null, params, user);
				
		// parse json
		String message = null;
		JSONObject json = null;
		boolean isSuccess = false;
		try {
			json = new JSONObject(body);
			isSuccess = "1".equals(json.getString("success"));
			message = json.getString("message");
		} catch (JSONException ex) {
			throw new SystemException(ex.getMessage());
		}
		if (isSuccess == false) {
			throw new ApplicationException(R.string.error_post_fault, message);
		}
	}

	@Override
	public void delete(Thread thread, User user) {
		HashMap<String, String> params = null;
		String body = null;
		String url = null;

		url = "http://www.tianya.cn/api/tw";
		params = new HashMap<String, String>();
		params.put("method", "bbsArticleMark.delete");
		params.put("params.blockId", thread.getSection().getSectionId());
		params.put("params.articleId", Integer.toString(thread.getThreadId()));
		
		body = super.getContent(url, RequestMethod.GET, null, params, user);
		
		// parse json
		String message = null;
		JSONObject json = null;
		boolean isSuccess = false;
		try {
			json = new JSONObject(body);
			isSuccess = "1".equals(json.getString("success"));
			message = json.getString("message");
		} catch (JSONException ex) {
			throw new SystemException(ex.getMessage());
		}
		if (isSuccess == false) {
			throw new ApplicationException(R.string.error_post_fault, message);
		}
	}

	@Override
	public DataSet<Thread> getThreads(User user, int pageIndex, int pageSize) {
		HashMap<String, String> params = null;
		String body = null;
		String url = null;

		url = "http://www.tianya.cn/api/tw";
		params = new HashMap<String, String>();
		params.put("method", "bbsArticleMark.select");	
		params.put("params.pageNo", Integer.toString(pageIndex));
		params.put("params.pageSize", Integer.toString(pageSize));
		
		body = super.getContent(url, RequestMethod.GET, null, params, user);
		
		// parse json
		String message;
		JSONObject json = null;
		JSONArray jarr = null;
		DataSet<Thread> datas =null;
		List<Thread> threads = null;
		Thread t = null;
		boolean isSuccess = false;
		threads = new ArrayList<Thread>();
		datas = new DataSet<Thread>();
		datas.setObjects(threads);
		
		try {
			json = new JSONObject(body);
			isSuccess = "1".equals(json.getString("success"));
			message = json.getString("message");
			
			if (isSuccess == false) {
				throw new ApplicationException(R.string.error_post_fault, message);
			}
			
			json = json.getJSONObject("data");
			if (json.length() == 0) 
				return datas;
			
			datas.setTotalRecords(json.getInt("total"));
			jarr = json.getJSONArray("list");
			for(int idx=0; idx<jarr.length(); idx++) {
				json = jarr.getJSONObject(idx);
				t = new Thread();
				
				t.setSubject(json.getString("title"));
				t.setAuthor(new User());
				t.getAuthor().setUserId(json.getInt("authorId"));
				t.getAuthor().setName(json.getString("authorName"));
				t.setSection(new Section());
				t.getSection().setSectionId(json.getString("blockId"));
				t.setThreadId(json.getInt("articleId"));
				t.setFloor(json.getInt("markFloorId"));
				t.setUpdateDate(new DateTime(json.getLong("updateTime")));
				threads.add(t);
			}
		} catch (JSONException ex) {
			throw new SystemException(ex.getMessage());
		}
		return datas;
	}

	@Override
	public List<Section> getSections(User user) {
		String body;
		String url;
		
		url = "http://www.tianya.cn/api/tw?method=userblock.ice.selectItems";
		body = super.getContent(url, user);
		
		// parse json
		JSONObject json = null;
		JSONArray jarr = null;
		List<Section> sections = null;
		Section s = null;
		boolean isSuccess = false;
		sections = new ArrayList<Section>();

		try {
			json = new JSONObject(body);
			isSuccess = "1".equals(json.getString("success"));

			if (isSuccess == false) {
				String message = json.getString("message");
				throw new ApplicationException(R.string.error_post_fault,message);
			}

			json = json.getJSONObject("data");
			jarr = json.getJSONArray("items");
			for (int idx = 0; idx < jarr.length(); idx++) {
				json = jarr.getJSONObject(idx);
				
				s = new Section();
				s.setName(json.getString("name"));
				s.setSectionId(json.getString("id"));
				sections.add(s);
			}
		} catch (JSONException ex) {
			throw new SystemException(ex.getMessage());
		}
		return sections;
	}

	@Override
	public int getPosition(Thread thread, User user) {
		// TODO Auto-generated method stub
		return 1;
	}

}
