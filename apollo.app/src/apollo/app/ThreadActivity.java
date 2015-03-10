package apollo.app;

import java.util.ArrayList;
import java.util.List;

import org.miscwidgets.widget.Panel;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import apollo.app.home.MainTabActivity;
import apollo.app.home.PersonInfoActivity;
import apollo.bll.AutoPosts;
import apollo.bll.Bookmarks;
import apollo.bll.Sections;
import apollo.bll.Threads;
import apollo.core.ApolloApplication;
import apollo.core.RequestResponseCode;
import apollo.data.model.AutoPost;
import apollo.data.model.Constants;
import apollo.data.model.Section;
import apollo.data.model.Thread;
import apollo.data.model.User;
import apollo.enums.PostMode;
import apollo.enums.SectionType;
import apollo.enums.SortBy;
import apollo.enums.ThreadViewType;
import apollo.util.AsyncImageLoader;
import apollo.util.AsyncImageLoader.OnImageLoaderListener;
import apollo.util.DataSet;
import apollo.util.ImageUtil;
import apollo.util.ResUtil;
import apollo.widget.DialogMoreAdapter;

public class ThreadActivity extends BaseActivity implements OnItemClickListener,OnItemLongClickListener {
	
	private int mPageIndex;
	private int mPageSize;
	private int mTotalThread;
	private String mBackTab;
	
	private Section mSection;
	private User mUser;
	private List<Thread> mThreads;
	private ThreadViewType mThreadViewType;
	private ThreadViewType mPreThreadViewType;
	private SortBy mSortBy;
	private PostMode mPostMode;
	
	private TextView mFootTitle;
	private TextView mTopTitle;
	private TextView mSearchText = null;
	private Button mBtnBack;
	private Button mBtnSearch;
	private Button mBtnPost;
	private Button mButtonSearchClean = null;
	private Panel mSearchPanel;
	private ProgressBar mProgressBar;
	private ProgressBar mFootProgressBar;
	private RelativeLayout mListFooter;
	private RelativeLayout mContentLayout = null;
	private LinearLayout mTopMiddleLayout = null;
	private ListView mList = null;
	private Dialog mMoreDialog = null;
	private DialogMoreAdapter mDialogAdapter;
	private AsyncImageLoader<ImageView> mAsyncImageLoader = null;
	private InputMethodManager mInputManager = null;
	
	private ThreadListAdapter mAdapter;
	private ThreadsAsyncTask mThreadsTask;
	private SectionAsyncTask mSectionTask;
	
	static class ThreadViewHolder {
		private ImageView thread_usericon;
		private ImageView thread_user_approved;
		private ImageView thread_hasimage;
		private TextView thread_author;
		private TextView thread_subject;
		private TextView thread_postdate;
		private TextView thread_replies;
		private TextView thread_views;
	}
	
	class SectionAsyncTask extends AsyncTask<String, Integer, Section> {
		@Override
		protected Section doInBackground(String... params) {
			Section section = null;
			
			section = Sections.getSection(params[0]);
			return section;
		}
		
		@Override
		protected void onPostExecute(Section section) {
			mSection.setName(section.getName());
			mTopTitle.setText(section.getName());
		}
	}
	
	class ThreadsAsyncTask extends AsyncTask<Object, Integer, String> {

