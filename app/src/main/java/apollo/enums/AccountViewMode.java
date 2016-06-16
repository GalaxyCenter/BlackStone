package apollo.enums;

import android.os.Parcel;
import android.os.Parcelable;

public enum AccountViewMode implements Parcelable {

	ACCOUNT_LIST_MODE(0),
	ACCOUNT_SELECT_MODE(1),
	FOLLOW_USER_LIST_MODE(2),
	FRIEND_LIST_MODE(3);
	
	private int value;
	AccountViewMode(int value) {
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
	
	public static final Creator<AccountViewMode> CREATOR = new Creator<AccountViewMode>() {
        @Override
        public AccountViewMode createFromParcel(final Parcel source) {
            return AccountViewMode.values()[source.readInt()];
        }

        @Override
        public AccountViewMode[] newArray(final int size) {
            return new AccountViewMode[size];
        }
    };
}
