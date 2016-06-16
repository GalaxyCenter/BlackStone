package apollo.xhtmlparser.node;

import org.xml.sax.SAXException;

import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import apollo.xhtmlparser.IllegalFormatException;

public class TextNode extends Node {
	private SpannableString mValue;
	
	public TextNode(CharSequence value) {
		super("text");
		this.mValue = new SpannableString(value);
	}
	
	/**
	 * 只对TextNod作处理, 也就是把本节点内容添加到目标节点前
	 */
	@Override
	public Node execute(Node nod) throws SAXException {
		if(nod instanceof TextNode){
			SpannableStringBuilder builder = new SpannableStringBuilder();
			builder.append(mValue).append(((TextNode)nod).getValue());
			return new TextNode(builder); 
		}
		
		throw new IllegalFormatException();
	}
	
	public SpannableString getValue(){
		return mValue;
	}
}
