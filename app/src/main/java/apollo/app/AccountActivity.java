package apollo.app;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import apollo.app.home.PersonInfoActivity;
import apollo.bll.Users;
import apollo.core.ApolloApplication;
import apollo.core.RequestResponseCode;
import apollo.data.model.User;
import apollo.enums.AccountViewMode;
import apollo.util.AsyncImageLoader;
import apollo.util.DataSet;
import apollo.util.AsyncImageLoader.OnImageLoaderListener;
import apollo.widget.SwipeListView;

public class AccountActivity extends BaseActivity {

	private AccountListAdapter mAdapter;
	private View.OnClickListener mDeleteListener;
	private DeleteAsyncTask mDeleteTask;
	private AccountsAsyncTask mAccountsTask;
	private SwipeListView mList;
	private TextView mFootTitle;
	private TextView mTopTitle;
	private ProgressBar mFootProgressBar;
	private RelativeLayout mListFooter;
	private AccountViewMode mViewMode;
	private Button mBtnAdd;
	private Button mBtnBack;
	private Button mBtnSelectAll;
	private Button mBtnOK;
	private AsyncImageLoader<ImageView> mAsyncImageLoader;
	
	private int mPageIndex;
	private int mPageSize;
	private int mTotalRecords;
	private int mPrePosition = 0;
	private User mUser;
	private ArrayList<User> mSelectedUsers;
	private List<User> mUsers;
	
	static class AccountViewHolder {
		private ImageView mUserIcon;
	    private TextView mAccount;
	    private ImageView mActive;
	    private Button mDelete;
	    private RelativeLayout mEditLayout;
	}
		
	class AccountsAsyncTask extends AsyncTask<Object, Integer, DataSet<User>> {
		@Override
		protected DataSet<User> doInBackground(Object... params) {
			DataSet<User> datas = null;
			
			if (mTotalRecords != 0 && mPageIndex * mPageSize > mTotalRecords) 
				return null;
				
			if (mViewMode == AccountViewMode.ACCOUNT_LIST_MODE ||
				mViewMode == AccountViewMode.ACCOUNT_SELECT_MODE)
				datas = Users.getUsers(mPageIndex, mPageSize);
			else
				datas = Users.getFriends(mUser, mPageIndex, mPageSize);
			
			mTotalRecords = datas.getTotalRecords();
			return datas;
		}
		
		@Override
		protected void onPostExecute(DataSet<User> datas) {
			if (datas == null) {
				showToast(getString(R.string.error_post_out_of_range));
			} else {				
				AccountActivity.this.mUsers.addAll(datas.getObjects());
				mAdapter.notifyDataSetChanged();
			}
			mFootProgressBar.setVisibility(View.GONE);
			mFootTitle.setText(R.string.more);
		}
	}
	
	class DeleteAsyncTask extends AsyncTask<Object, Integer, User> {
		
		@Override
		protected User doInBackground(Object... params) {
			User user = null;
			
			user = (User) params[0];
			
			if (mViewMode == AccountViewMode.ACCOUNT_LIST_MODE) 
				Users.delete(user);
			else if (mViewMode == AccountViewMode.FRIEND_LIST_MODE) 
				Users.removeFriend(ApolloApplication.app().getCurrentUser(), user.getUserId());
						
			return user;
		}
		
		@Override
		protected void onPostExecute(User result) {
			mUsers.remove(result);
			mAdapter.notifyDataSetChanged();
			
			if (mViewMode != AccountViewMode.ACCOUNT_LIST_MODE) 
				return;
			
			if (mAdapter.getCount() != 0) {
				View view = null;
				User user = null;
				AccountViewHolder viewHolder = null;
				
				mPrePosition = 0;
				view = mList.getChildAt(mPrePosition);
				user = mUsers.get(mPrePosition);
				user.setActive(true);
				ApolloApplication.app().setCurrentUser(user);
				
				viewHolder = (AccountViewHolder) view.getTag();
				viewHolder.mActive.setVisibility(user.isActive() ? View.VISIBLE : View.GONE);
			} else {
				ApolloApplication.app().setCurrentUser(null);
			}
		}
		
	}
	
	class AccountListAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		//private Context mContext;
		private View.OnClickListener mListener;
		
		private List<User> mUsers;

		public AccountListAdapter(Context context, View.OnClickListener listener, List<User> users) {
			//this.mContext = context;
			this.mListener = listener;
			this.mUsers = users;
			this.mInflater = LayoutInflater.from(context);
		}
			  
		@Override
		public int getCount() {
			return this.mUsers.size();
		}

		@Override
		public Object getItem(int position) {
			return this.mUsers.get(position);
		}

