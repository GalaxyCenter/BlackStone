package apollo.bll;

import java.util.ArrayList;
import java.util.List;

import apollo.cache.AppCache;
import apollo.data.dalfactory.DataAccess;
import apollo.data.idal.ISectionDataProvider;
import apollo.data.model.Section;
import apollo.data.model.SectionGroup;
import apollo.exceptions.ApplicationException;

public class Sections {
	private static ISectionDataProvider remoteProvider;
	private static ISectionDataProvider localProvider;
	public static String KEY_SECTIONS = "sections";
	
	static {
		remoteProvider = DataAccess.createRemoteSectionDataProvider();
		localProvider = DataAccess.createLocalSectionDataProvider();
	}
	
	public static List<Section> getTopSections() {
		return remoteProvider.getTopSections();
	}
	
	public static Section getSection(String id) {
		Section section = null;
		
		try {
			section = localProvider.getSection(id);
		} catch (ApplicationException ex) {
		}
		
		if (section == null) {
			section = remoteProvider.getSection(id);
			add(section);
		}
		return section;
	}
	
	public static void add(Section section) {
		localProvider.add(section);
	}
	
	@SuppressWarnings("unchecked")
	public static List<Section> search(String searchTerms) {
		List<Section> data = null;
		String key = "section_search:" + searchTerms;
		
		data = (List<Section>) AppCache.get(key);
		if (data == null) {
			data =remoteProvider.search(searchTerms);
			AppCache.add(key, data, false);
		}
		return data;
	}
	
	public static List<Section> loadSections() {
		List<Section> sections = null;
		List<SectionGroup> groups = null;
		
		groups = loadSectionGroups();
		sections = new ArrayList<Section>();
		for (SectionGroup group : groups) {
			sections.addAll(group.getSections());
		}
		return sections;
	}
	
	public static List<SectionGroup> loadSectionGroups() {
		return loadSectionGroups(true, false);
	}
	
	@SuppressWarnings("unchecked")
	public static List<SectionGroup> loadSectionGroups(boolean cacheable, boolean flush) {
		List<SectionGroup> groups = null;
		
		if (flush) {
			AppCache.remove(KEY_SECTIONS);
		}
		groups = (List<SectionGroup>)AppCache.get(KEY_SECTIONS);
		if (groups == null) {
			groups = remoteProvider.getSectionGroups();
			if (cacheable) {
				AppCache.add(KEY_SECTIONS, groups);
			}
		}
		return groups;
	}
}
