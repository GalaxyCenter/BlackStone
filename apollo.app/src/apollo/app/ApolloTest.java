package apollo.app;

import java.util.List;

import android.test.AndroidTestCase;
import apollo.bll.Threads;

public class ApolloTest extends AndroidTestCase {
	
	public void testSave() throws Exception {
		List<String> list = null;
		
		list = Threads.getRecommend();
		for(String s:list) {
			System.out.println(s);
		}
	}

}
