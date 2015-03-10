package apollo.app.home;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import apollo.app.BaseActivity;
import apollo.app.R;
import apollo.bll.PrivateMessages;
import apollo.core.ApolloApplication;
import apollo.data.model.PrivateMessage;
import apollo.data.model.User;
import apollo.enums.PrivateMessageType;
import apollo.exceptions.ApplicationException;
import apollo.util.DataSet;
import apollo.util.DateTime;
import apollo.widget.PostAdapter;
import apollo.widget.PostAdapter.DisplayViewHandle;
import apollo.widget.PostAdapter.PostViewHolder;

public class ChatActivity extends BaseActivity {

	private int mPageIndex;
	private int mPageSize;
	private int mTotalMessage;
	private User mRecipientor;
	private PrivateMessage mMessage;
	private PrivateMessageType mMsgType;
	
	private List<PrivateMessage> mMessages;
	private Object[] mMessagTaskParam;
	private TextView mFootTitle;
	private TextView mTopTitle;
	private EditText mEditBody = null;
	private Button mPost;
	private Button mBtnBack = null;
	private ProgressBar mProgressBar;
	private ProgressBar mFootProgressBar;
	private RelativeLayout mListFooter;
	private ListView mList;
	private InputMethodManager mInputManager = null;
	private MessageAsyncTask mMessageTask;
	private PostAdapter<PrivateMessage> mAdapter;
	private CreaetMessageAsyncTask mCreateMessageTask;
	
	
	class CreaetMessageAsyncTask extends AsyncTask<Object, PrivateMessage, PrivateMessage> {

		@Override
		protected PrivateMessage doInBackground(Object... params) {
			PrivateMessage msg = null;
			User user = null;
			
			msg = (PrivateMessage) params[0];
			user = (User) params[1];
			
			try {
				PrivateMessages.add(msg, user);
			} catch (ApplicationException ex) {
				ApolloApplication.app().setException(ex);
			}

			return msg;
		}
		
		protected void onPostExecute(PrivateMessage result) {
			ApplicationException ex = null;
			
			ex = ApolloApplication.app().getException();
			if (ex != null)
				showToast(ex.getMessage());
			else
				onMessagePostComplete();
			
		}
	}
	
	class MessageAsyncTask extends AsyncTask<Object, Integer, Boolean> {

		private int loops = 0;
		@Override
		protected Boolean doInBackground(Object... params) {
			PrivateMessageType type;
			Boolean unreadOnly;
			User user;
			DataSet<PrivateMessage> datas;
			Boolean datachange = Boolean.FALSE;
			
			type = (PrivateMessageType)params[0];
			unreadOnly = (Boolean) params[1];
			user = (User)params[2];
			datas = PrivateMessages.getPrivateMessages(user, type, unreadOnly, mPageIndex, mPageSize);
			mTotalMessage = datas.getTotalRecords();

			for (PrivateMessage pm : datas.getObjects()) {
				if (pm.getAuthor().getUserId() == mRecipientor.getUserId() || pm.getRecipientor().getUserId() == mRecipientor.getUserId()) {
					mMessages.add(0, pm);
					datachange = Boolean.TRUE;
				}
			}
			return datachange;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			if (result == Boolean.TRUE && loops <= mPageIndex) {
				mAdapter.notifyDataSetChanged();
				onRefreshFinish();
			} else {
				loops++;
				refresh();
			}
		}
		
	}
	
	
	public ChatActivity () {
		super();
		
		this.mMsgType = PrivateMessageType.NORMAL;
		this.mPageIndex = 0;
		this.mPageSize = 20;
		this.mMessages = new ArrayList<PrivateMessage>(mPageSize);
	}
	
	public static void startActivity(Activity activity, PrivateMessage msg, PrivateMessageType type) {
		Intent intent = null;
		Bundle bundle = null;
		
		bundle = new Bundle();
		bundle.putParcelable("message", msg);
		bundle.putParcelable("type", type);
		intent = new Intent(activity, ChatActivity.class);
		intent.putExtras(bundle);
		activity.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Intent intent = null;
		PrivateMessage msg;
		super.onCreate(savedInstanceState);

		intent = getIntent();
		msg = (PrivateMessage)intent.getExtras().get("message");
		mMsgType = (PrivateMessageType) intent.getExtras().get("type");
		if (msg.getAuthor().getUserId() == ApolloApplication.app().getCurrentUser().getUserId()) 
			mRecipientor = msg.getRecipientor();
		else
			mRecipientor = msg.getAuthor();
		
		setContentView(R.layout.activity_chat);

		this.mMessagTaskParam = new Object[]{this.mMsgType, Boolean.FALSE, ApolloApplication.app().getCurrentUser()};
		
		initViews();
		initListeners();
		
		refresh();
	}
	
