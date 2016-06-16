package apollo.data.idal;

import java.util.List;

import apollo.util.DateTime;
import apollo.data.model.Thread;
import apollo.data.model.User;
import apollo.enums.PostMode;
import apollo.enums.SectionType;
import apollo.enums.SortBy;

public interface IThreadDataProvider {

	List<Thread> getRecommendImageThread();
	List<Thread> getThreads(String sectionId, SortBy sortBy, DateTime postsOlderThan);
	List<Thread> search(String sectionId, String searchTerms, int pageIndex);
	List<String> getRecommends();
	
	List<Thread> getThreads(User user, SectionType type, PostMode mode, int pageIndex, int pageSize);
}
