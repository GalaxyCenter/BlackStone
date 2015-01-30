package apollo.widget;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import apollo.app.ImageActivity;
import apollo.app.R;
import apollo.app.ThreadActivity;
import apollo.app.home.PersonInfoActivity;
import apollo.bll.Configs;
import apollo.core.ApolloApplication;
import apollo.data.model.Config;
import apollo.data.model.Constants;
import apollo.data.model.Post;
import apollo.data.model.User;
import apollo.util.AsyncImageLoader;
import apollo.util.AsyncImageLoader.OnImageLoaderListener;
import apollo.util.CompatibleUtil;
import apollo.util.DateTime;
import apollo.util.EmotionUtil;
import apollo.util.ImageUtil;
import apollo.util.SpannableUtil;
import apollo.util.TimeUtil;
import apollo.util.Transforms;
import apollo.view.CustomTextView;

public class PostAdapter<E> extends BaseAdapter {
	
	public static class PostViewHolder {
		public ImageView usericon;
		public ImageView user_approved;
		public TextView author;
		public CustomTextView body;
		public TextView postdate;
		public TextView floor;
		public Object other;
	}
	
	public interface FormatBodyHandle {
		String formatted(String src);
	}
	
	public interface DisplayOtherHandle<E> {
		Object intView(View convertView);
		void setOther(Object obj, E e, int position);
	}
	public interface DisplayFloorHandle<E> {
		void setFloor(TextView view, E e, int position);
	}
	public interface DisplayAuthorHandle<E> {
		void setAuthor(TextView view, E e);
	}
	public interface DisplayBodyHandle<E> {
		void setBody(TextView view, SpannableString spannable);
	}
	public interface DisplayViewHandle<E> {
		void setViewHolder(PostViewHolder holder, E e);
	}
	public interface DisplayPostDateHandle<E> {
		void setPostDate(TextView view, DateTime time);
	}
	
	private int mResId;
	private LayoutInflater mInflater;
	private List<E> mPosts;
	private Context mContext;
	private int mFontHeight;
	private int mHaveFooter;
	private int mHaveHeader;
	private int mLineHeight;
	
	private boolean mIsShowImage;
	private Config mConfig;
	private AsyncImageLoader<SpannableString> mAsyncImageLoader;
	private AsyncImageLoader<ImageView> mAsyncIconLoader;
	private ListView mListView;
	private DisplayOtherHandle<E> mDisplayOtherHandle;
	private DisplayFloorHandle<E> mDisplayFloorHandle;
	private DisplayAuthorHandle<E> mDisplayAuthorHandle;
	private DisplayBodyHandle<E> mDisplayBodyHandle;
	private DisplayViewHandle<E> mDisplayViewHandle;
	private DisplayPostDateHandle<E> mPostDateHandle;
	private FormatBodyHandle mFormatBodyHandle;
	
	private static BitmapDrawable DRAWABLE_LOADING = (BitmapDrawable)ApolloApplication.app().getResources().getDrawable(R.drawable.image_default);
	private static BitmapDrawable DRAWABLE_USERICON = (BitmapDrawable)ApolloApplication.app().getResources().getDrawable(R.drawable.ic_launcher);
	private static String TEXT_LINK = ApolloApplication.app().getResources().getString(R.string.link_text);
	
	public PostAdapter(Context context, int resId, List<E> posts) {
		super();
		int display_width = 0;
		
		display_width = CompatibleUtil.getScreenDimensions(context)[0] - 20;
		this.mContext = context;
		this.mPosts = posts;
		this.mResId = resId;
		this.mInflater = LayoutInflater.from(context);
		this.mAsyncImageLoader = new AsyncImageLoader<SpannableString>();
		this.mAsyncImageLoader.setScaleWidth((float)display_width);
		this.mAsyncImageLoader.setScaleHeight(0f);
		this.mAsyncIconLoader = new AsyncImageLoader<ImageView>();
		this.mConfig = Configs.getConfig(0);
		SpannableUtil.defaultBBSClickLitener.setParent((Activity)context);
		SpannableUtil.defaultUserClickLitener.setParent((Activity)context);
	}
	
	public void setImageScaleWidth(int width) {
		this.mAsyncImageLoader.setScaleWidth((float)width);
	}
	
	public int getImageScaleWidth() {
		return (int)this.mAsyncImageLoader.getScaleWidth();
	}
	
