package apollo.enums;

public class SectionType {

	public static SectionType PUBLIC = new SectionType(1);
	public static SectionType TECH = new SectionType(2);
	public static SectionType CITY = new SectionType(3);
	
	private int mValue;
	public SectionType(int value) {
		mValue = value;
	}
	public String toString() {
		return Integer.toString(mValue);
	}
	
	@Override
	public boolean equals(Object obj) {
		return equals((SectionType)obj);
	}
	
	public boolean equals(SectionType obj) {
		return this.mValue == obj.mValue;
	}
	
	public static SectionType get(int value) {
		 return new SectionType(value);
	}
}
