package apollo.xhtmlparser.node;

import org.xml.sax.SAXException;

abstract public class Node {
	protected String mName;
	
	public Node(String name){
		this.mName = name;
	}
	
	public String getName(){
		return mName;
	}
	
	/**
	 * ��Ŀ��ڵ�������
	 * @param nod Ŀ��ڵ�
	 * @return ������Ľڵ�
	 * @throws SAXException
	 */
	abstract public Node execute(Node nod) throws SAXException;
}