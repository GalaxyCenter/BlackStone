package apollo.data.idal;

import java.util.List;

import apollo.data.model.Section;
import apollo.data.model.Thread;
import apollo.data.model.User;
import apollo.util.DataSet;

public interface IBookmarkDataProvider {

	void add(Thread thread, User user);
	void delete(Thread thread, User user);
	
	DataSet<Thread> getThreads(User user, int pageIndex, int pageSize);
	List<Section> getSections(User user);
	int getPosition(Thread thread, User user);
}
