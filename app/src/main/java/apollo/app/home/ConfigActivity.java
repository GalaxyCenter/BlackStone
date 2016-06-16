package apollo.app.home;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import apollo.app.AccountActivity;
import apollo.app.LoginActivity;
import apollo.app.R;
import apollo.app.ThreadActivity;
import apollo.bll.Configs;
import apollo.core.ApolloApplication;
import apollo.core.RequestResponseCode;
import apollo.data.model.Config;
import apollo.data.model.Constants;
import apollo.enums.AccountViewMode;
import apollo.enums.ThreadViewType;
import apollo.preference.MultiSelectListPreference;

public class ConfigActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {
	private Config mConfig;
	private boolean mConfigChanged;
	
	private static String[] mRemindModeEntries;
	private static String[] mRemindModeValues;
	private static final int mRemindModeValueSize = 3;
	
	private static String[] mFontSizeEntries;
	private static String[] mFontSizeValues;
	private static final int mFontSizeValueSize = 3;
	

	private PreferenceScreen mAccountManage;
	private PreferenceScreen mAutoPostManage;
	private MultiSelectListPreference mRemindMode = null;
	private ListPreference mFontSize = null;
	private CheckBoxPreference mRemindEnable = null;
	private CheckBoxPreference mShowImgEnable = null;
	private CheckBoxPreference mShowHeadEnable = null;
	private CheckBoxPreference mSoundEnable = null;
	private CheckBoxPreference mVibrateEnable = null;
	private Preference mClearCache = null;

	private Button mBack = null;
	private TextView mTopTitle = null;
	
	public static void startActivity(Activity activity) {
		Intent intent = null;
		
		intent = new Intent(activity, ConfigActivity.class);
		activity.startActivity(intent);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_settings);
		addPreferencesFromResource(R.xml.settings);

		initPreference();
		initEntryValues();
		
		initViews();
		
		if (mConfig == null)
			mConfig = Configs.getConfig(0);

