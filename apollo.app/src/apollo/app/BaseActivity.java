package apollo.app;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import apollo.core.ApolloApplication;

public class BaseActivity extends Activity {

	public static int dip2px(Context context, float f) {
		return (int) (0.5F + f
				* context.getResources().getDisplayMetrics().density);
	}
	 
	protected void showSoftKeyPad(InputMethodManager manager, View view) {
		if (view != null && manager != null)
			try {
				manager.showSoftInput(view, 0);
			} catch (Exception exception) {
			}
	}
	
	protected void hidenSoftKeyPad(InputMethodManager manager, View view) {
		try {
			manager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		} catch (Exception localException) {
		}
	}
		 
	public void showToast(String text) {
        Toast toast = null;
        
        toast = Toast.makeText(ApolloApplication.app(), text, 4000);
        toast.setGravity(17, 0, dip2px(this, 100F));
        toast.show();
	}
}
