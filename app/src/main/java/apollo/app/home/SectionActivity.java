package apollo.app.home;

import java.util.ArrayList;
import java.util.List;

import org.miscwidgets.widget.Panel;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import apollo.app.BaseActivity;
import apollo.app.R;
import apollo.app.ThreadActivity;
import apollo.bll.Bookmarks;
import apollo.bll.Sections;
import apollo.core.ApolloApplication;
import apollo.data.model.Section;
import apollo.data.model.User;
import apollo.exceptions.ApplicationException;
import apollo.util.ResUtil;

public class SectionActivity extends BaseActivity {
	
	private List<Section> mSections;
	private List<Section> mOldSections;
	private User mUser;
	private Panel mSearchPanel;
	private RadioButton mButtonLike;
	private RadioButton mButtonTop;
	private Button mButtonRefresh = null;
	private Button mButtonSearch = null;
	private Button mButtonSearchClean = null;
	private GridView mGridView = null;
	private TextView mSearchText = null;
	private ProgressBar mProgress =null;
	private RelativeLayout mContentLayout = null;
	private RadioGroup mBarLayout = null;
	private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener = null;
	private SectionAdapter mAdapter;
	private SectionAsyncTask mTask;
	private DisplaySectionType mDisplaySectionType;
	private DisplaySectionType mPreDisplaySectionType;
	private InputMethodManager mInputManager = null;
	
	enum DisplaySectionType {
		LIKE,
		TOP,
		SEARCH
	}
		
	class SectionAsyncTask extends AsyncTask<Object, Integer, List<Section>> {

		@Override
		protected List<Section> doInBackground(Object... params) {
			DisplaySectionType type = null;
			
			type = (DisplaySectionType)params[0];
			mSections.clear();

			try {
				if (type == DisplaySectionType.TOP) {
					mSections.addAll(Sections.getTopSections());
				} else if (type == DisplaySectionType.LIKE) {
					User user  = (User) params[1];
					mSections.addAll(Bookmarks.getSections(user));
				} else {
					String searchTerms = (String) params[1];
					mSections.addAll(Sections.search(searchTerms));
				}
			} catch (ApplicationException ex) {
				ApolloApplication.app().setException(ex);
			}

			return null;
		}
		
		@Override
		protected void onPostExecute(List<Section> result) {
			onRefreshFinish();
		}
	}
	
	static class SectionViewHolder {
		private ImageView image;
		private TextView name;
	}
	
	class SectionAdapter extends BaseAdapter {
		List<Section> mItems = new ArrayList<Section>();
	    LayoutInflater mInflater;
	    //AbsListView.LayoutParams mParams;
	    
	    public SectionAdapter (List<Section> items) {
	    	mItems = items;
	    	mInflater = LayoutInflater.from(SectionActivity.this);	       
	    	
	    	//mParams = new AbsListView.LayoutParams(110, LayoutParams.WRAP_CONTENT);
	    }
	    
	    public void setItems(List<Section> items) {
	    	mItems = items;
	    }
	    
