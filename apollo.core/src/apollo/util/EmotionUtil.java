package apollo.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import apollo.core.R;

public class EmotionUtil {

	private static final HashMap<String, Integer> emos;
	private static final HashMap<String, String> file2str;
	private static final HashMap<String, Integer> write_emos;
	private static final ArrayList<Integer> write_emos_list;

	static {
		emos = new HashMap<String, Integer>();
		emos.put("image_emoticon", R.drawable.image_emoticon);
		emos.put("image_emoticon2", R.drawable.image_emoticon2);
		emos.put("image_emoticon3", R.drawable.image_emoticon3);
		emos.put("image_emoticon4", R.drawable.image_emoticon4);
		emos.put("image_emoticon5", R.drawable.image_emoticon5);
		emos.put("image_emoticon6", R.drawable.image_emoticon6);
		emos.put("image_emoticon7", R.drawable.image_emoticon7);
		emos.put("image_emoticon8", R.drawable.image_emoticon8);
		emos.put("image_emoticon9", R.drawable.image_emoticon9);
		emos.put("image_emoticon10", R.drawable.image_emoticon10);
		emos.put("image_emoticon11", R.drawable.image_emoticon11);
		emos.put("image_emoticon12", R.drawable.image_emoticon12);
		emos.put("image_emoticon13", R.drawable.image_emoticon13);
		emos.put("image_emoticon14", R.drawable.image_emoticon14);
		emos.put("image_emoticon15", R.drawable.image_emoticon15);
		emos.put("image_emoticon16", R.drawable.image_emoticon16);
		emos.put("image_emoticon17", R.drawable.image_emoticon17);
		emos.put("image_emoticon18", R.drawable.image_emoticon18);
		emos.put("image_emoticon19", R.drawable.image_emoticon19);
		emos.put("image_emoticon20", R.drawable.image_emoticon20);
		emos.put("image_emoticon21", R.drawable.image_emoticon21);
		emos.put("image_emoticon22", R.drawable.image_emoticon22);
		emos.put("image_emoticon23", R.drawable.image_emoticon23);
		emos.put("image_emoticon24", R.drawable.image_emoticon24);
		emos.put("image_emoticon25", R.drawable.image_emoticon25);
		emos.put("image_emoticon26", R.drawable.image_emoticon26);
		emos.put("image_emoticon27", R.drawable.image_emoticon27);
		emos.put("image_emoticon28", R.drawable.image_emoticon28);
		emos.put("image_emoticon29", R.drawable.image_emoticon29);
		emos.put("image_emoticon30", R.drawable.image_emoticon30);
		emos.put("image_emoticon31", R.drawable.image_emoticon31);
		emos.put("image_emoticon32", R.drawable.image_emoticon32);
		emos.put("image_emoticon33", R.drawable.image_emoticon33);
		emos.put("image_emoticon34", R.drawable.image_emoticon34);
		emos.put("image_emoticon35", R.drawable.image_emoticon35);
		emos.put("image_emoticon36", R.drawable.image_emoticon36);
		emos.put("image_emoticon37", R.drawable.image_emoticon37);
		emos.put("image_emoticon38", R.drawable.image_emoticon38);
		emos.put("image_emoticon39", R.drawable.image_emoticon39);
		emos.put("image_emoticon40", R.drawable.image_emoticon40);
		emos.put("image_emoticon41", R.drawable.image_emoticon41);
		emos.put("image_emoticon42", R.drawable.image_emoticon42);
		emos.put("image_emoticon43", R.drawable.image_emoticon43);
		emos.put("image_emoticon44", R.drawable.image_emoticon44);
		emos.put("image_emoticon45", R.drawable.image_emoticon45);
		emos.put("image_emoticon46", R.drawable.image_emoticon46);
		emos.put("image_emoticon47", R.drawable.image_emoticon47);
		emos.put("image_emoticon48", R.drawable.image_emoticon48);
		emos.put("image_emoticon49", R.drawable.image_emoticon49);
		emos.put("image_emoticon50", R.drawable.image_emoticon50);

		write_emos_list = new ArrayList<Integer>();
		write_emos_list.add(R.drawable.image_emoticon);
		write_emos_list.add(R.drawable.image_emoticon2);
		write_emos_list.add(R.drawable.image_emoticon3);
		write_emos_list.add(R.drawable.image_emoticon4);
		write_emos_list.add(R.drawable.image_emoticon5);
		write_emos_list.add(R.drawable.image_emoticon6);
		write_emos_list.add(R.drawable.image_emoticon7);
		write_emos_list.add(R.drawable.image_emoticon8);
		write_emos_list.add(R.drawable.image_emoticon9);
		write_emos_list.add(R.drawable.image_emoticon10);
		write_emos_list.add(R.drawable.image_emoticon11);
		write_emos_list.add(R.drawable.image_emoticon12);
		write_emos_list.add(R.drawable.image_emoticon13);
		write_emos_list.add(R.drawable.image_emoticon14);
		write_emos_list.add(R.drawable.image_emoticon15);
		write_emos_list.add(R.drawable.image_emoticon16);
		write_emos_list.add(R.drawable.image_emoticon17);
		write_emos_list.add(R.drawable.image_emoticon18);
		write_emos_list.add(R.drawable.image_emoticon19);
		write_emos_list.add(R.drawable.image_emoticon20);
		write_emos_list.add(R.drawable.image_emoticon21);
		write_emos_list.add(R.drawable.image_emoticon22);
		write_emos_list.add(R.drawable.image_emoticon23);
		write_emos_list.add(R.drawable.image_emoticon24);
		write_emos_list.add(R.drawable.image_emoticon25);
		write_emos_list.add(R.drawable.image_emoticon26);
		write_emos_list.add(R.drawable.image_emoticon27);
		write_emos_list.add(R.drawable.image_emoticon28);
		write_emos_list.add(R.drawable.image_emoticon29);
		write_emos_list.add(R.drawable.image_emoticon30);
		write_emos_list.add(R.drawable.image_emoticon31);
		write_emos_list.add(R.drawable.image_emoticon32);
		write_emos_list.add(R.drawable.image_emoticon33);
		write_emos_list.add(R.drawable.image_emoticon34);
		write_emos_list.add(R.drawable.image_emoticon35);
		write_emos_list.add(R.drawable.image_emoticon36);
		write_emos_list.add(R.drawable.image_emoticon37);
		write_emos_list.add(R.drawable.image_emoticon38);
		write_emos_list.add(R.drawable.image_emoticon39);
		write_emos_list.add(R.drawable.image_emoticon40);
		write_emos_list.add(R.drawable.image_emoticon41);
		write_emos_list.add(R.drawable.image_emoticon42);
		write_emos_list.add(R.drawable.image_emoticon43);
		write_emos_list.add(R.drawable.image_emoticon44);
		write_emos_list.add(R.drawable.image_emoticon45);
		write_emos_list.add(R.drawable.image_emoticon46);
		write_emos_list.add(R.drawable.image_emoticon47);
		write_emos_list.add(R.drawable.image_emoticon48);
		write_emos_list.add(R.drawable.image_emoticon49);
		write_emos_list.add(R.drawable.image_emoticon50);
	
		write_emos = new HashMap<String, Integer>();
		write_emos.put("[ºÇºÇ]", write_emos_list.get(0));
		write_emos.put("[¹þ¹þ]", write_emos_list.get(1));
		write_emos.put("[ÍÂÉà]", write_emos_list.get(2));
		write_emos.put("[°¡]", write_emos_list.get(3));
		write_emos.put("[¿á]", write_emos_list.get(4));
		write_emos.put("[Å­]", write_emos_list.get(5));
		write_emos.put("[¿ªÐÄ]", write_emos_list.get(6));
		write_emos.put("[º¹]", write_emos_list.get(7));
		write_emos.put("[Àá]", write_emos_list.get(8));
		write_emos.put("[ºÚÏß]", write_emos_list.get(9));
		write_emos.put("[±ÉÊÓ]", write_emos_list.get(10));
		write_emos.put("[²»¸ßÐË]", write_emos_list.get(11));
		write_emos.put("[Õæ°ô]", write_emos_list.get(12));
		write_emos.put("[Ç®]", write_emos_list.get(13));
		write_emos.put("[ÒÉÎÊ]", write_emos_list.get(14));
		write_emos.put("[ÒõÏÕ]", write_emos_list.get(15));
		write_emos.put("[ÍÂ]", write_emos_list.get(16));
		write_emos.put("[ß×]", write_emos_list.get(17));
		write_emos.put("[Î¯Çü]", write_emos_list.get(18));
		write_emos.put("[»¨ÐÄ]", write_emos_list.get(19));
		write_emos.put("[ºô~]", write_emos_list.get(20));
		write_emos.put("[Ð¦ÑÛ]", write_emos_list.get(21));
		write_emos.put("[Àä]", write_emos_list.get(22));
		write_emos.put("[Ì«¿ªÐÄ]", write_emos_list.get(23));
		write_emos.put("[»¬»ü]", write_emos_list.get(24));
		write_emos.put("[ÃãÇ¿]", write_emos_list.get(25));
		write_emos.put("[¿ñº¹]", write_emos_list.get(26));
		write_emos.put("[¹Ô]", write_emos_list.get(27));
		write_emos.put("[Ë¯¾õ]", write_emos_list.get(28));
		write_emos.put("[¾ª¿Þ]", write_emos_list.get(29));
		write_emos.put("[ÉýÆð]", write_emos_list.get(30));
		write_emos.put("[¾ªÑÈ]", write_emos_list.get(31));
		write_emos.put("[Åç]", write_emos_list.get(32));
		write_emos.put("[°®ÐÄ]", write_emos_list.get(33));
		write_emos.put("[ÐÄËé]", write_emos_list.get(34));
		write_emos.put("[Ãµ¹å]", write_emos_list.get(35));
		write_emos.put("[ÀñÎï]", write_emos_list.get(36));
		write_emos.put("[²Êºç]", write_emos_list.get(37));
		write_emos.put("[ÐÇÐÇÔÂÁÁ]", write_emos_list.get(38));
		write_emos.put("[Ì«Ñô]", write_emos_list.get(39));
		write_emos.put("[Ç®±Ò]", write_emos_list.get(40));
		write_emos.put("[µÆÅÝ]", write_emos_list.get(41));
		write_emos.put("[²è±­]", write_emos_list.get(42));
		write_emos.put("[µ°¸â]", write_emos_list.get(43));
		write_emos.put("[ÒôÀÖ]", write_emos_list.get(44));
		write_emos.put("[haha]", write_emos_list.get(45));
		write_emos.put("[Ê¤Àû]", write_emos_list.get(46));
		write_emos.put("[´óÄ´Ö¸]", write_emos_list.get(47));
		write_emos.put("[Èõ]", write_emos_list.get(48));
		write_emos.put("[OK]", write_emos_list.get(49));
		

		file2str = new HashMap<String, String>();
		file2str.put("image_emoticon", "ºÇºÇ");
		file2str.put("image_emoticon2", "¹þ¹þ");
		file2str.put("image_emoticon3", "ÍÂÉà");
		file2str.put("image_emoticon4", "°¡?");
		file2str.put("image_emoticon5", "¿á");
		file2str.put("image_emoticon6", "Å­");
		file2str.put("image_emoticon7", "¿ªÐÄ");
		file2str.put("image_emoticon8", "º¹");
		file2str.put("image_emoticon9", "Àá");
		file2str.put("image_emoticon10", "ºÚÏß");
		file2str.put("image_emoticon11", "±ÉÊÓ");
		file2str.put("image_emoticon12", "²»¸ßÐË");
		file2str.put("image_emoticon13", "Õæ°ô");
		file2str.put("image_emoticon14", "Ç®");
		file2str.put("image_emoticon15", "ÒÉÎÊ");
		file2str.put("image_emoticon16", "ÒõÏÕ");
		file2str.put("image_emoticon17", "ÍÂ");
		file2str.put("image_emoticon18", "ß×?");
		file2str.put("image_emoticon19", "Î¯Çü");
		file2str.put("image_emoticon20", "»¨ÐÄ");
		file2str.put("image_emoticon21", "ºô~");
		file2str.put("image_emoticon22", "Ð¦ÑÛ");
		file2str.put("image_emoticon23", "Àä");
		file2str.put("image_emoticon24", "Ì«¿ªÐÄ");
		file2str.put("image_emoticon25", "»¬»ü");
		file2str.put("image_emoticon26", "ÃãÇ¿");
		file2str.put("image_emoticon27", "¿ñº¹");
		file2str.put("image_emoticon28", "¹Ô");
		file2str.put("image_emoticon29", "Ë¯¾õ");
		file2str.put("image_emoticon30", "¾ª¿Þ");
		file2str.put("image_emoticon31", "ÉýÆð");
		file2str.put("image_emoticon32", "¾ªÑÈ");
		file2str.put("image_emoticon33", "Åç");
		file2str.put("image_emoticon34", "°®ÐÄ");
		file2str.put("image_emoticon35", "ÐÄËé");
		file2str.put("image_emoticon36", "Ãµ¹å");
		file2str.put("image_emoticon37", "ÀñÎï");
		file2str.put("image_emoticon38", "²Êºç");
		file2str.put("image_emoticon39", "ÐÇÐÇÔÂÁÁ");
		file2str.put("image_emoticon40", "Ì«Ñô");
		file2str.put("image_emoticon41", "Ç®±Ò");
		file2str.put("image_emoticon42", "µÆÅÝ");
		file2str.put("image_emoticon43", "²è±­");
		file2str.put("image_emoticon44", "µ°¸â");
		file2str.put("image_emoticon45", "ÒôÀÖ");
		file2str.put("image_emoticon46", "haha");
		file2str.put("image_emoticon47", "Ê¤Àû");
		file2str.put("image_emoticon48", "´óÄ´Ö¸");
		file2str.put("image_emoticon49", "Èõ");
		file2str.put("image_emoticon50", "OK");
	}

