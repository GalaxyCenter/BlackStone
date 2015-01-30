package apollo.enums;

public enum SortOrder {
	//
	ASCENDING("asc"),
	
	//
	DESCENDING("desc");
	
	private String value;
	SortOrder(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return value;
	}
}
