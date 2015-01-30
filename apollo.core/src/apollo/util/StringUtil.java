package apollo.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import android.util.Log;

public class StringUtil {
	
	public static String join(String[] array, String separator) {
		return join(Arrays.asList(array), separator);
	}	
	
	public static String join(Collection<String> col, String separator) {
		return join(col.iterator(), separator);
	}

	public static String join(Iterable<String> array, String separator) {
		return join(array.iterator(), separator);
	}
	
	public static String join(Iterator<String> iterator, String separator) {
		StringBuffer buffer = null;
		String v = null;
		if (iterator.hasNext() == false)
			return "";

		buffer = new StringBuffer(iterator.next());
		while (iterator.hasNext()) {
			v = iterator.next();
			if (v == null)
				v = "";
			buffer.append(separator).append(v);
		}
		return buffer.toString();
	}
	
	public static String replace(String str, int start, int end,
			String replacement) {
		String new_str = null;
		char[] raw_str = null;
		char[] tar_raw_str = null;
		char[] new_raw_str = null;

		raw_str = str.toCharArray();
		tar_raw_str = replacement.toCharArray();
		new_raw_str = new char[start + tar_raw_str.length + raw_str.length
				- end - 1];

		System.arraycopy(raw_str, 0, new_raw_str, 0, start);
		System.arraycopy(tar_raw_str, 0, new_raw_str, start, tar_raw_str.length);
		System.arraycopy(raw_str, end + 1, new_raw_str, start
				+ tar_raw_str.length, str.length() - end - 1);

		new_str = new String(new_raw_str);
		return new_str;
	}

	public static String getMD5Str(String str) {
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(str.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException ex) {
			Log.e(FileUtil.class.toString(), ex.getMessage());
			return null;
		} catch (UnsupportedEncodingException ex) {
			Log.e(FileUtil.class.toString(), ex.getMessage());
			return null;
		}

		byte[] byteArray = messageDigest.digest();
		StringBuffer md5StrBuff = new StringBuffer();
		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
				md5StrBuff.append("0").append(
						Integer.toHexString(0xFF & byteArray[i]));
			else
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
		}

		return md5StrBuff.toString();
	}
}
