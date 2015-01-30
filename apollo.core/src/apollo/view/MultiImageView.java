package apollo.view;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import apollo.core.R;
import apollo.data.model.Constants;
import apollo.util.ResUtil;

import java.util.ArrayList;
import java.util.List;

public class MultiImageView extends RelativeLayout {
 
	class ImagePagerAdapter extends PagerAdapter {

		private List<String> mUrls;
		private Context mContext = null;
		private int mGifMaxUseableMem = 0;
		private int mCurrentPosition = -1;
		private View.OnClickListener mOnClickListener = null;

		public ImagePagerAdapter(Context context, List<String> urls) {
		    this.mContext = context;
		    this.mUrls = urls;
		}

		@Override
		public int getCount() {
			return mUrls == null ? 0 : mUrls.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object obj) {
			 return view.equals(obj);
		}

		public void setData(List<String> urls) {
			mUrls = urls;
			notifyDataSetChanged();
		}

		public void setGifMaxUseableMem(int size) {
			this.mGifMaxUseableMem = size;
		}

		public void setOnClickListener(View.OnClickListener listener) {
			this.mOnClickListener = listener;
		}
			
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			UrlTouchImageView view = null;
			String url = null;

			view = new UrlTouchImageView(mContext);
			url = (String) mUrls.get(position);
			view.setLayoutParams(new Gallery.LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT));
			view.setUrl(url);
			view.setTag(String.valueOf(position));		
			container.addView(view, 0);
			return view;
		}
		
		@Override
		public void setPrimaryItem(ViewGroup container, int position, Object obj) {
			super.setPrimaryItem(container, position, obj);
			if (mCurrentPosition == position)
				return;
			
			GalleryViewPager view_pager = ((GalleryViewPager) container);
			UrlTouchImageView img_view = (UrlTouchImageView) obj;
			
			if (view_pager.getCurrentView() != null)
				view_pager.getCurrentView().resetScale();

			mCurrentPosition = position;
			view_pager.setCurrentView(img_view.getImageView());
//			super.setPrimaryItem(container, position, obj);
	//
//			if (obj instanceof UrlTouchImageView) {
//				GalleryViewPager view_pager = null;
//				TouchImageView image_view = null;
//				UrlTouchImageView url_img_view = null;
	//
//				url_img_view = (UrlTouchImageView) obj;
//				view_pager = (GalleryViewPager) container;
//				image_view = url_img_view.getImageView();
	//
//				if (view_pager.getSelectedView() == null) {
//					ViewParent parent = null;
	//
//					view_pager.setSelectedView(image_view);
	//
//					parent = view_pager.getParent();
//					if (parent != null && (parent instanceof MultiImageView))
//						((MultiImageView) parent).setZoomButton(image_view);
//				}
	//
//				TouchImageView current_view = view_pager.getCurrentView();
//				if (image_view != current_view) {
//					if (current_view != null)
//						current_view.restoreSize();
	//
//					url_img_view.checkImage();
//					view_pager.setCurrentView(image_view);
	//
//					if (url_img_view.getImageType() == 1)
//						mOnGifSetListener.gifSet(image_view);
//				}
//			}
		}
	}
	
	private ImagePagerAdapter mAdapter = null;
	private GalleryViewPager mGalleryViewPager = null;
	private View.OnClickListener mOnClickListener = null;
	private ViewPager.OnPageChangeListener mOnPageChangeListener = null;
	private LinearLayout mTools = null;
	private ViewPager.OnPageChangeListener mUserOnPageChangeListener = null;
	private Button mZoomIn = null;
	private Button mZoomOut = null;
	private int mGifMaxMemory = 0;
	private boolean mGifPlayRealseOther = true;

	public MultiImageView(Context paramContext) {
		super(paramContext);
		init();
	}

	public MultiImageView(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		init();
	}

	public MultiImageView(Context paramContext, AttributeSet paramAttributeSet,
			int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
		init();
	}

