package apollo.service;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import apollo.bll.AutoPosts;
import apollo.bll.Posts;
import apollo.bll.Users;
import apollo.data.model.AutoPost;
import apollo.data.model.Post;
import apollo.data.model.User;
import apollo.util.DataSet;

public class AutoPostService extends Service {

	class AutoPostTimerTask extends TimerTask {

		public AutoPost post = null;
		private int curIdx = 0;
		
		@Override
		public void run() {		
			if (post.accounts.size() == 0)
				return;
			
			if (curIdx >= post.accounts.size())
				curIdx = 0;
			
			createPost(post, curIdx);
			curIdx++;
		}
	}
	
	class AutoPostThread extends Thread {
		
		public AutoPost post = null;
		private int curIdx = 0;
		
		@Override
		public void run() {
			int sleep_time = 10000;
			int cur_replys = 0;
			int diff = 0;
			boolean exit = false;
			
			if (post.accounts.size() == 0)
				exit = true;

			while(exit == false) {
				// 当前回复数不在回复区间内的时候
				
				diff = post.floorNum - cur_replys;
				if (diff < 0) {
					this.stop();
				} else if (diff > 0) {
					if (diff > 2000)
						sleep_time = 20000;
					else if (diff > 500)
						sleep_time = 10000;
					else if (diff > 200)
						sleep_time = 2800;
					else if (diff < post.accounts.size())
						sleep_time = -1;
					else
						sleep_time = 1100;
					
					if (sleep_time != -1) {
						try {sleep(sleep_time);} catch (InterruptedException e) {}
					} else {
						if (post.accounts.size() == 0)
							return;
						
						if (curIdx >= post.accounts.size())
							curIdx = 0;
						
						createPost(post, curIdx);
						curIdx++;
					}
					
				} 			
			}
		}
	}
	
	private void createPost(AutoPost ap, int userIdx) {
		Post p = null;
		User u = null;
		
			
		p = new Post();
		p.setThreadId(ap.thread.getThreadId());
		p.setSection(ap.thread.getSection());
		p.setBody(ap.postBody);
		p.setSubject(ap.thread.getSubject());
		
		u = ap.accounts.get(userIdx);
		
		Posts.add(p, u);
	}
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				DataSet<AutoPost> datas = null;
				List<AutoPost> posts = null;
				User user = null;
				AutoPostThread thread = null;
				AutoPostTimerTask task = null;
				Timer timer = null;
				long period = 0;
				
				datas = AutoPosts.getAutoPosts(1, 3);
				posts = datas.getObjects();
				for(AutoPost ap:posts) {
					for(int i=0; i<ap.accounts.size(); i++) {
						user = ap.accounts.get(i);
						user = Users.getUser(user.getUserId());
						ap.accounts.set(i, user);
					}

					if (ap.floorEnable) {
						thread = new AutoPostThread();
						thread.post = ap;
						thread.start();
					} else {
						task = new AutoPostTimerTask();
						task.post = ap;
						timer = new Timer(true);
						
						period = 45000 / ap.accounts.size();
					    timer.scheduleAtFixedRate(task, 100, period);
					}
				}
			}
		}).start();
		
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
