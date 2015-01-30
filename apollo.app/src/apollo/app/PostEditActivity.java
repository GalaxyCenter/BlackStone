package apollo.app;

import java.lang.ref.SoftReference;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import apollo.bll.Posts;
import apollo.bll.Users;
import apollo.core.ApolloApplication;
import apollo.core.RequestResponseCode;
import apollo.data.model.Constants;
import apollo.data.model.Post;
import apollo.data.model.Thread;
import apollo.data.model.Section;
import apollo.data.model.User;
import apollo.enums.AccountViewMode;
import apollo.enums.PostMode;
import apollo.enums.PostType;
import apollo.exceptions.ApplicationException;
import apollo.util.AsyncImageLoader;
import apollo.util.EmotionUtil;
import apollo.util.FileUtil;
import apollo.util.ImageUtil;
import apollo.util.IntentProxy;
import apollo.util.ResUtil;
import apollo.util.Transforms;
import apollo.util.AsyncImageLoader.OnImageLoaderListener;

public class PostEditActivity extends BaseActivity {

	private static String KEY_POST = "post";
	private static String KEY_SECTION = "section";
	private static String KEY_THREAD = "thread";
	private static String KEY_MODE = "mode";
	public static int MAX_WORD_COUNT = 200;
	
	private Button mBtnBack = null;
	private Button mBtnPost = null;
	private ImageView mSelectImg = null;
	private ImageView mSelectFollow = null;
	private ImageView mSelectEmotion = null;
	private ImageView mSelectW200 = null;
	private EditText mEditTitle = null;
	private EditText mEditBody = null;
	private TextView mTopTitle = null;
	private GridView mGridView = null;
	private InputMethodManager mInputManager = null;
	private CreaetPostAsyncTask mCreatePostTask = null;
	private AlertDialog mSelectImageDialog = null;
	private AlertDialog mDraftDialog = null;
	private EmotionAdapter mEmoAdapter;
	private Handler mHandler = new Handler();
	private Post mPost;
	private Thread mThread;
	private Section mSection;
	private PostMode mPostMode;
	private boolean mHasChanged = false;
	private AsyncImageLoader<Object> mAsyncImageLoader;
	
	private Runnable mShowEmoRun = new Runnable() {
		@Override
		public void run() {
			mGridView.setVisibility(mGridView.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
		}
	};
	
	private View.OnClickListener mEditOnClicked = new View.OnClickListener(){
		public void onClick(View paramView) {
	      if (PostEditActivity.this.mGridView.getVisibility() == View.VISIBLE)
	    	  PostEditActivity.this.mGridView.setVisibility(View.GONE);
	    }
		
	};
	
	private View.OnFocusChangeListener mFocusChangeListener = new View.OnFocusChangeListener(){
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if ((v == PostEditActivity.this.mEditTitle ||
				v == PostEditActivity.this.mBtnBack ||
				v == PostEditActivity.this.mBtnPost) && hasFocus ) {
				PostEditActivity.this.mSelectEmotion.setVisibility(View.GONE);
				PostEditActivity.this.mSelectFollow.setVisibility(View.GONE);
				PostEditActivity.this.mSelectImg.setVisibility(View.GONE);
				PostEditActivity.this.mSelectW200.setVisibility(View.GONE);
			}
			
			if (v == PostEditActivity.this.mEditBody) {
				PostEditActivity.this.mSelectEmotion.setVisibility(View.VISIBLE);
				PostEditActivity.this.mSelectFollow.setVisibility(View.VISIBLE);
				PostEditActivity.this.mSelectImg.setVisibility(View.VISIBLE);
				PostEditActivity.this.mSelectW200.setVisibility(View.VISIBLE);
			}
		}
	};
	
