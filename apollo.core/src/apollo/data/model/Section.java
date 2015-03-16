package apollo.data.model;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

public class Section implements Serializable, Parcelable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String sectionId;
	private String name;
	
	public static class Columns implements BaseColumns {
		public static String ID = "_id";
		public static String NAME = "name";
	}
	
    public static final Parcelable.Creator<Section> CREATOR = new Parcelable.Creator<Section>() {
		@Override
		public Section createFromParcel(Parcel p) {
			return new Section(p);
		}
		@Override
		public Section[] newArray(int size) {
			return new Section[size];
		}
    };
    
    public Section() {
    	
    }
    
    public Section(Parcel in) {
    	sectionId = in.readString();
    	name = in.readString();
    }
    
	public String getSectionId() {
		return sectionId;
	}
	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(sectionId);
		dest.writeString(name);		
	}
	
	
}
