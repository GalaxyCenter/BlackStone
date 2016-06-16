package apollo.data.model;

import java.util.ArrayList;
import java.util.List;

public class SectionGroup {
	private String name;
	private List<Section> sections;
	
	public SectionGroup() {
		sections = new ArrayList<Section>();
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public void add(Section section) {
		this.sections.add(section);
	}
	
	public List<Section> getSections() {
		return this.sections;
	}
	
}
