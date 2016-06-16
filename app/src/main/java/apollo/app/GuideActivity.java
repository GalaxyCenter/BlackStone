package apollo.app;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import apollo.app.home.MainTabActivity;

// http://www.cnblogs.com/nanguabing/archive/2012/12/08/2808275.html
public class GuideActivity extends Activity {

	private HotspotPagerAdapter mHotspotAdapter;
	private ViewPager mHotspotPager;
	private List<Bitmap> mBitmapList;
	private List<View> mHotspotViews;
	private Button mStartButton;
	public View.OnClickListener mStartOnClickListener;
	
	private int[] mImage;
		
	private class HotspotPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return mImage.length;
		}

		@Override
		public boolean isViewFromObject(View view, Object obj) {
			return view == obj;
		}

		public void destroyItem(View view, int i, Object obj) {
			((ViewPager) view).removeView(mHotspotViews.get(i));
		}
		
		public Object instantiateItem(View view, int i) {
			((ViewPager) view).addView(mHotspotViews.get(i), 0);
			mStartButton = (Button) findViewById(R.id.enter);
			if (mStartButton != null)
				mStartButton.setOnClickListener(mStartOnClickListener);
			return mHotspotViews.get(i);
		}
	}
	
	public static void startActivity(Context context) {
		Intent intent = null;
		
		intent = new Intent(context, GuideActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);		
		
		initViews();
		initListener();
	}
	
	@Override
	public void onDestroy() {
		View view = null;
		Bitmap bmp = null;
		
		super.onDestroy();
		
		for (int idx=0; idx<mImage.length; idx++) {
			view = (View) this.mHotspotViews.get(idx);
			view.setBackgroundDrawable(null);
			
			bmp = (Bitmap) this.mBitmapList.get(idx);
			if (bmp.isRecycled() == false) {
				bmp.recycle();
			}
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
		boolean flag = false;
		
		flag = super.onKeyDown(keyCode, event);
		startApp();
		
		return flag;
	}
	
	private void startApp() {
		ProgressBar progress = null;
		
		progress = (ProgressBar) super.findViewById(R.id.progress);
		progress.setVisibility(View.GONE);
		MainTabActivity.startActivity(this, MainTabActivity.ACTIVITY_HOME);
		finish();
	}
	
	private void initViews() {
		Bitmap bmp = null;
		LayoutInflater inflater = null;
		View view = null;
		
		this.mImage = new int[] {R.drawable.guide0, R.drawable.guide1, R.drawable.guide2, R.drawable.guide3, R.drawable.guide4};
		this.mHotspotAdapter = new HotspotPagerAdapter();
		this.mHotspotPager = (ViewPager) super.findViewById(R.id.pagerview);
		this.mHotspotPager.setAdapter(this.mHotspotAdapter);
		this.mBitmapList = new ArrayList<Bitmap>();
		this.mHotspotViews = new ArrayList<View>();
		 
		inflater = getLayoutInflater();
		for (int idx=0; idx<mImage.length; idx++) {
			bmp = BitmapFactory.decodeResource(getResources(), mImage[idx]);
			
			if (idx == (mImage.length - 1)) {
				view = inflater.inflate(R.layout.view_guide, null);
			} else {
				view = new ImageView(this);
			}
			view.setBackgroundDrawable(new BitmapDrawable(bmp));
			this.mBitmapList.add(bmp);
			this.mHotspotViews.add(idx, view);
		}
	}
	
	private void initListener() {
		this.mStartOnClickListener = new View.OnClickListener() {
			public void onClick(View paramView) {
				GuideActivity.this.startApp();
			}
		};
	}
}
