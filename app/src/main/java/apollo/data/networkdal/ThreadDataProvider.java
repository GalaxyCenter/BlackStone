package apollo.data.networkdal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import apollo.app.R;
import apollo.bll.Threads;
import apollo.data.idal.IThreadDataProvider;
import apollo.data.model.Section;
import apollo.data.model.Thread;
import apollo.data.model.User;
import apollo.enums.PostMode;
import apollo.enums.SectionType;
import apollo.enums.SortBy;
import apollo.exceptions.ApplicationException;
import apollo.exceptions.SystemException;
import apollo.net.RequestMethod;
import apollo.util.DateTime;
import apollo.util.TianyaUrlHelp;
import apollo.util.Transforms;

public class ThreadDataProvider extends DataProvider implements IThreadDataProvider {

	private List<Thread> parse(String sectionId, String content) {
		Thread thread = null;
		Section section = null;
		User author = null;
		ArrayList<Thread> threads = null;
		int idx = 0;
		//String url = null;
		Pattern pattern = null;
		Matcher matcher = null;
		Matcher sub_matcher = null;
		String td_cnt = null;
		
		pattern = Pattern.compile("(?s)[\\<]td.*?[\\>](.*?)</td>");
		matcher = pattern.matcher(content);
		threads = new ArrayList<Thread>(80);
		while (matcher.find()) {
			td_cnt = matcher.group(1);
			
			if (idx % 5 == 0) {
				thread = new Thread();
				section = new Section();
				
				section.setSectionId(sectionId);
				thread.setSection(section);
				pattern = Pattern.compile("(?s)<a\\s.*?href=\"([^\"]+)\"[^>]*>(.*?)</a>");
				sub_matcher = pattern.matcher(td_cnt);
				if (sub_matcher.find()) {	
					String subject = null;
					thread.setUrl(sub_matcher.group(1));
					thread.setHasImage(td_cnt.indexOf("art-ico") > 0);
					
					subject = Transforms.stripHtmlXmlTags(sub_matcher.group(2).replaceAll("\t", ""));
					subject = subject.replaceAll("\r\n", "").replaceAll("\t", "");
					thread.setSubject(subject);
				}
				
				pattern = Pattern.compile("post-" + sectionId + "-(.*?)-1");
				sub_matcher = pattern.matcher(thread.getUrl());
				if (sub_matcher.find()) {		
					thread.setThreadId(Integer.parseInt(sub_matcher.group(1)));
				}
			} else if (idx % 5 == 1) {
				author = new User();

				pattern = Pattern.compile("<a\\s.*?href=\"http://www.tianya.cn/([^\"]+)\"[^>]*>(.*?)</a>");
				sub_matcher = pattern.matcher(td_cnt);
				if (sub_matcher.find()) {	
					author.setUserId(Integer.parseInt(sub_matcher.group(1)));
					author.setName(sub_matcher.group(2));
				}
				author.setApproved(td_cnt.indexOf("park_starPark") > 0);
				thread.setAuthor(author);
			} else if (idx % 5 == 2) {
				thread.setViews(Integer.parseInt(td_cnt));
			} else if (idx % 5 == 3) {
				thread.setReplies(Integer.parseInt(td_cnt));
			} else if (idx % 5 == 4) {
				td_cnt = matcher.group(0);
				pattern = Pattern.compile("<td\\s.*?title=\"([^\"]+)\"[^>]*>.*?</td>");
				sub_matcher = pattern.matcher(td_cnt);
				if (sub_matcher.find()) {	
					thread.setUpdateDate(DateTime.parse(sub_matcher.group(1), "yyyy-MM-dd HH:mm"));
					threads.add(thread);
				}
			}
			idx ++;
		}
		return threads;
	}
	
	@Override
	public List<Thread> search(String sectionId, String searchTerms, int pageIndex) {
		String content = null;
		String url = null;

		url = "http://bbs.tianya.cn/list.jsp?order=1&item=" + sectionId + "&nextid=" + pageIndex + "&k=" + searchTerms;
		content = super.getContent(url);
		return parse(sectionId, content);
	}
	
	@Override
	public List<Thread> getThreads(String sectionId, SortBy sortBy, DateTime postsOlderThan) {
		String content = null;
		String url = null;
		
		switch(sortBy) {
		case LAST_POST:
			url = "http://bbs.tianya.cn/list.jsp?item=" + sectionId + "&order=1&nextid=" + postsOlderThan.getTime();
			break;
		case LAST_REPLY:
			url = "http://bbs.tianya.cn/list.jsp?item=" + sectionId + "&nextid=" + postsOlderThan.getTime();
			break;
		default:
			url = "http://bbs.tianya.cn/list.jsp?item=" + sectionId + "&order=1&grade=" + sortBy + "&nextid=" + postsOlderThan.getTime();
			break;
		}
		
		content = super.getContent(url);
		return parse(sectionId, content);
	}

