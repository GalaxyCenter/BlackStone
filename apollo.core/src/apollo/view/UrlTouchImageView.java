/*
 Copyright (c) 2012 Roman Truba

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
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import apollo.core.R;
import apollo.util.AsyncImageLoader;
import apollo.util.ImageUtil;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class UrlTouchImageView extends RelativeLayout {
	protected ProgressBar mProgressBar;
	protected TouchImageView mImageView;
	protected Context mContext;
	private AsyncImageLoader<TouchImageView> mAsyncImageLoader;
	private AsyncImageLoader.OnImageLoaderListener<TouchImageView> mImageLoaderCallback;
	
	public UrlTouchImageView(Context context) {
		super(context);
		mContext = context;
		
		mAsyncImageLoader = new AsyncImageLoader<TouchImageView>();
		mImageLoaderCallback = new AsyncImageLoader.OnImageLoaderListener<TouchImageView>(){
			@Override
			public void imageLoaded(Bitmap bmp, TouchImageView view, String url) {
				view.setImageBitmap(bmp);
				view.setVisibility(View.VISIBLE);
				mProgressBar.setVisibility(View.GONE);
			}
		};
		initViews();
	}

	public UrlTouchImageView(Context ctx, AttributeSet attrs) {
		super(ctx, attrs);
		mContext = ctx;
		initViews();
	}

	public TouchImageView getImageView() {
		return mImageView;
	}

	@SuppressWarnings("deprecation")
	protected void initViews() {		
		RelativeLayout.LayoutParams params = null;
		
	    mImageView = new TouchImageView(this.mContext);
	    params = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
	    mImageView.setLayoutParams(params);
	    mImageView.setVisibility(GONE);
	    addView(mImageView);
	    
	    mProgressBar = new ProgressBar(this.mContext, null, android.R.attr.progressBarStyleInverse);
	    mProgressBar.setIndeterminateDrawable(this.mContext.getResources().getDrawable(R.drawable.progressbar));
	    params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    params.addRule(RelativeLayout.CENTER_IN_PARENT);
	    mProgressBar.setLayoutParams(params);
	    mProgressBar.setIndeterminate(true);
	    addView(mProgressBar);
	}

	public void setUrl(String url) {
		mImageView.setTag(url);
		mAsyncImageLoader.loadImage(url, mImageView, mImageLoaderCallback);
	}
}
