package apollo.bll;

import java.text.MessageFormat;

import apollo.cache.AppCache;
import apollo.data.dalfactory.DataAccess;
import apollo.data.idal.IPrivateMessageDataProvider;
import apollo.data.model.PrivateMessage;
import apollo.data.model.User;
import apollo.enums.PrivateMessageType;
import apollo.util.DataSet;

public class PrivateMessages {

	private static IPrivateMessageDataProvider remoteProvider;
	public static String KEY_MESSAGES = "msg:t{0}_r:{1}_u:{2}_pi:{3}_ps:{4}";
	
	static {
		remoteProvider = DataAccess.createRemotePrivateMessageDataProvider();
	}
	
	public static void add(PrivateMessage pm, User user) {
		remoteProvider.add(pm, user);
	}
	
	public static DataSet<PrivateMessage> getPrivateMessages(User user, PrivateMessageType type, boolean unreadOnly, int pageIndex, int pageSize) {
		return PrivateMessages.getPrivateMessages(user, type, unreadOnly, pageIndex, pageSize, true, false);
	}
	
	@SuppressWarnings("unchecked")
	public static DataSet<PrivateMessage> getPrivateMessages(User user, PrivateMessageType type, boolean unreadOnly, int pageIndex, int pageSize, boolean cacheable, boolean flush) {
		DataSet<PrivateMessage> datas = null;
		String key = null;

		key = MessageFormat.format(KEY_MESSAGES, type.getValue(), unreadOnly, user.getUserId(), pageIndex, pageSize);
		if (flush) {
			AppCache.remove(key);
		}
		datas = (DataSet<PrivateMessage>)AppCache.get(key);
		if (datas == null) {
			datas = remoteProvider.getPrivateMessages(user, type, unreadOnly, pageIndex, pageSize);
			if (cacheable) {
				AppCache.add(key, datas);
			}
		}
		return datas;
	}
}
