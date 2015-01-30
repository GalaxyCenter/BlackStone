package apollo.data.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;

public class MyBitmapDrawable extends BitmapDrawable {

	public MyBitmapDrawable(Bitmap bitmap) {
		super(bitmap);
	}

	public void draw(Canvas canvas) {
		Bitmap bitmap = getBitmap();
		if (bitmap != null)
			canvas.drawBitmap(bitmap, 0.0F, 0.0F, getPaint());
	}

}
