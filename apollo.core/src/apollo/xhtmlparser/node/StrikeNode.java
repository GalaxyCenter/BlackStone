package apollo.xhtmlparser.node;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.text.Spannable;
import android.text.style.StrikethroughSpan;
import apollo.xhtmlparser.IllegalFormatException;

public class StrikeNode extends ElementNode {
	public StrikeNode() {
		super("strike", null);
	}

	/**
	 * 仅对TextNod作处理
	 */
	@Override
	public Node execute(Node nod) throws SAXException {
		if(nod instanceof TextNode){
			TextNode txNod = (TextNode) nod;
			txNod.getValue().setSpan(new StrikethroughSpan(), 0, txNod.getValue().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			return txNod;
		}
		
		throw new IllegalFormatException();
	}

}
