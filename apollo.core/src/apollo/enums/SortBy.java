package apollo.enums;

public enum SortBy {

	LAST_POST(0), LAST_REPLY(2), VALUE(1), VIEWS(3);

	private int value;

	SortBy(int value) {
		this.value = value;
	}

	public boolean equals(SortBy sortBy) {
		return sortBy.value == value;
	}

	public int getValue() {
		return value;
	}
}
