package xshare.tencent.weixin;

import xshare.framework.ProxyParams;

public class WXParams extends ProxyParams {

	public static class Type {
		public static String WEB = "webpage";
		public static String TEXT = "text";
		public static String IMAGE = "img";
		public static String VIDEO = "video";
		public static String MUSIC = "music";
		public static String DATA = "appdata";
		public static String EMOJI = "emoji";
	}

	private String subject = null;
	private String type = null;
	private boolean isTimeline = false;
	
	
	
	
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isTimeline() {
		return isTimeline;
	}

	public void setTimeline(boolean isTimeline) {
		this.isTimeline = isTimeline;
	}
	
	
}
