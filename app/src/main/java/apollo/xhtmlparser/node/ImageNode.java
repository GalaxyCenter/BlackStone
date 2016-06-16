package apollo.xhtmlparser.node;

import org.xml.sax.Attributes;

import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.Html.ImageGetter;
import android.text.style.ImageSpan;
import apollo.xhtmlparser.IllegalFormatException;


public class ImageNode extends ElementNode {
	ImageGetter mImageGetter;
	
	public ImageNode(String name, Attributes attributes, ImageGetter imageGetter) {
		super(name, attributes);
		this.mImageGetter = imageGetter;
	}

	/**
	 * 仅对TextNod作处理, 将TextNod值取出, 根据该值使用ImageGetter获取Drawable, 再替换为Drawable
	 */
	@Override
	public Node execute(Node nod) throws IllegalFormatException {
		if(nod instanceof TextNode){
			TextNode txNod = (TextNode)nod;
			Drawable d = mImageGetter.getDrawable(txNod.getValue().toString());
			
			txNod.getValue().setSpan(new ImageSpan(d), 0, txNod.getValue().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			
			return txNod;
		}
		
		throw new IllegalFormatException();
	}

}