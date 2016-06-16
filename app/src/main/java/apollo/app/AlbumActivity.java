package apollo.app;

import java.util.ArrayList;
import java.util.HashMap;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalBitmap.ImageLoadCompleteListener;

import com.dodowaterfall.widget.FlowView;

import sqq.ScrollViewPull.widget.TryPullToRefreshScrollView;
import sqq.ScrollViewPull.widget.TryPullToRefreshScrollView.OnScrollListener;
import sqq.ScrollViewPull.widget.TryRefreshableView;
import sqq.ScrollViewPull.widget.TryRefreshableView.OnBottomListener;
import sqq.ScrollViewPull.widget.TryRefreshableView.RefreshListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import apollo.bll.Gallerys;
import apollo.core.ApolloApplication;
import apollo.core.RequestResponseCode;
import apollo.data.model.Constants;
import apollo.data.model.Gallery;
import apollo.data.model.Photo;
import apollo.data.model.User;
import apollo.util.DataSet;

public class AlbumActivity extends BaseActivity implements ImageLoadCompleteListener {
	private static final int DOWNREFRESH = 1;
	private static final int UPREFRESH = 2;
	protected static final String TAG = "AlbumActivity";

	private TryPullToRefreshScrollView waterfall_scroll;
	private LinearLayout waterfall_container;
	private Display mDisplay;
	private FinalBitmap fb;
	private TryRefreshableView rv;
	private View firstView;
	private TextView mTopTitle = null;
	private Button mBtnBack = null;
	private Gallery mGallery;
	private User mUser;
	
	private int mLoadedCount = 0;
	private int mTotalRecords = 0;
	private int mItemWidth;
	private int mColumnSize = Constants.ALBUM_COLUMN_SIZE;// 显示列数
	private int mPageSize = Constants.PAGE_SIZE;// 每次加载30张图片
	private int mPageIndex = 0;// 当前页数
	private int mScrollHeight;
	private int mRefreshType = UPREFRESH;
	
	private int[] topIndex;
	private int[] bottomIndex;
	private int[] lineIndex;
	private int[] mColumnHeight;// 每列的高度

	private HashMap<Integer, Integer>[] pin_mark = null;
	//private List<LinkedList<View>> all_screen_view; // 封装每屏View集合的集合
	private ArrayList<LinearLayout> mWaterfallItems;

	public static void startActivityForResult(Activity activity, Gallery gallery, User user, int requestCode, String action) {
		Intent intent = null;
		Bundle bundle = null;
		
		bundle = new Bundle();
		bundle.putParcelable("gallery", gallery);
		bundle.putParcelable("user", user);
		
		intent = new Intent(activity, AlbumActivity.class);
		intent.putExtras(bundle);
		intent.setAction(action);
		activity.startActivityForResult(intent, requestCode);
	}

	private class GetPhotoAsyncTask extends AsyncTask<Object, Integer, DataSet<Photo>> {

		private Context mContext;

		public GetPhotoAsyncTask(Context context) {
			super();
			mContext = context;
		}

		@Override
		protected DataSet<Photo> doInBackground(Object... params) {
			User user = null;
			Integer gid = null;
			Integer page_index = null;
			Integer page_size = null;
			
			user = (User) params[0];
			gid = (Integer) params[1];
			page_index = (Integer) params[2];
			page_size = (Integer) params[3];
			return Gallerys.loadPhotos(user, gid, page_index, page_size);
		}

		@Override
		protected void onPostExecute(DataSet<Photo> result) {
			mTotalRecords = result.getTotalRecords();
			for (Photo info : result.getObjects()) 
				fb.display(info);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = null;
		
		setContentView(R.layout.activity_album);
		intent = getIntent();
		//all_screen_view = new ArrayList<LinkedList<View>>();

		mDisplay = this.getWindowManager().getDefaultDisplay();
		// 根据屏幕大小计算每列大小
		mItemWidth = mDisplay.getWidth() / mColumnSize + 2;

		mColumnHeight = new int[mColumnSize];
		pin_mark = new HashMap[mColumnSize];

		fb = new FinalBitmap(this).init();// 必须调用init初始化FinalBitmap模块
		fb.setCompleteListener(this);

		this.lineIndex = new int[mColumnSize];
		this.bottomIndex = new int[mColumnSize];
		this.topIndex = new int[mColumnSize];

		for (int i = 0; i < mColumnSize; i++) {
			lineIndex[i] = -1;
			bottomIndex[i] = -1;
			pin_mark[i] = new HashMap();
		}
		mGallery = (Gallery)intent.getExtras().get("gallery");
		mUser = (User) intent.getExtras().get("user");
		initViews();
		initListeners();
	}
	