	public List<String> getRecommends() {
		ArrayList<String> cts = null;
		String content = null;
		String url = "http://bbs.tianya.cn/";
		Pattern pattern = null;
		Matcher matcher = null;
		
		content = super.getContent(url);
		pattern = Pattern.compile("(?s)<ul class=\"bbs-list curr\".*?>(.*?)</ul>");
		matcher = pattern.matcher(content);
		cts = new ArrayList<String>(4);
		while (matcher.find()) {
			cts.add(matcher.group(1));
		}
		pattern = Pattern.compile("(?s)<ul class=\"bbs-list bbs-list2 curr\".*?>(.*?)</ul>");
		matcher = pattern.matcher(content);
		while (matcher.find()) {
			cts.add(matcher.group(1));
		}
		return cts;
	}

	@Override
	public List<Thread> getRecommendImageThread() {
		ArrayList<Thread> threads = null;
		Thread thread = null;
		String content = null;
		String icon_str = null;
		String icon = null;
		String url = "http://focus.tianya.cn/";
		Pattern pattern = null;
		Matcher matcher = null;
		Matcher sub_matcher = null;
		Matcher icon_matcher = null;
		int idx =0;
		content = getContent(url);
		pattern = Pattern.compile("(?s)<ul class=\"focusnew-bigpic\" id=\"focusnew-bigpic\".+?>(.*?)</ul>");
		matcher = pattern.matcher(content);
		
		threads = new ArrayList<Thread>();
		if (matcher.find()) {
			content = matcher.group(1);
		} else {
			return threads;
		}
		
		pattern = Pattern.compile("<a.+?href=\"([^\"]+)\"[^>]*>(.*?)</a>");
		sub_matcher = pattern.matcher(content);
		while (sub_matcher.find()) {
			if (idx % 2 == 0) {
				icon_str = sub_matcher.group(2);
				pattern = Pattern.compile("<[img|IMG].*?src=[\'|\"](.*?(?:[.(jpg|bmp|jpeg|gif|png)]))[\'|\"].*?[/]?>");
				icon_matcher = pattern.matcher(icon_str);
				if (icon_matcher.find()) {
					icon = icon_matcher.group(1);
				}
			} else {
				thread = TianyaUrlHelp.parseThreadUrl(sub_matcher.group(1));
				if (thread != null) {
					thread.setSubject(sub_matcher.group(2));
					thread.setIcon(icon);
					threads.add(thread);
				}
			}
			idx++;
		}
		return threads;
	}

	@Override
	public List<Thread> getThreads(User user, SectionType type, PostMode mode, int pageIndex, int pageSize) {
		List<Thread> threads = null;
		Thread t = null;
		int last_thread = Integer.MAX_VALUE;
		
		if (pageIndex != 1) {
			threads = Threads.getThreads(user, type, mode, pageIndex - 1, pageSize);
			if (threads.size() > 0) {
				last_thread = threads.get(threads.size() - 1).getThreadId();
			}
		}
		
		HashMap<String, String> params = null;
		String body = null;
		String url = null;

		url = "http://bbs.tianya.cn/api";
		params = new HashMap<String, String>();
		if (PostMode.CREATE.equals(mode)) {
			params.put("method", "bbs.ice.getUserArticleList");
		} else {
			params.put("method", "bbs.ice.getUserReplyList");
		}
		params.put("params[bMore]", "true");
		params.put("params[itemType]", type.toString());
		params.put("params[nextId]", Integer.toString(last_thread));
		params.put("params[pageSize]", Integer.toString(pageSize));
		params.put("params[userCate]", "1");
		params.put("params[userId]", Integer.toString(user.getUserId()));

		
		body = super.getContent(url, RequestMethod.GET, null, params, user);
		
		JSONObject json = null;
		JSONArray jarr = null;
		boolean isSuccess = false;
		String message;
		
		try {
			json = new JSONObject(body);
			isSuccess = "1".equals(json.getString("success"));
			message = json.getString("message");
			
			if (isSuccess == false) {
				throw new ApplicationException(R.string.error_post_fault, message);
			}
			
			json = json.getJSONObject("data");
			jarr = json.getJSONArray("rows");
			
			threads = new ArrayList<Thread>();
			for(int idx=0; idx<jarr.length(); idx++) {
				json = jarr.getJSONObject(idx);
				t = new Thread();
				
				t.setSubject(json.getString("title"));
//				t.setAuthor(new User());
//				t.getAuthor().setName(json.getString("user_name"));
				t.setAuthor(user);
				t.setSection(new Section());
				t.getSection().setSectionId(json.getString("item"));
				t.setThreadId(Integer.parseInt(json.getString("art_id")));
				t.setGUID(json.getString("id"));
				t.setUpdateDate(DateTime.parse(json.getString("reply_time")));
				
				t.setReplies(Integer.parseInt(json.getString("reply_counter")));
				t.setViews(Integer.parseInt(json.getString("click_counter")));
				threads.add(t);
			}
		} catch (JSONException ex) {
			throw new SystemException(ex.getMessage());
		}
		return threads;
	}
}
