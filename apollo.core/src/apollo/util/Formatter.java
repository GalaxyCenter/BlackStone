package apollo.util;

public class Formatter {
	public static String checkStringLength(String stringToCheck, int maxLength) {
		String checkedString = null;

		if (stringToCheck.length() <= maxLength)
			return stringToCheck;

		if ((stringToCheck.length() > maxLength) && (stringToCheck.indexOf(" ") == -1)) {
			checkedString = stringToCheck.substring(0, maxLength);// + "...";
		} else if (stringToCheck.length() > 0) {
			checkedString = stringToCheck.substring(0, maxLength);// + "...";
		} else {
			checkedString = stringToCheck;
		}

		return checkedString;
	}

}
