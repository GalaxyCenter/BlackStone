package apollo.data.model;

import java.io.Serializable;
import java.text.MessageFormat;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;
import android.text.TextUtils;
import apollo.enums.PostType;
import apollo.util.ConfigUtil;
import apollo.util.DateTime;

public class Post implements Parcelable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4030849352685437015L;

	public static class Columns implements BaseColumns {
		public static String ID = "_id";
		public static String SUBJECT = "subject";
		public static String BODY = "body";
		public static String SECTION_ID = "section_id";
		public static String THREAD_ID = "thread_id";
		public static String USER_ID = "user_id";
		public static String POST_DATE = "post_date";
		public static String TYPE = "type";
		public static String FLOOR = "floor";
		//public static final String[] USER_QUERY_COLUMNS = {ID, NAME, PASSWORD, TICKET, SELECTED};
	}
	
	private int postId;
	private int threadId;
	private int views; 
	private int replies;
	private int floor;
	private String guid;
	private String subject;
	private String body;
	private String url;
	private String icon;
	private Section section;
	private User author;
	private DateTime postDate;
	private DateTime updateDate;
	private PostType postType;
	
	public static final Parcelable.Creator<Post> CREATOR = new Parcelable.Creator<Post>() {
		@Override
		public Post createFromParcel(Parcel p) {
			return new Post(p);
		}
		@Override
		public Post[] newArray(int size) {
			return new Post[size];
		}
    };
    
    public Post() {
    	subject = "";
    	body = "";
    	url = "";
    	icon = "";
    	
    	postDate = DateTime.now();
    	postType = PostType.REPLY;
    }
	
	public Post(Parcel in) {
		postId = in.readInt();
		threadId = in.readInt();
		views = in.readInt();
		replies = in.readInt();
		floor = in.readInt();
		guid = in.readString();
		subject = in.readString();
		body = in.readString();
		url = in.readString();
		icon = in.readString();
		section = in.readParcelable(Section.class.getClassLoader());
		author = in.readParcelable(User.class.getClassLoader());
		postDate = in.readParcelable(DateTime.class.getClassLoader());
		updateDate = in.readParcelable(DateTime.class.getClassLoader());
		postType = in.readParcelable(PostType.class.getClassLoader());
	}
	public int getPostId() {
		return postId;
	}
	public void setPostId(int postId) {
		this.postId = postId;
	}
	public String getGUID() {
		return guid;
	}
	public void setGUID(String guid) {
		this.guid = guid;
	}
	public void setFloor(int floor) {
		this.floor = floor;
	}
	public int getFloor() {
		return floor;
	}
	public int getThreadId() {
		return threadId;
	}
	public void setThreadId(int threadId) {
		this.threadId = threadId;
	}
	public Section getSection() {
		return section;
	}
	public void setSection(Section section) {
		this.section = section;
	}
	public int getViews() {
		return views;
	}
	public void setViews(int views) {
		this.views = views;
	}
	public int getReplies() {
		return replies;
	}
	public void setReplies(int replies) {
		this.replies = replies;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getUrl() {
		if (TextUtils.isEmpty(url)) 
			url = MessageFormat.format(ConfigUtil.getInstance().getProperty("app.post.url.pattern"), getSection().getSectionId(), threadId);
		
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public User getAuthor() {
		return author;
	}
	public void setAuthor(User author) {
		this.author = author;
	}
	public DateTime getPostDate() {
		return postDate;
	}
	public void setPostDate(DateTime postDate) {
		this.postDate = postDate;
	}
	public DateTime getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(DateTime updateDate) {
		this.updateDate = updateDate;
	}
	
	public PostType getPostType() {
		return postType;
	}

	public void setPostType(PostType postType) {
		this.postType = postType;
	}

	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel parcel, int flag) {
		parcel.writeInt(postId);
		parcel.writeInt(threadId);
		parcel.writeInt(views);
		parcel.writeInt(replies);
		parcel.writeInt(floor);
		parcel.writeString(guid);
		parcel.writeString(subject);
		parcel.writeString(body);
		parcel.writeString(url);
		parcel.writeString(icon);
		parcel.writeParcelable(section, flag);
		parcel.writeParcelable(author, flag);
		parcel.writeParcelable(postDate, flag);
		parcel.writeParcelable(updateDate, flag);
		parcel.writeParcelable(postType, flag);
	}
		
	
}
