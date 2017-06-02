package logic;

import fa.NFA;
import parser.GrammarParserTreeConstants;
import parser.SimpleNode;
import utils.Pair;
import utils.Utils;

public class Term implements Convertable {
	private SimpleNode root;
	private String name;
	private Pair pair;
	

	public Term(SimpleNode root) {
		this.root = root;
		this.name = root.name;
		
		if (root.jjtGetNumChildren() > 0) {
			SimpleNode child = (SimpleNode) root.jjtGetChild(0);
			pair = Utils.getOperatorRange(child.name);
		} else {
			pair = new Pair(1,1);
		}
	}

	@Override
	public NFA convert() {
		return new NFA(this);
	}

	public SimpleNode getRoot() {
		return root;
	}

	public String getName() {
		return name;
	}

	public Pair getPair() {
		return pair;
	}
	
	

}
