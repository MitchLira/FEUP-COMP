package logic;

import java.util.ArrayList;

import fa.NFA;
import parser.SimpleNode;

public class NFASet implements Convertable {
	private SimpleNode root;
	private String operator;
	private ArrayList<Convertable> sets;
	
	
	public NFASet(SimpleNode root, String operator) {
		this.root = root;
		this.operator = operator;
		sets = new ArrayList<Convertable>();
		
		for (int i = 0; i < root.jjtGetNumChildren(); i++) {
			SimpleNode node = (SimpleNode) root.jjtGetChild(i);
			ExpressionSet expressionSet = new ExpressionSet(node);
			sets.add(expressionSet);
		}




	}

	@Override
	public NFA convert() {
		return new NFA(this);
	}


	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getOperator() {
		return operator;
	}
	
	public ArrayList<Convertable> getConvertables() {
		return sets;
	}
	
	public void dump() {
		root.dump("");
	}
}
