package apollo.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import xshare.framework.Proxy;
import xshare.framework.ProxyActionListener;
import xshare.sina.weibo.SinaWeiboParams;
import xshare.sina.weibo.SinaWeiboProxy;
import xshare.tencent.qzone.QZoneParams;
import xshare.tencent.qzone.QZoneProxy;
import xshare.tencent.weixin.WXParams;
import xshare.tencent.weixin.WXProxy;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import apollo.bll.Bookmarks;
import apollo.bll.Posts;
import apollo.core.ApolloApplication;
import apollo.core.RequestResponseCode;
import apollo.data.model.Post;
import apollo.data.model.Thread;
import apollo.data.model.User;
import apollo.enums.PostMode;
import apollo.enums.PostViewType;
import apollo.exceptions.ApplicationException;
import apollo.util.Formatter;
import apollo.util.ImageUtil;
import apollo.util.ResUtil;
import apollo.util.Transforms;
import apollo.widget.DialogMoreAdapter;
import apollo.widget.PostAdapter;
import apollo.widget.PostAdapter.DisplayFloorHandle;
import apollo.widget.PostAdapter.DisplayOtherHandle;

public class PostActivity extends BaseActivity  implements View.OnClickListener, ProxyActionListener {

	public static final int POST_LOAD_OUTOFRANG = 1002;
	public static final int POST_NOT_FOUND = 1003;
	
	private int mStartIndex;
	private int mFromIndex;
	private int mToIndex;
	private int mPageSize;
	private int mMoreItemCurIdx;
	private List<Post> mNewPosts;
	private List<Post> mPosts;
	private Thread mThread;
	private PostViewType mPostViewType;
	
	private TextView mFootTitle;
	private TextView mTopTitle;
	private View mSkipFloorDialogView;
	private ListView mPostList;
	private EditText mFloor;
	private RelativeLayout mListFooter;
	private LinearLayout mBottomBar;
	private ProgressBar mProgressBar;
	private ProgressBar mFootProgressBar;
	private Button mReply;
	private Button mMore;
	private Button mBtnBack;
	private Button mBtnAddBookMark;
	private Button mSkipFloor;
	private Dialog mSkipFloorDialog;
	private Dialog mMoreDialog;
	private Dialog mShareDialog = null;
	
	private ExecutorService mExeService;
	private AddPostAsyncTask mAddPostTask;
	private AddBookMarkAsyncTask mAddBMTask;
	private GetPostListHandler mHandler;
	private PostAdapter<Post> mAdapter;
	private DialogMoreAdapter mDialogMoreAdapter;
	private PostViewHolder mViewHolder;
	private int mFilterUserId;
	private boolean mFlushPost;
	
	private static int THREADPOOL_SIZE = 4;// ??????С

	public static class PostViewHolder {
		public TextView show_only_user;
		public TextView quote;
		public TextView copy;
		public TextView reply;
		public PostViewItemOnClickListener clickListener;
	}
	
	private class PostViewItemOnClickListener implements View.OnClickListener {

		private PostViewHolder mViewHolder;
		private Post mPost;
		private int mPosition;
		
		@Override
		public void onClick(View v) {
			if (mViewHolder.show_only_user.equals(v)) {	
				showOnlyUser(mPost.getAuthor().getUserId());
			} else if (mViewHolder.copy.equals(v)) {
				/*
				 *   http://www.eoeandroid.com/thread-178889-1-1.html
				 *   http://www.cnblogs.com/plokmju/p/3140099.html
				 *   http://blog.csdn.net/kimifdw/article/details/8196333
				 *   http://wenzhutech.diandian.com/post/2012-06-07/40028957456				 * 
				 * 
				 */
				ClipboardManager cbm = null;

				cbm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
				cbm.setText(Transforms.formatPost(mPost.getBody(), false));
				showToast(getResources().getString(R.string.copy_data_success));
			} else if (mViewHolder.quote.equals(v)) {
				replyThread(PostMode.REPLY_QUOTE, mPost);
			} else if (mViewHolder.reply.equals(v)) {
				replyThread(PostMode.REPLY_FLOOW, mPost);
			}
		}
		
		public void setPost(Post post) {
			mPost = post;
		}
		
		public void setPosition(int position) {
			mPosition = position;
		}
		
		public void setViewHolder(PostViewHolder viewHolder) {
			mViewHolder = viewHolder;
		}
	}
	
	class GetPostListThread implements Runnable {

