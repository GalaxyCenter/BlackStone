package apollo.data.networkdal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import apollo.app.R;
import apollo.data.idal.IPostDataProvider;
import apollo.data.model.Post;
import apollo.data.model.User;
import apollo.enums.SortBy;
import apollo.enums.SortOrder;
import apollo.exceptions.ApplicationException;
import apollo.exceptions.SystemException;
import apollo.net.RequestMethod;
import apollo.util.DataSet;
import apollo.util.DateTime;
import apollo.util.Encoding;
import apollo.util.MD5;
import apollo.util.StringUtil;

public class PostDataProvider extends DataProvider implements IPostDataProvider {

	@Override
	public DataSet<Post> getPosts(String sectionId, int threadId, int userId,
			int pageIndex, int pageSize, SortBy sortBy, SortOrder sortOrder) {
		String content = null;
		DataSet<Post> datas = null;
		ArrayList<Post> posts = null;
		Post p = null;
		User author = null;
		Pattern pattern = null;
		Matcher matcher = null;
		Matcher sub_matcher = null;
		String item = null;
		String url = "http://bbs.tianya.cn/post-" + sectionId + "-" + threadId + "-" + pageIndex + ".shtml";
		int floor = (pageIndex - 1) * 100;
		
		content = super.getContent(url);
		datas = new DataSet<Post>();
		posts = new ArrayList<Post>(100);
		datas.setObjects(posts);
		
		// 解析回帖信息
		pattern = Pattern.compile("(?s)<div class=\"atl-item\"[^>]*>(.*?)</div>\\s+</div>\\s+</div>");
		matcher = pattern.matcher(content);
		while (matcher.find()) {
			item = matcher.group(1);
			p = new Post();
			author = new User();
			p.setAuthor(author);
			p.setFloor(floor++);
			posts.add(p);
			
			// 解析 内容
			pattern = Pattern.compile("(?s)<div class=\"bbs-content\">(.*?)</div>");
			sub_matcher = pattern.matcher(item);
			if (sub_matcher.find()) {
				p.setBody(sub_matcher.group(1));
			}
			
			// 解析 作者
			pattern = Pattern.compile("<a\\s.*?uid=\"([^\"]+)\"[^>]*uname=\"([^\"]+)\"[^>]*>(.*?)</a>");
			sub_matcher = pattern.matcher(item);
			if (sub_matcher.find()) {
				author.setUserId(Integer.parseInt(sub_matcher.group(1)));
				author.setName(sub_matcher.group(2));
			}
			
			// 解析 回帖时间
			pattern = Pattern.compile("<a\\s.*?replytime=\"([^\"]+)\"[^>]*>(.*?)</a>");
			sub_matcher = pattern.matcher(item);
			if (sub_matcher.find()) {
				p.setPostDate(DateTime.parse(sub_matcher.group(1)));
			}
		}
		// 解析首帖信息
		//if (pageIndex == 1) {
			pattern = Pattern.compile("<div class=\"atl-menu clearfix js-bbs-act\"[^>]*js_replycount=\"(.*?)\"[^>]*js_clickcount=\"(.*?)\"[^>]*js_activityuserid=\"(.*?)\"[^>]*js_replytime=\"(.*?)\"[^>]*js_title=\"(.*?)\"[^>]*js_activityusername=\"(.*?)\"");
			matcher = pattern.matcher(content);
			if (matcher.find()) {
				p = posts.get(0);
				p.setReplies(Integer.parseInt(matcher.group(1)));
				p.setViews(Integer.parseInt(matcher.group(2)));
				p.getAuthor().setUserId(Integer.parseInt(matcher.group(3)));
				p.setPostDate(DateTime.parse(matcher.group(4)));
				p.setSubject(Encoding.urlDecode(matcher.group(5)));
				p.getAuthor().setName(Encoding.urlDecode(matcher.group(6)));
				datas.setTotalRecords(p.getReplies() + 1);
			}
			
			pattern = Pattern.compile("(?s)<div class=\"bbs-content clearfix\">(.*?)</div>");
			sub_matcher = pattern.matcher(content);
			if (sub_matcher.find()) {
				p.setBody(sub_matcher.group(1));
			}
		//}
			
		if (datas.getObjects().size() == 0)
			throw new ApplicationException(R.string.error_post_not_found);
		
		return datas;
	}
	
	private long M = 0;
	private int i = 0;
	private int V = 0;
	private String[] ad = null;
	private long ag = 0;
	private int RR = 2;
	private int T = 20;
	