		@Override
		protected String doInBackground(Object... params) {
			List<Thread> threads = null;
			ThreadViewType viewType = null;
			int pageIndex = 0;
			int pageSize = 0;
			
			viewType = (ThreadViewType)params[0];
			pageIndex = (Integer) params[1];
			pageSize = (Integer) params[2];
			
			if (ThreadViewType.NORMAL.equals(viewType)) {
				threads = Threads.getThreads(mSection.getSectionId(), pageIndex, pageSize, mSortBy);
				ThreadActivity.this.mThreads.addAll(threads);
				
			} else if (ThreadViewType.SEARCH.equals(viewType)) {
				String searchTerms = (String) params[3];
				
				threads = Threads.search(mSection.getSectionId(), searchTerms, pageIndex);
				ThreadActivity.this.mThreads.addAll(threads);
				
			} else if (ThreadViewType.BOOKMARK.equals(viewType)) {
				DataSet<Thread> datas = null;
				
				datas = Bookmarks.getThreads(mUser, pageIndex, pageSize);
				threads = datas.getObjects();
				mTotalThread = datas.getTotalRecords();
				ThreadActivity.this.mThreads.addAll(threads);
				
			} else if (ThreadViewType.USER.equals(viewType)) {
				
				if (mPostMode.equals(PostMode.CREATE)) {
					threads = Threads.getThreads(mUser, SectionType.PUBLIC, PostMode.CREATE, pageIndex, pageSize);
					ThreadActivity.this.mThreads.addAll(threads);
					threads = Threads.getThreads(mUser, SectionType.TECH, PostMode.CREATE, pageIndex, pageSize);
					ThreadActivity.this.mThreads.addAll(threads);
					threads = Threads.getThreads(mUser, SectionType.CITY, PostMode.CREATE, pageIndex, pageSize);
					ThreadActivity.this.mThreads.addAll(threads);
				} else {
					threads = Threads.getThreads(mUser, SectionType.PUBLIC, PostMode.REPLY, pageIndex, pageSize);
					ThreadActivity.this.mThreads.addAll(threads);
					threads = Threads.getThreads(mUser, SectionType.TECH, PostMode.REPLY, pageIndex, pageSize);
					ThreadActivity.this.mThreads.addAll(threads);
					threads = Threads.getThreads(mUser, SectionType.CITY, PostMode.REPLY, pageIndex, pageSize);
					ThreadActivity.this.mThreads.addAll(threads);
				}				
			} else if (ThreadViewType.AUTOPOST.equals(viewType)) {
				DataSet<AutoPost> datas = null;
				
				threads = new ArrayList<Thread>();
				datas = AutoPosts.getAutoPosts(pageIndex, pageSize);
				for(AutoPost post:datas.getObjects()) {
					threads.add(post.thread);
				}
				ThreadActivity.this.mThreads.addAll(threads);
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			mAdapter.notifyDataSetChanged();
			onRefreshFinish();
		}
	}
	
	class ThreadListAdapter extends BaseAdapter {
		
		private LayoutInflater inflater;
		private List<Thread> threads;
		
		public ThreadListAdapter(Context context, List<Thread> threads) {
			super();
			this.threads = threads;
			this.inflater = LayoutInflater.from(context);
		}
		
		public List<Thread> getThreads() {
			return threads;
		}
		public void setThreads(List<Thread> threads) {
			this.threads = threads;
		}
		@Override
		public int getCount() {
			return this.threads.size();
		}

		@Override
		public Object getItem(int position) {
			return this.threads.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Thread thread = null;
			ThreadViewHolder viewHolder = null;
			String icon_url = null;
			
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_list_thread, null);
				viewHolder = new ThreadViewHolder();
				viewHolder.thread_usericon = (ImageView) convertView.findViewById(R.id.user_head_icon);
				viewHolder.thread_user_approved = (ImageView) convertView.findViewById(R.id.thread_user_approved);
				viewHolder.thread_hasimage = (ImageView) convertView.findViewById(R.id.thread_hasimage);
				viewHolder.thread_author = (TextView) convertView.findViewById(R.id.author);
				viewHolder.thread_subject = (TextView) convertView.findViewById(R.id.thread_subject);
				viewHolder.thread_postdate = (TextView) convertView.findViewById(R.id.postdate);
				viewHolder.thread_replies = (TextView) convertView.findViewById(R.id.thread_replies);
				viewHolder.thread_views = (TextView) convertView.findViewById(R.id.thread_views);
				
