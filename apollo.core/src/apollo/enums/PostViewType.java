package apollo.enums;

public class PostViewType {

	public static PostViewType NORMAL = new PostViewType(0);
	public static PostViewType SHOW_ONLY_USER = new PostViewType(1);
	
	private int mValue;
	public PostViewType(int value) {
		mValue = value;
	}
	
	public String toString() {
		return Integer.toString(mValue);
	}
	
	@Override
	public boolean equals(Object obj) {
		return equals((PostViewType)obj);
	}
	
	public boolean equals(PostViewType obj) {
		return this.mValue == obj.mValue;
	}
	
	public static PostViewType get(int value) {
		 return new PostViewType(value);
	}
}
