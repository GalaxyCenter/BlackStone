package apollo.app;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import apollo.app.home.MainTabActivity;
import apollo.bll.AutoPosts;
import apollo.core.ApolloApplication;
import apollo.core.RequestResponseCode;
import apollo.data.model.AutoPost;
import apollo.data.model.Constants;
import apollo.data.model.Thread;
import apollo.data.model.User;
import apollo.enums.AccountViewMode;
import apollo.util.DateTime;
import apollo.widget.DateTimePicker;
import apollo.widget.DateTimePicker.OnDateTimeSetListener;

public class AutoPostConfigActivity extends PreferenceActivity implements
		Preference.OnPreferenceChangeListener {

	private EditTextPreference mFloorNum = null;
	private EditTextPreference mBody = null;
	private Preference mStartTime = null;
	private Preference mEndTime = null;
	private Preference mAccounts = null;
	private CheckBoxPreference mFloorEnable = null;
	private DateTimePicker  mStartDateTimePicker = null;
	private DateTimePicker  mEndDateTimePicker = null;
	private Button mBack = null;
	private LinearLayout mDeleteFooter = null;
	private TextView mTopTitle = null;
	
	private AutoPost mProfile = null;
	private boolean mProfileChanged = false;
	private Thread mThread = null;
	
	public static void startActivity(Activity activity, Thread thread) {
		Intent intent = null;
	
		intent = new Intent(activity, AutoPostConfigActivity.class);
		intent.putExtra("thread", (Parcelable)thread);
		activity.startActivity(intent);
	}
	
	public static void startActivityForResult(Activity activity, Thread thread, int requestCode) {
		Intent intent = null;
	
		intent = new Intent(activity, AutoPostConfigActivity.class);
		intent.putExtra("thread", (Parcelable)thread);
		activity.startActivityForResult(intent, requestCode);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Intent intent = null;
		super.onCreate(savedInstanceState);
	
		intent = getIntent();
		mThread = intent.getParcelableExtra("thread");
		mProfile = AutoPosts.getAutoPost(mThread);
		mProfileChanged = true;
		if (mProfile == null) {
			mProfile = new AutoPost();
			mProfile.thread = mThread;
			mProfileChanged = false;
		}
		
		setContentView(R.layout.activity_settings);
		addPreferencesFromResource(R.xml.settings_autopost);

		initPreference();
		initEntryValues();
		
		initViews();
		initListeners();
		
		updatePreference(mProfile);
		
		mStartDateTimePicker.set24HourFormat(true);
		mEndDateTimePicker.set24HourFormat(true);
	}
	
	private void initViews() {
		ListView listview = getListView();
		this.mDeleteFooter= (LinearLayout)LayoutInflater.from(this).inflate(R.layout.footer_delete, null);
		if (mProfile.id > 0) {
			listview.addFooterView(this.mDeleteFooter);
		}
		
		mBack = (Button) findViewById(R.id.back);
		mTopTitle = (TextView) findViewById(R.id.top_title);
		mTopTitle.setText(R.string.aotupost_settings);
	}
	
	private void initListeners() {
		mDeleteFooter.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent data = null;
				 				
				AutoPosts.delete(mProfile.id);
 
				data = new Intent();
				data.putExtra("thread_id", mProfile.thread.getThreadId());
				setResult(RESULT_OK, data);
				finish();
			}
		});
		
		mBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();				
			}
		});
		
		mStartDateTimePicker = new DateTimePicker(AutoPostConfigActivity.this, new OnDateTimeSetListener(){
			@Override
			public void onDateTimeSet(Calendar calendarSelected,
					Date dateSelected, int year, String monthFullName,
					String monthShortName, int monthNumber, int date,
					String weekDayFullName, String weekDayShortName,
					int hour24, int hour12, int min, int sec, String AM_PM) {
				mStartTime.setSummary((new DateTime(mProfile.start).toString()));
				onPreferenceChange(mStartTime, dateSelected);
			}

			@Override
			public void onDateTimeCancel() {
			}
		});
		
		mEndDateTimePicker = new DateTimePicker(AutoPostConfigActivity.this, new OnDateTimeSetListener(){
			@Override
			public void onDateTimeSet(Calendar calendarSelected,
					Date dateSelected, int year, String monthFullName,
					String monthShortName, int monthNumber, int date,
					String weekDayFullName, String weekDayShortName,
					int hour24, int hour12, int min, int sec, String AM_PM) {
				mEndTime.setSummary((new DateTime(mProfile.end).toString()));
				onPreferenceChange(mEndTime, dateSelected);
			}

			@Override
			public void onDateTimeCancel() {
			}			
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == RESULT_OK ) {
			if (requestCode == RequestResponseCode.REQUEST_USER_SELECTED) {
				
				// 当data为空时即选中所有用户
				if (data == null) {
					mAccounts.setSummary(R.string.autopost_all_user);
					mProfile.accounts = null;
				} else {
					Bundle bundle = null;
					
					bundle = data.getExtras();
					mProfile.accounts = bundle.getParcelableArrayList("users");
					mAccounts.setSummary(R.string.autopost_selected_user);
				}
				onPreferenceChange(mAccounts, mProfile.accounts);
			}
		}
	}

	private void initPreference() {
		mFloorNum = (EditTextPreference) findPreference(Constants.AutoPostSettings.KEY_FLOOR_NUM);
		mFloorNum.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
		mFloorNum.getEditText().setFilters(new InputFilter[]{new InputFilter.LengthFilter(9)});  
		
		mBody = (EditTextPreference) findPreference(Constants.AutoPostSettings.KEY_POST_BODY);
	
		mStartTime = (Preference) findPreference(Constants.AutoPostSettings.KEY_START_TIME);
		mEndTime = (Preference) findPreference(Constants.AutoPostSettings.KEY_END_TIME);
		mAccounts = (Preference) findPreference(Constants.AutoPostSettings.KEY_ACCOUNTS);
		mFloorEnable = (CheckBoxPreference) findPreference(Constants.AutoPostSettings.KEY_FLOOR_ENABLE);
		
		mStartTime.setOnPreferenceChangeListener(this);
		mEndTime.setOnPreferenceChangeListener(this);
		mFloorEnable.setOnPreferenceChangeListener(this);
		mBody.setOnPreferenceChangeListener(this);
		mFloorNum.setOnPreferenceChangeListener(this);
		
		mAccounts.setOnPreferenceClickListener(new OnPreferenceClickListener(){
			@Override
			public boolean onPreferenceClick(Preference preference) {
				if (ApolloApplication.app().getCurrentUser() == null) 
					LoginActivity.startActivityForResult(AutoPostConfigActivity.this, MainTabActivity.ACTIVITY_SETTINGS, AutoPostConfigActivity.this.getString(R.string.login_person_tab), RequestResponseCode.REQUEST_LOGIN_USE);
				else 
					AccountActivity.startActivityForResult(AutoPostConfigActivity.this, AccountViewMode.ACCOUNT_SELECT_MODE, RequestResponseCode.REQUEST_USER_SELECTED, Intent.ACTION_GET_CONTENT);
 
				return false;
			}
		});
	}

	private void initEntryValues() {

	}

	public boolean onPreferenceTreeClick(PreferenceScreen screen, Preference preference) {
		if (preference == this.mStartTime) {
			mStartDateTimePicker.setDate(mProfile.start);
			mStartDateTimePicker.show();
		} else if (preference == this.mEndTime) {
			mEndDateTimePicker.setDate(mProfile.end);
			mEndDateTimePicker.show();
		}
		return true;
	}
	
	private void updatePreference(AutoPost profile) {
		if (mProfileChanged == false)
			return;
		
		mFloorEnable.setChecked(profile.floorEnable);
		if (profile.floorEnable) {
			mFloorNum.setSummary(profile.floorNum + getString(R.string.floor));
		}
		
		if (profile.accounts == null) 
			mAccounts.setSummary(R.string.autopost_all_user);
		else
			mAccounts.setSummary(R.string.autopost_selected_user);
		
		mBody.setSummary(profile.postBody);
		mBody.setText(profile.postBody);
		
		mStartTime.setSummary((new DateTime(profile.start).toString()));
		mEndTime.setSummary((new DateTime(profile.end).toString()));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (preference == mFloorEnable) {
			mProfile.floorEnable = (Boolean) newValue;
		} else if (preference == mFloorNum) {
			if (TextUtils.isEmpty((String)newValue))
				mProfile.floorNum = 0;
			else
				mProfile.floorNum = Integer.parseInt((String)newValue);
			mFloorNum.setSummary(mProfile.floorNum + getString(R.string.floor));
		} else if (preference == mBody) {
			mProfile.postBody = (String) newValue;
			mBody.setSummary(mProfile.postBody);
		} else if (preference == mAccounts) {
			mProfile.accounts = (List<User>) newValue;
		} else if (preference == mEndTime) {
			mProfile.end = (Date)newValue;
		} else if (preference == mStartTime) {
			mProfile.start = (Date)newValue;
		}
		mProfileChanged = true;
		doSaveAction();
		mProfileChanged = false;
		return true;
	}
	
	private void doSaveAction() {
		mProfile.id = AutoPosts.save(mProfile);
	}
}
