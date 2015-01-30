package apollo.core;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;

import apollo.util.FileUtil;

public class ApolloExceptionHandler implements UncaughtExceptionHandler  {

	private UncaughtExceptionHandler mHandler = null;
	
	ApolloExceptionHandler() {
		mHandler = Thread.getDefaultUncaughtExceptionHandler();
	}
	
	private void addInfo(FileWriter writer, String key, String value) {
		try {
			writer.append(key);
			if (value != null) {
				writer.append("=");
				writer.append(value);
			}
			writer.append('\n');
		} catch (IOException ex) {
		}
	}
	
	@Override
	public void uncaughtException(Thread thread, Throwable throwable) {
		ByteArrayOutputStream baos = null;
		PrintStream ps = null;
		File file = null;
		FileWriter fw = null;
		String body = null;
		
		try {
			file = FileUtil.createFile("fatal_error.log");
		} catch (Exception ex) {
		}
		if (file == null) {
			mHandler.uncaughtException(thread, throwable);
			return;
		}
		
		try {
			baos = new ByteArrayOutputStream();
			ps = new PrintStream(baos);
			throwable.printStackTrace(ps);
			body = new String(baos.toByteArray());
			
			fw = new FileWriter(file, true);
			
			addInfo(fw, "apollo_error_start", null);
			addInfo(fw, "error", body);
			addInfo(fw, "apollo_error_end", null);

			fw.append("\n");
			fw.flush();
		} catch (IOException ex) {
			throwable.printStackTrace();
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException ex) {
				}
			}
			
			if (ps != null) {
				ps.close();
			}
			
			if (baos != null) {
				try {
					baos.close();
				} catch (IOException ex) {
				}
			}			
		}
		
		mHandler.uncaughtException(thread, throwable);
	}

}
