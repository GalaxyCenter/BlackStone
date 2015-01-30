package apollo.util;

import java.io.File;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import apollo.app.BaseActivity;
import apollo.core.RequestResponseCode;
import apollo.data.model.Constants;

public class IntentProxy {
	
	public static void getCameraImage(BaseActivity activity) {
		Intent intent = null;
		Uri uri =null;
		File file = null;
		
		file = FileUtil.createFile(Constants.APOLLO_CAMERA_TEMP);
		uri = Uri.fromFile(file);
		intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); 
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		activity.startActivityForResult(intent, RequestResponseCode.REQUEST_CAMERA_IMAGE);   
	}

	public static void getGalleryImage(BaseActivity activity) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(intent, RequestResponseCode.REQUEST_GALLERY_IMAGE);
	}
}
