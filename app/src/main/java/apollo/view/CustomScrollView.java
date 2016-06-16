package apollo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class CustomScrollView extends ScrollView {
 
	private GestureDetector gestureDetector;
	private int Scroll_height = 0;
	private int view_height = 0;

	class YScrollDetector extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			return Math.abs(distanceY) > Math.abs(distanceX);
		}
	}

	public CustomScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setFadingEdgeLength(0);
	}

	private void stopAnim() {
//		try {
//			Object obj = Reflector.getDeclaredField(this, "mScroller").get(
//					this);
//			if (obj != null)
//				obj.getClass().getMethod("abortAnimation", new Class[0])
//						.invoke(obj, new Object[0]);
//		} catch (Exception ex) {
//		}
	}

}
