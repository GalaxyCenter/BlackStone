package apollo.enums;

import android.os.Parcel;
import android.os.Parcelable;

public enum PrivateMessageType implements Parcelable {

	SYSTEM(0),
	NORMAL(1);
	
	private int value;
	PrivateMessageType(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(ordinal()); 
	}
	
	public static final Creator<PrivateMessageType> CREATOR = new Creator<PrivateMessageType>() {
        @Override
        public PrivateMessageType createFromParcel(final Parcel source) {
            return PrivateMessageType.values()[source.readInt()];
        }

        @Override
        public PrivateMessageType[] newArray(final int size) {
            return new PrivateMessageType[size];
        }
    };
}
