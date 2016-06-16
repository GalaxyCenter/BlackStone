package apollo.data.sqlitedal;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import apollo.data.idal.IPostDataProvider;
import apollo.data.model.Constants;
import apollo.data.model.Post;
import apollo.data.model.Section;
import apollo.data.model.User;
import apollo.enums.PostType;
import apollo.enums.SortBy;
import apollo.enums.SortOrder;
import apollo.util.DataSet;
import apollo.util.DateTime;
import apollo.util.StringUtil;

public class PostDataProvider implements IPostDataProvider {

	@Override
	public void add(Post post, User user) {
		SQLiteDatabase db = null;
		ContentValues values = null;
		
		values = new ContentValues(14);
		values.put(Post.Columns.SECTION_ID, post.getSection().getSectionId());
		values.put(Post.Columns.THREAD_ID, post.getThreadId());
		values.put(Post.Columns.USER_ID, user.getUserId());
		values.put(Post.Columns.SUBJECT, post.getSubject());
		values.put(Post.Columns.BODY, post.getBody());
		values.put(Post.Columns.POST_DATE, post.getPostDate().toString());
		
		db = DatabaseHelper.getWriteDatabase();
		db.insert(Constants.APOLLO_DATA_TABLE_POST, null, values);
		db.close();
	}

	@Override
	public DataSet<Post> getPosts(String sectionId, int threadId, int userId, int pageIndex, int pageSize, SortBy sortBy, SortOrder sortOrder) {
		SQLiteDatabase db = null;
		Cursor cursor = null;
		DataSet<Post> datas = null;
		List<Post> posts = null;
		List<String> clausesList = null;
		List<String> argsList = null;
		Post post = null;
		Section section = null;
		User author = null;
		String[] columns = null;
		String args[] = null;
		String clause = null;
		String orderBy = null;
		String limit = null;
		
		posts = new ArrayList<Post>();
		datas = new DataSet<Post>();
		datas.setObjects(posts);
		
		columns = new String[] {Post.Columns.ID, Post.Columns.SECTION_ID, Post.Columns.THREAD_ID, Post.Columns.USER_ID, Post.Columns.SUBJECT, Post.Columns.BODY, Post.Columns.POST_DATE};
		
		argsList = new ArrayList<String>();
		clausesList = new ArrayList<String>();
		
		clausesList.add(Post.Columns.SECTION_ID + "=?");
		argsList.add(sectionId);
		
		if (threadId != 0) {
			clausesList.add(Post.Columns.THREAD_ID + "=?");
			argsList.add(Integer.toString(threadId));
		}
		if (userId != 0) {
			clausesList.add(Post.Columns.USER_ID + "=?");
			argsList.add(Integer.toString(userId));
		}
		
		clause = StringUtil.join(clausesList, " AND ");
		args = new String[clausesList.size()];
		argsList.toArray(args);
		
		if (sortBy == SortBy.LAST_REPLY)
			orderBy = "post_date " + sortOrder;
		else
			orderBy = Post.Columns.ID + " " + sortOrder;
		
		limit = (pageIndex - 1) * pageSize + "," + pageSize;
		
		db = DatabaseHelper.getReadDatabase();
		cursor = db.query(Constants.APOLLO_DATA_TABLE_POST, columns, clause, args, null, null, orderBy, limit);
		if (cursor != null) {
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				post = new Post();
				section = new Section();
				author = new User();
				
				section.setSectionId(cursor.getString(1));
				author.setUserId(cursor.getInt(3));
				post.setAuthor(author);
				post.setSection(section);
				post.setPostId(cursor.getInt(0));
				post.setThreadId(cursor.getInt(2));
				post.setSubject(cursor.getString(4));
				post.setBody(cursor.getString(5));
				post.setPostDate(DateTime.parse(cursor.getString(6)));
				post.setPostType(PostType.DRAFT);
				posts.add(post);
			}
			cursor.close();
		}
		
		columns = new String[] {"count(0)"};
		cursor = db.query(Constants.APOLLO_DATA_TABLE_POST, columns, clause, args, null, null, null, null);
		if (cursor != null) {
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				datas.setTotalRecords(cursor.getInt(0));
			}
			cursor.close();
		}
		db.close();
		return datas;
	}

	@Override
	public void update(Post post) {
		SQLiteDatabase db = null;
		ContentValues values = null;
		String whereClause = null;
		String[] whereArgs = null;
		
		values = new ContentValues(6);
		values.put(Post.Columns.SECTION_ID, post.getSection().getSectionId());
		values.put(Post.Columns.THREAD_ID, post.getThreadId());
		values.put(Post.Columns.USER_ID, post.getAuthor().getUserId());
		values.put(Post.Columns.SUBJECT, post.getSubject());
		values.put(Post.Columns.BODY, post.getBody());
		values.put(Post.Columns.POST_DATE, post.getPostDate().toString());
		
		whereClause = Post.Columns.ID + "=?";
		whereArgs = new String[]{Integer.toString(post.getPostId())};
		
		db = DatabaseHelper.getWriteDatabase();
		db.update(Constants.APOLLO_DATA_TABLE_POST, values, whereClause, whereArgs);
		db.close();
	}

	@Override
	public void delete(int postId) {
		SQLiteDatabase db = null;
		String whereClause = null;
		String[] whereArgs = null;
		
		whereClause = Post.Columns.ID + "=?";
		whereArgs = new String[]{Integer.toString(postId)};
		db = DatabaseHelper.getWriteDatabase();
		db.delete(Constants.APOLLO_DATA_TABLE_POST, whereClause, whereArgs);
		db.close();
	}


}