	public void setDisplayOther(DisplayOtherHandle<E> callBack) {
		mDisplayOtherHandle = callBack;
	}
	
	public void setDisplayFloor(DisplayFloorHandle<E> callBack) {
		mDisplayFloorHandle = callBack;
	}
	
	public void setDisplayAuthorCallBack(DisplayAuthorHandle<E> callBack) {
		mDisplayAuthorHandle = callBack;
	}
	
	public void setDisplayBodyCallBack(DisplayBodyHandle<E> callBack) {
		mDisplayBodyHandle = callBack;
	}
	
	public void setDisplayViewCallBack(DisplayViewHandle<E> callBack) {
		mDisplayViewHandle = callBack;
	}
	
	public void setFormatBodyCallBack(FormatBodyHandle callBack) {
		mFormatBodyHandle = callBack;
	}
	
	public List<E> getPosts() {
		return mPosts;
	}
	
	public void setPosts(List<E> posts) {
		this.mPosts = posts;
	}
	
//	private void initHeight() {
//		TextView tv = new TextView(this.mContext);
//		tv.setLineSpacing(0.0F, 1.2F);
//		tv.setTextSize(12);
//		this.mLineHeight = tv.getLineHeight();
//		this.mFontHeight = (int) tv.getTextSize();
//	}
	
//	private void setTextForView(TextView textview, ContentData contentdata) {
//		if (textview != null && contentdata != null) {
//			if (contentdata.getType() == 2) 
//				textview.setText(contentdata.getSpannableString(mContext, mLineHeight, mFontHeight));
//			else 
//				textview.setText(contentdata.getUniteString());
//		}
//	}
	
//	private TextView createTextView(ContentData data) {
//        CustomTextView ctv = new CustomTextView(mContext);
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
//        int i = ResUtil.dip2px(mContext, 15F);
//        lp.rightMargin = 0;
//        lp.leftMargin = 0;
//        lp.topMargin = i;
//        lp.bottomMargin = 0;
//        ctv.setLineSpacing(0.0F, 1.2F);
//        ctv.setTextSize(Config.getContentSize());
//        ctv.setTextColor(0xff494848);
//        setTextForView(ctv, data);
//        ctv.setMovementMethod(LinkMovementMethod.getInstance());
//        ctv.setFocusable(false);
//        ctv.setLayoutParams(lp);
//        return ctv;
//	}
	
	@Override
	public int getCount() {
		int count = 0;
		
		if (this.mPosts != null) 
			count = this.mPosts.size();
		return count;
	}

