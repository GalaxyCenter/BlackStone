package apollo.data.model;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class Thread extends Post implements Parcelable {

	public static final Parcelable.Creator<Thread> CREATOR = new Parcelable.Creator<Thread>() {
		@Override
		public Thread createFromParcel(Parcel p) {
			return new Thread(p);
		}
		@Override
		public Thread[] newArray(int size) {
			return new Thread[size];
		}
    };
    
	public Thread () {
		
	}
	
	public Thread(Parcel in) {
		super(in);
		hasImage = in.readInt() == 1;
	}

	private boolean hasImage;

	public boolean hasImage() {
		return hasImage;
	}

	public void setHasImage(boolean hasImage) {
		this.hasImage = hasImage;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flag) {
		super.writeToParcel(dest, flag);
		dest.writeInt(hasImage ? 1 : 0);
	}
}
