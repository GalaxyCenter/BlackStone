package apollo.data.idal;

import apollo.data.model.AutoPostConfig;
import apollo.data.model.Thread;

public interface IAutoPostConfigDataProvider {


	AutoPostConfig getConfig(Thread thread);
	
	int add(AutoPostConfig config);
	int update(int id, AutoPostConfig config);
	int delete(AutoPostConfig config);
	
}