		updatePreference(mConfig);
	}
	
	private void initPreference() {
		mRemindMode = (MultiSelectListPreference) findPreference(Constants.Settings.KEY_REMIND_MODE);
		mFontSize = (ListPreference) findPreference(Constants.Settings.KEY_FONT_SIZE);
		mRemindEnable = (CheckBoxPreference) findPreference(Constants.Settings.KEY_REMIND_ENABLE);
		mShowImgEnable = (CheckBoxPreference) findPreference(Constants.Settings.KEY_SHOW_IMG);
		mShowHeadEnable = (CheckBoxPreference) findPreference(Constants.Settings.KEY_SHOW_HEAD_IMG);
		mSoundEnable = (CheckBoxPreference) findPreference(Constants.Settings.KEY_SOUND);
		mVibrateEnable = (CheckBoxPreference) findPreference(Constants.Settings.KEY_VIBRATE);
		mClearCache = (Preference) findPreference(Constants.Settings.KEY_CLEAR_CACHE);
		mAccountManage = (PreferenceScreen)findPreference(Constants.Settings.KEY_MANAGE_ACCOUNT);
		mAutoPostManage = (PreferenceScreen)findPreference(Constants.Settings.KEY_MANAGE_AUTOPOST);
		
		mAccountManage.setOnPreferenceClickListener(new OnPreferenceClickListener(){
			@Override
			public boolean onPreferenceClick(Preference preference) {
				if (ApolloApplication.app().getCurrentUser() == null) 
					LoginActivity.startActivityForResult(ConfigActivity.this, MainTabActivity.ACTIVITY_SETTINGS, ConfigActivity.this.getString(R.string.login_person_tab), RequestResponseCode.REQUEST_LOGIN_USE);
				else 
					AccountActivity.startActivity(ConfigActivity.this, AccountViewMode.ACCOUNT_LIST_MODE);
 
				return false;
			}
		});
		
		mAutoPostManage.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				ThreadActivity.startActivity(ConfigActivity.this, ThreadViewType.AUTOPOST);
				return false;
			}
			
		});
		
		mRemindMode.setOnPreferenceChangeListener(this);
		mFontSize.setOnPreferenceChangeListener(this);
		mRemindEnable.setOnPreferenceChangeListener(this);
		mShowImgEnable.setOnPreferenceChangeListener(this);
		mShowHeadEnable.setOnPreferenceChangeListener(this);
		mSoundEnable.setOnPreferenceChangeListener(this);
		mVibrateEnable.setOnPreferenceChangeListener(this);
		mClearCache.setOnPreferenceChangeListener(this);
	}
	
	private void initEntryValues() {
		Resources res = getResources();
		
		mRemindModeEntries = res.getStringArray(R.array.remind_entries);
		mRemindModeValues = res.getStringArray(R.array.remind_values);
		
		mFontSizeEntries = res.getStringArray(R.array.font_entries);
		mFontSizeValues = res.getStringArray(R.array.font_values);
	}
	
	private void initViews() {
		mBack = (Button) findViewById(R.id.back);
		mBack.setVisibility(View.INVISIBLE);
		mTopTitle = (TextView) findViewById(R.id.top_title);
		mTopTitle.setText(R.string.settings);
	}
	
	private void setPreference(ListPreference preference, int value, String strs[], String values[], int size) {
		if (strs != null && values != null) {
			int val = -1;
			for (int idx=0; idx<size; idx++) {
				val = Integer.parseInt(values[idx]);
				if (val == value) {
					preference.setSummary(strs[idx]);
					
					if (mConfigChanged == false)
						preference.setValueIndex(idx);
					break;
				}
			}
		} else {
			preference.setSummary(Constants.Settings.BLANK_SUMMARY);
		}
	}
	
	private void setPreference(MultiSelectListPreference preference, int value, String strs[], String values[], int size) {
		if (strs != null && values != null) {
			int val = -1;
			String str = null;
			StringBuffer buf = new StringBuffer(24);
			List<String> new_values = new ArrayList<String>(3);
			
			for (int idx = 0; idx < values.length; idx++)  {
				val = value & (1 << Integer.parseInt(values[idx]));
				if (val != 0) {
					new_values.add(values[idx]);
					buf.append(strs[idx]).append(",");
				}
			}
			
			int idx = buf.lastIndexOf(",");
			if (idx > 0) 
				buf.deleteCharAt(idx);

			str = buf.toString();
			preference.setSummary(str);
			
			if (mConfigChanged == false)
				preference.setValues(new_values);
		} else {
			preference.setSummary(Constants.Settings.BLANK_SUMMARY);
		}
	}

	private void updatePreference(Config profile) {
		mRemindEnable.setChecked(profile.remindEnabled);
		mShowImgEnable.setChecked(profile.showImage);
		mShowHeadEnable.setChecked(profile.showHead);
		mSoundEnable.setChecked(profile.soundEnabled);
		mVibrateEnable.setChecked(profile.vibrateEnabled);
		
		setPreference(mRemindMode, profile.remind, mRemindModeEntries, mRemindModeValues, mRemindModeValueSize);
		setPreference(mFontSize, profile.fontSize, mFontSizeEntries, mFontSizeValues, mFontSizeValueSize);
	}
	
	private void doSaveAction() {
		Configs.save(mConfig);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (preference == mRemindMode) {
			List<String> values = (List<String>) newValue;

			mConfig.remind = 0;
			for (int idx = 0; idx < values.size(); idx++)
				mConfig.remind |= 1 << Integer.parseInt(values.get(idx));
			
			setPreference(mRemindMode, mConfig.remind, mRemindModeEntries, mRemindModeValues, mRemindModeValueSize);
		} else if (preference == mFontSize) {
			mConfig.fontSize = Integer.parseInt((String) newValue);
			setPreference(mFontSize, mConfig.fontSize, mFontSizeEntries, mFontSizeValues, mFontSizeValueSize);
		} else if (preference == mRemindEnable) {
			mConfig.remindEnabled = (Boolean) newValue;
		} else if (preference == mShowImgEnable) {
			mConfig.showImage = (Boolean) newValue;
		} else if (preference == mShowHeadEnable) {
			mConfig.showHead = (Boolean) newValue;
		} else if (preference == mSoundEnable) {
			mConfig.soundEnabled = (Boolean) newValue;
		} else if (preference == mVibrateEnable) {
			mConfig.vibrateEnabled = (Boolean) newValue;
		}
		mConfigChanged = true;
		doSaveAction();
		return true;
	}
}
