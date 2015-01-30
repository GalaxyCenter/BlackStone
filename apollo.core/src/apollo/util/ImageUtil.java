package apollo.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.Log;
import apollo.core.R;
import apollo.exceptions.ApplicationException;
import apollo.net.WebRequest;
import apollo.net.WebResponse;

public class ImageUtil {
	
	public static Bitmap getResBitmap(Context context, int resid) {
		Bitmap bmp = null;
		BitmapFactory.Options options = null;
		
		options = new BitmapFactory.Options();
		options.inPreferredConfig = apollo.data.model.Constants.BitmapConfig;
		bmp = BitmapFactory.decodeResource(context.getResources(), resid, options);
		return bmp;
	}
	
	public static Bitmap getBitmap(Context context, Uri uri, int maxSize) {
		Bitmap bmp = null;
		BitmapFactory.Options options = null;
		ContentResolver resolver = null;
		ParcelFileDescriptor pfd = null;
		int sampleSize = 0;
		resolver = context.getContentResolver();
		try {
			pfd = resolver.openFileDescriptor(uri, "r");
		} catch (FileNotFoundException ex) {
			throw new ApplicationException(0, ex.getMessage());
		}
		options = new BitmapFactory.Options();
		options.inPreferredConfig = apollo.data.model.Constants.BitmapConfig;
		options.inDither=false;                     //Disable Dithering mode  
		options.inJustDecodeBounds = true;
		
		BitmapFactory.decodeFileDescriptor(pfd.getFileDescriptor(), null, options);
		while(true) {
			if ((options.outWidth / (sampleSize + 1) <= maxSize) && (options.outHeight / (sampleSize + 1) <= maxSize)) {
				options.inJustDecodeBounds = false;
				options.inSampleSize = sampleSize;
				bmp = BitmapFactory.decodeFileDescriptor(pfd.getFileDescriptor(), null, options);
				break;
			}
			sampleSize ++;
		}

		try {
			pfd.close();
		} catch (IOException ex) {
		}
		return bmp;
	}
		
	public static Bitmap getScaleBitmap(Bitmap src, float new_width, float new_height) {
		Matrix matrix = null;
		Bitmap scale_bmp = null;
		float raw_width = 0;
		float raw_height = 0;
		float sx = 0f;
		float sy = 0f;

		raw_width = src.getWidth();
		raw_height = src.getHeight();
		
		if (new_width != 0f)
			sx = new_width / raw_width; 
		
		if (new_height != 0f)
			sy = new_height / raw_height;
		
		if (sx == 0f && sy == 0f)
			throw new IllegalArgumentException("invalid new_width and new_height");
		
		if (sx == 0f)
			sx = sy;
		else if (sy == 0f)
			sy = sx;
		
		matrix = new Matrix();
		matrix.postScale(sx, sy);	
		scale_bmp = Bitmap.createBitmap(src, 0, 0, (int)raw_width, (int)raw_height, matrix, true);
		return scale_bmp;
	}
	
	public static Bitmap getScaleCanvasBitmap(Bitmap src, float new_width, float new_height) {
		float raw_width = 0;
		float raw_height = 0;
		float scale_width = 0;
		float scale_height = 0;
		float canvas_width = 0;
		float canvas_height = 0;
		float raw_scale = 0;
		float left = 0;
		float top = 0;
		Matrix matrix = null;
		Bitmap scale_bmp = null;
		Bitmap canvas_bmp = null;
		Canvas canvas = null;
		Paint paint = null;
		
		raw_width = src.getWidth();
		raw_height = src.getHeight();
		canvas_width = canvas_height = new_width;
		canvas_width = canvas_height = new_height;
		
		if (raw_width > raw_height) {
			raw_scale = raw_width / raw_height;
			new_height = new_width / raw_scale;
		} else {
			raw_scale = raw_height / raw_width;
			new_width = new_height / raw_scale;
		}		
		scale_width = new_width / raw_width;
		scale_height = new_height / raw_height;
		
		matrix = new Matrix();
		matrix.postScale(scale_width, scale_height);	
		scale_bmp = Bitmap.createBitmap(src, 0, 0, (int)raw_width, (int)raw_height, matrix, true);
		
		paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setStyle(Style.FILL);
		
		canvas_bmp = Bitmap.createBitmap((int)canvas_width, (int)canvas_height, Bitmap.Config.ARGB_8888);
		canvas = new Canvas(canvas_bmp);
		canvas.drawRect(new Rect(0, 0, (int)canvas_width, (int)canvas_height), paint); 
		
		left = (int) (canvas_width - new_width) >> 1;
		top = (int) (canvas_height - new_height) >> 1;
		canvas.drawBitmap(scale_bmp, left, top, null);
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
        
        return canvas_bmp;
	}
	
	public static InputStream getStream(String url) {
		byte[] bytes =null;
		
		bytes = getBytes(url);
		
		return new ByteArrayInputStream(bytes);
	}
	
