package apollo.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import apollo.app.home.MainTabActivity;
import apollo.bll.Users;
import apollo.core.ApolloApplication;
import apollo.data.model.User;
import apollo.exceptions.ApplicationException;

public class LoginActivity extends BaseActivity {

	public static final String INFO = "info";
	public static final String CLOSEACTIVITY = "close_activity";
	
	private Button mBtnAccountDel = null;
	private Button mBtnBack = null;
	private Button mBtnPassDel = null;
	private Button mBtnRefreshVcode = null;
	private Button mBtnRegedit = null;
	private Button mBtnVcodeDel = null;
	private EditText mEditAccount = null;
	private EditText mEditPassword = null;
	private EditText mEditVcode = null;
	private View mLayoutAccount = null;
	private View mLayoutLogin = null;
	private View mLayoutPassword = null;
	private View mLayoutVcode = null;
	private ProgressBar mLoginProgressBar = null;
	private ProgressBar mProgressBar = null;
	private ImageView mImageVcode = null;
	private ImageView mImageVcode1 = null;
	private ImageView mImageVcode2 = null;
	private TextView mTextAccountTitle = null;
	private TextView mTextError = null;
	private TextView mTextInfo = null;
	private TextView mTextLogin = null;
	private InputMethodManager mInputManager = null;
	private LoginAsyncTask mTask = null;
	
	private boolean mIsInputCorrect = true;
	private boolean mClose = false;
	
	private String mTicket = null;
	private String mInfo = null;
	private User mUser = null;
	
	private class LoginAsyncTask extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			String ticket = null;
		
