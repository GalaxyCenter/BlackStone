package apollo.xhtmlparser;

import org.xml.sax.SAXException;

public class IllegalFormatException extends SAXException {

	public IllegalFormatException() {
		super();
	}
	
	public IllegalFormatException(String message) {
		super(message);
	}
}