		@Override
		public long getItemId(int position) {
			return this.mUsers.get(position).getUserId();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			AccountViewHolder viewHolder = null;
			User user = null;
			Bitmap bmp = null;
			String icon_url = null;
			
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.item_list_account, null);				
				viewHolder = new AccountViewHolder();
				viewHolder.mAccount = (TextView) convertView.findViewById(R.id.account);
				viewHolder.mUserIcon = (ImageView) convertView.findViewById(R.id.user_head_icon);
				viewHolder.mActive = (ImageView) convertView.findViewById(R.id.active);
				viewHolder.mDelete = (Button) convertView.findViewById(R.id.delete);
				viewHolder.mEditLayout = (RelativeLayout) convertView.findViewById(R.id.edit_layout);
				viewHolder.mDelete.setOnClickListener(this.mListener);
				
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (AccountViewHolder) convertView.getTag();
			}
	
			user = this.mUsers.get(position);
			viewHolder.mAccount.setVisibility(View.VISIBLE);
			viewHolder.mAccount.setText(user.getName());
			viewHolder.mDelete.setTag(user);
			viewHolder.mEditLayout.setVisibility(View.GONE);
			
			if (mViewMode == AccountViewMode.ACCOUNT_LIST_MODE)
				viewHolder.mActive.setVisibility(user.isActive() ? View.VISIBLE : View.GONE);
						
			icon_url = "http://tx.tianyaui.com/logo/small/" + user.getUserId();
			viewHolder.mUserIcon.setTag(icon_url);		
			bmp = mAsyncImageLoader.loadImage(icon_url, viewHolder.mUserIcon, new OnImageLoaderListener<ImageView>() {
	            @Override
	            public void imageLoaded(Bitmap bmp, ImageView view, String url) {
	            	String tag = (String)view.getTag();
	            	if (url.equals(tag))
	            		view.setImageDrawable(new BitmapDrawable(bmp));
	            }
	        });
						
			if (bmp != null) 
				viewHolder.mUserIcon.setImageDrawable(new BitmapDrawable(bmp));
			
			return convertView;
		}
		
		public void setUsers(List<User> users) {
			this.mUsers = users;
		}
	}
	
	public AccountActivity() {
		super();
		
		this.mPageIndex = 0;
		this.mPageSize = 20;
		this.mSelectedUsers = new ArrayList<User>();
		this.mUsers = new ArrayList<User>();
		this.mViewMode = AccountViewMode.ACCOUNT_LIST_MODE;
		this.mAsyncImageLoader = new AsyncImageLoader<ImageView>();
	}
	
	public void setViewMode(AccountViewMode viewMode) {
		this.mViewMode = viewMode;
	}
	
	public AccountViewMode getViewMode() {
		return this.mViewMode;
	}
	
	public static void startActivity(Activity activity, AccountViewMode viewMode) {		
		Intent intent = null;
		Bundle bundle = null;
		
		bundle = new Bundle();
		bundle.putParcelable("viewmode", viewMode);
		
		intent = new Intent(activity, AccountActivity.class);
		intent.putExtras(bundle);
		activity.startActivity(intent);
	}
	
	public static void startActivity(Activity activity, AccountViewMode viewMode, User user) {
		Intent intent = null;
		Bundle bundle = null;
		
		bundle = new Bundle();
		bundle.putParcelable("viewmode", viewMode);
		bundle.putParcelable("user", user);
		
		intent = new Intent(activity, AccountActivity.class);
		intent.putExtras(bundle);
		activity.startActivity(intent);
	}
	
	public static void startActivityForResult(Activity activity, AccountViewMode viewMode, int requestCode, String action) {
		startActivityForResult(activity, viewMode, null, requestCode, action);
	}
	
	public static void startActivityForResult(Activity activity, AccountViewMode viewMode, User user, int requestCode, String action) {
		Intent intent = null;
		Bundle bundle = null;
		
		bundle = new Bundle();
		bundle.putParcelable("viewmode", viewMode);
		bundle.putParcelable("user", user);
		
		intent = new Intent(activity, AccountActivity.class);
		intent.setAction(action);
		intent.putExtras(bundle);
		activity.startActivityForResult(intent, requestCode);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == RESULT_OK ) {
			if (requestCode == RequestResponseCode.REQUEST_LOGIN_USE) {
				refresh();
			}
		}
	}
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Intent intent = null;
		
        intent = getIntent();
        mViewMode = (AccountViewMode) intent.getExtras().get("viewmode");
        if (intent.hasExtra("user")) 
			mUser = (User) intent.getExtras().get("user");
	
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_account);
        		
        initViews();
        initListeners();
        refresh();
	}
		
	private void initViews() {	
		this.mBtnAdd = (Button) super.findViewById(R.id.add);
		this.mBtnBack = (Button) super.findViewById(R.id.back);
		this.mBtnSelectAll =  (Button) super.findViewById(R.id.select_all);
		this.mBtnOK =  (Button) super.findViewById(R.id.ok);
		
		this.mTopTitle = (TextView) super.findViewById(R.id.top_title);
		if (mViewMode == AccountViewMode.ACCOUNT_LIST_MODE) {
			this.mTopTitle.setText(R.string.account_manage);
			this.mBtnAdd.setVisibility(View.VISIBLE);
		} else if (mViewMode == AccountViewMode.ACCOUNT_SELECT_MODE) {
			this.mTopTitle.setText(R.string.account_manage);
			this.mBtnOK.setVisibility(View.VISIBLE);
			this.mBtnSelectAll.setVisibility(View.VISIBLE);
		} else {
			this.mTopTitle.setText(R.string.friend_list);
		}
		
		this.mDeleteListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				User user = null;
				
				user = (User) v.getTag();

				AccountActivity.this.mDeleteTask = new AccountActivity.DeleteAsyncTask();
				AccountActivity.this.mDeleteTask .execute(user);
			}
		}; 
		this.mAdapter = new AccountListAdapter(AccountActivity.this, this.mDeleteListener, this.mUsers);
		this.mListFooter = (RelativeLayout)LayoutInflater.from(AccountActivity.this).inflate(R.layout.footer_list_more, null);
		this.mFootTitle = (TextView) mListFooter.findViewById(R.id.footer_title);
		this.mFootProgressBar = (ProgressBar) mListFooter.findViewById(R.id.foot_progress);
		
		this.mList = (SwipeListView) super.findViewById(R.id.list);
		this.mList.addFooterView(this.mListFooter);
		this.mList.setAdapter(mAdapter);
	}
	
	
	private void initListeners() {
		this.mBtnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		this.mBtnSelectAll.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(RESULT_OK);
				finish();
			}
		});
		
		this.mBtnOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent data = null;
				Bundle bundle = null;
				
				bundle = new Bundle();
				bundle.putSerializable("users", mSelectedUsers);
	
				data = new Intent();
				data.putExtras(bundle);
				setResult(RESULT_OK, data);
				finish();
			}
		});
		
		this.mBtnAdd.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				LoginActivity.startActivityForResult(AccountActivity.this, getString(R.string.login_to_use), true, RequestResponseCode.REQUEST_LOGIN_USE);
			}
		});
				
		this.mListFooter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mFootTitle.setText(R.string.loading);
				mFootProgressBar.setVisibility(View.VISIBLE);
				
				mPageIndex ++;
				mAccountsTask = new AccountsAsyncTask();
		        mAccountsTask.execute();
			}
		});
		
		this.mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (mViewMode == AccountViewMode.FOLLOW_USER_LIST_MODE) {
					Intent data = null;
					AccountViewHolder viewHolder = null;
					
					viewHolder = (AccountViewHolder) view.getTag();
					data = new Intent();
					data.putExtra("name", viewHolder.mAccount.getText());
					setResult(RESULT_OK, data);
					finish();
				} else if (mViewMode == AccountViewMode.FRIEND_LIST_MODE) {
					User curUser = (User) mAdapter.getItem(position);
					PersonInfoActivity.startActivity(AccountActivity.this, curUser);
				} else if (mViewMode == AccountViewMode.ACCOUNT_LIST_MODE) {
					User curUser = null;
					User preUser = null;
					View preView = null;
					View editLayout = null;
					AccountViewHolder viewHolder = null;
					if (position ==  mPrePosition)
						return;
					
					editLayout = (RelativeLayout) view.findViewById(R.id.edit_layout);
					if (editLayout.getVisibility() == View.VISIBLE)
						return;
					
					curUser = (User) mAdapter.getItem(position);
					preUser = (User) mAdapter.getItem(mPrePosition);
					
					ApolloApplication.app().setCurrentUser(curUser);
					preUser.setActive(false);
					
					preView = mList.getChildAt(mPrePosition); 
					//mAdapter.getView(mPrePosition, preView, mList);
					viewHolder = (AccountViewHolder) preView.getTag();
					viewHolder.mActive.setVisibility(preUser.isActive() ? View.VISIBLE : View.GONE);
					
					//mAdapter.getView(position, view, mList);
					viewHolder = (AccountViewHolder) view.getTag();
					viewHolder.mActive.setVisibility(curUser.isActive() ? View.VISIBLE : View.GONE);
					
					mPrePosition = position;
				} else if (mViewMode == AccountViewMode.ACCOUNT_SELECT_MODE) {
					User curUser = null;
					AccountViewHolder viewHolder = null;
					
					curUser = (User) mAdapter.getItem(position);
					viewHolder = (AccountViewHolder) view.getTag();
					if (mSelectedUsers.contains(curUser)) {
						mSelectedUsers.remove(curUser);
						viewHolder.mActive.setVisibility(View.GONE);
					} else {
						mSelectedUsers.add(curUser);
						viewHolder.mActive.setVisibility(View.VISIBLE);
					}
				}
			}
		});
	}
		
	private void refresh() {
		mPageIndex = 1;
		mTotalRecords = 0;
		mUsers.clear();
		mAdapter.notifyDataSetChanged();
		
        mAccountsTask = new AccountsAsyncTask();
        mAccountsTask.execute();
	}
}
