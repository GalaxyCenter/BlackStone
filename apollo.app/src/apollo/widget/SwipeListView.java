package apollo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import apollo.app.R;

public class SwipeListView extends ListView implements OnTouchListener,
		OnGestureListener {

	private GestureDetector mDetector;
	private View mEditLayout;
	
	private int mPosition;
	private int mEditLayoutId;
	private boolean mCanShowEditLayout;
	private boolean mEnableEditMode;
	
	public SwipeListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray styled = null;
		
		styled = getContext().obtainStyledAttributes(attrs, R.styleable.SwipeListView);
		mEditLayoutId = styled.getResourceId(R.styleable.SwipeListView_editLayout, 0);
		mEnableEditMode = styled.getBoolean(R.styleable.SwipeListView_enableEditMode, false);
				
		mCanShowEditLayout = false;
		mDetector = new GestureDetector(getContext(), this);
		this.setOnTouchListener(this);
	}
	
	public boolean getEnableEditMode() {
		return mEnableEditMode;
	}
	public void setEnableEditMode(boolean enable) {
		mEnableEditMode = enable;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		if (mEditLayout != null)
			mEditLayout.setVisibility(View.GONE);
		
		mCanShowEditLayout = !mCanShowEditLayout;
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		float deltaX = e2.getX() - e1.getX();
		deltaX = Math.abs(deltaX);
		
		if (mCanShowEditLayout == true && deltaX > 20) {
			ViewGroup viewGroup = null;
			Animation animaction = null;
			
			mPosition = this.pointToPosition((int)e1.getX(), (int)e1.getY());
			animaction = AnimationUtils.loadAnimation(getContext(), R.anim.push_left_in);
			viewGroup = (ViewGroup) this.getChildAt(mPosition - this.getFirstVisiblePosition());
			
			mEditLayout = viewGroup.findViewById(mEditLayoutId);
			mEditLayout.setVisibility(View.VISIBLE);
			mEditLayout.setAnimation(animaction);
		}
		return false;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (mEnableEditMode == false)
			return false;
		else
			return mDetector.onTouchEvent(event);
	}

}
