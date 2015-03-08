package apollo.bll;

import apollo.core.ApolloApplication;
import apollo.data.dalfactory.DataAccess;
import apollo.data.idal.IUserDataProvider;
import apollo.data.model.User;
import apollo.net.Cookie;
import apollo.util.CookieUtil;
import apollo.util.DataSet;

public class Users {
	private static IUserDataProvider remoteProvider;
	private static IUserDataProvider localProvider;
	
	static {
		remoteProvider = DataAccess.createRemoteUserDataProvider();
		localProvider = DataAccess.createLocalUserDataProvider();
	}
			
	public static String validUser(String name, String password) {
		Cookie[] cookies = null;
		String ticket = null;
		
		cookies = remoteProvider.validUser(name, password, null);
		ticket = "user=" + CookieUtil.getString(cookies, "user") + "; temp=" + CookieUtil.getString(cookies, "temp") + "; sso=" + CookieUtil.getString(cookies, "sso") + ";";
		return ticket;
	}
	
	public static DataSet<User> getUsers(int pageIndex, int pageSize) {
		DataSet<User> datas = null;
		
		datas = localProvider.getUsers(pageIndex, pageSize);
		return datas;
	}
	
	public static DataSet<User> getFriends(User user, int pageIndex, int pageSize) {
		return remoteProvider.getFriends(user, pageIndex, pageSize);
	}
	
	public static boolean removeFriend(User user, int friendUserId) {
		return remoteProvider.removeFriend(user, friendUserId);
	}
	
	public static User getUserProfile(User user) {
		return remoteProvider.getUserProfile(user);
	}
	
	public static User getActiveUser() {
		return getUser(-1);
	}
	
	public static User getUser() {
		return ApolloApplication.app().getCurrentUser();
	}
	
	public static User getUser(int userId) {
		User user = null;
		
		user = localProvider.getUser(userId);
		return user;
	}
	
	public static User getUserByName(String name) {
		User user = null;

		user = localProvider.getUserByName(name);
		return user;
	}
	
	public static void add(User user) {
		localProvider.add(user);
	}
	
	public static void update(User user) {
		localProvider.update(user);
	}
	
	public static void delete(User user) {
		localProvider.delete(user.getUserId());
	}
	
	public static int getUserId(String name) {
		return remoteProvider.getUserId(name);
	}
}
