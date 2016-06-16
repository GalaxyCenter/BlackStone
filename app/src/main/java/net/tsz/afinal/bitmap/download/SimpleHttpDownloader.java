/**
 * Copyright (c) 2012-2013, Michael Yang 杨福海 (www.yangfuhai.com).
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
package net.tsz.afinal.bitmap.download;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import apollo.util.ImageUtil;


public class SimpleHttpDownloader implements Downloader {

	private static final String TAG = "BitmapDownloader";

	private static final int IO_BUFFER_SIZE = 8 * 1024; // 8k

	public boolean downloadToLocalStreamByUrl(String urlString, OutputStream outputStream) {
		byte[] bytes = null;
		BufferedOutputStream out = null;
		
		out = new BufferedOutputStream(outputStream, IO_BUFFER_SIZE);
		bytes = ImageUtil.getBytes(urlString);
		try {
			out.write(bytes);
		} catch (IOException ex) {
		} finally {
			if (out != null) {
                try {out.close();}catch(Exception e) {};
            }
		}
		return true;
	}

}