	public static Bitmap getBitmap(Context context, String s) {
		Integer integer = (Integer) emos.get(s);
		Bitmap bitmap;
		if (integer != null)
			bitmap = ImageUtil.getResBitmap(context, integer.intValue());
		else
			bitmap = null;
		return bitmap;
	}

	public static String getFaceStrByFile(String s) {
		return (String) file2str.get(s);
	}

	public static HashMap<String, Integer> getWriteEmotion() {
		return write_emos;
	}

	public static ArrayList<Integer> getWriteEmotionList() {
		return write_emos_list;
	}

	public static void parserEmotion(Context context, SpannableString  spannable, String source) {
		Matcher matcher = Pattern.compile("\\[(\\w?)+\\]").matcher(source);
		while (matcher.find()) {
			String s1 = matcher.group();
			Integer integer = (Integer) write_emos.get(s1);
			if (integer != null) {
				Bitmap bitmap = ImageUtil.getResBitmap(context, integer.intValue());
				if (bitmap != null) {
					int i = s1.length();
					int j = matcher.start();
					BitmapDrawable bitmapdrawable = new BitmapDrawable(
							bitmap);
					bitmapdrawable.setBounds(0, 0, bitmap.getWidth(),
							bitmap.getHeight());
					spannable.setSpan(
							new ImageSpan(bitmapdrawable, 0), j, j + i, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			}
		}
	}
}
