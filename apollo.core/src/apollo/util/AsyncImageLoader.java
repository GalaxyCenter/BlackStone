package apollo.util;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import apollo.core.ApolloApplication;
import apollo.core.R;
import apollo.exceptions.ApplicationException;

public class AsyncImageLoader<Container> {

	private LruCache<String, Bitmap> mImageCache;
	private float mScaleWidth = 0f;
	private float mScaleHeight = 0f;
	
	public interface OnImageLoaderListener<Container> {
		public void imageLoaded(Bitmap bmp, Container container, String url);
	}
    
    class ImageAsyncTask extends AsyncTask<String, Void, Bitmap> {
		private String mUrl = null;
		private Container mResult = null;
		private OnImageLoaderListener<Container> mImgCallBack = null;
		
		public ImageAsyncTask(Container result, OnImageLoaderListener<Container> l) {
			this.mImgCallBack = l;
			this.mResult = result;
		}
		
		@Override
		protected Bitmap doInBackground(String... params) {
			Bitmap bmp = null;
			
			this.mUrl = params[0];

			if (FileUtil.isLocalFile(this.mUrl)) {
				byte[] bytes = FileUtil.getFileData(this.mUrl);
				bmp = ImageUtil.byteToBitmap(bytes);
			} else {
				try {
					bmp = ImageUtil.getBitmapFromUrl(this.mUrl);
				} catch(ApplicationException ex) {
					bmp = ImageUtil.getResBitmap(ApolloApplication.app(), R.drawable.drgimage_fail);
				}
			}
            if (mScaleWidth != 0f || mScaleHeight != 0f) 
            	bmp = ImageUtil.getScaleBitmap(bmp, mScaleWidth, mScaleHeight);
            
            mImageCache.put(this.mUrl, bmp);
            return bmp;
		}
		
		@Override
        protected void onPostExecute(Bitmap bmp) {
			this.mImgCallBack.imageLoaded(bmp, this.mResult, this.mUrl);
		}
	};
	
    public AsyncImageLoader() {
    	int maxMem = CompatibleUtil.getMaxMemory();
    	int cacheSize = maxMem / 8;
    	
    	mImageCache = new LruCache<String, Bitmap>(cacheSize) {
    		@Override
    		protected int sizeOf(String key, Bitmap bmp) {
    			return bmp.getRowBytes() * bmp.getHeight();
    		}
    	};
    }
            
    public void setScaleWidth(float width) {
    	mScaleWidth = width;
    }
    
    public float getScaleWidth() {
    	return mScaleWidth;
    }
    
    public void setScaleHeight(float height) {
    	mScaleHeight = height;
    }

    public float getScaleHeight() {
    	return mScaleHeight;
    }
    
    public Bitmap loadImage(String url, Container result, OnImageLoaderListener<Container> callback) {
    	Bitmap bmp = null;
    	
    	bmp = mImageCache.get(url);
    	if (bmp == null) {
    		ImageAsyncTask task = new ImageAsyncTask(result, callback);
    		task.execute(url);
    	}
    	return bmp;
    }
}