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
	 * ֻ��TextNod������, Ҳ���ǰѱ��ڵ�������ӵ�Ŀ��ڵ�ǰ
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
