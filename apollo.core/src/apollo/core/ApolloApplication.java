package apollo.core;

import java.util.List;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import apollo.bll.Sections;
import apollo.bll.Users;
import apollo.data.model.Constants;
import apollo.data.model.Section;
import apollo.data.model.User;
import apollo.exceptions.ApplicationException;
import apollo.exceptions.CoreException;

public class ApolloApplication extends Application {

	private static final String LAST_VERSION = "lase_version";
	private static final String SECTION_INITIALISED = "section_initialised";
	
	private static ApolloApplication app;

	private ApplicationException exception;
	private User mCurrentUser;

	public ApplicationException getException() {
		ApplicationException ex = this.exception;
		exception = null;
		return ex;
	}

	public void setException(ApplicationException exception) {
		this.exception = exception;
	}
	
	public User getCurrentUser() {
		return mCurrentUser;
	}

	public void setCurrentUser(User user) {
		if (this.mCurrentUser != null) {
			this.mCurrentUser.setActive(false);
			Users.update(this.mCurrentUser);
		}

		this.mCurrentUser = user;
		
		if (this.mCurrentUser != null && this.mCurrentUser.isActive() == false) {
			this.mCurrentUser.setActive(true);
			Users.update(this.mCurrentUser);
		}
	}
	
	public static ApolloApplication app() {
		return ApolloApplication.app;
	}
		
	private void initAccount() {
		User user = null;
		
		user = Users.getActiveUser();
		this.setCurrentUser(user);
	}
	
	private void initVersion() {
		try {
			Constants.VERSION = getPackageManager().getPackageInfo("apollo.app", PackageManager.GET_CONFIGURATIONS).versionName;
		} catch (NameNotFoundException ex) {
			Log.e(getClass().getName(), "InitVersion" + ex.getMessage());
	        Constants.VERSION = "";
		}
	}
		
	@Override
	public void onCreate() {
		super.onCreate();
		app = this;
		
		Thread.setDefaultUncaughtExceptionHandler(new ApolloExceptionHandler());
		
		initVersion();
		initAccount();
	}
	
	public boolean isFirstUse() {
        boolean isFirest = false;
        String ver = null;
        
        ver = getSharedPreferences(Constants.SETTINGFILE, 0).getString(LAST_VERSION, "");
        isFirest = ver.equals(Constants.VERSION) == false;
        return isFirest;
	}
	
	public void setUsed() {
		SharedPreferences.Editor editor = getSharedPreferences(Constants.SETTINGFILE, 0).edit();
		editor.putString(LAST_VERSION, Constants.VERSION);
		editor.commit();
	}
}
