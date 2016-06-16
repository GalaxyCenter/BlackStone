package apollo.data.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;

public class ContentData {
	public static final int TYPE_AT = 4;
	public static final int TYPE_FACE = 2;
	public static final int TYPE_LINK = 1;
	public static final int TYPE_PIC = 3;
	public static final int TYPE_SONG = 6;
	public static final int TYPE_TEXT = 0;
	public static final int TYPE_VIDEO = 5;
	public static final int TYPE_VIDEO_IMAGE = 1000;
	private int height;
	private String link;
	private String text;
	private int type;
	private SpannableStringBuilder uniteString;
	private int width;

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public SpannableStringBuilder getUniteString() {
		return uniteString;
	}

	public void setUniteString(SpannableStringBuilder uniteString) {
		this.uniteString = uniteString;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public SpannableString getSpannableString(Context context, int i, int j) {
		SpannableString spannablestring = null;
		if (type == 2) {
			spannablestring = new SpannableString((new StringBuilder(
					String.valueOf(text))).append(" ").toString());
			Bitmap bitmap = null;//TiebaApplication.app.getFace(text);
			if (bitmap != null) {
				MyBitmapDrawable mybitmapdrawable = new MyBitmapDrawable(bitmap);
				int k;
				if (i - j > 0)
					k = bitmap.getHeight() + (i - j >> 1);
				else
					k = bitmap.getHeight();
				mybitmapdrawable.setBounds(0, 0, bitmap.getWidth(), k);
				spannablestring.setSpan(new ImageSpan(mybitmapdrawable, 1), 0,
						text.length(), 33);
			}
		}
		return spannablestring;
	}

}
