package apollo.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.util.Log;
import apollo.core.ApolloApplication;

public class ConfigUtil  {
	private Properties props;
	
	protected ConfigUtil(String file) {
		InputStream is = null;
		
		this.props = new Properties();
		try {
			is = ApolloApplication.app().getAssets().open(file);
			this.props.load(is);
		} catch (FileNotFoundException ex) {
			Log.e(ConfigUtil.class.toString(), ex.getMessage());
		} catch (IOException ex) {
			Log.e(ConfigUtil.class.toString(), ex.getMessage());
		}
	}
	
	public static ConfigUtil getInstance() {
		ConfigUtil config = null;
		String file = null;
		
		file = "config.properties";
		config = new ConfigUtil(file);
		return config;
	}
	
	public String getProperty(String key) {
		return this.props.getProperty(key);
	}
}
