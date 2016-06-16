package apollo.enums;

public class PostMode {
	
	public static PostMode CREATE = new PostMode(1);
	public static PostMode REPLY = new PostMode(2);
	public static PostMode REPLY_QUOTE = new PostMode(3);
	public static PostMode REPLY_FLOOW = new PostMode(4);
	
	public static PostMode parse(int value) {
		return new PostMode(value);
	}
	
	private int mValue;
	PostMode(int value) {
		this.mValue = value;
	}
	
	public int getValue() {
		return mValue;
	}
	
	public String toString() {
		return Integer.toString(mValue);
	}
	
	@Override
	public boolean equals(Object obj) {
		return equals((PostMode)obj);
	}
	
	public boolean equals(PostMode obj) {
		return this.mValue == obj.mValue;
	}
	
	public static PostMode get(int value) {
		 return new PostMode(value);
	}
}
