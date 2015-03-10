package apollo.data.idal;

import apollo.data.model.AutoPost;
import apollo.data.model.Thread;
import apollo.util.DataSet;

public interface IAutoPostDataProvider {


	AutoPost getAutoPost(Thread thread);
	DataSet<AutoPost> getAutoPosts(int pageIndex, int pageSize);
	
	int add(AutoPost config);
	int update(int id, AutoPost config);
	int delete(int id);
	
}