	public void buidUserAction(String keyCode, int chr) {
		String b = null;
		String e = null;
	
		long f = DateTime.now().getTime();
	
		b = "focusout".equals(keyCode) ? "blur" : "focusin".equals(keyCode) ? "focus" : keyCode;
		e = b.replace("key", "").substring(0, 1);
		if (M == 0)
			M = f;
		
		if (RR>=T)
			RR = 2;
		
		ad["c".equals(e) ? 1 : "f".equals(e) ? 0 : RR++] = e+(i++) + "." + ("b".equals(e)?RR:"f".equals(e)?M:chr)+"."+(f-("c".equals(e)||"f".equals(e)?M:ag));
		ag=f;	
	}
	
	@Override
	public void add(Post post, User user) {
		HashMap<String, String> params = null;
		HashMap<String, String> propertys = null;
		String body = null;
		String url = null;
		Random rdm = null;
		String d =null;
		String g = null;
		int size = 0;
		String ticket = null;
		
		body = post.getBody();
		rdm = new Random(System.currentTimeMillis());
		size = (body.length() )* 3 + 2;
		if(size > 20)
			size = 20;
		
		ad= new String[size];
		buidUserAction("focusin", 0);
		for (int idx = 0; idx < body.length(); idx++) {
			ag -= 600 + rdm.nextInt(500);
			buidUserAction("keydown", body.charAt(idx) - 32);
			ag -= 3 + rdm.nextInt(8);
			buidUserAction("keypress", body.charAt(idx));
			ag -= 30 + rdm.nextInt(30);
			buidUserAction("keyup", body.charAt(idx) - 32);
		}
		ag -= 2000 + rdm.nextInt(1000);
		buidUserAction("focusout", 0);

		ticket = user.getTicket();
		g = ticket.substring(ticket.indexOf("&id=") + "&id=".length());
		g = g.substring(0, g.indexOf("&"));
		g = "".equals(g) ? "0" : g;
		
		V = (int)Math.ceil((body.length() - 1) * Math.random());
		
		ticket += " __u_a=v2.2."+V + ";";
		user.setTicket(ticket);

		d = StringUtil.join(ad, ",");
		d=d+"|"+MD5.crypt(d+g+V)+"|"+MD5.crypt(body.substring((int)V,(int)V+1))+"|Mozilla/5.0 (Windows NT 6.2; rv:18.0) Gecko/20100101 Firefox/18.0|v2.2";
		
		propertys = new HashMap<String, String>();
		propertys.put("Referer", post.getUrl());
		propertys.put("User-Agent", "Mozilla/5.0 (Windows NT 6.2; rv:18.0) Gecko/20100101 Firefox/18.0");
		propertys.put("Accept", "application/json, text/javascript, */*; q=0.01");
		propertys.put("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
		propertys.put("Accept-Encoding", "gzip, deflate");
		propertys.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		propertys.put("X-Requested-With", "XMLHttpRequest");
		propertys.put("Connection", "keep-alive");
		propertys.put("Pragma", "no-cache");
		propertys.put("Cache-Control", "no-cache");
		
		params = new HashMap<String, String>();
		if (post.getThreadId() == 0) {  
			params.put("params.content", post.getBody());
			params.put("params.item", post.getSection().getSectionId());
			params.put("params.title", post.getSubject());
			params.put("params.bScore", "true");
			
			url = "http://bbs.tianya.cn/api?method=bbs.ice.compose";
		} else {
			params.put("params.action", d);
			params.put("params.appBlock", post.getSection().getSectionId());
			params.put("params.appId", "bbs");
			params.put("params.artId", Integer.toString(post.getThreadId()));
			params.put("params.bScore", "true");
			params.put("params.bWeibo", "false");
			params.put("params.content", post.getBody());
			params.put("params.item", post.getSection().getSectionId());
			params.put("params.postId", Integer.toString(post.getThreadId()));
			params.put("params.prePostTime", Long.toString(DateTime.now().getTime()));
			params.put("params.preTitle", post.getSubject());
			params.put("params.preUrl", "");
			params.put("params.preUserId", "");
			params.put("params.preUserName", "");
			params.put("params.sourceName", "iTianya");
			params.put("params.title", post.getBody());
			params.put("params.appId", "3");
			
			url = "http://bbs.tianya.cn/api?method=bbs.ice.reply";
		}
		body = super.getContent(url, RequestMethod.POST, propertys, params, user);
		
		// parse json
		String message = null;
		JSONObject json = null;
		boolean isSuccess = false;
		try {
			json = new JSONObject(body);
			isSuccess = "1".equals(json.get("success"));
			message = json.getString("message");
		} catch (JSONException ex) {
			throw new SystemException(ex.getMessage());
		}
		if (isSuccess == false) {
			throw new ApplicationException(R.string.error_post_fault, message);
		}
	}

	@Override
	public void update(Post post) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(int postId) {
		// TODO Auto-generated method stub
		
	}

}
