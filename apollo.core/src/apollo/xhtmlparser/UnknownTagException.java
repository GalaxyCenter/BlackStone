package apollo.xhtmlparser;

import org.xml.sax.SAXException;

public class UnknownTagException extends SAXException {

	public UnknownTagException() {
		super();
	}
	public UnknownTagException(String message) {
		super(message);
	}
}