		@Override
		public int getCount() {
			return mItems.size();
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
			SectionViewHolder viewHolder = null;
	        Section section = null;
	        
	        if (convertView == null) {
	        	convertView = mInflater.inflate(R.layout.item_list_section, null);
	            viewHolder = new SectionViewHolder();
	            viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
	            viewHolder.name =  (TextView) convertView.findViewById(R.id.name);
	            
	            //convertView.setLayoutParams(mParams);
	            convertView.setTag(viewHolder);
	        } else {
	        	viewHolder = (SectionViewHolder) convertView.getTag();
	        }
	        
	        section = mItems.get(position);
	        viewHolder.name.setText(section.getName());
	        viewHolder.image.setBackgroundResource(R.drawable.like_star_gray);
			return convertView;
		}
		
	}
	
//	public static void startActivity(Activity activity, User user) {
//		Intent intent = null;
//		Bundle bundle = null;
//
//		intent = new Intent(activity, SectionActivity.class);
//		bundle = new Bundle();
//		bundle.putParcelable("user", user);
//		intent.putExtras(bundle);
//		activity.startActivity(intent);
//	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Intent intent =null;
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_section);
		
		mSections = new ArrayList<Section>();
		mAdapter = new SectionAdapter(mSections);
		intent = this.getIntent();
		mUser = (User) intent.getExtras().get("user");
		mInputManager = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
		
		initViews();
		initListeners();
		
		if (mUser == null) {
			mButtonLike.setVisibility(View.GONE);
			mButtonTop.setChecked(true);
		} else {
			mButtonLike.setChecked(true);
		}
	}
	
	private void initViews() {
		View view = null;
		
		mButtonRefresh = (Button) findViewById(R.id.refresh);
		mButtonSearch = (Button) findViewById(R.id.search);
		mButtonLike = (RadioButton) findViewById(R.id.like_forums);
		mButtonTop = (RadioButton) findViewById(R.id.top_forums);
		mGridView = (GridView) findViewById(R.id.grid);
		mGridView.setAdapter(mAdapter);
		mProgress = (ProgressBar) findViewById(R.id.progress);
		mContentLayout = (RelativeLayout) findViewById(R.id.layout_content);
		mBarLayout = (RadioGroup) findViewById(R.id.layout_bar);
		
		view = getLayoutInflater().inflate(R.layout.bar_search, null);
		mSearchText = (TextView) view.findViewById(R.id.text);
		mSearchText.setHint(R.string.please_enter_section_name);
		mButtonSearchClean = (Button) view.findViewById(R.id.clean);
		mSearchPanel = (Panel) view.findViewById(R.id.layout_search);
		mContentLayout.addView(mSearchPanel);
	}

	private void initListeners() {
		mContentLayout.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
			@Override
			public boolean onPreDraw() {
				LayoutParams params = null;
				
				params = mSearchPanel.getLayoutParams();
				params.width= ResUtil.getEquipmentWidth(SectionActivity.this) - mButtonRefresh.getWidth() - mButtonSearch.getWidth() - 6;
				mSearchPanel.setLayoutParams(params);
				return true;
			}
		});
		
		mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	Section item = null;
            	
            	item = mAdapter.mItems.get(position);
            	ThreadActivity.startActivity(SectionActivity.this, item, MainTabActivity.ACTIVITY_SECTION);
            	//AlbumActivity.startActivityForResult(GalleryActivity.this, item, mUser, RequestResponseCode.REQUEST_IMAGE_VIEW, GalleryActivity.this.getIntent().getAction());
            }
        });
		
		mOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked == true) {
					buttonView.setTextColor(getResources().getColor(R.color.tab_hightlight_text_color));
					mDisplaySectionType = buttonView == mButtonTop ? DisplaySectionType.TOP : DisplaySectionType.LIKE;
					refresh(mDisplaySectionType);
				} else {
					buttonView.setTextColor(getResources().getColor(R.color.tab_text_color));
				}
			}
		};
		
		mButtonSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mSearchPanel.isOpen()) {
					mSearchPanel.setOpen(false, true);
					mBarLayout.setVisibility(View.VISIBLE);
					mSections.addAll(mOldSections);
					mDisplaySectionType = mPreDisplaySectionType;
					SectionActivity.super.hidenSoftKeyPad(mInputManager, mSearchText);
				} else {
					mSearchPanel.setOpen(true, true);
					mBarLayout.setVisibility(View.GONE);
					mOldSections = new ArrayList<Section>(mSections);
					mPreDisplaySectionType = mDisplaySectionType;
					mSections.clear();
					mSearchText.requestFocus();
					SectionActivity.super.showSoftKeyPad(mInputManager, mSearchText);
				}
				onRefreshFinish();
			}			
		});
		
		mButtonSearchClean.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mSearchText.setText("");
			}			
		});
		
		mButtonRefresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mDisplaySectionType == DisplaySectionType.SEARCH) {
					String searchTxt = mSearchText.getText().toString().trim();
					if (searchTxt.length() != 0) {
						refresh(searchTxt);
					}
				} else {
					refresh(mDisplaySectionType);
				}
			}			
		});		
		
		mSearchText.addTextChangedListener(new TextWatcher(){

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				if (mTask != null) {
					mTask.cancel(true);
				}
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String searchTxt = s.toString().trim();
				if (searchTxt.length() != 0) {
					refresh(searchTxt);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
		
		mButtonLike.setOnCheckedChangeListener(mOnCheckedChangeListener);
		mButtonTop.setOnCheckedChangeListener(mOnCheckedChangeListener);
	}
	
	private void onRefreshFinish() {
		mAdapter.notifyDataSetChanged();
		mProgress.setVisibility(View.GONE);
	}
	
	private void refresh(DisplaySectionType type) {
		mTask = new SectionAsyncTask();
		mTask.execute(type, mUser);
		mProgress.setVisibility(View.VISIBLE);
		
		mSections.clear();
		mAdapter.notifyDataSetChanged();
	}
	
	private void refresh(String searchTxt) {
		mSections.clear();
		mAdapter.notifyDataSetChanged();
		
		mTask = new SectionAsyncTask();
		mTask.execute(DisplaySectionType.SEARCH, searchTxt);
		mProgress.setVisibility(View.VISIBLE);
	}
}
