package apollo.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import apollo.util.DateTime;

public class Gallery implements Parcelable {
	private int id;
	private int photos;
	private String subject;
	private String cover;
	private DateTime postDate;
	
	public static final Parcelable.Creator<Gallery> CREATOR = new Parcelable.Creator<Gallery>() {
		@Override
		public Gallery createFromParcel(Parcel p) {
			return new Gallery(p);
		}
		@Override
		public Gallery[] newArray(int size) {
			return new Gallery[size];
		}
    };
    
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeInt(photos);
		dest.writeString(subject);
		dest.writeString(cover);
		dest.writeParcelable(postDate, flags);
	}
	
	public Gallery() {
		
	}
	
    public Gallery(Parcel in) { 
    	id = in.readInt();
    	photos = in.readInt();
    	subject = in.readString();
    	cover = in.readString();
    	postDate = in.readParcelable(DateTime.class.getClassLoader());
    }
    
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getPhotos() {
		return photos;
	}
	public void setPhotos(int photos) {
		this.photos = photos;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getCover() {
		return cover;
	}
	public void setCover(String cover) {
		this.cover = cover;
	}
	public DateTime getPostDate() {
		return postDate;
	}
	public void setPostDate(DateTime postDate) {
		this.postDate = postDate;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}	
}
