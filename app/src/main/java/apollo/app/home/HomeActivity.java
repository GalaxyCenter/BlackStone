package apollo.app.home;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import apollo.data.model.Section;
import apollo.data.model.Thread;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;
import apollo.app.BaseActivity;
import apollo.app.PostActivity;
import apollo.app.R;
import apollo.app.ThreadActivity;
import apollo.bll.Threads;
import apollo.util.AsyncImageLoader;
import apollo.util.AsyncImageLoader.OnImageLoaderListener;
import apollo.util.ResUtil;
import apollo.util.TianyaUrlHelp;
import apollo.view.BaseWebView;
import apollo.view.CustomScrollView;

public class HomeActivity extends BaseActivity implements
		BaseWebView.OnLoadUrlListener {
 
	private List<View> mHotspotViews;
	private List<Thread> mRecommImageThreads;

	private TextView mBannerText;
	private ProgressBar mProgressBar;
	private CustomScrollView mScrollView;
	private View mHotspotView;
	private View mHotspotWebView;
	private ViewPager mHotspotPager;
	private BaseWebView mWebview;
	private ImageView mWebviewFailImageView;
	private ImageView[] mRadioButtons;
	private HomeRecommendAsyncTask mRecommendAsyncTask;
	private HotspotAsyncTask mHotspotAsyncTask;
	private HotspotPagerAdapter mHotspotAdapter;
	private AsyncImageLoader<View> mAsyncImageLoader;
	private View.OnClickListener mHotspotClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			Thread thread;

			thread = (Thread) v.getTag();
			PostActivity.startActivity(HomeActivity.this, thread);
		}
	};
	
	private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int i) {
			for (ImageView iv : mRadioButtons) 
				iv.setBackgroundResource(R.drawable.banner_dot);
			
			i %= mRadioButtons.length;

			mRadioButtons[i].setBackgroundResource(R.drawable.banner_dot_hl);

			if (mRecommImageThreads != null && mRecommImageThreads.size() > 0) 
				mBannerText.setText(mRecommImageThreads.get(i).getSubject());
			
		}
	};

	private class HotspotAsyncTask extends
			AsyncTask<Object, Integer, List<Thread>> {

		@Override
		protected List<Thread> doInBackground(Object... params) {
			List<Thread> threads = null;

			threads = Threads.getRecommendImageThread();

			return threads;
		}

		@Override
		protected void onPostExecute(List<Thread> threads) {
			mRecommImageThreads = threads;
			buildSilder(mRecommImageThreads.size());
			
			onRefreshFinish();
		}
	}

	private class HomeRecommendAsyncTask extends AsyncTask<Object, Integer, String> {
		@Override
		protected String doInBackground(Object... params) {
			String data = null;
			List<String> re_html = null;
			int width = 0;
			
			width = ResUtil.getEquipmentWidth(HomeActivity.this) - 98;
			data = ResUtil.read(getAssets(), "tp_recommend.html");
			re_html = Threads.getRecommend();
			data = MessageFormat.format(data, re_html.get(0), re_html.get(1),
					re_html.get(2), re_html.get(3), width);

			return data;
		}
		
		@Override
		protected void onPostExecute(String data) {
			mWebview.loadDataWithBaseURL(null, data, "text/html", "utf-8", null);
			mWebview.setVisibility(View.VISIBLE);
			onRefreshFinish();
		}
	}

	private class HotspotPagerAdapter extends PagerAdapter {

		private int tag;

		@Override
		public int getCount() {
			return Integer.MAX_VALUE;
		}

		@Override
		public boolean isViewFromObject(View view, Object obj) {
			return view == obj;
		}

		public void destroyItem(View view, int i, Object obj) {
			if (i >= mHotspotViews.size())
				i %= mHotspotViews.size();

			if (i < 0)
				i = -i % mHotspotViews.size();

			if (tag != i)
				((ViewPager) view).removeView(mHotspotViews.get(i));
		}

		public Object instantiateItem(View view, int i) {
			if (mHotspotViews.size() == 0)
				return null;
			
			if (i >= mHotspotViews.size()) 
				i %= mHotspotViews.size();
			
			if (i < 0) 
				i = -i % mHotspotViews.size();
			
			tag = i;
			if (mHotspotViews.get(i).getParent() == view) 
				((ViewPager) view).removeView(mHotspotViews.get(i));
			
			((ViewPager) view).addView(mHotspotViews.get(i), 0);
			return mHotspotViews.get(i);
		}
	}

	private void initViews() {
		mScrollView = (CustomScrollView) super.findViewById(R.id.mScrollView);
		mProgressBar = (ProgressBar) super.findViewById(R.id.progress);
		mHotspotAdapter = new HotspotPagerAdapter();
		mHotspotView = super.findViewById(R.id.hotspot_item);
		mHotspotPager = (ViewPager) mHotspotView.findViewById(R.id.pagerview);
		mHotspotViews = new ArrayList<View>();
		mHotspotPager.setOnPageChangeListener(this.mOnPageChangeListener);
		mHotspotPager.setAdapter(mHotspotAdapter);
		mHotspotPager.setCurrentItem(0);

		mHotspotWebView = super.findViewById(R.id.hotspot_webview_item);
		mWebview = (BaseWebView) mHotspotWebView.findViewById(R.id.home_webView);
		
		mWebview.setOnLoadUrlListener(this);
		mWebviewFailImageView = (ImageView) mHotspotWebView.findViewById(R.id.webview_fail_imageview);
		
		refresh();
	}
	
	private void buildSilder(int count) {
		ImageView image_view = null;
		RelativeLayout layout = null;
		RelativeLayout.LayoutParams rly_params = null;
		MarginLayoutParams ml_params = null;
		
		if (mRadioButtons != null) 
			return;
	
		layout = (RelativeLayout) mHotspotView.findViewById(R.id.banner_layout);
		mRadioButtons = new ImageView[count];
		
		ml_params = new MarginLayoutParams(5, 5);
		for (int i = 0; i < count; i++) {
			// 创建图片
			image_view = new ImageView(this);
			mHotspotPager.addView(image_view);
			
			image_view.setOnClickListener(mHotspotClickListener);
			mHotspotViews.add(i, image_view);
			
			// 创建小圆点
			rly_params = new LayoutParams(ml_params);
			rly_params.addRule(RelativeLayout.CENTER_IN_PARENT);
			rly_params.rightMargin = ResUtil.dip2px(this, 10f);
			
			if (i == count - 1)
				rly_params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			else
				rly_params.addRule(RelativeLayout.ALIGN_RIGHT, i+200);
			
			image_view = new ImageView(this);
			image_view.setId(199+i);
		
			mRadioButtons[i] = image_view;
			layout.addView(image_view, rly_params);
		}
		
		// 创建标题栏
		rly_params = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		rly_params.addRule(RelativeLayout.CENTER_IN_PARENT);
		rly_params.addRule(RelativeLayout.ALIGN_RIGHT, 199);
		rly_params.setMargins(10, 0, 10, 0);
		
		mBannerText = new TextView(this);
		mBannerText.setTextSize(14);
		mBannerText.setTextColor(0xffffffff);
		mBannerText.setSingleLine();
		mBannerText.setId(R.id.banner_text);
		layout.addView(mBannerText, rly_params);
	}

	private void cancelTask() {
		if (this.mRecommendAsyncTask != null)
			this.mRecommendAsyncTask.cancel(true);
		if (this.mHotspotAsyncTask != null)
			this.mHotspotAsyncTask.cancel(true);
	}

	private void refresh() {
		mProgressBar.setVisibility(View.VISIBLE);

		cancelTask();

		mRecommendAsyncTask = new HomeRecommendAsyncTask();
		mRecommendAsyncTask.execute();

		mHotspotAsyncTask = new HotspotAsyncTask();
		mHotspotAsyncTask.execute();
	}

	private void onRefreshFinish() {
		Drawable drawable = null;
		
		this.mProgressBar.setVisibility(View.GONE);
		this.mScrollView.setVisibility(View.VISIBLE);
		
		drawable = getResources().getDrawable(R.drawable.banner_dot);
		
		if (mRadioButtons != null && mRadioButtons.length != 0) {
			for (ImageView iv : mRadioButtons) 
				iv.setBackgroundDrawable(drawable);
						
			drawable = getResources().getDrawable(R.drawable.banner_dot_hl);
			mRadioButtons[0].setBackgroundDrawable(drawable);
		}
		
		if (mRecommImageThreads != null && mRecommImageThreads.size() > 0) {
			View view = null;
			Thread thread = null;
			Bitmap bmp = null;
			
			drawable = getResources().getDrawable(R.drawable.image_fail);
			for (int idx = 0; idx < mHotspotViews.size(); idx++) {
				thread = mRecommImageThreads.get(idx);
				view = this.mHotspotViews.get(idx);
				view.setTag(thread);
				bmp = mAsyncImageLoader.loadImage(thread.getIcon(), view,
						new OnImageLoaderListener<View>() {
							@Override
							public void imageLoaded(Bitmap bmp, View view, String url) {
								view.setBackgroundDrawable(new BitmapDrawable(bmp));
							}
						});
				if (bmp == null)
					view.setBackgroundDrawable(drawable);
				else
					view.setBackgroundDrawable(new BitmapDrawable(bmp));
			}

			mBannerText.setText(mRecommImageThreads.get(0).getSubject());
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAsyncImageLoader = new AsyncImageLoader<View>();
		setContentView(R.layout.activity_home);
		initViews();
	}

	@Override
	public boolean shouldOverrideUrlLoading(WebView webview, String s) {

		if (s.indexOf("post") > 0) {
			Thread thread;

			thread = TianyaUrlHelp.parseThreadUrl(s);
			PostActivity.startActivity(this, thread);
		} else {
			Section section = null;

			section = TianyaUrlHelp.parseSectionUrl(s);
			ThreadActivity.startActivity(this, section);
		}

		return true;
	}
}
