package apollo.app;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import apollo.app.home.MainTabActivity;
import apollo.core.ApolloApplication;
import apollo.util.ImageUtil;

public class SplashActivity extends BaseActivity {

	private AlphaAnimation mAnim = null;
	private Bitmap mBitmap = null;
	private ImageView mImage = null;
	private Handler mHandler;

	private boolean mHaveFinishiAnim = false;
	private boolean mHaveInitData = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		initViews();
		initListener();

		mImage.startAnimation(mAnim);
	}
	
	private void initViews() {
		this.mBitmap = ImageUtil.getResBitmap(this, R.drawable.splash_logo);
		this.mImage = (ImageView) super.findViewById(R.id.splash_logo);
		this.mImage.setImageBitmap(this.mBitmap);
		
		this.mAnim = new AlphaAnimation(0.5F, 1.0F);
		this.mAnim.setDuration(2500L);
		this.mAnim.setAnimationListener(new Animation.AnimationListener(){

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				SplashActivity.this.mHaveFinishiAnim = true;
				if (SplashActivity.this.mHaveInitData) {
					SplashActivity.this.startApp();
				}
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			
		});
	}
	
	private void initListener() {
		this.mHandler = new Handler(){
			
			public void handleMessage(Message msg) {
				SplashActivity.this.mHaveInitData = true;
				if (SplashActivity.this.mHaveFinishiAnim) {
					SplashActivity.this.startApp();
				}
				super.handleMessage(msg);
			}
			
		};
		
		(new Thread(){
			
			@Override
			public void run() {
				mHandler.handleMessage(mHandler.obtainMessage());
			}
			
		}).start();
	}
	
	private void startApp() {
		if (ApolloApplication.app().isFirstUse()) {
			ApolloApplication.app().setUsed();
			GuideActivity.startActivity(this);
		} else {
			MainTabActivity.startActivity(this, MainTabActivity.ACTIVITY_HOME);
		}
		finish();
	}
}
