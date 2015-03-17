package xshare.framework;

import android.graphics.Bitmap;

public abstract class ProxyParams {

	private String text;
	private Bitmap img;
	private String url;
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setImage(Bitmap img) {
		this.img = img;
	}

	public Bitmap getImage() {
		return img;
	}
}
