package apollo.data.idal;

import apollo.data.model.Post;
import apollo.data.model.User;
import apollo.enums.PostType;
import apollo.enums.SortBy;
import apollo.enums.SortOrder;
import apollo.util.DataSet;

public interface IPostDataProvider {

	DataSet<Post> getPosts(String sectionId, int threadId, int userId, int pageIndex, int pageSize, SortBy sortBy, SortOrder sortOrder);
	
	void add(Post post, User user);
	void update(Post post);
	void delete(int postId);
}
