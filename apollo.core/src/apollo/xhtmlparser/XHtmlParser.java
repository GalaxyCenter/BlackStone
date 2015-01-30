package apollo.xhtmlparser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import apollo.xhtmlparser.node.BrNode;
import apollo.xhtmlparser.node.ElementNode;
import apollo.xhtmlparser.node.FontNode;
import apollo.xhtmlparser.node.ImageNode;
import apollo.xhtmlparser.node.Node;
import apollo.xhtmlparser.node.RootNode;
import apollo.xhtmlparser.node.StrikeNode;
import apollo.xhtmlparser.node.TextNode;
 

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.text.Spannable;
import android.text.Html.ImageGetter;
import android.text.SpannableString;

/**
 * font (size, color)
 * strike
 * br
 * img
 * @author John
 *
 */
public class XHtmlParser {
	private ImageGetter mImageGetter;
	private SpannableString mResult;
	
	public class XHtmlHandler extends DefaultHandler{
		private Stack<Node> mElementStack;
		
		@Override
		public void startDocument() throws SAXException {
			mElementStack = new Stack<Node>();
			super.startDocument();
		}
		
		@Override
		public void endDocument() throws SAXException {
			Node thisNod;
			Node childNod = null;
			//��ջ��ʣ�µ�Ԫ�ض�������
			while(!mElementStack.isEmpty()){
				thisNod = mElementStack.pop();
				if(childNod == null){
					childNod = thisNod;
				}
				else{
					childNod = thisNod.execute(childNod);
				}
			}
			
			mResult = ((TextNode)childNod).getValue();
			
			super.endDocument();
		}
		
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			//��elementת��Ϊ����, ��ջ
			ElementNode nod = createNod(localName, attributes);
			mElementStack.push(nod);
			
			super.startElement(uri, localName, qName, attributes);
		}
		
		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			Node thisNod;
			Node childNod = null;
			//��element����ʱ, ʹ�ø�element���������а�����Ԫ��
			while(!(thisNod = mElementStack.pop()).getName().equals(localName)){
				if(childNod == null){
					childNod = thisNod;
				}
				else{
					childNod = thisNod.execute(childNod);
				}
			}
			//��������ѹջ
			mElementStack.push(thisNod.execute(childNod));
			
			super.endElement(uri, localName, qName);
		}
		
		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			//������textʱ, ����TextNod, ��ѹջ
			TextNode text = new TextNode(new String(ch, start, length));
			mElementStack.push(text);
			
			super.characters(ch, start, length);
		}
	}
	
	
	public XHtmlParser(ImageGetter getter) {
		this.mImageGetter = getter;
	}
	
	/**
	 * ����text(������δʹ��xhtml��ǩ��Χ)
	 * @param text
	 * @return Spannable
	 */
	public Spannable parseText(String text) {
		//�����text(������δʹ��xhtml��ǩ��Χ), ����xhtml��ǩ
		if(text.indexOf("<xhtml>") == -1){
			text = new StringBuilder().append("<xhtml>").append(text).append("<xhtml>").toString();
		}
		
		return parseXHtml(text);
	}
	
	/**
	 * ����XHtml
	 * @param xhtml xhtml
	 * @return Spannable
	 */
	public Spannable parseXHtml(String xhtml){
		try{
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			ByteArrayInputStream bais = new ByteArrayInputStream(xhtml.getBytes());
			XHtmlHandler handler = new XHtmlHandler();
			parser.parse(bais, handler);
			return mResult;
		}
		catch(ParserConfigurationException e){
			e.printStackTrace();
			//do nothing
		}
		catch(IOException e){
			e.printStackTrace();
			//do nothong
		}
		catch(SAXException e){
			e.printStackTrace();
			//do nothing
		}
		
		return null;
	}
	
	/**
	 * ����element����, ���Ҫ��չ�������͵ı�ǩ��֧��, ֻ��Ҫ����µ�ElementNod����������������ɴ����OK��
	 * @param name element��
	 * @param attributes  element����
	 * @return ElementNod
	 * @throws UnknownTagException ����TAGδ������ʱ, �쳣���ᱻ�׳�
	 */
	public ElementNode createNod(String name, Attributes attributes) throws UnknownTagException {
		if ("xhtml".equals(name)){
			return new RootNode();
		}
		else if ("font".equals(name)) {
			return new FontNode(attributes);
		}
		else if ("strike".equals(name)) {
			return new StrikeNode();
		}
		else if ("br".equals(name)) {
			return new BrNode();
		}
		else if ("img".equals(name)) {
			return new ImageNode(name, attributes, mImageGetter);
		}

		throw new UnknownTagException(name);
	}
	
	public SpannableString getResult(){
		return mResult;
	}
}