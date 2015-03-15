package apollo.app;

import java.util.List;

import android.test.AndroidTestCase;
import apollo.bll.Posts;
import apollo.data.model.Post;

public class ApolloTest extends AndroidTestCase {

	int mFromIndex = 0;
	int mToIndex = 0;
	int mPageSize = 20;
	boolean flush = false;
	
	public void testSave() throws Exception {
		List<Post> posts = null;
		
		for(int i=0; i<10; i++) {
			try {
				posts = load();
				flush = false;
			} catch (Exception ex) {
				flush = true;
			}
			
			for(Post p:posts) {
				System.out.println(p.getBody());
			}
		}
	}

	public List<Post> load() {
		List<Post> posts = null;

		
		mFromIndex = mToIndex;
		mToIndex += mPageSize;
		
		posts = Posts.getIndexOf("funinfo", 6227066, 0, mFromIndex, mToIndex, flush);
		return posts;
	}
}
