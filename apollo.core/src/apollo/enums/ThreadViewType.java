package apollo.enums;

public class ThreadViewType {

	public static ThreadViewType NORMAL = new ThreadViewType(0);
	public static ThreadViewType BOOKMARK = new ThreadViewType(1);
	public static ThreadViewType USER = new ThreadViewType(2);
	public static ThreadViewType SEARCH = new ThreadViewType(3);
	public static ThreadViewType AUTOPOST = new ThreadViewType(4);
	
	private int mValue;
	public ThreadViewType(int value) {
		mValue = value;
	}
	public int getValue() {
		return mValue;
	}
	
	public String toString() {
		return Integer.toString(mValue);
	}
	
	@Override
	public boolean equals(Object obj) {
		return equals((ThreadViewType)obj);
	}
	
	public boolean equals(ThreadViewType obj) {
		return this.mValue == obj.mValue;
	}
	
	public static ThreadViewType get(int value) {
		 return new ThreadViewType(value);
	}
}
