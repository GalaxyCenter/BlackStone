package apollo.bll;

import apollo.data.dalfactory.DataAccess;
import apollo.data.idal.IConfigDataProvider;
import apollo.data.model.Config;

public class Configs {

	public static Config getConfig(int id) {
		IConfigDataProvider localProvider = DataAccess.createLocalConfigDataProvider();
		return localProvider.getConfig(id);
	}

	public static int save(Config config) {
		if (config.id < 0)
			return add(config);
		else
			return update(config.id, config);
	}

	public static int add(Config config) {
		IConfigDataProvider localProvider = DataAccess.createLocalConfigDataProvider();
		return localProvider.add(config);
	}

	public static int update(int id, Config config) {
		IConfigDataProvider localProvider = DataAccess.createLocalConfigDataProvider();
		return localProvider.update(id, config);
	}
}