			try {
				ticket = Users.validUser(params[0], params[1]);
			} catch (ApplicationException ex) {
				ApolloApplication.app().setException(ex);
			}		
			return ticket;
		}
		
		protected void onPostExecute(String ticket) {			
			LoginActivity.this.mLoginProgressBar.setVisibility(View.GONE);
			LoginActivity.this.mTextLogin.setText(R.string.account_login);
			LoginActivity.this.enableViews();
			
			if (ticket == null) {
				ApplicationException ex = null;
				ex = ApolloApplication.app().getException();
				if (ex != null) {
					LoginActivity.this.mTextError.setVisibility(View.VISIBLE);
					LoginActivity.this.mTextError.setText(ex.getResid());
				}
		        LoginActivity.this.mTextInfo.setVisibility(View.GONE);
		        LoginActivity.this.loginFail();
			} else {
		        LoginActivity.this.mTicket = ticket;
		        LoginActivity.this.loginSucc();
			}
		}
		
		protected void onPreExecute() {
			LoginActivity.this.hidenSoftKeyPad(LoginActivity.this.mInputManager, LoginActivity.this.mEditAccount);
			LoginActivity.this.hidenSoftKeyPad(LoginActivity.this.mInputManager, LoginActivity.this.mEditPassword);
			LoginActivity.this.mLoginProgressBar.setVisibility(View.VISIBLE);
			LoginActivity.this.mTextError.setVisibility(View.INVISIBLE);
			LoginActivity.this.mTextLogin.setText(R.string.account_login_loading);
		}

		public void cancel() {
			super.cancel(true);
			LoginActivity.this.mLoginProgressBar.setVisibility(View.GONE);
			LoginActivity.this.mTextLogin.setText(R.string.account_login);
			LoginActivity.this.enableViews();
		}
	}
	
	public static void startActivityForResult(Activity activity, String mainTab, String message, int requestCode) {
		if (ApolloApplication.app().getCurrentUser() == null) {
			Intent intent = new Intent(activity, LoginActivity.class);
			intent.putExtra(MainTabActivity.TAGET_TAB, mainTab);
			intent.putExtra(LoginActivity.INFO, message);
			activity.startActivityForResult(intent, requestCode);
		} 
	}
	
	public static void startActivityForResult(Activity activity, String message, int requestCode) {
		Intent intent = new Intent(activity, LoginActivity.class);
		intent.putExtra(LoginActivity.INFO, message);
		activity.startActivityForResult(intent, requestCode);
	}
	
	public static void startActivityForResult(Activity activity, String message, boolean close, int requestCode) {
		Intent intent = new Intent(activity, LoginActivity.class);
		intent.putExtra(LoginActivity.INFO, message);
		intent.putExtra(LoginActivity.CLOSEACTIVITY, close);
		activity.startActivityForResult(intent, requestCode);
	}
	
	
	private void startPrevActivity() {
		String tab = null;
		
		tab = getIntent().getStringExtra(MainTabActivity.TAGET_TAB);
		if (!TextUtils.isEmpty(tab)) {
			MainTabActivity.startActivity(this, tab);
		} else {
			Intent data = null;
			Bundle bundle = null;
			
			bundle = new Bundle();
			bundle.putParcelable("user", mUser);
			
			data = new Intent();
			data.putExtras(bundle);
			setResult(RESULT_OK, data);
		}

		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (mClose)
			setResult(RESULT_OK);
		else
			startPrevActivity();

		finish();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		this.mInfo = getIntent().getStringExtra(LoginActivity.INFO);
		this.mClose = getIntent().getBooleanExtra(LoginActivity.CLOSEACTIVITY, false);
		initViews();
		initListeners();
	}
	
	@Override
	protected void onDestroy() {
		cancelAsyncTask();
		System.gc();
		super.onDestroy();
	}

	private void initViews() {
		this.mInputManager = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
		this.mEditAccount = ((EditText) findViewById(R.id.login_edit_account));
		this.mEditPassword = ((EditText) findViewById(R.id.login_edit_password));
		this.mEditVcode = ((EditText) findViewById(R.id.edit_vcode));
		this.mLayoutAccount = findViewById(R.id.layout_account);
		this.mLayoutPassword = findViewById(R.id.layout_password);
		this.mLayoutVcode = findViewById(R.id.layout_vcode);
		this.mProgressBar = ((ProgressBar) findViewById(R.id.image_progress));
		this.mImageVcode1 = ((ImageView) findViewById(R.id.image_vcode1));
		this.mImageVcode2 = ((ImageView) findViewById(R.id.image_vcode2));
		this.mImageVcode = this.mImageVcode1;
		this.mLoginProgressBar = ((ProgressBar) findViewById(R.id.progress_login));
		this.mBtnRefreshVcode = ((Button) findViewById(R.id.button_vcode_refresh));
		this.mBtnAccountDel = ((Button) findViewById(R.id.button_account_del));
		this.mBtnPassDel = ((Button) findViewById(R.id.button_pass_del));
		this.mBtnVcodeDel = ((Button) findViewById(R.id.button_vcode_del));
		this.mBtnBack = (Button) findViewById(R.id.back);
		this.mTextAccountTitle = ((TextView) findViewById(R.id.text_title_account));
		this.mTextError = ((TextView) findViewById(R.id.text_error));
		this.mTextInfo = ((TextView) findViewById(R.id.text_info));
		this.mTextLogin = ((TextView)findViewById(R.id.text_login));
		this.mLayoutLogin = findViewById(R.id.layout_login);
		this.mLayoutLogin.setEnabled(false);
		
		if (TextUtils.isEmpty(this.mInfo) == false) {
			this.mTextInfo.setText(this.mInfo);
			this.mTextInfo.setVisibility(View.VISIBLE);
		}
	}
	
	private void initListeners() {
		TextWatcher watcher = null;
		
		watcher = new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {				
			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
		        LoginActivity.this.mIsInputCorrect = true;
		        LoginActivity.this.setEditBg();
		        LoginActivity.this.cancelAsyncTask();
			}
			@Override
			public void afterTextChanged(Editable s) {
				LoginActivity.this.validateLogin();
			}
		};
		
		this.mEditAccount.addTextChangedListener(watcher);
		this.mEditPassword.addTextChangedListener(watcher);
		this.mLayoutLogin.setOnClickListener(new View.OnClickListener() {
			public void onClick(View paramView) {
				LoginActivity.this.login();
			}
		});
		
		mBtnBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				LoginActivity.this.closeActivity();
			}
		});
	}
	
	public void closeActivity() {
		///MainTabActivity.startActivity(this, MainTabActivity.ACTIVITY_HOME);
		super.finish();
	}
	
	private void loginFail() {
	    this.mIsInputCorrect = false;
	    setEditBg();
	}
	
	private void loginSucc() {
		mUser = new User();
		mUser.setTicket(this.mTicket);
		mUser.setName(this.mEditAccount.getText().toString());
		mUser.setPassword(this.mEditPassword.getText().toString());
		Users.add(mUser);
		ApolloApplication.app().setCurrentUser(mUser);
		
		//startMainTabActivity();
		startPrevActivity();
	}
	
	private void login() {
		String name = null;
		String password = null;
		
		name = this.mEditAccount.getText().toString();
		password = this.mEditPassword.getText().toString();
 
		disableViews();
		cancelAsyncTask();
		this.mTask = new LoginAsyncTask();
		this.mTask.execute(name, password);
	}
	
	private void validateLogin() {
		boolean enabled = false;
		
		LoginActivity.this.enableViews();
		enabled = !TextUtils.isEmpty(this.mEditAccount.getText()) && !TextUtils.isEmpty(this.mEditPassword.getText());
		this.mLayoutLogin.setEnabled(enabled);
	}
		
	private void disableViews() {
		this.mEditAccount.setEnabled(false);
		this.mEditPassword.setEnabled(false);
		this.mEditVcode.setEnabled(false);
		this.mBtnRefreshVcode.setEnabled(false);
		this.mImageVcode.setEnabled(false);
		this.mBtnAccountDel.setEnabled(false);
		this.mBtnPassDel.setEnabled(false);
		this.mBtnVcodeDel.setEnabled(false);
		this.mEditAccount.setTextColor(Color.rgb(136, 136, 136));
		this.mEditPassword.setTextColor(Color.rgb(136, 136, 136));
		this.mEditVcode.setTextColor(Color.rgb(136, 136, 136));
	}
	
	private void setEditBg() {
		if (this.mIsInputCorrect == true) {
			this.mLayoutAccount.setBackgroundResource(R.drawable.login_input_top);
			this.mLayoutPassword.setBackgroundResource(R.drawable.login_input_top);
		} else {
			this.mLayoutAccount.setBackgroundResource(R.drawable.login_input_topwrong);
			this.mLayoutPassword.setBackgroundResource(R.drawable.login_input_topwrong);
		}
	}
		  
	private void enableViews() {
		this.mEditAccount.setEnabled(true);
		this.mEditPassword.setEnabled(true);
		this.mEditVcode.setEnabled(true);
		this.mBtnRefreshVcode.setEnabled(true);
		this.mImageVcode.setEnabled(true);
		this.mBtnAccountDel.setEnabled(true);
		this.mBtnPassDel.setEnabled(true);
		this.mBtnVcodeDel.setEnabled(true);
		this.mEditAccount.setTextColor(Color.BLACK);
		this.mEditPassword.setTextColor(Color.BLACK);
		this.mEditVcode.setTextColor(Color.BLACK);
	}
	
	private void cancelAsyncTask() {
		if (this.mTask != null) {
			this.mTask.cancel();
			this.mTask = null;
		}
	}

	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.button_account_del:
			this.mEditAccount.setText("");
			break;
		case R.id.button_pass_del:
			this.mEditPassword.setText("");
			break;
		}
	}
}
