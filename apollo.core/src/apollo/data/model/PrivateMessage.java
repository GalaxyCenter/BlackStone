package apollo.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class PrivateMessage extends Post implements Parcelable {

	private User recipientor;
	private int flag;
	
	public static final Parcelable.Creator<PrivateMessage> CREATOR = new Parcelable.Creator<PrivateMessage>() {
		@Override
		public PrivateMessage createFromParcel(Parcel p) {
			return new PrivateMessage(p);
		}
		@Override
		public PrivateMessage[] newArray(int size) {
			return new PrivateMessage[size];
		}
    };
    
	public PrivateMessage() {

	}

	public PrivateMessage(Parcel in) {
		super(in);
		recipientor = in.readParcelable(User.class.getClassLoader());
		flag = in.readInt();
	}
    
	public User getRecipientor() {
		return recipientor;
	}

	public void setRecipientor(User recipientor) {
		this.recipientor = recipientor;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flag) {
		super.writeToParcel(dest, flag);
		dest.writeParcelable(recipientor, flag);
		dest.writeInt(this.flag);
	}
}
