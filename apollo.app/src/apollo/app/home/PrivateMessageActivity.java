package apollo.app.home;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
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
import apollo.app.BaseActivity;
import apollo.app.R;
import apollo.app.ThreadActivity;
import apollo.bll.PrivateMessages;
import apollo.core.ApolloApplication;
import apollo.data.model.PrivateMessage;
import apollo.data.model.User;
import apollo.enums.PrivateMessageType;
import apollo.enums.SortBy;
import apollo.exceptions.ApplicationException;
import apollo.exceptions.SystemException;
import apollo.util.AsyncImageLoader;
import apollo.util.AsyncImageLoader.OnImageLoaderListener;
import apollo.util.DataSet;
import apollo.util.Formatter;
import apollo.util.ResUtil;
import apollo.util.Transforms;
import apollo.widget.DialogMoreAdapter;
import apollo.widget.PostAdapter;
import apollo.widget.PostAdapter.DisplayBodyHandle;
import apollo.widget.PostAdapter.FormatBodyHandle;

public class PrivateMessageActivity extends BaseActivity implements OnItemClickListener, OnItemLongClickListener {

	private int mPageIndex;
	private int mPageSize;
	private int mTotalMessage;
	private List<PrivateMessage> mMessages;
	private Object[] mMessagTaskParam;
	private PrivateMessageType mMsgType;
	
	private TextView mFootTitle;
	private TextView mTopTitle;
	private Button mPost;
	private ProgressBar mProgressBar;
	private ProgressBar mFootProgressBar;
	private RelativeLayout mListFooter;
	private LinearLayout mTopMiddleLayout = null;
	private ListView mList;
	
	private Dialog mMoreDialog = null;
	private DialogMoreAdapter mDialogAdapter;
	
	private MessageAsyncTask mMessageTask;
	private PostAdapter mAdapter;

	class MessageAsyncTask extends AsyncTask<Object, Integer, Integer> {

		@Override
		protected Integer doInBackground(Object... params) {
			PrivateMessageType type;
			Boolean unreadOnly;
			User user;
			DataSet<PrivateMessage> datas = null;
			boolean is_contain = false;
			
			type = (PrivateMessageType)params[0];
			unreadOnly = (Boolean) params[1];
			user = (User)params[2];
			try {
				datas = PrivateMessages.getPrivateMessages(user, type, unreadOnly, mPageIndex, mPageSize);
			} catch(SystemException ex) {
				return apollo.data.R.string.error_system;
			} catch(ApplicationException ex) {
				return Integer.valueOf(ex.getResid());
			}
			
			mTotalMessage = datas.getTotalRecords();

			for (PrivateMessage npm : datas.getObjects()) {
				for (PrivateMessage pm : mMessages) {
					if (pm.getAuthor().getUserId() == npm.getAuthor().getUserId()) {
						is_contain = true;
						break;
					}
					if (ApolloApplication.app().getCurrentUser().getUserId() ==npm.getAuthor().getUserId()) {
						if (pm.getAuthor().getUserId() == npm.getRecipientor().getUserId()) {
							is_contain = true;
							break;
						}
					}
				}
				if (is_contain == false) {
					mMessages.add(npm);
				}
				is_contain = false;
			}
			return Integer.valueOf(0);
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			if (result == 0) {
				mAdapter.notifyDataSetChanged();
				onRefreshFinish();
			} else {
				showToast(ApolloApplication.app().getString(result));
			}
		}
		
	}
	
	
	
	public PrivateMessageActivity () {
		super();
		
		this.mMsgType = PrivateMessageType.NORMAL;
		this.mPageIndex = 0;
		this.mPageSize = 20;
		this.mMessages = new ArrayList<PrivateMessage>(mPageSize);
	}
	
//	public static void startActivity(Activity activity, String mainTab, String message, int requestCode) {
//		Intent intent = new Intent(activity, LoginActivity.class);
//		intent.putExtra(MainTabActivity.TAGET_TAB, mainTab);
//		intent.putExtra(LoginActivity.INFO, message);
//		activity.startActivityForResult(intent, requestCode);
//	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		PrivateMessage msg = null;
		
