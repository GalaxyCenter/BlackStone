package apollo.data.model;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;
import apollo.net.Cookie;
import apollo.util.CookieUtil;
import apollo.util.DateTime;

public class User implements Parcelable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1727852856401416380L;

	public static class Columns implements BaseColumns {
		public static String ID = "_id";
		public static String NAME = "name";
		public static String PASSWORD = "password";
		public static String TICKET = "ticket";
		public static String ACTIVE = "active";
		
		//public static final String[] USER_QUERY_COLUMNS = {ID, NAME, PASSWORD, TICKET, SELECTED};
	}
	
	private int userId;
	private int forums;
	private int posts;
	private int bookmarks;
	private int photos;
	private int friends;
	private boolean approved;
	private boolean active;
	private boolean gender;
	private boolean isOnline;
	private String name;
	private String password;
	private String ticket;
	private String instroduce;
	private DateTime dateCreated;
	private DateTime lastLogin;
	private DateTime lastReg;
	
	public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
		@Override
		public User createFromParcel(Parcel p) {
			return new User(p);
		}
		@Override
		public User[] newArray(int size) {
			return new User[size];
		}
    };
    
    public User() {
    	
    }
    
    public User(Parcel in) {
    	userId = in.readInt();
    	approved = in.readInt() == 1;
    	active = in.readInt() == 1;
    	name = in.readString();
    	password = in.readString();
    	ticket = in.readString();
    }
    
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public boolean isApproved() {
		return approved;
	}
	public void setApproved(boolean approved) {
		this.approved = approved;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
    public String getTicket() {
		return ticket;
	}
	public boolean isGender() {
		return gender;
	}
	public void setGender(boolean gender) {
		this.gender = gender;
	}
	public boolean isOnline() {
		return isOnline;
	}
	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}
	public String getInstroduce() {
		return instroduce;
	}
	public void setInstroduce(String instroduce) {
		this.instroduce = instroduce;
	}
    
	public DateTime getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(DateTime dateCreated) {
		this.dateCreated = dateCreated;
	}

	public DateTime getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(DateTime lastLogin) {
		this.lastLogin = lastLogin;
	}

	public DateTime getLastReg() {
		return lastReg;
	}

	public void setLastReg(DateTime lastReg) {
		this.lastReg = lastReg;
	}

	public void setTicket(String ticket) {
		Cookie[] cookies = null;
		this.ticket = ticket;
		
		cookies = CookieUtil.parse(ticket);
		userId = CookieUtil.getInt32(cookies, "user", "id");
		name = CookieUtil.getString(cookies, "user", "w");
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public int getForums() {
		return forums;
	}

	public void setForums(int forums) {
		this.forums = forums;
	}

	public int getPosts() {
		return posts;
	}

	public void setPosts(int posts) {
		this.posts = posts;
	}

	public int getBookmarks() {
		return bookmarks;
	}

	public void setBookmarks(int bookmarks) {
		this.bookmarks = bookmarks;
	}

	public int getPhotos() {
		return photos;
	}

	public void setPhotos(int photos) {
		this.photos = photos;
	}

	public int getFriends() {
		return friends;
	}

	public void setFriends(int friends) {
		this.friends = friends;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(userId);
		dest.writeInt(approved ? 1 : 0);
		dest.writeInt(active ? 1 : 0);
		dest.writeString(name);
		dest.writeString(password);
		dest.writeString(ticket);
	}
}
