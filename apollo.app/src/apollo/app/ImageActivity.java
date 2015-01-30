package apollo.app;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import apollo.util.ResUtil;
import apollo.view.MultiImageView;

public class ImageActivity extends BaseActivity {
	
	private class SaveImageAsyncTask extends AsyncTask<String, Integer, String> {

		private byte mData[];
		private String mUrl;

		public SaveImageAsyncTask(String s, byte abyte0[]) {
			mUrl = null;
			mData = null;
			mUrl = s;
			mData = abyte0;
		}

		public void cancel() {
			mSaveImageTask = null;
			mSave.setVisibility(View.VISIBLE);
			mProgress.setVisibility(View.GONE);
			
			super.cancel(true);
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return null;
		}

		protected void onCancelled() {
			super.onCancelled();
		}

		protected void onPostExecute(String s) {
			showToast(s);
			
			mSaveImageTask = null;
			mSave.setVisibility(View.VISIBLE);
			mProgress.setVisibility(View.GONE);
		}
	}
	
	static final long DOUBLE_PRESS_INTERVAL = 600;
	private int mIndex;
	private int topLayoutStatus = 0;
	private long lastPressTime = 0;
	private boolean changeTopLayoutStatus = false;
	private String mTitle = null;
	
	private MultiImageView mMultiImageView;
	private ProgressBar mProgress;
	private Button mSave;
	private Button mBack;
	private SaveImageAsyncTask mSaveImageTask;
	private TextView mTopTitle;
	private LinearLayout mTopLayout;
	private RelativeLayout mRootLayout;
	final Handler mHandler = new Handler(){};
	private final Runnable mDismissOnScreenControlRunner = new Runnable() {
        public void run() {
            showHideScreenControls();
        }
    };
	private ArrayList<String> mUrls;
	
	public static void startActivity(Context context, ArrayList<String> urls,
			int i, String title) {
		Intent intent = new Intent(context, ImageActivity.class);
		
		if (urls != null && urls.size() > 0) {
			intent.putStringArrayListExtra("url", urls);
			intent.putExtra("index", i);
			intent.putExtra("title", title);
			context.startActivity(intent);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image);

		if (savedInstanceState != null) {
			this.mUrls = savedInstanceState.getStringArrayList("url");
			this.mIndex = savedInstanceState.getInt("index", -1);
			this.mTitle = savedInstanceState.getString("title");
		} else {
			Intent intent = getIntent();
			if (intent != null) {
				this.mUrls = intent.getStringArrayListExtra("url");
				this.mIndex = intent.getIntExtra("index", -1);
				this.mTitle = intent.getStringExtra("title");
			}
		}

		initViews();
		initListeners();
		
		setTitle();
	}

	private void initViews() {
		mRootLayout = (RelativeLayout) findViewById(R.id.layout_root);
		mTopLayout = (LinearLayout) findViewById(R.id.layout_top);
		mProgress = (ProgressBar) findViewById(R.id.progress);
		mSave = (Button) findViewById(R.id.save);
		mBack = (Button) findViewById(R.id.back);	
		mTopTitle = (TextView) findViewById(R.id.top_title);
		
		mMultiImageView = (MultiImageView) findViewById(R.id.pagerview);
		mMultiImageView.setPageMargin(ResUtil.dip2px(this, 8F));
		//mMultiImageView.setOffscreenPageLimit(2, Config.THREAD_IMAGE_MAX_WIDTH* Config.THREAD_IMAGE_MAX_WIDTH);
		mMultiImageView.setUrlData(mUrls);
		//mMultiImageView.setCurrentItem(calCurrentIndex(), false);
	}

	private void initListeners() {		 		
		mSave.setOnClickListener(new  OnClickListener() {

			@Override
			public void onClick(View v) {
				changeTopLayoutStatus = false;
			}
		});
	
		mBack.setOnClickListener(new  OnClickListener() {
			@Override
			public void onClick(View v) {
				changeTopLayoutStatus = false;
				ImageActivity.this.finish();			
			}
		});
	}
		
	@Override
	public boolean dispatchTouchEvent(MotionEvent m) {
		
		switch (m.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			changeTopLayoutStatus = true;
			topLayoutStatus++;
			break;
		case MotionEvent.ACTION_UP:
			long pressTime = System.currentTimeMillis();
			if (pressTime - lastPressTime <= DOUBLE_PRESS_INTERVAL) {
				changeTopLayoutStatus = false;
			} 
			lastPressTime = pressTime;
			topLayoutStatus--;
			if (topLayoutStatus == 0) {
				scheduleDismissOnScreenControls();
			}
			topLayoutStatus = 0;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			topLayoutStatus++;
			break;
		}
		return super.dispatchTouchEvent(m);
	}

	private void scheduleDismissOnScreenControls() {
		mHandler.removeCallbacks(mDismissOnScreenControlRunner);
        mHandler.postDelayed(mDismissOnScreenControlRunner, 300);
	}
		
	private void showHideScreenControls() {
		if (changeTopLayoutStatus == false)
			return;
		
		if (mTopLayout.getVisibility() != View.VISIBLE) {
			Animation animation = new AlphaAnimation(0, 1);
			animation.setDuration(500);
			mTopLayout.startAnimation(animation);
			mTopLayout.setVisibility(View.VISIBLE);
		} else {
			Animation animation = new AlphaAnimation(1, 0);
            animation.setDuration(500);
            mTopLayout.startAnimation(animation);
            mTopLayout.setVisibility(View.INVISIBLE);
		}
	}
	
	private int calCurrentIndex() {
		if (mUrls != null && mUrls.size() > 0) {
			int i = mUrls.size();
			if (mIndex >= i)
				mIndex = i - 1;
			if (mIndex < 0)
				mIndex = 0;
		} else {
			mIndex = 0;
		}
		return mIndex;
	}

	private void setTitle() {
		if (mUrls != null) {
			String name = null;

			name = mTitle + ":" + (mIndex + 1) + "/" + mUrls.size();
			mTopTitle.setText(name);
		}
	}
}
