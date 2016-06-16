package apollo.data.idal;

import java.util.List;

import apollo.data.model.Section;
import apollo.data.model.SectionGroup;

public interface ISectionDataProvider {
	List<SectionGroup> getSectionGroups();
	List<Section> getTopSections();
	List<Section> search(String searchTerms);
	
	Section getSection(String id);
	
	void add(Section section);
}
