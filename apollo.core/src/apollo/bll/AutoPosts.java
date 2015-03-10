package apollo.bll;

import apollo.data.dalfactory.DataAccess;
import apollo.data.idal.IAutoPostDataProvider;
import apollo.data.model.AutoPost;
import apollo.data.model.Thread;
import apollo.util.DataSet;

public class AutoPosts {
	
	public static AutoPost getAutoPost(Thread thread) {
		IAutoPostDataProvider localProvider = DataAccess.createLocalAutoPostDataProvider();
		return localProvider.getAutoPost(thread);
	}
	
	public static DataSet<AutoPost> getAutoPosts(int pageIndex, int pageSize) {
		IAutoPostDataProvider localProvider = DataAccess.createLocalAutoPostDataProvider();
		return localProvider.getAutoPosts(pageIndex, pageSize);
	}
	
	public static int save(AutoPost config) {
		if (config.id < 0) {
			return add(config);
		} else {
			update(config.id, config);
			return config.id;
		}
	}

	public static int add(AutoPost config) {
		IAutoPostDataProvider localProvider = DataAccess.createLocalAutoPostDataProvider();
		return localProvider.add(config);
	}

	public static int update(int id, AutoPost config) {
		IAutoPostDataProvider localProvider = DataAccess.createLocalAutoPostDataProvider();
		return localProvider.update(id, config);
	}
	
	public static int delete(int id) {
		IAutoPostDataProvider localProvider = DataAccess.createLocalAutoPostDataProvider();
		return localProvider.delete(id);
	}
}
