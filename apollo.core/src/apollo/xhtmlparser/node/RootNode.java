package apollo.xhtmlparser.node;

import org.xml.sax.SAXException;

public class RootNode extends ElementNode {

	public RootNode() {
		super("xhtml", null);
	}

	/**
	 * �����κδ���
	 */
	@Override
	public Node execute(Node nod) throws SAXException {
		return nod;
	}

}