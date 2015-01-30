package apollo.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import apollo.core.ApolloApplication;
import apollo.exceptions.ApplicationException;

public class FileUtil {

	public static final File EXTERNAL_STORAGE_DIRECTORY = Environment
			.getExternalStorageDirectory();

	private static void saftCloseStream(InputStream is) {
		if (is != null) {
			try {
				is.close();
			} catch (IOException ex) {

			}
		}
	}

	private static void saftCloseStream(OutputStream os) {
		if (os != null) {
			try {
				os.close();
			} catch (IOException ex) {

			}
		}
	}

	public static boolean createPath(String path) {
		File f = new File(path);

		if (f.exists() == false)
			return f.mkdirs();

		return false;
	}

	public static File createFile(String name) {
		File file = null;

		if (isLocalFile(name) == false)
			file = createFile("error", name);
		else
			file = new File(name);
		
		if (file.exists() == false) {
			try {
				String path = file.getParent();

				createPath(path);
				file.createNewFile();
			} catch (IOException ex) {
				Log.e(FileUtil.class.toString(), ex.getMessage());
			}
		}

		return file;
	}

	public static File createFile(String folder, String name) {
		return createFile(EXTERNAL_STORAGE_DIRECTORY + "/"
				+ ApolloApplication.app().getPackageName() + "/" + folder + "/"
				+ name);
	}

	public static boolean isLocalFile(String file) {
		if (file.indexOf("file://") == 0)
			return true;
		else if (file.charAt(0) == '/')
			return true;

		return false;
	}

	public static boolean exists(String folder, String name) {
		if (checkSDCard() == false) 
			return false;

		return new File(EXTERNAL_STORAGE_DIRECTORY + "/" + "apollo" + "/"
				+ folder + "/" + name).exists();
	}

	public static boolean checkSDCard() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	public static String getFilePath(Context context, Uri uri) {
		String[] cols = null;
		Cursor cursor = null;
		int column_idx = 0;
		String path = null;

		cols = new String[] { MediaStore.Images.Media.DATA };
		cursor = context.getContentResolver()
				.query(uri, cols, null, null, null);
		column_idx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		if (!cursor.isAfterLast()) 
			path = cursor.getString(column_idx);
		
		cursor.close();
		return path;
	}

	public static byte[] getFileData(String folder, String name) {
		String fullpath = null;

		fullpath = EXTERNAL_STORAGE_DIRECTORY + "/" + "apollo" + "/" + folder
				+ "/" + name;
		return getFileData(fullpath);
	}

	public static byte[] getFileData(String fullpath) {
		return getFileData(new File(fullpath));
	}

	public static byte[] getFileData(File file) {
		byte[] data = null;
		int size = 0;
		ByteArrayOutputStream baos = null;
		FileInputStream fis = null;

		baos = new ByteArrayOutputStream(1024);
		data = new byte[1024];
		try {
			fis = new FileInputStream(file);
			while ((size = fis.read(data)) != -1) 
				baos.write(data, 0, size);			
		} catch (FileNotFoundException ex) {
			throw new ApplicationException(0, ex.getMessage());
		} catch (IOException ex) {
			throw new ApplicationException(0, ex.getMessage());
		} finally {
			saftCloseStream(fis);
			saftCloseStream(baos);
		}
		data = baos.toByteArray();
		return data;
	}
	
	public static Object data2Object (byte[] bytes) {  
		ByteArrayInputStream bis = null;
		ObjectInputStream ois = null;
        Object obj = null;     
        try {       
            bis = new ByteArrayInputStream (bytes);       
            ois = new ObjectInputStream (bis);       
            obj = ois.readObject();     
            ois.close();  
            bis.close();  
        } catch (IOException ex) {       
        	throw new ApplicationException(0, ex.getMessage());
        } catch (ClassNotFoundException ex) {       
        	throw new ApplicationException(0, ex.getMessage());
        } finally {
        	saftCloseStream(bis);
        	saftCloseStream(ois);
        }
        return obj;   
    }  
	
	public static void deleteFile(String folder, String name) {
		String fullpath = null;
		File file = null;

		fullpath = EXTERNAL_STORAGE_DIRECTORY + "/" + "apollo" + "/" + folder
				+ "/" + name;
		file = new File(fullpath);
		file.delete();
	}

	public static void saveFile(String folder, String name, Object obj) {
		byte[] bytes = null;     
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        
        bos = new ByteArrayOutputStream();     
        try {       
            oos = new ObjectOutputStream(bos);        
            oos.writeObject(obj);       
            oos.flush();        
			bytes = bos.toByteArray();
        } catch (IOException ex) {       
        	Log.e(FileUtil.class.toString(), ex.getMessage());
        }  finally {
        	saftCloseStream(oos);
        	saftCloseStream(bos);
        }
        saveFile(folder, name, bytes);
	}
	
	public static void saveFile(String folder, String name, byte[] data) {
		String fullpath = null;
		File file = null;

		fullpath = EXTERNAL_STORAGE_DIRECTORY + "/" + "apollo" + "/" + folder
				+ "/" + name;
		file = createFile(fullpath);
		saveFile(file, data);
	}

	public static void saveFile(File file, byte[] data) {
		OutputStream os = null;

		try {
			os = new FileOutputStream(file);
			os.write(data);
			os.flush();
		} catch (FileNotFoundException ex) {
			Log.e(FileUtil.class.toString(), ex.getMessage());
		} catch (IOException ex) {
			Log.e(FileUtil.class.toString(), ex.getMessage());
		} finally {
			saftCloseStream(os);
		}
	}
}