	private void initListeners() {
		rv.setRefreshListener(new RefreshListener() {

			@Override
			public void onDownRefresh() {
				if (rv.mRefreshState == TryRefreshableView.READYTOREFRESH) {
					// 记录第一个view的位置
					firstView = mWaterfallItems.get(0).getChildAt(0);
					mRefreshType = DOWNREFRESH;
					addItemToContainer(++mPageIndex, mPageSize);
				}
			}
		});
		rv.setOnBottomListener(new OnBottomListener() {

			@Override
			public void onBottom() {
				if (rv.mRefreshState != TryRefreshableView.REFRESHING) {
					mRefreshType = UPREFRESH;
					addItemToContainer(++mPageIndex, mPageSize);
				}
			}
		});

		waterfall_scroll.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onAutoScroll(int l, int t, int oldl, int oldt) {

				// Log.d("MainActivity",
				// String.format("%d  %d  %d  %d", l, t, oldl, oldt));

				// Log.d("MainActivity", "range:" + range);
				// Log.d("MainActivity", "range-t:" + (range - t));

				if (pin_mark.length <= 0) 
					return;
				
				mScrollHeight = waterfall_scroll.getMeasuredHeight();
				Log.d("MainActivity", "scroll_height:" + mScrollHeight);

				if (t > oldt) {// 向下滚动
					if (t > 3 * mScrollHeight) {// 超过两屏幕后

						for (int k = 0; k < mColumnSize; k++) {

							LinearLayout localLinearLayout = mWaterfallItems
									.get(k);

							if (pin_mark[k].get(Math.min(bottomIndex[k] + 1,
									lineIndex[k])) <= t + 3 * mScrollHeight) {// 最底部的图片位置小于当前t+3*屏幕高度
								View childAt = localLinearLayout.getChildAt(Math.min(
												1 + bottomIndex[k],
												lineIndex[k]));

								FlowView picView = (FlowView) childAt.findViewById(R.id.news_pic);
								if (picView.bitmap == null
										&& !TextUtils.isEmpty(picView.getUrl())) {
									fb.reload(picView.getUrl(), picView);
								}
								bottomIndex[k] = Math.min(1 + bottomIndex[k],
										lineIndex[k]);

							}
							// Log.d("MainActivity",
							// "headIndex:" + topIndex[k] + "  footIndex:"
							// + bottomIndex[k] + "  headHeight:"
							// + pin_mark[k].get(topIndex[k]));
							if (pin_mark[k].get(topIndex[k]) < t - 2
									* mScrollHeight) {// 未回收图片的最高位置<t-两倍屏幕高度

								int i1 = topIndex[k];
								topIndex[k]++;
								((FlowView) localLinearLayout.getChildAt(i1)
										.findViewById(R.id.news_pic)).recycle();
								Log.d("MainActivity", "recycle,k:" + k
										+ " headindex:" + topIndex[k]);

							}
						}

					}
				} else {// 向上滚动
					if (t > 3 * mScrollHeight) {// 超过两屏幕后
						for (int k = 0; k < mColumnSize; k++) {
							LinearLayout localLinearLayout = mWaterfallItems
									.get(k);
							if (pin_mark[k].get(bottomIndex[k]) > t + 3
									* mScrollHeight) {
								((FlowView) localLinearLayout.getChildAt(
										bottomIndex[k]).findViewById(
										R.id.news_pic)).recycle();
								Log.d("MainActivity", "recycle,k:" + k
										+ " headindex:" + topIndex[k]);

								bottomIndex[k]--;
							}

							if (pin_mark[k].get(Math.max(topIndex[k] - 1, 0)) >= t
									- 2 * mScrollHeight) {
								FlowView picView = ((FlowView) localLinearLayout
										.getChildAt(
												Math.max(-1 + topIndex[k], 0))
										.findViewById(R.id.news_pic));

								if (picView.bitmap == null
										&& !TextUtils.isEmpty(picView.getUrl())) {
									fb.reload(picView.getUrl(), picView);
								}

								topIndex[k] = Math.max(topIndex[k] - 1, 0);
							}
						}
					}

				}
			}
		});
		
