package apollo.data.networkdal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import apollo.data.R;
import apollo.data.idal.IPrivateMessageDataProvider;
import apollo.data.model.PrivateMessage;
import apollo.data.model.User;
import apollo.enums.PrivateMessageType;
import apollo.exceptions.ApplicationException;
import apollo.exceptions.SystemException;
import apollo.net.RequestMethod;
import apollo.net.WebRequest;
import apollo.net.WebResponse;
import apollo.util.DataSet;
import apollo.util.DateTime;

public class PrivateMessageDataProvider extends DataProvider implements IPrivateMessageDataProvider {

	@Override
	public void add(PrivateMessage pm, User user) {
		HashMap<String, String> params = null;
		String body = null;
		String url = null;

		url = "http://www.tianya.cn/api/msg?method=messageuser.insertmessage";
		params = new HashMap<String, String>();

		params.put("params.content", pm.getBody());	
		params.put("params.receiveUserName", pm.getRecipientor().getName());
		params.put("params.sourceName", "msg");
		
		body = super.getContent(url, RequestMethod.POST, params, user);
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
			throw new ApplicationException(R.string.error_post_msg_fault, message);
		}
	}

	@Override
	public void delete(PrivateMessage pm, User user) {
	}

	@Override
	public DataSet<PrivateMessage> getPrivateMessages(User user, PrivateMessageType type, boolean unreadOnly, int pageIndex, int pageSize) {
		DataSet<PrivateMessage> datas = null;
		List<PrivateMessage> msgs = null;
		PrivateMessage msg = null;
		HashMap<String, String> params = null;
		String body = null;
		String url = null;

		url = "http://www.tianya.cn/api/msg";
		params = new HashMap<String, String>();

		if (type == PrivateMessageType.NORMAL) {
			params.put("method", "messageuser.selectmessage");
		} else {
			params.put("method", "messagesys.selectmessage");
		}		
		params.put("params.pageNo", Integer.toString(pageIndex));
		params.put("params.pageSize", Integer.toString(pageSize));
		
		body = super.getContent(url, RequestMethod.POST, params, user);
		
		datas = new DataSet<PrivateMessage>();
		// parse json
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
			
			json = json.getJSONObject("data");
			datas.setTotalRecords(json.getInt("total"));
			msgs = new ArrayList<PrivateMessage>();
			datas.setObjects(msgs);
			
			jarr = json.getJSONArray("list");
			for(int idx=0; idx<jarr.length(); idx++) {
				json = jarr.getJSONObject(idx);
				msg = new PrivateMessage();
				
				msg.setBody(json.getString("content"));
				msg.setGUID(json.getString("id"));
				msg.setPostDate(DateTime.parse(json.getString("createDate")));
				
				msg.setAuthor(new User());
				msg.getAuthor().setUserId(json.getInt("fromUserId"));
				msg.getAuthor().setName(json.getString("fromUserName"));
				
				msg.setRecipientor(new User());
				msg.getRecipientor().setUserId(json.getInt("toUserId"));
				msg.getRecipientor().setName(json.getString("toUserName"));
				msgs.add(msg);
			}
		} catch (JSONException ex) {
			throw new SystemException(ex.getMessage());
		}
		return datas;
	}

}
