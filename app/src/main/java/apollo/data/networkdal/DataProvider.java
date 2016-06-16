package apollo.data.networkdal;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import apollo.data.idal.IDataProvider;
import apollo.data.model.User;
import apollo.exceptions.SystemException;
import apollo.net.RequestMethod;
import apollo.net.WebRequest;
import apollo.net.WebResponse;

public class DataProvider implements IDataProvider {
 
	public String getContent(String url) {
		return getContent(url, RequestMethod.GET, null, null, null);
	}
	
	public String getContent(String url, User user) {
		return getContent(url, RequestMethod.GET, null, null, user);
	}
	
	public String getContent(String url, RequestMethod method, Map<String, String> params) {
		return getContent(url, method, params, null);
	}
	public String getContent(String url, RequestMethod method, Map<String, String> params, User user) {
		return getContent(url, method, null, params, user);
	}
	
	public String getContent(String url, RequestMethod method, Map<String, String> propertys, Map<String, String> params, User user) {
		WebRequest _req = null;
		WebResponse _resp = null;
		String body = null;
		
		_req = new WebRequest();
		_req.setResponseCharset("utf-8");
		_req.setContentCharset("utf-8");
		_req.setMethod(method);

		if (propertys == null) 
			propertys = new HashMap<String, String>();
		
		if (propertys.containsKey("Referer") == false) 
			propertys.put("Referer", "http://www.tianya.cn");
		
		if (propertys.containsKey("Accept-Language") == false) 
			propertys.put("Accept-Language", "zh-CN,en-US;q=0.8,en;q=0.6");
		
		if (propertys.containsKey("Accept-Encoding") == false) 
			propertys.put("Accept-Encoding", "gzip, deflate");
		
		if (propertys.containsKey("Cookie") == false && user != null && user.getTicket() != null) 
			propertys.put("Cookie", user.getTicket());
		
		try {
			_resp = _req.create(url, params, propertys);
			body = new String(_resp.getContent(), _resp.getContentCharset());
		} catch (IOException ex) {
			throw new SystemException(ex.getMessage());
		}
		return body;
	}
}
