package apollo.util;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.View;
import apollo.data.model.Thread;
import apollo.data.model.User;
import apollo.exceptions.ApplicationException;
import apollo.app.PostActivity;
import apollo.app.home.PersonInfoActivity;
import apollo.widget.LinkSpan;

public class SpannableUtil {
	
	public static OnTextClickListener defaultUserClickLitener = new OnTextClickListener() {
		private Activity mActivity = null;
		@Override
		public void setParent(Activity activiy) {
			mActivity = activiy;
		}

		@Override
		public void onClick(View v, String text) {
			User user = null;
			
			user = new User();
			user.setName(text);
			PersonInfoActivity.startActivity(mActivity, user);
		}
		
	};
	
	public static OnTextClickListener defaultBBSClickLitener = new OnTextClickListener() {
		private Activity mActivity = null;
		@Override
		public void onClick(View v, String text) {
			Thread thread = null;
			
			thread = TianyaUrlHelp.parseThreadUrl(text);
			if (thread != null) 
				PostActivity.startActivity(mActivity, thread);
		}

		@Override
		public void setParent(Activity activiy) {
			mActivity = activiy;
		}
			
	}; 
	
	public interface ImageLoadHandle {
		public void load(SpannableString spannable, String url);
	}
	
	public interface OnImageClickListener {
		public void onClick(View v, String url);
	}
	
	public interface OnTextClickListener {
		public void setParent(Activity activiy);
		public void onClick(View v, String text);
	}
	
	//@(\w+?)(?=\W|$)(.)
	//话题 Pattern.compile("#(\\w+?)#")
	public static void highlightFllowUser(SpannableString spannable, String source, final OnTextClickListener listener) {
		decorateRefersInStr(spannable, Regex.getStartAndEndIndex(source, Pattern.compile("@(\\w+?)(?=\\W|$)(.)")), listener);
	}
	
	public static void highlightUrl(SpannableString spannable, String source, final OnTextClickListener listener) {
		//((http://|https://){1}[\\w\\.\\-/:]+)|(#(.+?)#)|(@[\\u4e00-\\u9fa5\\w\\-]+)
		decorateRefersInStr(spannable, Regex.getStartAndEndIndex(source, Pattern.compile("http://\\w+(\\.\\w+|)+(/\\w+)*(/\\w+\\.(\\w+|))?")), listener);
	}
	
