package apollo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

public class CustomTextView extends TextView {
 
	public CustomTextView(Context context) {
		super(context);
	}

	public CustomTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		return event.getAction() == 0 && hasSelection();
	}

}