	@Override
	public Object getItem(int position) {
		return this.mPosts.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		E e = null;
		PostViewHolder viewHolder = null;
		Bitmap cachedImage = null;
		
		if (convertView == null) {
			convertView = mInflater.inflate(mResId, null);
			viewHolder = new PostViewHolder();
			viewHolder.usericon = (ImageView) convertView.findViewById(R.id.user_head_icon);
			viewHolder.user_approved = (ImageView) convertView.findViewById(R.id.post_user_approved);
			viewHolder.author = (TextView) convertView.findViewById(R.id.author);
			viewHolder.body = (CustomTextView) convertView.findViewById(R.id.post_body);
			viewHolder.body.setLineSpacing(0.0F, 1.2F);
			viewHolder.body.setTextSize(mConfig.fontSize);
			viewHolder.postdate = (TextView) convertView.findViewById(R.id.postdate);
			viewHolder.floor = (TextView) convertView.findViewById(R.id.post_floor);
			
			if (mDisplayOtherHandle != null)
				viewHolder.other = mDisplayOtherHandle.intView(convertView);
			
			View.OnClickListener listener = new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					PersonInfoActivity.startActivity((Activity)mContext, (User)v.getTag());
				}
			};
			viewHolder.usericon.setOnClickListener(listener);
			viewHolder.author.setOnClickListener(listener);
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (PostViewHolder) convertView.getTag();
		}
		
		final PostViewHolder refViewHolder = viewHolder;
		e = this.mPosts.get(position);
		final Post post = (Post)e;
		if (post != null) {
			String body = null;
			SpannableString spannable = null;

			body = post.getBody();
			if (mFormatBodyHandle != null)
				body = mFormatBodyHandle.formatted(body);
			
			body = Transforms.formatPost(body);
			body = body.replaceAll("\t", "");
			
			spannable = new SpannableString(body);
			SpannableUtil.highlightFllowUser(spannable, body, SpannableUtil.defaultUserClickLitener);
			SpannableUtil.drawLink(spannable, body, TEXT_LINK, SpannableUtil.defaultBBSClickLitener);
			SpannableUtil.drawImage(spannable, body, DRAWABLE_LOADING, mAsyncImageLoader.getScaleWidth(), mAsyncImageLoader.getScaleHeight(), new SpannableUtil.ImageLoadHandle() {
				
				@Override
				public void load(SpannableString spannable, String url) {
					if (!mConfig.showImage) 
						return;
					
					mAsyncImageLoader.loadImage(url, spannable, new OnImageLoaderListener<SpannableString>() {
						@Override
						public void imageLoaded(Bitmap bmp, SpannableString spannable, String url) {			
							ImageSpan[] image_spans = spannable.getSpans(0, spannable.length(), ImageSpan.class);
							for (ImageSpan span : image_spans) {
								if ( span.getDrawable() == DRAWABLE_LOADING && span.getSource().equals(url) ) {
									int start = spannable.getSpanStart(span);
									int end = spannable.getSpanEnd(span);
									spannable.removeSpan(span);

									BitmapDrawable draw = new BitmapDrawable(bmp);
									draw.setBounds(0, 0, draw.getIntrinsicWidth(), draw.getIntrinsicHeight());
									span = new ImageSpan(draw, url, ImageSpan.ALIGN_BOTTOM);
									spannable.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
									
									refViewHolder.body.setText(spannable);
									break;
								}
							}
						}
					});
				}
			}, new SpannableUtil.OnImageClickListener() {

				@Override
				public void onClick(View v, String url) {					
					ArrayList<String> urls = null;
					
					urls = new ArrayList<String>();
					urls.add(url);
					ImageActivity.startActivity(mContext, urls, 0, post.getSubject());
				}
			});
			EmotionUtil.parserEmotion(mContext, spannable, body);
			
			if (mDisplayFloorHandle != null) 
				mDisplayFloorHandle.setFloor(viewHolder.floor, e, position);
			
			if (mDisplayAuthorHandle != null) 
				mDisplayAuthorHandle.setAuthor(viewHolder.author, e);
			else if (viewHolder.author != null) 
				viewHolder.author.setText(post.getAuthor().getName());
			viewHolder.author.setTag(post.getAuthor());

			if (mDisplayBodyHandle != null) 
				mDisplayBodyHandle.setBody(viewHolder.body, spannable);
			else 
				viewHolder.body.setText(spannable);
			
			// 代替author右边的icon，下一次实现。。。
			//viewHolder.author.setCompoundDrawables(left, top, right, bottom)
			
			if (mPostDateHandle != null) 
				mPostDateHandle.setPostDate(viewHolder.postdate, post.getPostDate());
			else
				viewHolder.postdate.setText(TimeUtil.converTime(post.getPostDate()));
			
			viewHolder.body.setMovementMethod(LinkMovementMethod.getInstance());
			viewHolder.body.setFocusable(false);
			
			String icon_url = "http://tx.tianyaui.com/logo/small/" + post.getAuthor().getUserId();
			viewHolder.usericon.setTag(post.getAuthor());
			if (mConfig.showHead) {
				cachedImage = mAsyncIconLoader.loadImage(icon_url, viewHolder.usericon, new OnImageLoaderListener<ImageView>() {
					@Override
					public void imageLoaded(Bitmap bmp, ImageView parent, String url) {
						String icon_url = "http://tx.tianyaui.com/logo/small/" + ((User)parent.getTag()).getUserId();
	                	
	                	if (url.equals(icon_url)) {
							bmp = ImageUtil.toRoundCorner(bmp, 10);
							parent.setImageBitmap(bmp);
						}
					}
				});
			}
			if (cachedImage != null) 
				viewHolder.usericon.setImageBitmap(cachedImage);				
			
			if (viewHolder.user_approved != null && post.getAuthor().isApproved()) 
				viewHolder.user_approved.setVisibility(View.GONE);// 非vip隐藏vip标志
			
			if (mDisplayOtherHandle != null)
				mDisplayOtherHandle.setOther(viewHolder.other,  e, position);
		}
		
		if (mDisplayViewHandle != null)
			mDisplayViewHandle.setViewHolder(viewHolder, e);

		return convertView;
	}
}
