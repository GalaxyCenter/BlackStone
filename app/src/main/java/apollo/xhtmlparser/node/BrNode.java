package apollo.xhtmlparser.node;

public class BrNode extends ElementNode {

	public BrNode() {
		super("br", null);
	}

	@Override
	public Node execute(Node value) {
		//仅仅转换为内容为换行符的TextNod
		return new TextNode("\r\n");
	}
}