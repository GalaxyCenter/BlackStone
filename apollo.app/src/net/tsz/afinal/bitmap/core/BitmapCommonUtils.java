/**
 * Copyright (c) 2012-2013, Michael Yang ��� (www.yangfuhai.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.tsz.afinal.bitmap.core;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

public class BitmapCommonUtils {
	
	private static final String TAG = "BitmapCommonUtils";
	
	/**
	 * ��ȡ����ʹ�õĻ���Ŀ¼
	 * @param context
	 * @param uniqueName Ŀ¼����
	 * @return
	 */
    public static File getDiskCacheDir(Context context, String uniqueName) {
        final String cachePath = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ? 
                		getExternalCacheDir(context).getPath() : context.getCacheDir().getPath();

        return new File(cachePath + File.separator + uniqueName);
    }

  

    /**
     * ��ȡbitmap���ֽڴ�С
     * @param bitmap
     * @return
     */
    public static int getBitmapSize(Bitmap bitmap) {
        return bitmap.getRowBytes() * bitmap.getHeight();
    }


   /**
    * ��ȡ�����ⲿ�Ļ���Ŀ¼
    * @param context
    * @return
    */
    public static File getExternalCacheDir(Context context) {
        final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
    }

    /**
     * ��ȡ�ļ�·���ռ��С
     * @param path
     * @return
     */
    public static long getUsableSpace(File path) {
    	try{
    		 final StatFs stats = new StatFs(path.getPath());
    	     return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
    	}catch (Exception e) {
			Log.e(TAG, "��ȡ sdcard �����С ������鿴AndroidManifest.xml �Ƿ������sdcard�ķ���Ȩ��");
			e.printStackTrace();
			return -1;
		}
       
    }

}