	public static void drawLink(SpannableString spannable, String source, String showText, final OnTextClickListener listener) {
		List<Map<String, Object>> list = null;
		Map<String, Object> map = null;
		ClickableSpan click_span = null;
		LinkSpan link_span = null;
		int start = 0;
		int end = 0;		
		

		//list = Regex.getStartAndEndIndex(str, Pattern.compile("(?s)<[a|A].*?href.*?=.*?\"([^\"]+)\"[^>]*>(.*?)</[a|A]>"));
		// (http|ftp|https):\/\/[\w]+(.[\w]+)([\w\-\.,@?^=%&amp;:/~\+#]*[\w\-\@?^=%&amp;/~\+#])
		//list = Regex.getStartAndEndIndex(str, Patterns.WEB_URL);
		
		list = Regex.getStartAndEndIndex(source, Pattern.compile("(?s)\\[link\\](.*?)\\[/link\\]"));
		for (int i = 0; i < list.size(); i++) {	
			map = list.get(i);
			start = (Integer) map.get("startIndex");
			end = (Integer) map.get("endIndex") + 1;
			final String url = (String) map.get("str1");

			if (TextUtils.isEmpty(showText)) 
				showText = url;
			
			link_span = new LinkSpan(showText, Color.argb(255, 33, 92, 110));
			click_span = new ClickableSpan() {
				@Override
				public void onClick(View widget) {
					listener.onClick(widget, url);
				}
			};
			
			spannable.setSpan(link_span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			spannable.setSpan(click_span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}
		
	public static void drawImage(SpannableString  spannable, String source, BitmapDrawable drawable, float scale_width, float scale_height, ImageLoadHandle loadCallback, final OnImageClickListener listener) {
		BitmapDrawable container = null;
		Bitmap bmp = null;
		ImageSpan img_span = null;
		List<Map<String, Object>> list = null;
		Map<String,Object> map = null;
		String img_src = null;
		String img_regex = null;

		int start = 0;
		int end = 0;
		
		//img_regex = "(?s)<[img|IMG].*?>";
		//img_src_regex = "(src|original)=[\'|\"](.*?(?:[.(jpg|bmp|jpeg|gif|png)]))[\'|\"].*?[/]?";
		//img_src_regex = "(?s)(src|original)=[\'|\"](.*?)[\'|\"]"; 
		
		img_regex = "\\[img\\](.*?)\\[/img\\]";
		list = Regex.getStartAndEndIndex(source, Pattern.compile(img_regex));
		for (int idx=0; idx<list.size(); idx++) {
			map = list.get(idx);
			img_src = (String)map.get("str1");
			
			if (ImageUtil.isCached(img_src)) {
				try {					
					bmp = ImageUtil.getBitmapFromUrl(img_src);
					bmp = ImageUtil.getScaleBitmap(bmp, scale_width, scale_height);
					container = new BitmapDrawable(bmp);
				} catch (ApplicationException ex) {
					container = drawable;
				}				
			} else {
				container = drawable;
				loadCallback.load(spannable, img_src);
			}
			container.setBounds(0, 0, container.getIntrinsicWidth(), container.getIntrinsicHeight());
			img_span = new ImageSpan(container, img_src, ImageSpan.ALIGN_BOTTOM);
			start = (Integer) map.get("startIndex");
			end = (Integer) map.get("endIndex") + 1;
			spannable.setSpan(img_span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		
		
		ImageSpan[] image_spans = spannable.getSpans(0, spannable.length(), ImageSpan.class);
		for (ImageSpan span : image_spans) {
			final String image_src = span.getSource();
			ClickableSpan click_span = null;
			ClickableSpan[] click_spans = null;

			// clear clickspan
			start = spannable.getSpanStart(span);
			end = spannable.getSpanEnd(span);
			click_spans = spannable.getSpans(start, end, ClickableSpan.class);
			for (ClickableSpan c_span : click_spans) {
				spannable.removeSpan(c_span);
			}
			
			click_span = new ClickableSpan() {
				@Override
				public void onClick(View widget) {
					listener.onClick(widget, image_src);
				}
			};
			spannable.setSpan(click_span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}
	
//	public static void drawEmoticon(SpannableString  spannable, String source) {
//		String emo_regex = null;
//		ImageSpan img_span = null;
//		List<Map<String, Object>> list = null;
//		Map<String,Object> map = null;
//		String emo_src = null;
//
//		int start = 0;
//		int end = 0;
//		
//		
//		emo_regex = "\\[([^\\x00-\\xff]+)\\]";
//		list = Regex.getStartAndEndIndex(source, Pattern.compile(emo_regex));
//		for (int idx=0; idx<list.size(); idx++) {
//			map = list.get(idx);
//			emo_src = (String)map.get("str1");
//			
//
//		}
//	}
	
//	public static SpannableString decorateTopicInStr(SpannableString spannable,List<Map<String,Object>> list){
//		int size = list.size();
//		Map<String, Object> map = null;
//		CharacterStyle foregroundColorSpan = null;
//		
//		if (list != null && list.size() > 0) {
//			for (int i = 0; i < size; i++) {
//				map = list.get(i);
//				foregroundColorSpan = new ForegroundColorSpan(Color.argb(255, 33, 92, 110));
//				spannable.setSpan(foregroundColorSpan,
//						(Integer) map.get("startIndex"),
//						(Integer) map.get("endIndex"),
//						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//			}
//		}
//		return spannable;
//	}
	
	public static void decorateRefersInStr(SpannableString spannable, List<Map<String, Object>> list, final OnTextClickListener listener) {
		Map<String, Object> map = null;
		CharacterStyle text_span = null;
		ClickableSpan click_span = null;
		int start = 0;
		int end = 0;

		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {			
				map = list.get(i);
				start = (Integer) map.get("startIndex");
				end = (Integer) map.get("endIndex") + 1;
				final String text = (String) map.get("match");
				
				text_span = new ForegroundColorSpan(Color.argb(255, 33, 92, 110));
				click_span = new ClickableSpan() {
					@Override
					public void onClick(View widget) {
						listener.onClick(widget, text.replace("@", ""));
					}
				};
				
				spannable.setSpan(text_span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				spannable.setSpan(click_span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
	}
}
