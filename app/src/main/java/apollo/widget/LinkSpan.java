package apollo.widget;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.text.TextPaint;
import android.text.style.ReplacementSpan;

public class LinkSpan extends ReplacementSpan {

	private int mColor;
	private String mText = null;
	private boolean mUnderlineText;
	
	public LinkSpan(String text) {
		this(text, -1);
	}
	
	public LinkSpan(String text, int color) {
		this.mText = text;
		this.mColor = color;
	}
	
	public void setUnderlineText(boolean underlineText) {
		this.mUnderlineText = underlineText;
	}
	
	public void setColor(int color) {
		this.mColor = color;
	}
	
	@Override
	public int getSize(Paint paint, CharSequence text, int start, int end,
			FontMetricsInt fm) {
		return Math.round(paint.measureText(mText, 0, mText.length())) ;
	}

	@Override
	public void draw(Canvas canvas, CharSequence text, int start, int end,
			float x, int top, int y, int bottom, Paint paint) {
		
		canvas.drawText(this.mText, 0, this.mText.length(), x, y, paint);
	}

	@Override
    public void updateDrawState(TextPaint ds) {
		int color = -1;
		
		color = mColor == -1 ? ds.linkColor : mColor;
        ds.setColor(color);
        ds.setUnderlineText(mUnderlineText);
    }
}
