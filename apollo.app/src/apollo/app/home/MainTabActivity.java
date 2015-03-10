package apollo.app.home;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import apollo.app.LoginActivity;
import apollo.app.R;
import apollo.app.R.color;
import apollo.app.R.drawable;
import apollo.app.R.id;
import apollo.app.R.layout;
import apollo.app.R.string;
import apollo.core.ApolloApplication;
import apollo.core.RequestResponseCode;
import apollo.widget.AnimationTabHost;

public class MainTabActivity extends TabActivity implements OnCheckedChangeListener {

	//public static final String TAB_HOME = "tab_home";
	public static final String ACTIVITY_HOME = "activity_home";
	public static final String ACTIVITY_SECTION = "activity_section";
	public static final String ACTIVITY_PERSON = "activity_person";
	public static final String ACTIVITY_MESSAGE = "activity_message";
	public static final String ACTIVITY_SETTINGS = "activity_more";
	
	public static final String TAGET_TAB = "taget_tab";
	
	private AnimationTabHost mHost;
	private RadioButton mHomeButton;
	private RadioButton mSectionButton;
	private RadioButton mMessageButton;
	private RadioButton mPersonButton;
	private RadioButton mSettingsButton;
	
	private CompoundButton mCurrentButton = null;
	private CompoundButton mPreButton;
	
	private Intent mHomeIntent;
	private Intent mSectionIntent;
	private Intent mMessageIntent;
	private Intent mPersonIntent;
	private Intent mSettingsIntent;
	
	
	/*
	 * 
	 * http://www.cnblogs.com/xiaoQLu/archive/2012/07/17/2595294.html
	 * http://blog.csdn.net/mayingcai1987/article/details/6200909
	 * 
	 */
	public static void startActivity(Context context, String tab) {
		Intent intent = null;
		
		intent = new Intent(context, MainTabActivity.class);
		intent.setFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		if (tab != null) 
			intent.putExtra(MainTabActivity.TAGET_TAB, tab);
		
		intent.putExtra("close_dialog", true);
		context.startActivity(intent);
	}
	  
	public static void startActivityOnUserChanged(Context context, String tab) {
		startActivityRefresh(context, tab, false);
	}
	
	public static void startActivityRefresh(Context context, String tab, boolean flag) {
		Intent intent = null;

		intent = new Intent(context, MainTabActivity.class);
		if (tab != null) 
			intent.putExtra(MainTabActivity.TAGET_TAB, tab);
		
		intent.putExtra("refresh_all", true);
		intent.setFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		if (flag) 
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
	
	private TabSpec buildTab(String str, Intent intent) {
		return mHost.newTabSpec(str).setContent(intent).setIndicator("", getResources().getDrawable(R.drawable.icon));
	}
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_maintabs);
		mHomeButton = (RadioButton) findViewById(R.id.radio_home);
		mSectionButton = (RadioButton) findViewById(R.id.radio_forum);
		mMessageButton = (RadioButton) findViewById(R.id.radio_message);
		mPersonButton = (RadioButton) findViewById(R.id.radio_person_info);
		mSettingsButton = (RadioButton) findViewById(R.id.radio_more);
		
		// ×°ÔØtab
		mHomeIntent = new Intent(this, HomeActivity.class);
		mSectionIntent = new Intent(this, SectionActivity.class);
		mMessageIntent = new Intent(this, PrivateMessageActivity.class); 
		mPersonIntent = new Intent(this, PersonInfoActivity.class);
		mSettingsIntent = new Intent(this, ConfigActivity.class);
		
		mHost = (AnimationTabHost) findViewById(android.R.id.tabhost);//super.getTabHost();
		mHost.setOpenAnimation(true);
		
		mHomeButton.setOnCheckedChangeListener(this);
		mSectionButton.setOnCheckedChangeListener(this);
		mMessageButton.setOnCheckedChangeListener(this);
		mPersonButton.setOnCheckedChangeListener(this);
		mSettingsButton.setOnCheckedChangeListener(this);
		
