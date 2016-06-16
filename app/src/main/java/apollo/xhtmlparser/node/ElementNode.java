package apollo.xhtmlparser.node;

import org.xml.sax.Attributes;

abstract public class ElementNode extends Node{
	protected Attributes mAttributes;

	public ElementNode(String name, Attributes attributes) {
		super(name);
		
		this.mAttributes = attributes;
	}
}