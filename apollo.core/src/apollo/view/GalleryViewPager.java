package apollo.view;

import android.content.Context;
import android.graphics.PointF;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import apollo.util.CompatibleUtil;

// e.g:http://www.java2s.com/Open-Source/Android-Open-Source-App/Media/AndroidTouchGallery/ru/truba/touchgallery/GalleryWidget/GalleryViewPager.java.htm
public class GalleryViewPager extends ViewPager {

	private PointF last;
	private TouchImageView mCurrentView;
	private TouchImageView mSelectedView;
 
	public GalleryViewPager(Context context) {
		super(context);
	}

	public GalleryViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private float[] handleMotionEvent(MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			this.last = new PointF(event.getX(0), event.getY(0));
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_MOVE:
			PointF curr = new PointF(event.getX(0), event.getY(0));
			return new float[] { curr.x - last.x, curr.y - last.y };
		}
		return null;
	}

	public TouchImageView getCurrentView() {
		return this.mCurrentView;
	}

	public TouchImageView getSelectedView() {
		return this.mSelectedView;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) 
			super.onInterceptTouchEvent(event);
		
		float[] difference = handleMotionEvent(event);

		if (mCurrentView.pagerCanScroll()) {
			return super.onInterceptTouchEvent(event);
		} else {
			if (difference != null && mCurrentView.onRightSide
					&& difference[0] < 0) // move right
			
				return super.onInterceptTouchEvent(event);
			
			if (difference != null && mCurrentView.onLeftSide
					&& difference[0] > 0) // move left
			
				return super.onInterceptTouchEvent(event);
			
			if (difference == null
					&& (mCurrentView.onLeftSide || mCurrentView.onRightSide)) 
				return super.onInterceptTouchEvent(event);
			
		}
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) 
			super.onTouchEvent(event);
		

		float[] difference = handleMotionEvent(event);

		if (mCurrentView.pagerCanScroll()) {
			return super.onTouchEvent(event);
		} else {
			if (difference != null && mCurrentView.onRightSide
					&& difference[0] < 0) // move right

				return super.onTouchEvent(event);

			if (difference != null && mCurrentView.onLeftSide
					&& difference[0] > 0) // move left

				return super.onTouchEvent(event);

			if (difference == null
					&& (mCurrentView.onLeftSide || mCurrentView.onRightSide))
				return super.onTouchEvent(event);

		}

		return false;
	}

	public void setCurrentView(TouchImageView view) {
		this.mCurrentView = view;
	}

	public void setSelectedView(TouchImageView view) {
		this.mSelectedView = view;
	}

}
