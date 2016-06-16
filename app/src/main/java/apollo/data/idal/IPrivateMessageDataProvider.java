package apollo.data.idal;

import apollo.data.model.PrivateMessage;
import apollo.data.model.User;
import apollo.enums.PrivateMessageType;
import apollo.util.DataSet;

public interface IPrivateMessageDataProvider {
	
	void add(PrivateMessage pm, User user);
	void delete(PrivateMessage pm, User user);
	
	DataSet<PrivateMessage> getPrivateMessages(User user, PrivateMessageType type, boolean unreadOnly, int pageIndex, int pageSize);
}
