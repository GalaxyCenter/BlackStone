package apollo.xhtmlparser.node;

public class BrNode extends ElementNode {

	public BrNode() {
		super("br", null);
	}

	@Override
	public Node execute(Node value) {
		//����ת��Ϊ����Ϊ���з���TextNod
		return new TextNode("\r\n");
	}
}