	private TextWatcher mWatcher = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			mHasChanged = true;
			if (mPostMode.equals(PostMode.CREATE)) {
				if (count >= MAX_WORD_COUNT) 
					mSelectW200.setVisibility(View.GONE);
				else
					mSelectW200.setVisibility(View.VISIBLE);
			}
		}
		@Override
		public void afterTextChanged(Editable s) {
			refreshPostButton();
		}
	};
	
	class EmotionAdapter extends BaseAdapter {

		private Context mContext = null;
		private HashMap<String, Integer> mEmo = EmotionUtil.getWriteEmotion();
		private ArrayList<Integer> mEmoList = EmotionUtil.getWriteEmotionList();
		private HashMap<Integer, SoftReference<Bitmap>> mSoftBitmap = null;
		  
		public EmotionAdapter(Context context) {
			mEmo = EmotionUtil.getWriteEmotion();
			mEmoList = EmotionUtil.getWriteEmotionList();
			mSoftBitmap = new HashMap<Integer, SoftReference<Bitmap>>();
			mContext = null;
			mContext = context;
		}
		
		@Override
		public int getCount() {
			return this.mEmoList.size();
		}

		@Override
		public Object getItem(int position) {
			Bitmap bmp = null;
			SoftReference<Bitmap> ref = null;
			Integer resid = null;
			
			resid = mEmoList.get(position);
			ref = mSoftBitmap.get(resid);
			if (ref != null) {
				bmp = ref.get();
			} else {
				bmp = ImageUtil.getResBitmap(mContext, resid);
				ref = new SoftReference<Bitmap>(bmp);
				mSoftBitmap.put(resid, ref);
			}
			return bmp;
		}
		
		public String getName(int position) {
			String name = null;
			Iterator<Entry<String,Integer>> iter;
			Map.Entry<String, Integer> entry;
			int resid = 0;
			
			resid = mEmoList.get(position).intValue();
			iter = mEmo.entrySet().iterator();
			while(iter.hasNext()) {
				entry = (Map.Entry<String, Integer>)iter.next();
				if (entry.getValue().intValue() == resid){
					name = entry.getKey();
					break;
				}
			}
			return name;
		}
		
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView view = null;
			
			if (convertView == null) {
				int px = 0;
				
				px = ResUtil.dip2px(mContext, 50f);
				view = new ImageView(mContext);
				view.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
				view.setLayoutParams(new AbsListView.LayoutParams(px, px));
				
				convertView = view;
			} else {
				view = (ImageView) convertView;
			}
			view.setFocusable(false);
			view.setImageBitmap((Bitmap) getItem(position));
			return view;
		}
		
	}
	
	class CreaetPostAsyncTask extends AsyncTask<Post, Post, Post> {

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
				if (PostMode.CREATE.equals(mPostMode))
					showToast(ApolloApplication.app().getString(
							R.string.post_create_success));
				else
					showToast(ApolloApplication.app().getString(
							R.string.post_reply_success));
				
				if (mPost.getPostType().equals(PostType.DRAFT))
					Posts.removeDraft(mPost.getPostId());
				
	            setResult(-1);
	            finish();
			}
		}
	}
	
	public static void startActivity(Activity activity, Section section) {
		Intent intent = null;
		Bundle bundle = null;
		
		intent = new Intent(activity, PostEditActivity.class);
		bundle = new Bundle();
		bundle.putParcelable(KEY_SECTION, section);
		intent.putExtras(bundle);
		intent.putExtra(KEY_MODE, PostMode.CREATE.getValue());
		activity.startActivity(intent);
	}
	
	public static void startActivity(Activity activity, Thread thread) {
		Intent intent = null;
		Bundle bundle = null;
		
		intent = new Intent(activity, PostEditActivity.class);
		bundle = new Bundle();
		bundle.putParcelable(KEY_THREAD, thread);
		intent.putExtras(bundle);
		intent.putExtra(KEY_MODE, PostMode.REPLY.getValue());
		activity.startActivity(intent);
	}
	
	public static void startActivity(Activity activity, PostMode mode, Thread thread, Post post) {
		Intent intent = null;
		Bundle bundle = null;
		
		intent = new Intent(activity, PostEditActivity.class);
		bundle = new Bundle();
		bundle.putParcelable(KEY_THREAD, thread);
		bundle.putParcelable(KEY_POST, post);
		intent.putExtras(bundle);
		intent.putExtra(KEY_MODE, mode.getValue());
		activity.startActivity(intent);
	}
	
	private void fillPost() {
		User user = null;
		
		user = Users.getUser();
		mPost.setAuthor(user);
		mPost.setSubject(this.mEditTitle.getText().toString());
		mPost.setBody(this.mEditBody.getText().toString());
		if (PostMode.CREATE.equals(mPostMode)) {
			mPost.setSection(mSection);
		} else {
			mPost.setThreadId(mThread.getThreadId());
			mPost.setSection(mThread.getSection());
		}
	}
	private void createPost() {
		fillPost();
		mCreatePostTask = new CreaetPostAsyncTask();
		mCreatePostTask.execute(mPost);
	}
	
	private void popupSaveDraft() {
		if (this.mEditTitle.getText().toString().trim().length() > 0 || this.mEditBody.getText().toString().trim().length() > 0) {
			if (mHasChanged)
				this.mDraftDialog.show();
			else
				finish();					
		} else {
			finish();
		}
	}
	
	private void insertImage(SpannableStringBuilder spannable, Bitmap bmp) {
		BitmapDrawable draw = null;
		
		draw = new BitmapDrawable(bmp);
		draw.setBounds(0, 0, draw.getIntrinsicWidth(), draw.getIntrinsicHeight());
    	spannable.setSpan(new ImageSpan(draw, 0), 0, spannable.length(), Spannable.SPAN_POINT_MARK);
    	int start = mEditBody.getSelectionStart();
		mEditBody.getText().insert(start, spannable);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == RESULT_OK ) {
			if (requestCode == RequestResponseCode.REQUEST_GALLERY_IMAGE) {
				String path = null;
			
				path = data.getStringExtra("url");
				if (TextUtils.isEmpty(path)) 
					path = FileUtil.getFilePath(this, data.getData());

				buidImageSpan(path);
			} else if (requestCode == RequestResponseCode.REQUEST_CAMERA_IMAGE) {				
				buidImageSpan(Constants.APOLLO_CAMERA_TEMP);
			} else if (requestCode == RequestResponseCode.REQUEST_FOLLOW_USER) {
				int last_position = mEditBody.getSelectionStart();
				String name = " @" + data.getStringExtra("name") + " ";
				
				mEditBody.getText().insert(last_position, name);
			}
		} else {
			
		}
	}
	
	private void buidImageSpan(String path) {
		Bitmap bmp = null;
		SpannableStringBuilder spannable = null;
		
		spannable = new SpannableStringBuilder(" [img]" + path +"[/img] ");
		bmp = mAsyncImageLoader.loadImage(path, spannable, new OnImageLoaderListener<Object>() {
            @Override
            public void imageLoaded(Bitmap bmp, Object obj, String url) {
            	SpannableStringBuilder spannable = (SpannableStringBuilder) obj;

            	insertImage(spannable, bmp);
            }
        });
		if (bmp != null) {
			insertImage(spannable, bmp);
		}
	}
	
	private void refreshPostButton() {
		boolean isEmpty = false;
		
		if (mEditBody.getText().toString().trim().length() == 0) 
			isEmpty  = true;

		if (mPostMode.equals(PostMode.CREATE) && mEditTitle.getText().toString().trim().length() == 0) 
			isEmpty  &= true;
		
		if (isEmpty == true) {
			mBtnPost.setEnabled(false);
			mBtnPost.setTextColor(0xff8aaef2);
		} else {
			mBtnPost.setEnabled(true);
			mBtnPost.setTextColor(-1);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = null;
		String sectionId = null;
		int threadId = 0;
		
		intent = super.getIntent();
		
		mPostMode = PostMode.parse(intent.getExtras().getInt(KEY_MODE));
		if (PostMode.CREATE.equals(mPostMode)) {
			mSection =  (Section) intent.getExtras().get(KEY_SECTION);
			sectionId = mSection.getSectionId();
			mPost = new Post();
			threadId = -1;
		} else {
			Object obj = null;
			
			obj = intent.getExtras().get(KEY_THREAD);
			if (obj != null)
				mThread = (Thread) obj;
			
			obj = intent.getExtras().get(KEY_POST);
			if (obj != null)
				mPost = (Post) obj;
			else 
				mPost = new Post();
			
			sectionId = mThread.getSection().getSectionId();
			threadId = mThread.getThreadId();
		}
		// check draft
		Post draft = null;
		
		try {
			draft = Posts.getLastDraft(sectionId, threadId, Users.getUser().getUserId());
			mPost = draft;
		} catch (ApplicationException ex) {
		}
		
		setContentView(R.layout.activity_post_edit);
		mAsyncImageLoader = new AsyncImageLoader<Object>();
		mAsyncImageLoader.setScaleWidth(200f);
		initViews();
		initListeners();
		
		refreshPostButton();
	}
	
	private void initViews() {
		this.mEmoAdapter = new EmotionAdapter(this);
		this.mGridView = (GridView) findViewById(R.id.face_view);
		this.mGridView.setAdapter(this.mEmoAdapter);
		this.mEditTitle = (EditText) findViewById(R.id.post_subject);
		this.mEditBody = (EditText) findViewById(R.id.post_body);
		this.mTopTitle = (TextView) findViewById(R.id.top_title);
		this.mBtnPost = (Button)findViewById(R.id.post);
		
		this.mSelectEmotion = (ImageView) findViewById(R.id.select_face);
		this.mSelectFollow = (ImageView) findViewById(R.id.select_follow);
		this.mSelectImg = (ImageView) findViewById(R.id.select_image);
		this.mSelectW200 = (ImageView) findViewById(R.id.select_w200);
		this.mBtnBack = (Button) findViewById(R.id.back);
		if (mPostMode.equals(PostMode.CREATE)) 
			mSelectW200.setVisibility(View.VISIBLE);
		else
			mSelectW200.setVisibility(View.GONE);

		if (!this.mPostMode.equals(PostMode.CREATE))
			this.mEditTitle.setVisibility(View.GONE);
		
		if (this.mPostMode.equals(PostMode.REPLY_FLOOW)) {
			String text = null;
			
			text = " @" + mPost.getAuthor().getName() + " ";
			
			this.mEditBody.getText().insert(0, text);
		} else if (this.mPostMode.equals(PostMode.REPLY_QUOTE)) {
			String text = null;
			
			text = "[quote] @" + mPost.getAuthor().getName() + " " +  Transforms.formatPost(mPost.getBody(), false) + "[/quote]" ;
			text += this.getResources().getString(R.string.template_separator);
			
			this.mEditBody.getText().insert(0, text);
		} else if (mPost != null) {
			this.mEditBody.getText().insert(0, mPost.getBody());
		}
		
		if (this.mPostMode.equals(PostMode.CREATE)) {
			this.mTopTitle.setText(R.string.post_create);
		} else {
			this.mTopTitle.setText(getResources().getString(R.string.post_reply) + mThread.getSubject());
		}
	}
	
	private void initListeners() {
		this.mEditTitle.setOnFocusChangeListener(this.mFocusChangeListener);
		this.mEditTitle.setOnClickListener(mEditOnClicked);
		this.mEditTitle.addTextChangedListener(mWatcher);
		this.mBtnPost.setOnFocusChangeListener(this.mFocusChangeListener);		
		this.mEditBody.setOnFocusChangeListener(this.mFocusChangeListener);
		this.mEditBody.setOnClickListener(mEditOnClicked);
		this.mEditBody.addTextChangedListener(mWatcher);
		
		this.mEditBody.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP)
					mGridView.setVisibility(View.GONE);
				return false;
			}
		});
		
		this.mSelectFollow.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AccountActivity.startActivityForResult(PostEditActivity.this, AccountViewMode.FOLLOW_USER_LIST_MODE, ApolloApplication.app().getCurrentUser(),
						RequestResponseCode.REQUEST_FOLLOW_USER, Intent.ACTION_GET_CONTENT);
			}
		});
		
		this.mSelectW200.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String temp_word_count = getResources().getString(R.string.template_word_count);
				String text = "";
				int loop_count = 0;
				int last_position = 0;
				
				temp_word_count = MessageFormat.format(temp_word_count, MAX_WORD_COUNT);
				loop_count = MAX_WORD_COUNT / temp_word_count.length();
				for(int idx=0; idx<=loop_count; idx++) {
					text += temp_word_count;
				}
				last_position = mEditBody.getSelectionStart();
				mEditBody.getText().insert(last_position, text);
			}
		});
 
		this.mSelectImg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mSelectImageDialog.show();
			}			
		});
		
		AlertDialog.Builder builder = (new AlertDialog.Builder(this))
													.setTitle(R.string.selecte)
													.setIcon(android.R.drawable.ic_dialog_info);

		builder.setItems(getResources().getStringArray(R.array.select_gallery), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch(which) {
				case 0:
					IntentProxy.getCameraImage(PostEditActivity.this);
					break;
					
				case 1:
					IntentProxy.getGalleryImage(PostEditActivity.this);
					break;
					
				case 2:
					GalleryActivity.startActivityForResult(PostEditActivity.this, ApolloApplication.app().getCurrentUser(), RequestResponseCode.REQUEST_GALLERY_IMAGE, Intent.ACTION_GET_CONTENT);
					break;
				}
			}
			
		});
		
		this.mSelectImageDialog = builder.create();
		this.mSelectImageDialog.setCanceledOnTouchOutside(true);
		builder = (new AlertDialog.Builder(this)).setTitle(R.string.edit_back_msg).setCancelable(false).setIcon(android.R.drawable.ic_dialog_info).setCancelable(false)
				.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						fillPost();
						Posts.save(mPost, Users.getUser());
						finish();
					}
				})
				.setNeutralButton(getString(R.string.no), new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				})
				.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		this.mDraftDialog = builder.create();
		
		this.mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String emo = null;
				
				emo = mEmoAdapter.getName(position);
				if (emo != null) {
					int start = 0;
					SpannableStringBuilder spannable = null;
					Bitmap bmp = null;
					
					start = mEditBody.getSelectionStart();
					spannable = new SpannableStringBuilder(emo);
					bmp = (Bitmap) mEmoAdapter.getItem(position);
					if (bmp != null) {
						BitmapDrawable draw = null;
						
						draw = new BitmapDrawable(bmp);
						draw.setBounds(0, 0, 1 + bmp.getWidth(), bmp.getHeight());
						draw.setGravity(3);
						spannable.setSpan(new ImageSpan(draw, 0), 0, spannable.length(), Spannable.SPAN_POINT_MARK);
						mEditBody.getText().insert(start, spannable);
					}
				}
			}
		});
		
		this.mBtnPost.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hidenSoftKeyPad(mInputManager, mEditTitle);
				hidenSoftKeyPad(mInputManager, mEditBody);
                if(mGridView.getVisibility() == View.VISIBLE)
                    mGridView.setVisibility(View.GONE);
                PostEditActivity.this.createPost();				
			}
		});
		
		this.mSelectEmotion.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mGridView.getVisibility() == View.VISIBLE) {
					mEditBody.requestFocus();
					mGridView.setVisibility(View.GONE);
					PostEditActivity.this.showSoftKeyPad(mInputManager, mEditBody);
				} else {
					hidenSoftKeyPad(mInputManager, mEditTitle);
					hidenSoftKeyPad(mInputManager, mEditBody);
                    mHandler.postDelayed(mShowEmoRun, 200L);
				}
			}			
		});
		
		this.mBtnBack.setOnFocusChangeListener(this.mFocusChangeListener);
		this.mBtnBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PostEditActivity.this.popupSaveDraft();
			}
		});
	}
}
