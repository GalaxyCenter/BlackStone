/*
 Copyright (c) 2012 Robert Foss, Roman Truba

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial
 portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package apollo.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

/*
 * 
 * http://blog.csdn.net/barryhappy/article/details/7392326
 * http://www.2cto.com/kf/201304/204326.html
 * http://blog.csdn.net/liu_zhen_wei/article/details/7652908
 * http://blog.csdn.net/xiedantibu/article/details/7793641
 * http://laladin.iteye.com/blog/1545377
 * 
 * http://www.doc88.com/p-989737446704.html
 * http://www.havenliu.com/android/668.html
 */
public class TouchImageView extends ImageView {

	static float mMaxZoom = 5.0f;
	
	public boolean onLeftSide = false, onTopSide = false, onRightSide = false,
			onBottomSide = false;
	protected GestureDetector mGestureDetector;
	protected ScaleGestureDetector mScaleGestureDetector;
	protected Handler mHandler = new Handler();
	private final Matrix mDisplayMatrix = new Matrix();
	protected Matrix mBaseMatrix = new Matrix();
	protected Matrix mSuppMatrix = new Matrix();
	private final float[] mMatrixValues = new float[9];
	protected Bitmap mBitmapDisplayed = null;
	private float mWidth;
	private float mHeight;
	int mThisWidth = -1, mThisHeight = -1;
	
	public TouchImageView(Context context) {
		super(context);

		init();
	}