				View.OnClickListener listener = new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						PersonInfoActivity.startActivity(ThreadActivity.this, (User)v.getTag());
					}
				};
				viewHolder.thread_usericon.setOnClickListener(listener);
				viewHolder.thread_author.setOnClickListener(listener);
				
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ThreadViewHolder) convertView.getTag();
			}
			
			thread = this.threads.get(position);
			
			if (ThreadViewType.AUTOPOST.equals(mThreadViewType)) {
				viewHolder.thread_subject.setText(thread.getSubject());
				((View)viewHolder.thread_usericon.getParent()).setVisibility(View.GONE);
				((View)viewHolder.thread_views.getParent()).setVisibility(View.GONE);
				viewHolder.thread_user_approved.setVisibility(View.GONE);
				viewHolder.thread_author.setVisibility(View.GONE);
			} else {
				viewHolder.thread_subject.setText(thread.getSubject());
				viewHolder.thread_author.setText(thread.getAuthor().getName());
				viewHolder.thread_author.setTag(thread.getAuthor());
				viewHolder.thread_postdate.setText(thread.getUpdateDate().toString("yyyy-MM-dd HH:mm"));
				viewHolder.thread_replies.setText(Integer.toString(thread.getReplies()));
				viewHolder.thread_views.setText(Integer.toString(thread.getViews()));
				
				icon_url = "http://tx.tianyaui.com/logo/small/" + thread.getAuthor().getUserId();
				viewHolder.thread_usericon.setTag(thread.getAuthor());
				Bitmap bmp = mAsyncImageLoader.loadImage(icon_url, viewHolder.thread_usericon, new OnImageLoaderListener<ImageView>() {
	                @Override
	                public void imageLoaded(Bitmap bmp, ImageView view, String url) {
	                	String icon_url = "http://tx.tianyaui.com/logo/small/" + ((User)view.getTag()).getUserId();
	                	
	                	if (url.equals(icon_url)) {
		                	bmp = ImageUtil.toRoundCorner(bmp, 10);
		                	view.setImageBitmap(bmp);
	                	}
	                }
	            });
				if (bmp != null) 
					viewHolder.thread_usericon.setImageDrawable(new BitmapDrawable(bmp));
				
				if(thread.getAuthor().isApproved()){
					viewHolder.thread_user_approved.setVisibility(View.GONE);//·ÇvipÒþ²Øvip±êÖ¾
				}
				if(thread.hasImage()){
					viewHolder.thread_hasimage.setImageResource(R.drawable.hasimage);
				}
			}
			
			return convertView;
		}
		
	}
	
	public ThreadActivity() {
		super();
		
		this.mPageIndex = 1;
		this.mThreads = new ArrayList<Thread>(mPageSize);
		this.mSortBy = SortBy.LAST_REPLY;
		this.mPostMode = PostMode.CREATE;
	}
	
	public static void startActivity(Activity activity, Section section) {
		startActivity(activity, section, null);
	}
	
	public static void startActivity(Activity activity, Section section, String back_tab) {
		Intent intent = null;
		Bundle bundle = null;
		
		bundle = new Bundle();
		bundle.putParcelable("section", section);
		bundle.putString("back_tab", back_tab);
		
		intent = new Intent(activity, ThreadActivity.class);
		intent.putExtras(bundle);
		activity.startActivity(intent);
	}
	
	public static void startActivity(Activity activity, ThreadViewType viewType) {
		startActivity(activity, null, viewType, null);
	}
	
	public static void startActivity(Activity activity, User user, ThreadViewType viewType) {
		startActivity(activity, user, viewType, null);
	}
	
	public static void startActivity(Activity activity, User user, ThreadViewType viewType, String back_tab) {
		Intent intent = null;
		Bundle bundle = null;
		
		bundle = new Bundle();
		bundle.putParcelable("user", user);
		bundle.putString("back_tab", back_tab);
		
		intent = new Intent(activity, ThreadActivity.class);
		intent.putExtras(bundle);
		intent.putExtra("viewtype", viewType.getValue());
		activity.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Intent intent = null;
	
		super.onCreate(savedInstanceState);
		intent = getIntent();
		mSection = (Section)intent.getExtras().get("section");
		mUser = (User) intent.getExtras().get("user");
		mBackTab = intent.getStringExtra("back_tab");
		
		mThreadViewType = ThreadViewType.get(intent.getIntExtra("viewtype", 0));
		if (mThreadViewType.equals(ThreadViewType.USER)) {
			this.mPageSize = Constants.PAGE_SIZE / 3;
		} else {
			this.mPageSize = Constants.PAGE_SIZE;
		}
		mAsyncImageLoader = new AsyncImageLoader<ImageView>();
		mInputManager = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
		
		setContentView(R.layout.activity_list_thread);
		initViews();
		initListeners();
		refresh();
		
		if (ThreadViewType.NORMAL.equals(mThreadViewType)) {
			mSectionTask = new SectionAsyncTask();
			mSectionTask.execute(mSection.getSectionId());
			mBtnSearch.setVisibility(View.VISIBLE);
		} else if (ThreadViewType.BOOKMARK.equals(mThreadViewType)) {
			mTopTitle.setText(R.string.my_bookmark);
		} else if (ThreadViewType.USER.equals(mThreadViewType)) {
			String topTitle = null;
			topTitle = getResources().getStringArray(R.array.dialog_thread_bookmark_more_menu_item)[0];
			mTopTitle.setText(topTitle);
		} else if (ThreadViewType.AUTOPOST.equals(mThreadViewType)) {
			mTopTitle.setText(R.string.my_autopost);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
		return false;
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Thread thread = null;
		
		thread = mThreads.get(position);
		
		if (ThreadViewType.AUTOPOST.equals(mThreadViewType)) 
			AutoPostConfigActivity.startActivityForResult(this, thread, RequestResponseCode.REQUEST_AUTOPOST_DELETED);
		else
			PostActivity.startActivity(this, thread);
	}
	
	public void closeActivity() {		
		if (TextUtils.isEmpty(mBackTab) == true)
			finish();
		else
			MainTabActivity.startActivity(this, mBackTab);
	}

	private void initViews() {
		View view = null;
		mListFooter = (RelativeLayout)LayoutInflater.from(ThreadActivity.this).inflate(R.layout.footer_list_more, null);
		mFootProgressBar = (ProgressBar) mListFooter.findViewById(R.id.foot_progress);
		mFootTitle = (TextView) mListFooter.findViewById(R.id.footer_title);
		mTopTitle = (TextView) findViewById(R.id.top_title);	
		mBtnBack = (Button) findViewById(R.id.back);		
		mBtnPost = (Button) findViewById(R.id.post);
		mProgressBar = (ProgressBar)findViewById(R.id.progress);
		mBtnSearch = (Button) findViewById(R.id.search);
		mContentLayout = (RelativeLayout) findViewById(R.id.layout_content);
		mTopMiddleLayout = (LinearLayout) findViewById(R.id.layout_top_middle);
		mAdapter = new ThreadListAdapter(ThreadActivity.this, mThreads);
		mList = (ListView) findViewById(R.id.list);
		mList.addFooterView(mListFooter);
		mList.setAdapter(mAdapter);
		
		view = getLayoutInflater().inflate(R.layout.bar_search, null);
		mSearchText = (TextView) view.findViewById(R.id.text);
		mSearchText.setHint(R.string.please_enter_post_subject);
		mButtonSearchClean = (Button) view.findViewById(R.id.clean);
		mSearchPanel = (Panel) view.findViewById(R.id.layout_search);
		mContentLayout.addView(mSearchPanel);
	}
	
	private void createPost() {
		if (ApolloApplication.app().getCurrentUser() == null) {
			LoginActivity.startActivityForResult(this, getString(R.string.login_to_use), true, RequestResponseCode.REQUEST_LOGIN_CREATE_POST);
		} else {
			PostEditActivity.startActivity(this, mSection);
		}		
	}
	
	private void initListeners() {
		if (ThreadViewType.NORMAL.equals(mThreadViewType)) {
			mTopMiddleLayout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mMoreDialog == null) {
						AlertDialog.Builder builder = null;
						View dialogView;
						ListView moreListView = null;
						LayoutParams params = null;
						
						builder = new AlertDialog.Builder(ThreadActivity.this);
						mMoreDialog = builder.create();
						mMoreDialog.setCanceledOnTouchOutside(true);
						mMoreDialog.show();
						
						params = mMoreDialog.getWindow().getAttributes();
						params.gravity = Gravity.CENTER | Gravity.TOP;
						params.x = 2;
						params.y = mTopMiddleLayout.getTop() + mTopMiddleLayout.getHeight() + 10;
						params.alpha = 1.0f;
						params.width = ResUtil.dip2px(ThreadActivity.this, 140f);
						params.height = LayoutParams.WRAP_CONTENT;
						mMoreDialog.getWindow().setAttributes(params);
					
						dialogView = getLayoutInflater().inflate(R.layout.view_dialog_thread_more, null);
						mMoreDialog.setContentView(dialogView);
						
						mDialogAdapter = new DialogMoreAdapter(ThreadActivity.this, getResources().getStringArray(R.array.dialog_thread_normal_more_menu_item));
						moreListView = (ListView) dialogView.findViewById(R.id.list);		
						moreListView.setAdapter(mDialogAdapter);
						moreListView.setOnItemClickListener(new OnItemClickListener() {
		
							@Override
							public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
								String text = null;
								
								switch(position) {
								case 0:
									mSortBy = SortBy.LAST_REPLY;
									break;
								case 1:
									mSortBy = SortBy.LAST_POST;
									break;
								case 2:
									mSortBy = SortBy.VALUE;
									break;
								case 3:
									mSortBy = SortBy.VIEWS;
									break;
								}
								mPageIndex = 1;
								mThreads.clear();
								mAdapter.notifyDataSetChanged();
								refresh();
								
								text = getResources().getStringArray(R.array.dialog_thread_normal_more_menu_item)[position];
								mTopTitle.setText(mSection.getName() + ":" + text);
								mMoreDialog.dismiss();
							}
						});
					} else {
						mMoreDialog.show();
					}
					mDialogAdapter.notifyDataSetInvalidated();
				}
			});
		} else if (ThreadViewType.USER.equals(mThreadViewType)) {
			mTopMiddleLayout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mMoreDialog == null) {
						AlertDialog.Builder builder = null;
						View dialogView;
						ListView moreListView = null;
						LayoutParams params = null;
						
						builder = new AlertDialog.Builder(ThreadActivity.this);
						mMoreDialog = builder.create();
						mMoreDialog.setCanceledOnTouchOutside(true);
						mMoreDialog.show();
						
						params = mMoreDialog.getWindow().getAttributes();
						params.gravity = Gravity.CENTER | Gravity.TOP;
						params.x = 2;
						params.y = mTopMiddleLayout.getTop() + mTopMiddleLayout.getHeight() + 10;
						params.alpha = 1.0f;
						params.width = ResUtil.dip2px(ThreadActivity.this, 140f);
						params.height = LayoutParams.WRAP_CONTENT;
						mMoreDialog.getWindow().setAttributes(params);
					
						dialogView = getLayoutInflater().inflate(R.layout.view_dialog_thread_more, null);
						mMoreDialog.setContentView(dialogView);
						
						mDialogAdapter = new DialogMoreAdapter(ThreadActivity.this, getResources().getStringArray(R.array.dialog_thread_bookmark_more_menu_item));
						moreListView = (ListView) dialogView.findViewById(R.id.list);		
						moreListView.setAdapter(mDialogAdapter);
						moreListView.setOnItemClickListener(new OnItemClickListener() {
		
							@Override
							public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
								String text = null;
								
								switch(position) {
								case 0:
									mPostMode = PostMode.CREATE;
									break;
								case 1:
									mPostMode = PostMode.REPLY;
									break;
								}
								mPageIndex = 1;
								mThreads.clear();
								mAdapter.notifyDataSetChanged();
								refresh();
								
								text = getResources().getStringArray(R.array.dialog_thread_bookmark_more_menu_item)[position];
								mTopTitle.setText(text);
								mMoreDialog.dismiss();
							}
						});
					} else {
						mMoreDialog.show();
					}
					mDialogAdapter.notifyDataSetInvalidated();
				}
			});
		}
		
		mContentLayout.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
			@Override
			public boolean onPreDraw() {
				ViewGroup.LayoutParams params = null;
				
				params = mSearchPanel.getLayoutParams();
				params.width= ResUtil.getEquipmentWidth(ThreadActivity.this) - mBtnSearch.getWidth() - 6;
				mSearchPanel.setLayoutParams(params);
				return true;
			}
		});
		
		mBtnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ThreadActivity.this.closeActivity();
			}
		});
		
		mListFooter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mFootTitle.setText(R.string.loading);
				mFootProgressBar.setVisibility(View.VISIBLE);
				
				refresh();
			}
		});
		
		mBtnPost.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ThreadActivity.this.createPost();
			}
		});

		mBtnSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mSearchPanel.isOpen()) {
					mSearchPanel.setOpen(false, true);
					mThreadViewType = mPreThreadViewType;
					ThreadActivity.super.hidenSoftKeyPad(mInputManager, mSearchText);
					refresh();
				} else {
					mSearchPanel.setOpen(true, true);
					mThreads.clear();
					mSearchText.requestFocus();
					mPreThreadViewType = mThreadViewType;
					mThreadViewType = ThreadViewType.SEARCH;
					ThreadActivity.super.showSoftKeyPad(mInputManager, mSearchText);
				}
				mListFooter.setVisibility(View.GONE);
				mPageIndex = 1;
			}
		});
		
		mButtonSearchClean.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mSearchText.setText("");
				mListFooter.setVisibility(View.GONE);
			}			
		});
		
		mSearchText.addTextChangedListener(new TextWatcher(){

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				if (mThreadsTask != null) {
					mThreadsTask.cancel(true);
				}
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String searchTxt = s.toString().trim();
				if (searchTxt.length() != 0) {
					mThreads.clear();
					mAdapter.notifyDataSetChanged();
					refresh();
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
		mList.setOnItemClickListener(this);
		mList.setOnItemLongClickListener(this);
	}
	
	private void onRefreshFinish() {
		if (ThreadViewType.USER.equals(mThreadViewType)) {
			int maxPage = -1;
			
			maxPage = mTotalThread / mPageSize;
			if (mTotalThread % mPageSize > 0) {
				maxPage++;
			}
			if (mPageIndex >= maxPage) {
				//mFootTitle.setText(R.string.pagination_nomore_message);
				//mListFooter.setOnClickListener(null);
			} else {
				mFootTitle.setText(R.string.more);
			}
		}
		
		mListFooter.setVisibility(View.VISIBLE);
		mProgressBar.setVisibility(View.GONE);
		mList.setVisibility(View.VISIBLE);
		
		mFootTitle.setText(R.string.more);
		mFootProgressBar.setVisibility(View.GONE);
	}
	
	private void refresh() {		
		mProgressBar.setVisibility(View.VISIBLE);
		
		if (mThreadViewType.equals(ThreadViewType.SEARCH)) {
			mThreadsTask = new ThreadsAsyncTask();
			mThreadsTask.execute(ThreadViewType.SEARCH, mPageIndex++, mPageSize, mSearchText.getText().toString());
		} else {
			mThreadsTask = new ThreadsAsyncTask();
			mThreadsTask.execute(mThreadViewType, mPageIndex++, mPageSize);
		}
		
	}
}
