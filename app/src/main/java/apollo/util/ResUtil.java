package apollo.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextPaint;
import android.util.Log;

public class ResUtil {
	
	public static float getTextWidth(String text, float Size) { // 第一个参数是要计算的字符串，第二个参数是字提大小
		TextPaint FontPaint = new TextPaint();
		FontPaint.setTextSize(Size);
		return FontPaint.measureText(text);
	}
	
	public static int getBitmapMaxMemory(Context context) {
		int i = CompatibleUtil.getBitmapMaxMemory(context);
		Log.d("ResUtil", "getBitmapMaxMemory:" + String.valueOf(i));
		return i;
	}
	
	public static int dip2px(Context context, float f) {
		return (int) (0.5F + f * context.getResources().getDisplayMetrics().density);
	}
	
	public static boolean isSupportGesture() {		
		return android.os.Build.VERSION.SDK_INT > 4;
	}
	
	public static int getEquipmentHeight(Context context) {
		return context.getResources().getDisplayMetrics().heightPixels;
	}

	public static int getEquipmentWidth(Context context) {
		return context.getResources().getDisplayMetrics().widthPixels;
	}

	public static String read(AssetManager assets, String file) {
		InputStream is = null;
		ByteArrayOutputStream baos = null;
		byte buf[] = null;
		int len = 0;
		
		buf = new byte[1024];
		try {
			is = assets.open(file);
			baos = new ByteArrayOutputStream();
			while ((len = is.read(buf)) != -1) {
				baos.write(buf, 0, len);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try { baos.close(); } catch (Exception ex) {}
			try { is.close(); } catch (Exception ex) {}
		}
		return baos.toString();
	}
}