	private void init() {
		setScaleType(ImageView.ScaleType.MATRIX);

		mGestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {

			@Override
			public boolean onDown(MotionEvent e) {
				return true;
			}

			@Override
			public void onShowPress(MotionEvent e) {
			}

			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				return false;
			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
				if (getScale() > 1F) {
	                postTranslateCenter(-distanceX, -distanceY);
	            }
	            return true;
			}

			@Override
	        public boolean onDoubleTap(MotionEvent e) {
	            if (getScale() > 2F) {
	              //  zoomTo(1f);
	            	zoomTo(1.0f, mWidth / 2, mHeight / 2, 400f);
	            } else {
	                //zoomToPoint(3f, e.getX(), e.getY());
	            	zoomTo(mMaxZoom, e.getX(), e.getY(), 400f);
	            }
	            return true;
	        }
			
			@Override
			public void onLongPress(MotionEvent e) {
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				return false;
			}

		});
		
		mScaleGestureDetector = new ScaleGestureDetector(this.getContext(), new ScaleGestureDetector.OnScaleGestureListener(){

			@Override
			public boolean onScale(ScaleGestureDetector detector) {
				float scale = detector.getScaleFactor();
		        float currentScale = getScale();
		        
		        if (currentScale < 0.7f && scale < 1.0f) {
		            scale = 1.0f;
		        }
		        if (currentScale > 8.0f && scale > 1.0f) {
		            scale = 1.0f;
		        }

		        zoomToPoint(currentScale * scale, detector.getFocusX(), detector.getFocusY());
		        return true;
			}

			@Override
			public boolean onScaleBegin(ScaleGestureDetector detector) {
				return true;
			}

			@Override
			public void onScaleEnd(ScaleGestureDetector detector) {
			}
			
			
		});
	}

	public void resetScale() {

	}

	public boolean pagerCanScroll() {
		return false;
	}

	protected float getValue(Matrix matrix, int whichValue) {
		matrix.getValues(mMatrixValues);
		return mMatrixValues[whichValue];
	}

	// Get the scale factor out of the matrix.
	protected float getScale(Matrix matrix) {
		return getValue(matrix, Matrix.MSCALE_X);
	}

	protected float getScale() {
		return getScale(mSuppMatrix);
	}
	
	protected void panBy(float dx, float dy) {
        postTranslate(dx, dy);
        setImageMatrix(getImageViewMatrix());
    }
	
	protected void zoomToPoint(float scale, float pointX, float pointY) {
        float cx = mWidth / 2F;
        float cy = mHeight / 2F;

        panBy(cx - pointX, cy - pointY);
        zoomTo(scale, cx, cy);
    }
	
	protected void zoomTo(float scale) {
        float cx = getWidth() / 2F;
        float cy = getHeight() / 2F;

        zoomTo(scale, cx, cy);
    }
	
	protected void zoomTo(final float scale, final float centerX, final float centerY, final float durationMs) {
		final float incrementPerMs = (scale - getScale()) / durationMs;
		final float oldScale = getScale();
		final long startTime = System.currentTimeMillis();

		mHandler.post(new Runnable() {
			public void run() {
				long now = System.currentTimeMillis();
				float currentMs = Math.min(durationMs, now - startTime);
				float target = oldScale + (incrementPerMs * currentMs);
				zoomTo(target, centerX, centerY);
				if (currentMs < durationMs) {
					mHandler.post(this);
				}
			}
		});
	}
	
	protected void zoomTo(float scale, float centerX, float centerY) {
        if (scale > mMaxZoom) {
            scale = mMaxZoom;
        }

        float oldScale = getScale();
        float deltaScale = scale / oldScale;

        mSuppMatrix.postScale(deltaScale, deltaScale, centerX, centerY);
        setImageMatrix(getImageViewMatrix());
        center(true, true);
    }
	
	protected Matrix getImageViewMatrix() {
        mDisplayMatrix.set(mBaseMatrix);
        mDisplayMatrix.postConcat(mSuppMatrix);
        return mDisplayMatrix;
    }
	
	protected void center(boolean horizontal, boolean vertical) {
        if (mBitmapDisplayed == null) 
            return;

        Matrix m = getImageViewMatrix();

        RectF rect = new RectF(0, 0,
        		mBitmapDisplayed.getWidth(),
        		mBitmapDisplayed.getHeight());

        m.mapRect(rect);

        float height = rect.height();
        float width  = rect.width();

        float deltaX = 0, deltaY = 0;

        if (vertical) {
            int viewHeight = getHeight();
			if (height < viewHeight)
				deltaY = (viewHeight - height) / 2 - rect.top;
			else if (rect.top > 0)
				deltaY = -rect.top;
			else if (rect.bottom < viewHeight)
				deltaY = getHeight() - rect.bottom;

		}

		if (horizontal) {
			int viewWidth = getWidth();
			if (width < viewWidth)
				deltaX = (viewWidth - width) / 2 - rect.left;
			else if (rect.left > 0)
				deltaX = -rect.left;
			else if (rect.right < viewWidth)
				deltaX = viewWidth - rect.right;

		}

        postTranslate(deltaX, deltaY);
        setImageMatrix(getImageViewMatrix());
    }
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mGestureDetector.onTouchEvent(event);   
		mScaleGestureDetector.onTouchEvent(event);
		return true;
	}

	protected void postTranslateCenter(float dx, float dy) {
		postTranslate(dx, dy);
		center(true, true);
	}

	protected void postTranslate(float dx, float dy) {
		mSuppMatrix.postTranslate(dx, dy);
	}

	private void getProperBaseMatrix(Bitmap bitmap, Matrix matrix) {
        float w = bitmap.getWidth();
        float h = bitmap.getHeight();
        matrix.reset();

        // We limit up-scaling to 3x otherwise the result may look bad if it's
        // a small icon.
        float widthScale = Math.min(mWidth / w, mMaxZoom);
        float heightScale = Math.min(mHeight / h, mMaxZoom);
        float scale = Math.min(widthScale, heightScale);

      //  matrix.postConcat(bitmap.getRotateMatrix());
        matrix.postScale(scale, scale);

        matrix.postTranslate(
                (mWidth  - w * scale) / 2F,
                (mHeight - h * scale) / 2F);
    }
	
	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);
		mBitmapDisplayed = bm;
	}
	
	@Override
    protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        
		getProperBaseMatrix(mBitmapDisplayed, mBaseMatrix);
        setImageMatrix(getImageViewMatrix());
		mBaseMatrix.reset();
	}
	
    @Override
    protected void onLayout(boolean changed, int left, int top,
                            int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mThisWidth = right - left;
        mThisHeight = bottom - top;

        if (mBitmapDisplayed != null) {
            getProperBaseMatrix(mBitmapDisplayed, mBaseMatrix);
            setImageMatrix(getImageViewMatrix());
        }
    }
}