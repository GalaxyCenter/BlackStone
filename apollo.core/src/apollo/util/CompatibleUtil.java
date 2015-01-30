package apollo.util;

import android.app.ActivityManager;
import android.content.Context;
import android.util.FloatMath;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;

public class CompatibleUtil {

	public static int getMaxMemory() {
		return (int)Runtime.getRuntime().maxMemory();
	}
	public static int[] getScreenDimensions(Context context) {
		int[] wh = new int[2];
		Display display = null;
		
		display = ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		wh[0] = display.getWidth();
		wh[1] = display.getHeight();
		return wh;
	}
	
	public static int getBitmapMaxMemory(Context context) {
		int i = getMemoryClass(context);
		if (i <= 0)
			i = 16;
		return 1024 * (i * 1024) / 2;
	}

	public static int getMemoryClass(Context context) {
		return ((ActivityManager) context.getSystemService("activity"))
				.getMemoryClass();
	}

	public static float getSpacing(MotionEvent event) {
		if (event.getPointerCount() < 2) {
			return -1F;
		} else {
			float x = event.getX(0) - event.getX(1);
			float y = event.getY(0) - event.getY(1);
			return FloatMath.sqrt(x * x + y * y);
		}
	}
}
