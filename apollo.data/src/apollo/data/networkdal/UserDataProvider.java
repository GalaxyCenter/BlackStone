package apollo.data.networkdal;

import android.text.TextUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;







import apollo.data.R;
import apollo.data.idal.IUserDataProvider;
import apollo.data.model.User;
import apollo.exceptions.ApplicationException;
import apollo.exceptions.SystemException;
import apollo.net.Cookie;
import apollo.net.RequestMethod;
import apollo.net.WebRequest;
import apollo.net.WebResponse;
import apollo.util.DataSet;
import apollo.util.DateTime;
import apollo.util.Encoding;
import apollo.util.Transforms;

public class UserDataProvider extends DataProvider implements IUserDataProvider {

	@Override
	public Cookie[] validUser(String name, String password, String vcode) {
//		Map<String, String> params = null;
//		Map<String, String> propertys = null;
//		String body = null;
//		Pattern pattern = null;
//		Matcher matcher = null;
//		ArrayList<String> infos = null;
//		Cookie[] cookies = null;
//		String temp = null;
//		WebRequest _req = null;
//		WebResponse _resp = null;
//		int start_pos;
//		int end_pos;
//	
//		_req = new WebRequest();
//		_req.setResponseCharset("utf-8");
//		_req.setContentCharset("utf-8");
//		_req.setMethod(RequestMethod.POST);
//		
//		propertys = new HashMap<String, String>();
//		propertys.put("Referer", "http://www.tianya.cn");
//		
//		params = new HashMap<String, String>();
//		params.put("vwriter", name);
//		params.put("vpassword", password);
//		params.put("method", "name");
//		params.put("rmflag", "1");
//
//		try {
//			_resp = _req.create("http://passport.tianya.cn/login", params, propertys);
//			body = new String(_resp.getContent(), _resp.getContentEncoding());
//		} catch (IOException ex) {
//			throw new SystemException(ex.getMessage());
//		}
//		
//		pattern = Pattern.compile("document.cookie='(.*)'");
//		matcher = pattern.matcher(body);
//		
//		infos = new ArrayList<String>();
//		while(matcher.find()) 
//			infos.add(matcher.group(1));
//		
//		if (infos.size() < 5) 
//			throw new ApplicationException(R.string.error_login_fault);
//		
//		cookies = new Cookie[3];
//		// 解析sso
//		String cookie = _resp.getHeaderField("Set-Cookie");
//		if (TextUtils.isEmpty(cookie)) 
//			throw new ApplicationException(R.string.error_login_fault);
//
//		temp = cookie.split(";")[0];
//		start_pos = temp.indexOf('=');
//		end_pos = temp.length();
//		cookies[0] = new Cookie("sso", temp.substring(start_pos+1, end_pos));
//		
//		// 解析temp
//		temp = infos.get(2);
//		start_pos = temp.indexOf('=');
//		end_pos = temp.indexOf(';', start_pos+1);
//		cookies[1] = new Cookie("temp", temp.substring(start_pos+1, end_pos));
//	
//		// 解析user
//		temp = infos.get(0);
//		start_pos = temp.indexOf('=');
//		end_pos = temp.indexOf(';', start_pos+1);
//	
//		cookies[2] = new Cookie("user", temp.substring(start_pos+1, end_pos));
//		
//		return cookies;
		
		WebRequest _req = null;
		WebResponse _resp = null;
		HashMap<String, String> params = null;
		HashMap<String, String> propertys = null;
		String body = null;
		Pattern pattern = null;
		Matcher matcher = null;
		List<String> infos = null;
		Cookie[] cookies = null;
		String url = null;
		
		_req = new WebRequest();
		_req.setResponseCharset("utf-8");
		_req.setContentCharset("utf-8");
		_req.setMethod(RequestMethod.POST);
		
		params = new HashMap<String, String>();
		params.put("vwriter", name);
		params.put("vpassword", password);

		propertys = new HashMap<String, String>();
		propertys.put("Referer", "http://www.tianya.cn");
		url = "http://passport.tianya.cn/login";
		try {
			_resp = _req.create(url, params, propertys);
			body = new String(_resp.getContent(), _resp.getContentCharset());
		} catch (Exception ex) {
			throw new SystemException(ex.getMessage());
		}
		
		// 提取登陆后获得的cachekey
		String querys = null;
		pattern = Pattern.compile("&t=(.*)");
		matcher = pattern.matcher(body);
		if(matcher.find()) {
			querys = matcher.group();
			querys = querys.substring(1, querys.length() - 2);
		} else {
			throw new ApplicationException(R.string.error_login_fault);//throw new ApplicationException(ApplicationExceptionType.CustomError,  name +" Login failed, Invalid username or password.");
		}
		
		// 发送请求到 http://passport.tianya.cn/online/domain.jsp?cacheKey=d7066e991a0a85d2ace9e376a09105c7&domain=tianya.cn 获得cookies
		url = "http://passport.tianya.cn/online/domain.jsp?" + querys + "&domain=tianya.cn";
		_req.setMethod(RequestMethod.GET);
		propertys = new HashMap<String, String>();
		propertys.put("Referer", "http://passport.tianya.cn/online/loginSuccess.jsp?fowardurl=http%3A%2F%2Fwww.tianya.cn%2F1749397&userthird=index&regOrlogin=%E7%99%BB%E5%BD%95%E4%B8%AD......=" + querys);
		try {
			_resp = _req.create(url, null, propertys);
			infos = _resp.getHeaderFields("Set-Cookie");
		} catch (Exception ex) {
			throw new SystemException(ex.getMessage());
		}
		
		if (infos == null)
			throw new ApplicationException(R.string.error_login_fault);
		
		cookies = new Cookie[infos.size()];
		for(int idx=0; idx<infos.size(); idx++) {
			String temp = infos.get(idx);
			int i1 = temp.indexOf("=");
			int i2 = temp.indexOf(";");
			if (i1 != -1 && i2 != -1) {
				String _value = temp.substring(i1 + 1, i2);
				String _key = temp.substring(0, i1);
				cookies[idx] = new Cookie(_key, _value);
			}
		}
		return cookies;
	}

