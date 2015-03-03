package apollo.app.home;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import apollo.app.AccountActivity;
import apollo.app.BaseActivity;
import apollo.app.GalleryActivity;
import apollo.app.R;
import apollo.app.ThreadActivity;
import apollo.bll.Bookmarks;
import apollo.bll.Gallerys;
import apollo.bll.Users;
import apollo.core.ApolloApplication;
import apollo.data.model.Constants;
import apollo.data.model.User;
import apollo.enums.AccountViewMode;
import apollo.enums.ThreadViewType;
import apollo.util.AsyncImageLoader;
import apollo.util.AsyncImageLoader.OnImageLoaderListener;

public class PersonInfoActivity extends BaseActivity {

	private User mUser;
	private ListMenuItem[] mMenuItems;
	
	private ListView mList;
	private Button mHome;
	private Button mRefresh;
	private Button mBack;
	private Button mEdit;
	private TextView mTopTitle;
	private TextView mName;
	private TextView mIntroduce;
	private ImageView mUsericon;
	private ImageView mGender;
	private LinearLayout mEditLayout;
	private LinearLayout mUserLayout;
	private ProgressBar mAttentionProgress;
	private ProgressBar mProgress;
	
	private AsyncImageLoader<ImageView> mAsyncImageLoader;
	private PersonAsyncTask mPersonTask = null;
	private ListMenuItemAdapter  mListItemAdapter;
	
	public static void startActivity(Activity activity, User user) {
		Intent intent = null;
		Bundle bundle = null;
		
		bundle = new Bundle();
		bundle.putParcelable("user", user);
		
		intent = new Intent(activity, PersonInfoActivity.class);
		intent.putExtras(bundle);
		activity.startActivity(intent);
	}
	
	private class PersonAsyncTask extends AsyncTask<User, User, User> {

		@Override
		protected User doInBackground(User... params) {
			User user = null;
			
			user = params[0];
			mUser = Users.getUserProfile(user);
			
			mMenuItems[0].num = mUser.getPosts();
			if (mUser == ApolloApplication.app().getCurrentUser()) {
				mMenuItems[1].num = Users.getFriends(user, 1, Constants.PAGE_SIZE).getTotalRecords();
				mMenuItems[2].num = Bookmarks.getThreads(user, 1, Constants.PAGE_SIZE).getTotalRecords();
				mMenuItems[3].num = Gallerys.getPhotos(user);
			}
			
			return mUser;
		}
		
		@Override
		protected void onPostExecute(User result) {
			onRefreshFinish();
		}
	}
	
	private class ListMenuItem {
		
		public ListMenuItem (String name, int num) {
			this.name= name;
			this.num = num;
		}

		public ListMenuItem(String name) {
			this(name, 0);
		}

		String name;
		int num;
	}
	
	private class ListMenuViewHolder {
		TextView text;
		TextView num;
	}
	
	private class ListMenuItemAdapter extends BaseAdapter {

		private ListMenuItem[] mItems = null;
		
		public ListMenuItemAdapter(ListMenuItem[] items) {
			mItems = items;
		}
		
		@Override
		public int getCount() {
			return mItems.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ListMenuViewHolder vh = null;

			if (convertView == null) {
				convertView = LayoutInflater.from(PersonInfoActivity.this).inflate(R.layout.item_menu_person, null);
				
				vh = new ListMenuViewHolder();
				vh.text = (TextView) convertView.findViewById(R.id.text);
				vh.num = (TextView) convertView.findViewById(R.id.number);
				
				convertView.setTag(vh);
			} else {
				vh = (ListMenuViewHolder) convertView.getTag();
			}
			
			vh.text.setText(mItems[position].name);
			vh.num.setText(Integer.toString(mItems[position].num));
			return convertView;
		}
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Intent intent = null;
		String[] items_str = null;
		super.onCreate(savedInstanceState);

