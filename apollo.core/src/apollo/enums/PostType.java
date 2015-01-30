package apollo.enums;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class PostType implements Serializable, Parcelable {
	
	private static final long serialVersionUID = 1967115875448833607L;
	public static PostType REPLY = new PostType(0);
	public static PostType DRAFT = new PostType(1);
	
	private int mValue;
	
	public static final Parcelable.Creator<PostType> CREATOR = new Parcelable.Creator<PostType>() {
		@Override
		public PostType createFromParcel(Parcel p) {
			return new PostType(p);
		}
		@Override
		public PostType[] newArray(int size) {
			return new PostType[size];
		}
    };
    
	public PostType(int value) {
		mValue = value;
	}
	
	public PostType(Parcel p) {
		mValue = p.readInt();
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
	
	public boolean equals(PostType obj) {
		return this.mValue == obj.mValue;
	}
	
	public static PostType get(int value) {
		 return new PostType(value);
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(mValue);
	}
}
