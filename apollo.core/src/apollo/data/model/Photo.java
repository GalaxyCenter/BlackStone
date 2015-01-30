package apollo.data.model;

import apollo.util.DateTime;

public class Photo {
	private String bigImg;
	private String mediumImg;
	private String smallImg;
	private String subject;
	private int id;
	private DateTime postDate;
	public String getBigImg() {
		return bigImg;
	}
	public void setBigImg(String bigImg) {
		this.bigImg = bigImg;
	}
	public String getMediumImg() {
		return mediumImg;
	}
	public void setMediumImg(String mediumImg) {
		this.mediumImg = mediumImg;
	}
	public String getSmallImg() {
		return smallImg;
	}
	public void setSmallImg(String smallImg) {
		this.smallImg = smallImg;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public DateTime getPostDate() {
		return postDate;
	}
	public void setPostDate(DateTime postDate) {
		this.postDate = postDate;
	} 
	
	
	
}