		msg = mMessages.get(position);
		ChatActivity.startActivity(PrivateMessageActivity.this, msg, mMsgType);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		return false;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_msg);
		
		initViews();
		initListeners();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if (ApolloApplication.app().getCurrentUser() != null && this.mMessagTaskParam == null) 
			refresh();
	}

	private void onRefreshFinish() {
		int maxPage = -1;
		
		maxPage = mTotalMessage / mPageSize;
		if (mTotalMessage % mPageSize > 0) {
			maxPage++;
		}
		if (mPageIndex >= maxPage) {
			mFootTitle.setText(R.string.pagination_nomore_message);
			mListFooter.setOnClickListener(null);
		} else {
			mFootTitle.setText(R.string.more);
		}
	
		mProgressBar.setVisibility(View.GONE);
		mList.setVisibility(View.VISIBLE);
		
		mFootProgressBar.setVisibility(View.GONE);
	}
	
	private void refresh() {
		mProgressBar.setVisibility(View.VISIBLE);
		
		this.mMessagTaskParam = new Object[]{mMsgType, Boolean.FALSE, ApolloApplication.app().getCurrentUser()};
		mPageIndex ++;
		mMessageTask = new MessageAsyncTask();
		mMessageTask.execute(mMessagTaskParam);
	}
		
	private void initViews() {
		mTopTitle = (TextView) findViewById(R.id.top_title);
		mTopTitle.setText(R.string.msg_user);
		mListFooter = (RelativeLayout)LayoutInflater.from(PrivateMessageActivity.this).inflate(R.layout.footer_list_more, null);
		mTopMiddleLayout = (LinearLayout) findViewById(R.id.layout_top_middle);
		mFootProgressBar = (ProgressBar) mListFooter.findViewById(R.id.foot_progress);
		mFootTitle = (TextView) mListFooter.findViewById(R.id.footer_title);		
		mPost = (Button) findViewById(R.id.post);
		mProgressBar = (ProgressBar)findViewById(R.id.progress);
		mAdapter = new PostAdapter<PrivateMessage>(this, R.layout.item_list_msg, mMessages);
		mList = (ListView) super.findViewById(R.id.list);
		mList.addFooterView(mListFooter);
		mList.setAdapter(mAdapter);
	}

	private void initListeners() {
		mTopMiddleLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mMoreDialog == null) {
					AlertDialog.Builder builder = null;
					View dialogView;
					ListView moreListView = null;
					LayoutParams params = null;
					
					builder = new AlertDialog.Builder(PrivateMessageActivity.this);
					mMoreDialog = builder.create();
					mMoreDialog.setCanceledOnTouchOutside(true);
					mMoreDialog.show();
					
					params = mMoreDialog.getWindow().getAttributes();
					params.gravity = Gravity.CENTER | Gravity.TOP;
					params.x = 2;
					params.y = mTopMiddleLayout.getTop() + mTopMiddleLayout.getHeight() + 10;
					params.alpha = 1.0f;
					params.width = ResUtil.dip2px(PrivateMessageActivity.this, 140f);
					params.height = -2;
					mMoreDialog.getWindow().setAttributes(params);
				
					dialogView = getLayoutInflater().inflate(R.layout.view_dialog_thread_more, null);
					mMoreDialog.setContentView(dialogView);
					
					mDialogAdapter = new DialogMoreAdapter(PrivateMessageActivity.this, getResources().getStringArray(R.array.dialog_msg_more_menu_item));
					moreListView = (ListView) dialogView.findViewById(R.id.list);		
					moreListView.setAdapter(mDialogAdapter);
					moreListView.setOnItemClickListener(new OnItemClickListener() {
	
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {							
							switch(position) {
							case 0:
								mMsgType = PrivateMessageType.SYSTEM;
								mTopTitle.setText(R.string.msg_sys);
								break;
							case 1:
								mMsgType = PrivateMessageType.NORMAL;
								mTopTitle.setText(R.string.msg_user);
								break;
							}
							mPageIndex = 0;
							mMessages.clear();
							mAdapter.notifyDataSetChanged();
							refresh();
							
							mMoreDialog.dismiss();
						}
					});
				} else {
					mMoreDialog.show();
				}
				mDialogAdapter.notifyDataSetInvalidated();
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
		
		mPost.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

			}
		});
		
//		mAdapter.setDisplayBodyCallBack(new DisplayBodyHandle() {
//
//			@Override
//			public void setBody(TextView view, SpannableString spannable) {
//				int end = spannable.length();
//				
//				if (end > 20) {
//					end = 20;
//				}
//				spannable = (SpannableString) spannable.subSequence(0, end);
//				view.setText(spannable);
//			}
//			
//		});
		
		mAdapter.setFormatBodyCallBack(new FormatBodyHandle() {

			@Override
			public String formatted(String src) {
				return Transforms.stripHtmlXmlTags(src);
			}
		
		});
		mList.setOnItemClickListener(this);
		mList.setOnItemLongClickListener(this);
	}
}