//	private TouchImageView getCurrentImageView() {
//		return this.mGalleryViewPager.getCurrentView();
//	}

	private void init() {
		initViews();
		initListeners();
	}

	private void initViews() {
		RelativeLayout.LayoutParams rll_params = null;
		LinearLayout.LayoutParams lll_params = null;
		
	    this.mGalleryViewPager = new GalleryViewPager(getContext());
	    rll_params = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
	    this.mGalleryViewPager.setLayoutParams(rll_params);
	    this.mGalleryViewPager.setOnPageChangeListener(this.mOnPageChangeListener);
	    addView(this.mGalleryViewPager);
	    
	    this.mTools = new LinearLayout(getContext());
	    rll_params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    rll_params.bottomMargin = ResUtil.dip2px(getContext(), 10.0F);
	    rll_params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
	    rll_params.addRule(RelativeLayout.CENTER_HORIZONTAL);
	    this.mTools.setOrientation(LinearLayout.HORIZONTAL);
	    this.mTools.setLayoutParams(rll_params);
	    addView(this.mTools);
	    
	    lll_params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    this.mZoomOut = new Button(getContext());
	    this.mZoomOut.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.image_zoomout));
	    this.mZoomOut.setLayoutParams(lll_params);
	    this.mZoomOut.setOnClickListener(this.mOnClickListener);
	    this.mZoomOut.setEnabled(false);
	    this.mTools.addView(this.mZoomOut);
	    
	    this.mZoomIn = new Button(getContext());
	    this.mZoomIn.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.image_zoomin));
	    this.mZoomIn.setLayoutParams(lll_params);
	    this.mZoomIn.setOnClickListener(this.mOnClickListener);
	    this.mZoomIn.setEnabled(false);
	    this.mTools.addView(this.mZoomIn);
	    
	    if (ResUtil.isSupportGesture())
	      this.mTools.setVisibility(View.GONE);
	    this.mAdapter = new ImagePagerAdapter(getContext(), null);
	    setAdapter(this.mAdapter);
	}

	private void initListeners() {
//		mOnClickListener = new android.view.View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//
//			}
//		};

//		mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
//
//			@Override
//			public void onPageScrollStateChanged(int arg0) {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void onPageScrolled(int arg0, float arg1, int arg2) {
//				// TODO Auto-generated method stub
//
//			}
//
//			@Override
//			public void onPageSelected(int arg0) {
//				// TODO Auto-generated method stub
//
//			}
//
//		};

//		mOnSizeChangedListener = new TouchImageView.OnSizeChangedListener() {
//
//			@Override
//			public void sizeChenged(TouchImageView dragimageview, boolean flag,
//					boolean flag1) {
//				// TODO Auto-generated method stub
//				
//			}
//		
//		};

//		mOnGifSetListener = new TouchImageView.OnGifSetListener() {
//			@Override
//			public void gifSet(TouchImageView dragimageview) {
//			}
//		};
		
		
		//this.mGalleryViewPager.setOnPageChangeListener(this.mOnPageChangeListener);
	}
	
	private void setAdapter(ImagePagerAdapter adapter) {
		//adapter.setmOnSizeChangedListener(this.mOnSizeChangedListener);
		this.mGalleryViewPager.setAdapter(adapter);
	}

	public void setPageMargin(int i) {
		mGalleryViewPager.setPageMargin(i);
	}

	public void setUrlData(List<String> urls) {
		mAdapter.setData(urls);
	}

//	public void setOffscreenPageLimit(int paramInt1, int paramInt2) {
//		this.mGalleryViewPager.setOffscreenPageLimit(paramInt1);
//		this.mGifMaxMemory = (ResUtil.getBitmapMaxMemory(getContext()) - 2 * (paramInt2 * (1 + paramInt1 * 2)));
//		this.mGifMaxMemory = (int) (0.8D * this.mGifMaxMemory);
//		if (this.mGifMaxMemory < Config.THREAD_GIF_MIN_USE_MEMORY) {
//			this.mGifPlayRealseOther = true;
//			this.mGifMaxMemory = (int) (0.7D * ResUtil.getBitmapMaxMemory(getContext()));
//		} else {
//			this.mGifPlayRealseOther = false;
//		}
//		
//		PagerAdapter adapter = null;
//		
//		adapter = this.mGalleryViewPager.getAdapter();
//		if ((adapter != null) && ((adapter instanceof ImagePagerAdapter)))
//			((ImagePagerAdapter) adapter).setGifMaxUseableMem(this.mGifMaxMemory);
//	}
//	
//	public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
//		this.mUserOnPageChangeListener = listener;
//	}
//	
//	public void setItemOnclickListener(View.OnClickListener listener) {
//		this.mAdapter.setOnClickListener(listener);
//	}
//	
//	public void setCurrentItem(int item, boolean smoothScroll) {
//		this.mGalleryViewPager.setCurrentItem(item, smoothScroll);
//	}
	
//	public void setZoomButton(TouchImageView view) {
//		if (view != null) {
//			if (view.canZoomIn())
//				mZoomIn.setEnabled(true);
//			else
//				mZoomIn.setEnabled(false);
//			if (view.canZoomOut())
//				mZoomOut.setEnabled(true);
//			else
//				mZoomOut.setEnabled(false);
//		} else {
//			mZoomOut.setEnabled(false);
//			mZoomIn.setEnabled(false);
//		}
//	}
}
