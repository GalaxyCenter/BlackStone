package apollo.data.idal;

import apollo.data.model.User;
import apollo.net.Cookie;
import apollo.util.DataSet;

public interface IUserDataProvider {

	Cookie[] validUser(String name, String password, String vcode);
	
	DataSet<User> getUsers(int pageIndex, int pageSize);
	
	DataSet<User> getFriends(User user, int pageIndex, int pageSize);
	
	User getUser(int userId);
	
	User getUserProfile(User user);
	
	int add(User user);
	
	int update(User user);
	
	int delete(int userId);
	
	int getUserId(String name);
	
	boolean removeFriend(User user, int friendUserId);
}