		intent = getIntent();
		if (intent.getExtras() != null)
			mUser = (User) intent.getExtras().get("user");
		else
			mUser = ApolloApplication.app().getCurrentUser();
		
		mAsyncImageLoader = new AsyncImageLoader<ImageView>();
		
		if (mUser == ApolloApplication.app().getCurrentUser())
			items_str = getResources().getStringArray(R.array.person_menu_item);
		else
			items_str = getResources().getStringArray(R.array.user_menu_item);
		
		mMenuItems = new ListMenuItem[items_str.length];
		for (int idx=0; idx<mMenuItems.length; idx++) {
			mMenuItems[idx] = new ListMenuItem(items_str[idx]);
		}
		mListItemAdapter = new ListMenuItemAdapter(mMenuItems);
		
		setContentView(R.layout.activity_person_info);
		initViews();
		initListeners();
		refresh();
	}
	
/*	@Override
	protected void onResume() {
		super.onResume();
		
		if (ApolloApplication.app().getCurrentUser() != null && this.mUser == null) 
			this.mUser = ApolloApplication.app().getCurrentUser();
		
		refresh();
	}*/
	
	private void initViews() {
		mList = (ListView) findViewById(R.id.list);
		mList.setAdapter(mListItemAdapter);
		
		mHome = (Button) findViewById(R.id.home);
		mRefresh = (Button) findViewById(R.id.refresh);
		mBack = (Button) findViewById(R.id.back);
		if (mUser == ApolloApplication.app().getCurrentUser())
			mBack.setVisibility(View.GONE);
		
		mEdit = (Button) findViewById(R.id.edit);
		mTopTitle = (TextView) findViewById(R.id.top_title);
		mName = (TextView) findViewById(R.id.name);
		mIntroduce = (TextView) findViewById(R.id.introduce);
		mUsericon = (ImageView) findViewById(R.id.user_head_icon);
		mGender = (ImageView) findViewById(R.id.gender);
		mEditLayout = (LinearLayout) findViewById(R.id.edit_layout);
		mUserLayout = (LinearLayout) findViewById(R.id.user_layout);
		mAttentionProgress = (ProgressBar) findViewById(R.id.attention_progress);
		mProgress = (ProgressBar) findViewById(R.id.progress);
	}
	
	private void initListeners() {

		mBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();				
			}
		});
		
		mList.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch(position) {
				case 0:
					ThreadActivity.startActivity(PersonInfoActivity.this, mUser, ThreadViewType.USER);
					break;
				case 1:
					AccountActivity.startActivity(PersonInfoActivity.this, AccountViewMode.FRIEND_LIST_MODE, mUser);
					break;
				case 2:
					ThreadActivity.startActivity(PersonInfoActivity.this, mUser, ThreadViewType.BOOKMARK, MainTabActivity.ACTIVITY_PERSON);
					break;
				case 3:
					GalleryActivity.startActivity(PersonInfoActivity.this, mUser);
					break;
				}
			}
		});
		
		mRefresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				refresh();
			}
		});
	}
	
	private void onRefreshFinish() {
		Bitmap bmp = null;
		bmp = mAsyncImageLoader.loadImage("http://tx.tianyaui.com/logo/small/" + mUser.getUserId(), mUsericon, new OnImageLoaderListener<ImageView>() {
            @Override
            public void imageLoaded(Bitmap bmp, ImageView view, String url) {
            	view.setImageDrawable(new BitmapDrawable(bmp));
            }
        });
		if (bmp != null) 
			mUsericon.setImageDrawable(new BitmapDrawable(bmp));
		
		mName.setText(mUser.getName());
		mIntroduce.setText(mUser.getInstroduce());
		mGender.setImageResource(mUser.isGender() ? R.drawable.male : R.drawable.female);
		mListItemAdapter.notifyDataSetChanged();
	}
	
	private void refresh() {
		if (mUser != null) {
			mPersonTask = new PersonAsyncTask();
			mPersonTask.execute(mUser);
		}
	}
}
