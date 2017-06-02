package logic;

import java.util.ArrayList;

import fa.NFA;
import parser.GrammarParserTreeConstants;
import parser.SimpleNode;

public class ExpressionSet implements Convertable {
	private SimpleNode root;
	private ArrayList<Convertable> sets;
	
	public ExpressionSet(SimpleNode root) {
		this.root = root;
		sets = new ArrayList<>();
		
		for (int i = 0; i < root.jjtGetNumChildren(); i++) {
			SimpleNode node = (SimpleNode) root.jjtGetChild(i);
			
			Convertable c = null;
			if (GrammarParserTreeConstants.jjtNodeName[node.id] == "Term") {
				c = new Term(node);
			} else if (GrammarParserTreeConstants.jjtNodeName[node.id] == "SubNFA") {
				if (node.jjtGetNumChildren() == 1)
					c = new NFASet((SimpleNode) node.jjtGetChild(0), null);
				else
					c = new NFASet((SimpleNode) node.jjtGetChild(0), ((SimpleNode) node.jjtGetChild(1)).name);
			} else {
				System.err.println("Parser Error");
				System.out.println(GrammarParserTreeConstants.jjtNodeName[node.id]);
				System.exit(-1);
			}
			
			sets.add(c);
		}
	}

	@Override
	public NFA convert() {
		return new NFA(this);
	}
	
	
	public ArrayList<Convertable> getConvertables() {
		return sets;
	}
}