	private void initViews() {
		mListFooter = (RelativeLayout)LayoutInflater.from(ChatActivity.this).inflate(R.layout.footer_list_more, null);
		mFootProgressBar = (ProgressBar) mListFooter.findViewById(R.id.foot_progress);
		mTopTitle = (TextView) super.findViewById(R.id.top_title);
		mTopTitle.setText(mRecipientor.getName());
		mFootTitle = (TextView) mListFooter.findViewById(R.id.footer_title);		
		mPost = (Button) findViewById(R.id.post);
		mEditBody = (EditText) findViewById(R.id.post_body);
		mProgressBar = (ProgressBar)findViewById(R.id.progress);
		mBtnBack = (Button) findViewById(R.id.back);
		mAdapter = new PostAdapter<PrivateMessage>(this, R.layout.item_list_chat, mMessages);
		mAdapter.setImageScaleWidth(mAdapter.getImageScaleWidth() - 24);
		mList = (ListView) super.findViewById(R.id.list);
		mList.addFooterView(mListFooter);
		mList.setAdapter(mAdapter);
	}
	
	private void initListeners() {
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
				hidenSoftKeyPad(mInputManager, mEditBody);
                createPrivateMessage();
			}
		});
		
		mBtnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ChatActivity.this.finish();
			}
		});
		
		mAdapter.setDisplayViewCallBack(new DisplayViewHandle<PrivateMessage>(){
			@Override
			public void setViewHolder(PostViewHolder holder, PrivateMessage msg) {
				LayoutParams layout_params = null;
				
				if (msg.getAuthor().getUserId() == ApolloApplication.app().getCurrentUser().getUserId()) {
					layout_params = new LayoutParams(holder.usericon.getLayoutParams());
					layout_params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT );
					holder.usericon.setLayoutParams(layout_params); 
					
					layout_params = new LayoutParams(holder.body.getLayoutParams());
					layout_params.addRule(RelativeLayout.LEFT_OF, R.id.user_head_icon);
					holder.body.setLayoutParams(layout_params); 
					holder.body.setBackgroundResource(R.drawable.chatto_bg);
				} else {
					layout_params = new LayoutParams(holder.usericon.getLayoutParams());
					layout_params.addRule(RelativeLayout.ALIGN_PARENT_LEFT );
					holder.usericon.setLayoutParams(layout_params); 
					
					layout_params = new LayoutParams(holder.body.getLayoutParams());
					layout_params.addRule(RelativeLayout.RIGHT_OF, R.id.user_head_icon);
					holder.body.setLayoutParams(layout_params); 
					holder.body.setBackgroundResource(R.drawable.chatfrom_bg);
				}
			}
		});
	}
	
	private void createPrivateMessage() {
		mMessage = new PrivateMessage();
		mMessage.setAuthor(ApolloApplication.app().getCurrentUser());
		mMessage.setRecipientor(this.mRecipientor);
		mMessage.setBody(mEditBody.getText().toString());
		mMessage.setPostDate(DateTime.now());
		
		mCreateMessageTask = new CreaetMessageAsyncTask();
		mCreateMessageTask.execute(mMessage, mMessage.getAuthor());
	}
	
	private void onMessagePostComplete() {
		this.mEditBody.setText("");
		this.mMessages.add(mMessage);
		mAdapter.notifyDataSetChanged();
	}
	
	private void refresh() {
		mProgressBar.setVisibility(View.VISIBLE);
		
		mPageIndex ++;
		mMessageTask = new MessageAsyncTask();
		mMessageTask.execute(mMessagTaskParam);
	}
	
	private void onRefreshFinish() {
		int maxPage = -1;
		
		maxPage = mTotalMessage / mPageSize;
		if (mTotalMessage % mPageSize > 0)
			maxPage++;

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
}
