package apollo.data.networkdal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import apollo.app.R;
import apollo.data.idal.ISectionDataProvider;
import apollo.data.model.Section;
import apollo.data.model.SectionGroup;
import apollo.exceptions.ApplicationException;
import apollo.exceptions.SystemException;
import apollo.net.RequestMethod;

public class SectionDataProvider extends DataProvider implements
		ISectionDataProvider {

	@Override
	public List<SectionGroup> getSectionGroups() {
		List<SectionGroup> groups = null;
		SectionGroup group = null;
		Section section = null;
		Pattern pattern = null;
		Matcher matcher = null;
		Matcher ul_matcher = null;
		Matcher item_matcher = null;
		String content = null;
		String nav_child_box = null;
		String nav_child = null;
		String url = "http://bbs.tianya.cn/";

		content = super.getContent(url);
		groups = new ArrayList<SectionGroup>();
		
		pattern = Pattern.compile("(?s)<div class=\"nav_child_box\"[^>]*>(.*?)</div>");
		matcher = pattern.matcher(content);
		while (matcher.find()) {
			nav_child_box = matcher.group(1);
			group = new SectionGroup();
			groups.add(group);
			
			pattern = Pattern.compile("(?s)<ul class=\"nav_child\">(.*?)</ul>");
			ul_matcher = pattern.matcher(nav_child_box);
			while (ul_matcher.find()) {
				nav_child = ul_matcher.group(1);
								
				pattern = Pattern.compile("<a\\s.*?itemid=\"([^\"]+)\"[^>]*>(.*?)</a>");
				item_matcher = pattern.matcher(nav_child);
				while (item_matcher.find()) {
					section = new Section();
					section.setSectionId(item_matcher.group(1));
					section.setName(item_matcher.group(2));
					group.add(section);
				}
			}
		}
		
		int idx = 0;
		pattern = Pattern.compile("<a hidefocus class=\"folder.*\" href=\"#\">(.*?)</a>");
		matcher = pattern.matcher(content);
		while (matcher.find()) {
			group = groups.get(idx++);
			group.setName(matcher.group(1));
		}
		group = groups.get(9);
		group.setName("社区服务");
		return groups;
	}

	@Override
	public Section getSection(String id) {
		Section section = null;
		Pattern pattern = null;
		Matcher matcher = null;
		String url = null;
		String content = null;
		
		url = "http://bbs.tianya.cn/list-" + id + "-1.shtml";
		content = super.getContent(url);
		pattern = Pattern.compile("<title>(.*?)_");
		matcher = pattern.matcher(content);
		section = new Section();
		section.setSectionId(id);
		if (matcher.find()) {
			section.setName(matcher.group(1));
		}
		return section;
	}

	@Override
	public void add(Section section) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Section> getTopSections() {
		String url = "http://bbs.tianya.cn/";
		String body = null;
		Pattern pattern = null;
		Matcher matcher = null;
		List<Section> sections = null;
		Section section = null;
		
		body = super.getContent(url);
		pattern = Pattern.compile("<span class=\"ranking-num.*?\">&nbsp;</span><a\\s.*?href=\"/list-([^\"]+)-1.shtml\"[^>]*>(.*?)</a>");
		matcher = pattern.matcher(body);
		sections = new ArrayList<Section>();
		while (matcher.find()) {
			section = new Section();
			
			section.setSectionId(matcher.group(1));
			section.setName(matcher.group(2));
			
			sections.add(section);
		}
		return sections;
	}

	@Override
	public List<Section> search(String searchTerms) {
		//http://bbs.tianya.cn/api?method=bbs.ice.itemSearch&var=forumArr&params.key=yuleba&params.all=false&params.mode=0&_=1364354103971
		HashMap<String, String> params = null;
		String body = null;
		String url = null;

		url = "http://bbs.tianya.cn/api";
		params = new HashMap<String, String>();

		params.put("method", "bbs.ice.itemSearch");	
		params.put("params.key", searchTerms);
		params.put("params.all", "false");
		params.put("params.mode", "0");
		
		body = super.getContent(url, RequestMethod.GET, params);
		// parse json
		JSONObject json = null;
		JSONArray jarr = null;
		boolean isSuccess = false;
		List<Section> sections = null;
		Section s = null;
		String[] temp = null;
		try {
			json = new JSONObject(body);
			isSuccess = "1".equals(json.getString("success"));
			if (isSuccess == false) {
				String message = json.getString("message");
				throw new ApplicationException(R.string.error_section_not_found, message);
			}
			
			sections = new ArrayList<Section>();
			json = json.getJSONObject("data");
			jarr = json.getJSONArray("rows");
			for(int idx=0; idx<jarr.length(); idx++) {
				temp = jarr.getString(idx).split("/");
				s = new Section();
				s.setSectionId(temp[0]);
				s.setName(temp[1]);
				sections.add(s);
			}
		} catch (JSONException ex) {
			throw new SystemException(ex.getMessage());
		}
		return sections;
	}

}