		Intent intent = new Intent();
		intent.putExtras(getIntent());
	    if (savedInstanceState != null)
	    	intent.putExtra(MainTabActivity.TAGET_TAB, savedInstanceState.getString(MainTabActivity.TAGET_TAB));
	    setupIntent(intent);
	}
	
	@Override 
	protected void onResume() {
		super.onResume();
		
		if (ApolloApplication.app().getCurrentUser() == null) {
			if (this.mCurrentButton != null && (this.mCurrentButton == this.mHomeButton || 
																	this.mCurrentButton == this.mSettingsButton ||
																	this.mCurrentButton == this.mSectionButton )) {
				this.mCurrentButton.setChecked(true);
			} else {
				this.mHomeButton.setChecked(true);
			}
		}
	}
	
	@Override
	public void onNewIntent(Intent intent) {
		String taget_tab = null;
		
		super.onNewIntent(intent);
		
		taget_tab = intent.getStringExtra(TAGET_TAB);
		if (MainTabActivity.ACTIVITY_HOME.equals(taget_tab))
			this.mHomeButton.setChecked(true);
		else if (MainTabActivity.ACTIVITY_SECTION.equals(taget_tab))
			this.mSectionButton.setChecked(true);
		else if (MainTabActivity.ACTIVITY_MESSAGE.equals(taget_tab))
			this.mMessageButton.setChecked(true);
		else if (MainTabActivity.ACTIVITY_PERSON.equals(taget_tab))
			this.mPersonButton.setChecked(true);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked == true) {
			buttonView.setTextColor(getResources().getColor(R.color.main_button_hightlight_color));
			this.mPreButton = this.mCurrentButton;
			this.mCurrentButton = buttonView;
			
			switch(buttonView.getId()) {
			case R.id.radio_home:
				this.mHost.setCurrentTabByTag(MainTabActivity.ACTIVITY_HOME);
				break;
				
			case R.id.radio_forum:
					Bundle bundle = null;
					bundle = new Bundle();
					bundle.putParcelable("user", ApolloApplication.app().getCurrentUser());
					mSectionIntent.putExtras(bundle);
					this.mHost.setCurrentTabByTag(MainTabActivity.ACTIVITY_SECTION);
				break;
			case R.id.radio_message:
				LoginActivity.startActivityForResult(this, MainTabActivity.ACTIVITY_MESSAGE, getString(R.string.login_msg_tab), RequestResponseCode.REQUEST_LOGIN_USE);
				this.mHost.setCurrentTabByTag(MainTabActivity.ACTIVITY_MESSAGE);
				break;
				
			case R.id.radio_person_info:
				LoginActivity.startActivityForResult(this, MainTabActivity.ACTIVITY_PERSON, getString(R.string.login_person_tab), RequestResponseCode.REQUEST_LOGIN_USE);
				//if (ApolloApplication.app().getCurrentUser() != null) {
					//bundle = new Bundle();
					//bundle.putParcelable("user", ApolloApplication.app().getCurrentUser());
					//mPersonIntent.putExtras(bundle);
					this.mHost.setCurrentTabByTag(MainTabActivity.ACTIVITY_PERSON);
				//}
				break;
				
			case R.id.radio_more:
				this.mHost.setCurrentTabByTag(MainTabActivity.ACTIVITY_SETTINGS);
				break;
			}
		} else {
			buttonView.setTextColor(getResources().getColor(R.color.main_button_color));
		}
	}
	
	private void setupIntent(Intent intent) {
		String type = null;
		
		if (ApolloApplication.app().getCurrentUser() == null) {
			type = MainTabActivity.ACTIVITY_HOME;
		} else {
			type = intent.getStringExtra(MainTabActivity.TAGET_TAB);
		}
		
		mHost.addTab(buildTab(ACTIVITY_HOME, mHomeIntent));
		mHost.addTab(buildTab(ACTIVITY_SECTION, mSectionIntent));
		mHost.addTab(buildTab(ACTIVITY_MESSAGE, mMessageIntent));
		mHost.addTab(buildTab(ACTIVITY_PERSON, mPersonIntent));
		mHost.addTab(buildTab(ACTIVITY_SETTINGS, mSettingsIntent));
		
		
		if (MainTabActivity.ACTIVITY_SETTINGS.equals(type)) {
			this.mCurrentButton = this.mSettingsButton;
		} else {
			this.mCurrentButton = this.mHomeButton;
		}
		 this.mCurrentButton.setChecked(true);
	}
}
