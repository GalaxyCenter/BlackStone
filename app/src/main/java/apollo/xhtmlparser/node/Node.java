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
	 * 对目标节点作处理
	 * @param nod 目标节点
	 * @return 处理过的节点
	 * @throws SAXException
	 */
	abstract public Node execute(Node nod) throws SAXException;
}