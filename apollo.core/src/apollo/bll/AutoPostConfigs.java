package apollo.bll;

import apollo.data.dalfactory.DataAccess;
import apollo.data.idal.IAutoPostConfigDataProvider;
import apollo.data.model.AutoPostConfig;
import apollo.data.model.Thread;

public class AutoPostConfigs {
	
	public static AutoPostConfig getConfig(Thread thread) {
		IAutoPostConfigDataProvider localProvider = DataAccess.createLocalAutoPostConfigDataProvider();
		return localProvider.getConfig(thread);
	}
	
	public static int save(AutoPostConfig config) {
		if (config.id < 0)
			return add(config);
		else
			return update(config.id, config);
	}

	public static int add(AutoPostConfig config) {
		IAutoPostConfigDataProvider localProvider = DataAccess.createLocalAutoPostConfigDataProvider();
		return localProvider.add(config);
	}

	public static int update(int id, AutoPostConfig config) {
		IAutoPostConfigDataProvider localProvider = DataAccess.createLocalAutoPostConfigDataProvider();
		return localProvider.update(id, config);
	}
}
