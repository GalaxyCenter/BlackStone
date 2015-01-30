package apollo.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import apollo.data.model.Section;
import apollo.data.model.Thread;

public class TianyaUrlHelp {
	
	public static Section parseSectionUrl(String url) {
		Section section = null;
		Pattern pattern = null;
		Matcher matcher = null;
		
		pattern = Pattern.compile("list-(.*?)-1");
		matcher = pattern.matcher(url);
		if (matcher.find()) {
			section = new Section();
			
			section.setSectionId(matcher.group(1));
		} 
		return section;
	}

	public static Thread parseThreadUrl(String url) {
		Thread thread = null;
		Pattern pattern = null;
		Matcher matcher = null;
		
		pattern = Pattern.compile("post-(.*?)-(.*?)-");
		matcher = pattern.matcher(url);
		if (matcher.find()) {
			thread = new Thread();
			
			thread.setSection(new Section());
			thread.getSection().setSectionId(matcher.group(1));
			thread.setThreadId(Integer.parseInt(matcher.group(2)));
		} 
		return thread;
	}
}
