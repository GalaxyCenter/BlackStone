package apollo.data.idal;

import apollo.data.model.Config;

public interface IConfigDataProvider {

	Config getConfig(int id);
	
	int add(Config config);
	int update(int id, Config config);
	int delete(Config config);
}