		@Override
		public void run() {
			Message msg = null;
			
			msg = mHandler.obtainMessage();
			try {
				loadPosts();
			} catch (Exception ex) {
				msg.what = POST_NOT_FOUND;
				msg.obj = ex;
			}
			if (mToIndex == mFromIndex) {
				msg.what = POST_LOAD_OUTOFRANG;
			}
			mHandler.sendMessage(msg);
		}
	}

	class GetPostListHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case POST_NOT_FOUND:
				ApplicationException ex = (ApplicationException) msg.obj;
				showToast(ApolloApplication.app().getString(ex.getResid()));
				break;
			case POST_LOAD_OUTOFRANG:
				showToast(getString(R.string.post_load_outofrang));
				mFlushPost = true;
				break;

			default:
				mPosts.addAll(mNewPosts);
				mAdapter.setPosts(mPosts);
				mAdapter.notifyDataSetChanged();
				if (TextUtils.isEmpty(mTopTitle.getText()))
					mTopTitle.setText(mPosts.get(0).getSubject());
				break;
			}

			mProgressBar.setVisibility(View.GONE);
			mFootProgressBar.setVisibility(View.GONE);
			mFootTitle.setText(R.string.more);
		}
	}
		
	class AddBookMarkAsyncTask extends AsyncTask<Object, Post, Post> {

		@Override
		protected Post doInBackground(Object... params) {
			Thread bm = null;
			User user = null;
			
			bm = (Thread)params[0];
			user = (User) params[1];
			
			try {
				Bookmarks.add(bm, user);
			} catch (ApplicationException ex) {
				ApolloApplication.app().setException(ex);
			}
			return bm;
		}
		
		protected void onPostExecute(Post result) {
			ApplicationException ex = null;
			
			ex = ApolloApplication.app().getException();
			if (ex != null)
				showToast(ex.getMessage());
			else
				showToast(ApolloApplication.app().getString(
						R.string.add_bookmark_succeed));
			
		}
	}
	
	class AddPostAsyncTask extends AsyncTask<Post, Post, Post> {

		@Override
		protected Post doInBackground(Post... params) {
			Post post = null;
			
			post = params[0];
			
			try {
				Posts.add(post, post.getAuthor());
			} catch (ApplicationException ex) {
				ApolloApplication.app().setException(ex);
			}

			return post;
		}
		
		protected void onPostExecute(Post result) {
			ApplicationException ex = null;
			
			ex = ApolloApplication.app().getException();
			if (ex != null) {
				showToast(ex.getMessage());
			} else {
				showToast(ApolloApplication.app().getString(R.string.post_mark_success));
			}
		}
	}
	
	public PostActivity() {
		super();
		this.mFlushPost = false;
		this.mToIndex = 0;
		this.mPageSize = 20;
		this.mPostViewType = PostViewType.NORMAL;
		this.mNewPosts = new ArrayList<Post>(mPageSize);
		this.mPosts = new ArrayList<Post>();
	}
	
	public static void startActivity(Activity activity, Thread thread) {
		Intent intent = null;
		Bundle bundle = null;
		
		bundle = new Bundle();
		bundle.putParcelable("thread", thread);
		intent = new Intent(activity, PostActivity.class);
		intent.putExtras(bundle);
		activity.startActivity(intent);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK ) {
			if (requestCode == RequestResponseCode.REQUEST_POST_REPLY) {
				if (mFlushPost == true) {
					mExeService.submit(new GetPostListThread());
				}					
			}
		}
	}
	
	private void addPost() {
		User user = null;
		Post post = null;
		
		user = ApolloApplication.app().getCurrentUser();
		post = new Post();
		post.setAuthor(user);
		post.setSubject(getString(R.string.post_subject_prefix) + mThread.getSubject());
		post.setBody(getString(R.string.post_body_mark));
		post.setThreadId(mThread.getThreadId());
		post.setSection(mThread.getSection());
		mAddPostTask = new AddPostAsyncTask();
		mAddPostTask.execute(post);
	}
	
	private void addBookmark(Thread thread) {
		thread.setFloor(mStartIndex + mPostList.getFirstVisiblePosition());
		mAddBMTask = new AddBookMarkAsyncTask();
		mAddBMTask.execute(thread, ApolloApplication.app().getCurrentUser());
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = null;
		
		setContentView(R.layout.activity_list_post);
		intent = getIntent();
		mThread = (Thread)intent.getExtras().get("thread");
		mMoreItemCurIdx = -1;
		initViews();
		initListeners();
				
		mHandler = new GetPostListHandler();
		mExeService = Executors.newFixedThreadPool(THREADPOOL_SIZE);
		mExeService.submit(new GetPostListThread());//???????,?????????????????
		
		mProgressBar.setVisibility(View.VISIBLE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private void initViews() {		
		mListFooter = (RelativeLayout)LayoutInflater.from(PostActivity.this).inflate(R.layout.footer_list_more, null);
		mBottomBar = (LinearLayout) findViewById(R.id.bottom_bar);
		mFootProgressBar = (ProgressBar) mListFooter.findViewById(R.id.foot_progress);
		mFootTitle = (TextView) mListFooter.findViewById(R.id.footer_title);
		mProgressBar = (ProgressBar)findViewById(R.id.progress);
		mReply = (Button) findViewById(R.id.reply);
		mTopTitle = (TextView) findViewById(R.id.top_title);
		mTopTitle.setText(mThread.getSubject());
		mBtnBack = (Button) findViewById(R.id.back);
		mBtnAddBookMark = (Button) findViewById(R.id.add_bookmark);
		mSkipFloor = (Button) findViewById(R.id.skip_floor);
		mMore = (Button) findViewById(R.id.more);
		mPostList = (ListView) super.findViewById(R.id.list);
		mPostList.addFooterView(mListFooter);
		mAdapter = new PostAdapter<Post>(PostActivity.this, R.layout.item_list_post, null);
		mPostList.setAdapter(mAdapter);
	}
	
	private void initListeners() {
		mListFooter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mFootTitle.setText(R.string.loading);
				mFootProgressBar.setVisibility(View.VISIBLE);
				
				mExeService.submit(new GetPostListThread());
			}
		});
		
		mReply.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramView) {
				replyThread();
			}
		});
		
		mBtnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PostActivity.this.finish();
			}
		});
		
		mBtnAddBookMark.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addBookmark(mThread);
			}
		});
		
		mSkipFloor.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LayoutInflater inflater = null;
				LayoutParams params = null;
				
				mSkipFloorDialog = new Dialog(PostActivity.this, R.style.Theme_Apollo_Dialog);
				mSkipFloorDialog.setCancelable(true);
				mSkipFloorDialog.setCanceledOnTouchOutside(true);
				
				inflater = getLayoutInflater();
				mSkipFloorDialogView = inflater.inflate(R.layout.view_dialog_skip_floor, null);
				mSkipFloorDialog.setContentView(mSkipFloorDialogView);
				
				params = mSkipFloorDialog.getWindow().getAttributes();
				params.gravity = 49;
				params.y = ResUtil.dip2px(PostActivity.this, 54.0f);
				params.width = (int) (0.9D * ResUtil.getEquipmentWidth(PostActivity.this));
				mSkipFloorDialog.getWindow().setAttributes(params);
				
				mFloor = (EditText) mSkipFloorDialogView.findViewById(R.id.target_floor);
				mSkipFloorDialogView.findViewById(R.id.cancel).setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						mSkipFloorDialog.dismiss();
					}
				});
				mSkipFloorDialogView.findViewById(R.id.ok).setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						toFloor(Integer.parseInt(mFloor.getText().toString()));
						mSkipFloorDialog.dismiss();
					}
				});
				mSkipFloorDialog.show();
			}
		});
				
		mMore.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mMoreDialog == null) {
					AlertDialog.Builder builder = null;
					View dialogView;
					ListView moreListView = null;
					LayoutParams params = null;
					String[] moreItems = null;
					
					
					builder = new AlertDialog.Builder(PostActivity.this);
					mMoreDialog = builder.create();
					mMoreDialog.setCanceledOnTouchOutside(true);
					mMoreDialog.show();
					
					params = mMoreDialog.getWindow().getAttributes();
					params.gravity = Gravity.RIGHT | Gravity.BOTTOM;
					params.x = 2;
					params.y = mBottomBar.getHeight();
					params.alpha = 1.0f;
					params.width = ResUtil.dip2px(PostActivity.this, 120f);
					params.height = -2;
					mMoreDialog.getWindow().setAttributes(params);
	
					dialogView = getLayoutInflater().inflate(R.layout.view_dialog_more, null);
					mMoreDialog.setContentView(dialogView);
					moreItems = getResources().getStringArray(R.array.dialog_post_more_menu_item);
					
					if (mThread.getFloor() ==0) {
						mDialogMoreAdapter = new DialogMoreAdapter(PostActivity.this, moreItems);
					} else {
						String[] newMoreItems = null;

						newMoreItems = Arrays.copyOf(moreItems, moreItems.length + 1);
						newMoreItems[moreItems.length] = getResources().getString(R.string.skip_to_bookmark);
						mDialogMoreAdapter = new DialogMoreAdapter(PostActivity.this, newMoreItems);
					}
					
					moreListView = (ListView) dialogView.findViewById(R.id.list);
					moreListView.setAdapter(mDialogMoreAdapter);
					moreListView.setOnItemClickListener(new OnItemClickListener() {
	
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							mMoreItemCurIdx = position;
							switch(mMoreItemCurIdx) {
							case 0://mark
								addPost();
								break;
							case 1://fav
								addBookmark(mThread);
								break;
							case 2:// share
								if (mShareDialog == null) {
									AlertDialog.Builder builder = null;
									View dialogView = null;
									LayoutParams params = null;
									View shareView = null;
									
									builder = new AlertDialog.Builder(PostActivity.this);
									mShareDialog = builder.create();
									mShareDialog.setCanceledOnTouchOutside(true);
									mShareDialog.show();
									
									params = mShareDialog.getWindow().getAttributes();
									params.gravity = Gravity.CENTER | Gravity.BOTTOM;
									params.alpha = 1.0f;
									params.height = LayoutParams.WRAP_CONTENT;
									mShareDialog.getWindow().setAttributes(params);
									
									dialogView = getLayoutInflater().inflate(R.layout.view_dialog_share, null);
									shareView = dialogView.findViewById(R.id.cancel);
									shareView.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											mShareDialog.dismiss();
										}
									});
									shareView = dialogView.findViewById(R.id.iconWeixinTimeline);
									shareView.setOnClickListener(PostActivity.this);
									shareView = dialogView.findViewById(R.id.iconWeixin);
									shareView.setOnClickListener(PostActivity.this);
									shareView = dialogView.findViewById(R.id.iconQZone);
									shareView.setOnClickListener(PostActivity.this);
									shareView = dialogView.findViewById(R.id.iconSinaWeibo);
									shareView.setOnClickListener(PostActivity.this);
									
									mShareDialog.setContentView(dialogView);
								} else {
									mShareDialog.show();
								}
								break;
								
							case 3:// auto post
								AutoPostConfigActivity.startActivity(PostActivity.this, mThread);
								break;
								
							case 4:// 
								toFloor(mThread.getFloor());
								break;
							}
							mMoreDialog.dismiss();
						}
						
					});				
				} else {
					mMoreDialog.show();
				}
				mDialogMoreAdapter.notifyDataSetInvalidated();
			}
		});
				
		mAdapter.setDisplayFloor(new DisplayFloorHandle<Post>() {
			@Override
			public void setFloor(TextView view, Post p, int position) {
				if (mThread.getAuthor().getName().equals(p.getAuthor().getName()))
					view.setText(R.string.floor_building);
				else
					view.setText(Integer.toString(mStartIndex + position)
							+ getResources().getText(R.string.floor));
			}
		});
		mAdapter.setDisplayOther(new DisplayOtherHandle<Post>() {

			@Override
			public Object intView(View convertView) {
				mViewHolder = new PostViewHolder();
				
				mViewHolder.show_only_user = (TextView) convertView.findViewById(R.id.post_filter_user);
				mViewHolder.quote = (TextView) convertView.findViewById(R.id.post_quote);
				mViewHolder.copy = (TextView) convertView.findViewById(R.id.post_copy);
				mViewHolder.reply = (TextView) convertView.findViewById(R.id.post_reply);
				mViewHolder.clickListener = new PostViewItemOnClickListener();
				return mViewHolder;
			}

			@Override
			public void setOther(Object obj, Post post, int position) {
				mViewHolder = (PostViewHolder) obj;
				
				mViewHolder.clickListener.setPosition(position);
				mViewHolder.clickListener.setPost(post);
				mViewHolder.clickListener.setViewHolder(mViewHolder);
				
				
				if (mPostViewType.equals(PostViewType.SHOW_ONLY_USER)) {
					Drawable drawable = getResources().getDrawable(
							R.drawable.show_normal_user);

					drawable.setBounds(0, 0, drawable.getMinimumWidth(),
							drawable.getMinimumHeight());
					mViewHolder.show_only_user.setCompoundDrawables(drawable,
							null, null, null);
					mViewHolder.show_only_user.setText(R.string.show_normal_user);
				} else {
					Drawable drawable = getResources().getDrawable(
							R.drawable.show_only_user);

					drawable.setBounds(0, 0, drawable.getMinimumWidth(),
							drawable.getMinimumHeight());
					mViewHolder.show_only_user.setCompoundDrawables(drawable,
							null, null, null);
					mViewHolder.show_only_user.setText(R.string.show_only_user);
				}
				
				mViewHolder.show_only_user.setOnClickListener(mViewHolder.clickListener);
				mViewHolder.quote.setOnClickListener(mViewHolder.clickListener);
				mViewHolder.copy.setOnClickListener(mViewHolder.clickListener);
				mViewHolder.reply.setOnClickListener(mViewHolder.clickListener);
			}
			
		});
	}
	
	private void toFloor(int pos) {
		mPosts.clear();
		//mStartIndex = mToIndex = Integer.parseInt(mFloor.getText().toString());
		mStartIndex = mToIndex = pos;
		mExeService.submit(new GetPostListThread());
		mPostList.setSelection(0);
	}
	
	private void replyThread() {
		replyThread(PostMode.REPLY, mThread, null);
	}
	
	private void replyThread(PostMode mode, Post post) {
		replyThread(mode, mThread, post);
	}
	
	private void replyThread(PostMode mode, Thread thread, Post post) {
		if (ApolloApplication.app().getCurrentUser() == null) {
			LoginActivity.startActivityForResult(PostActivity.this, getString(R.string.login_to_use), true, RequestResponseCode.REQUEST_LOGIN_REPLY);
		} else {
			if (PostMode.REPLY.equals(mode))
				PostEditActivity.startActivityForResult(PostActivity.this, thread, RequestResponseCode.REQUEST_POST_REPLY);
			else
				PostEditActivity.startActivity(PostActivity.this, mode, thread, post);
		}	
	}
	
	private void showOnlyUser(int userId) {
		mPosts = new ArrayList<Post>();
		
		if (mFilterUserId != userId)
			mFilterUserId = userId;
		else
			mFilterUserId = 0;

		if (mFilterUserId == 0)
			mPostViewType = PostViewType.NORMAL;
		else
			mPostViewType = PostViewType.SHOW_ONLY_USER;
		mFromIndex = mToIndex = 0;
		mExeService.submit(new GetPostListThread());
	}
	
	private void loadPosts() {	
		mFromIndex = mToIndex;
		mToIndex += mPageSize;

		mNewPosts = Posts.getIndexOf(mThread.getSection().getSectionId(), mThread.getThreadId(), 
				mFilterUserId, mFromIndex, mToIndex, mFlushPost);
		
		// 当加载的内容的数量跟mPageSize不相等时，则设置Flush为true
		mToIndex = mFromIndex + mNewPosts.size();
		mFlushPost = mToIndex % mPageSize != 0;

		if (mFromIndex == 0) {
			Post p = mNewPosts.get(0);
			mThread.setSubject(p.getSubject());
			mThread.setBody(p.getBody());
			mThread.setAuthor(p.getAuthor());
		}
	}

	@Override
	public void onClick(View v) {
		Proxy proxy;
		String text = null;
		
		switch(v.getId()) {
		case R.id.iconSinaWeibo:
			SinaWeiboParams wb_param = new SinaWeiboParams();
			
			wb_param.setText(mThread.getSubject());
			wb_param.setUrl(mThread.getUrl());
			
			proxy = new SinaWeiboProxy(this);
			proxy.setProxyActionListener(this);
			//proxy.init();
			// 执行图文分享
			proxy.share(wb_param);
			
			break;
		case R.id.iconWeixinTimeline:
		case R.id.iconWeixin:
			WXParams wx_params = new WXParams();
			
			text = Transforms.formatPost(mThread.getBody());
			text = Formatter.checkStringLength(text, 30);
			
			wx_params.setTimeline(v.getId() == R.id.iconWeixinTimeline);
			wx_params.setType(WXParams.Type.WEB);
			wx_params.setSubject(mThread.getSubject());
			wx_params.setText(text);
			wx_params.setUrl(mThread.getUrl());
			wx_params.setImage(ImageUtil.getResBitmap(this, R.drawable.ic_launcher));
			
			proxy = new WXProxy(this);
			proxy.share(wx_params);
			break;
		case R.id.iconQZone:
			QZoneParams qz_params = new QZoneParams();
			
			qz_params.setSubject(mThread.getSubject());
			qz_params.setText(text);
			qz_params.setUrl(mThread.getUrl());
			
			proxy = new QZoneProxy(this);
			proxy.share(qz_params);
			break;
		}
	}

	@Override
	public void onComplete(Proxy proxy, int code, Map<String, Object> map) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onError(Proxy proxy, int paramInt, Throwable throwable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCancel(Proxy proxy, int paramInt) {
		// TODO Auto-generated method stub
		
	}
}