	@Override
	public DataSet<User> getUsers(int pageIndex, int pageSize) {
		throw new UnsupportedOperationException("unsupported getUsers");
	}

	@Override
	public User getUser(int userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int add(User user) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int update(User user) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int delete(int userId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public DataSet<User> getFriends(User user, int pageIndex, int pageSize) {
		HashMap<String, String> params = null;
		String body = null;
		String url = null;

		url = "http://www.tianya.cn/api/tw";
		params = new HashMap<String, String>();

		params.put("method", "friend.ice.selectbygroup");	
		params.put("params.pageNo", Integer.toString(pageIndex));
		params.put("params.pageSize", Integer.toString(pageSize));
		
		body = super.getContent(url, RequestMethod.GET, params, user);
		
		//  解析数据
		DataSet<User> datas = null;
		List<User> users = null;
		User friend = null;
		String message = null;
		JSONObject json = null;
		JSONArray jarr = null;
		boolean isSuccess = false;
		
		datas = new DataSet<User>();
		try {
			json = new JSONObject(body);
			isSuccess = "1".equals(json.getString("success"));
			message = json.getString("message");
			
			if (isSuccess == false) {
				throw new ApplicationException(R.string.error_post_fault, message);
			}
			
			json = json.getJSONObject("data");
			datas.setTotalRecords(json.getInt("total"));
			users = new ArrayList<User>();
			datas.setObjects(users);
			
			jarr = json.getJSONArray("user");
			for(int idx=0; idx<jarr.length(); idx++) {
				json = jarr.getJSONObject(idx);
				friend = new User();
				
				friend.setName(json.getString("name"));
				friend.setUserId(json.getInt("id"));
				users.add(friend);
			}
		} catch (JSONException ex) {
			throw new SystemException(ex.getMessage());
		}
		return datas;
	}
	
	public boolean removeFriend(User user, int friendUserId) {
		HashMap<String, String> params = null;
		String body = null;
		String url = null;

		url = "http://www.tianya.cn/api/tw";
		params = new HashMap<String, String>();

		params.put("method", "friend.ice.delete");	
		params.put("params.friendUserId", Integer.toString(friendUserId));
		
		body = super.getContent(url, RequestMethod.GET, params, user);
		
		// 解析数据
		String message = null;
		JSONObject json = null;
		JSONArray jarr = null;
		boolean isSuccess = false;
		
		try {
			json = new JSONObject(body);
			isSuccess = "1".equals(json.getString("success"));
			message = json.getString("message");
			
			if (isSuccess == false) {
				throw new ApplicationException(R.string.error_post_fault, message);
			}
		} catch (JSONException ex) {
			throw new SystemException(ex.getMessage());
		}
		return isSuccess;
	}

	@Override
	public User getUserProfile(User user) {
		String body = null;
		String url = null;
		Pattern pattern = null;
		Matcher matcher = null;
		
		if (user.getUserId() == 0)
			user.setUserId(getUserId(user.getName()));
		
		url = "http://www.tianya.cn/" + user.getUserId();
		body = super.getContent(url);
		
		pattern = Pattern.compile("(?s)<div class=\"profile\">(.*?)</div>");
		matcher = pattern.matcher(body);
		if (matcher.find()) {
			String intro = Transforms.stripHtmlXmlTags(matcher.group(1));
			intro = intro.trim();
			user.setInstroduce(intro);
		}
		
		pattern = Pattern.compile("<p>上次登录：(.*?)</p>");
		matcher = pattern.matcher(body);
		if (matcher.find()) {
			user.setLastLogin(DateTime.parse(matcher.group(1)));
		}
		
		pattern = Pattern.compile("<p>注册日期：(.*?)</p>");
		matcher = pattern.matcher(body);
		if (matcher.find()) {
			user.setLastReg(DateTime.parse(matcher.group(1)));
		}
				
		pattern = Pattern.compile("主贴(\\w+)");
		matcher = pattern.matcher(body);
		if (matcher.find()) {
			user.setPosts(Integer.parseInt(matcher.group(1)));
		}
		
		pattern = Pattern.compile("female");
		matcher = pattern.matcher(body);
		if (matcher.find()) {
			user.setGender(false);
		} else {
			user.setGender(true);
		}
		return user;
	}

	@Override
	public int getUserId(String name) {
		WebRequest _req = null;
		WebResponse _resp = null;
		Map<String, String> propertys;
		String body = null;
		String url = null;
		
		_req = new WebRequest();
		_req.setResponseCharset("utf-8");
		_req.setContentCharset("utf-8");
		_req.setMethod(RequestMethod.GET);

		propertys = new HashMap<String, String>();
		propertys.put("Referer", "http://www.tianya.cn");
		
		url = "http://my.tianya.cn/info/" + Encoding.urlEncode(name);
		
		try {
			_resp = _req.create(url, null, propertys);
			body = new String(_resp.getContent(), _resp.getContentCharset());
		} catch (IOException ex) {
			throw new SystemException(ex.getMessage());
		}
		body = _resp.getUrlString();
		if (body.length() > 21)
			body = body.substring(21);
		return Integer.parseInt(body);
	}

	@Override
	public User getUserByName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

}