		this.mBtnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlbumActivity.this.finish();
			}
		});
	}

	private void initViews() {
		
		mTopTitle = (TextView) findViewById(R.id.top_title);
		mTopTitle.setText(mGallery.getSubject());
		
		waterfall_scroll = (TryPullToRefreshScrollView) findViewById(R.id.waterfall_scroll);
		rv = (TryRefreshableView) findViewById(R.id.trymyRV);
		rv.sv = waterfall_scroll;
		// 隐藏mfooterView

		waterfall_container = (LinearLayout) findViewById(R.id.waterfall_container);
		mWaterfallItems = new ArrayList<LinearLayout>();
		for (int i = 0; i < mColumnSize; i++) {
			LinearLayout itemLayout = new LinearLayout(this);
			LinearLayout.LayoutParams itemParam = new LinearLayout.LayoutParams(
					mItemWidth, LayoutParams.WRAP_CONTENT);

			itemLayout.setPadding(2, 0, 2, 2);
			itemLayout.setOrientation(LinearLayout.VERTICAL);

			itemLayout.setLayoutParams(itemParam);
			mWaterfallItems.add(itemLayout);
			waterfall_container.addView(itemLayout);
		}
		this.mBtnBack = (Button) findViewById(R.id.back);
		// 第一次加载
		addItemToContainer(mPageIndex, mPageSize);
	}

	GetPhotoAsyncTask task = new GetPhotoAsyncTask(this);

	private void addItemToContainer(int pageIndex, int pageSize) {
		if (task.getStatus() != Status.RUNNING) {
			if (mLoadedCount >= mTotalRecords) {
				mLoadedCount = 0;
				GetPhotoAsyncTask task = new GetPhotoAsyncTask(this);
				task.execute(mUser, mGallery.getId(), pageIndex, pageSize);
			}
		}
	}

	private int getMinValue(int[] array) {
		int m = 0;
		int length = array.length;
		for (int i = 0; i < length; ++i) {

			if (array[i] < array[m]) 
				m = i;
			
		}
		return m;
	}

	@Override
	public synchronized void onLoadComplete(Bitmap bitmap, Photo _info) {		
		++mLoadedCount;
		if (mLoadedCount >= mTotalRecords) 
			rv.finishRefresh();
		

		View convertView = null;
		LayoutParams layoutParams = null;
		TextView timeView = null;
		TextView titleView = null;
		FlowView picv = null;
		LinearLayout.LayoutParams picParams = null;
		
		int layoutHeight = 0;
		int wspec = 0;
		
		convertView = LayoutInflater.from(this).inflate(R.layout.infos_list, null);
		layoutParams = new LayoutParams(mItemWidth, LayoutParams.WRAP_CONTENT);
		convertView.setLayoutParams(layoutParams);
		
		timeView = (TextView) convertView.findViewById(R.id.news_time);
		titleView = (TextView) convertView.findViewById(R.id.news_title);
		picv = (FlowView) convertView.findViewById(R.id.news_pic);
		layoutHeight = (bitmap.getHeight() * mItemWidth) / bitmap.getWidth();// 调整高度

		picParams = new LinearLayout.LayoutParams(mItemWidth, layoutHeight);
		picv.setPhoto(_info);
		picv.setLayoutParams(picParams);
		picv.setImageBitmap(bitmap);
		picv.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				FlowView fv = null;
				
				fv = (FlowView) v;
				if (isPickIntent()) {
					Intent data = null;

					data = new Intent();
					data.putExtra("url", fv.getPhoto().getBigImg());
					setResult(RESULT_OK, data);
					finish();
				} else {
					ArrayList<String> urls = null;
					
					urls = new ArrayList<String>();
					urls.add(fv.getPhoto().getBigImg());
					ImageActivity.startActivity(AlbumActivity.this, urls, 0, fv.getPhoto().getSubject());
				}
			}
		});
		
		titleView.setText(_info.getSubject());
		timeView.setText(_info.getPostDate().toString());
		wspec = MeasureSpec.makeMeasureSpec(mItemWidth, MeasureSpec.EXACTLY);
		convertView.measure(wspec, 0);

		Log.d("MainActivity",
				"titleView.getMeasuredHeight():" + titleView.getMeasuredWidth());

		int h = convertView.getMeasuredHeight();
		int w = convertView.getMeasuredWidth();
		Log.d("MainActivity", "w:" + w + ",h:" + h);

		// 此处计算列值
		int columnIndex = getMinValue(mColumnHeight);

		picv.setColumnIndex(columnIndex);
		lineIndex[columnIndex]++;
		mColumnHeight[columnIndex] += h;
		HashMap<Integer, Integer> hashMap = pin_mark[columnIndex];

		if (mRefreshType == UPREFRESH) {
			hashMap.put(lineIndex[columnIndex], mColumnHeight[columnIndex]);// 第index个view所在的高度
			mWaterfallItems.get(columnIndex).addView(convertView);
		} else {
			for (int i = lineIndex[columnIndex] - 1; i >= 0; i--) 
				hashMap.put(i + 1, hashMap.get(i) + h);
			
			hashMap.put(0, h);
			mWaterfallItems.get(columnIndex).addView(convertView, 0);
		}
		bottomIndex[columnIndex] = lineIndex[columnIndex];
	}

    private boolean isPickIntent() {
        String action = getIntent().getAction();
        return (Intent.ACTION_PICK.equals(action) || Intent.ACTION_GET_CONTENT.equals(action));
    }
}