	public static byte[] getBytes(String url) {
		byte[] data = null;
		String name = null;
		
		name = StringUtil.getMD5Str(url);
		if (FileUtil.exists("image", name) == false) {
			WebRequest req = null;
			WebResponse resp = null;
			HashMap<String, String> propertys = null;
			Pattern pattern = null;
			Matcher matcher = null;
			String referer = null;
			
			pattern = Pattern.compile("[^.]+\\.(com|cn|net|org|biz|info|cc|tv)", Pattern.CASE_INSENSITIVE);
			matcher = pattern.matcher(url);
			if (matcher.find()) {
				String key  = "app.http.reffer." + matcher.group();
				
				referer = ConfigUtil.getInstance().getProperty(key);
			}
			
			if (TextUtils.isEmpty(referer))
				referer = url;
			
			propertys = new HashMap<String, String>();
			propertys.put("Referer",  referer);
			
			req = new WebRequest();
			try {
				resp = req.create(url, null, propertys);
			} catch (Exception ex) {
				Log.e(ImageUtil.class.toString(), url + "#request error!");
				throw new ApplicationException(R.string.error_request_img);
			}
			data = resp.getContent();
			FileUtil.saveFile("image", name, data);
		} else {
			data = FileUtil.getFileData("image", name);
		}		
		return data;
	}

	public static boolean isCached(String url) {
		String name = null;

		name = StringUtil.getMD5Str(url);
		return FileUtil.exists("image", name);
	}
//	public static Drawable loadImageFromUrl(String url) {
//		URL m;
//		InputStream i = null;
//		try {
//			m = new URL(url);
//			i = (InputStream) m.getContent();
//		} catch (MalformedURLException e1) {
//			e1.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		Drawable d = Drawable.createFromStream(i, "src");
//		return d;
//	}

	public static Drawable getDrawableFromUrl(String url) {
		return Drawable.createFromStream(getStream(url), null);
	}

	public static Bitmap getBitmapFromUrl(String url)  {
		byte[] bytes = getBytes(url);
		return byteToBitmap(bytes);
	}

	public static Bitmap getRoundBitmapFromUrl(String url, int pixels)
			throws Exception {
		byte[] bytes = getBytes(url);
		Bitmap bitmap = byteToBitmap(bytes);
		return toRoundCorner(bitmap, pixels);
	}

	public static Drawable geRoundDrawableFromUrl(String url, int pixels)
			throws Exception {
		byte[] bytes = getBytes(url);
		BitmapDrawable bitmapDrawable = (BitmapDrawable) byteToDrawable(bytes);
		return toRoundCorner(bitmapDrawable, pixels);
	}

	public static Bitmap byteToBitmap(byte[] byteArray) {
		Bitmap bmp = null;
		if (byteArray.length != 0) 
			bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
		
		if (bmp == null)
			throw new ApplicationException(0, "bitmap decode error");
		else
			return bmp;
	}

	public static Drawable byteToDrawable(byte[] byteArray) {
		ByteArrayInputStream ins = new ByteArrayInputStream(byteArray);
		return Drawable.createFromStream(ins, null);
	}

	public static byte[] bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {

		Bitmap bitmap = Bitmap
				.createBitmap(
						drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight(),
						drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
								: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	/**
	 * 图片去色,返回灰度图片
	 * 
	 * @param bmpOriginal
	 *            传入的图片
	 * @return 去色后的图片
	 */
	public static Bitmap toGrayscale(Bitmap bmpOriginal) {
		int width, height;
		height = bmpOriginal.getHeight();
		width = bmpOriginal.getWidth();

		Bitmap bmpGrayscale = Bitmap.createBitmap(width, height,
				Bitmap.Config.RGB_565);
		Canvas c = new Canvas(bmpGrayscale);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		c.drawBitmap(bmpOriginal, 0, 0, paint);
		return bmpGrayscale;
	}

	/**
	 * 去色同时加圆角
	 * 
	 * @param bmpOriginal
	 *            原图
	 * @param pixels
	 *            圆角弧度
	 * @return 修改后的图片
	 */
	public static Bitmap toGrayscale(Bitmap bmpOriginal, int pixels) {
		return toRoundCorner(toGrayscale(bmpOriginal), pixels);
	}

	/**
	 * 把图片变成圆角
	 * 
	 * @param bitmap
	 *            需要修改的图片
	 * @param pixels
	 *            圆角的弧度
	 * @return 圆角图片
	 */
	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	/**
	 * 使圆角功能支持BitampDrawable
	 * 
	 * @param bitmapDrawable
	 * @param pixels
	 * @return
	 */
	public static BitmapDrawable toRoundCorner(BitmapDrawable bitmapDrawable,
			int pixels) {
		Bitmap bitmap = bitmapDrawable.getBitmap();
		bitmapDrawable = new BitmapDrawable(toRoundCorner(bitmap, pixels));
		return bitmapDrawable;
	}
}
