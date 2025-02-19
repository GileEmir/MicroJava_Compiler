package rs.ac.bg.etf.pp1;

import rs.ac.bg.etf.pp1.ast.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import rs.ac.bg.etf.pp1.ast.VisitorAdaptor;
import rs.etf.pp1.symboltable.concepts.Struct;

public class ActParCounter extends VisitorAdaptor {
	
	List<Struct> finalActParList = new ArrayList<>();
	
	Stack<List<Struct>> actParList = new Stack<>();
	
	
	@Override
	public void visit(ActParListBegin actParListBegin) {
		actParList.push(new ArrayList<>());
	}
	
	@Override
	public void visit(ActPar actPar) {
		actParList.peek().add(actPar.getExpr().struct);
	}
	
	@Override
	public void visit(ActParList_recursive actParList_recursive) {
		finalActParList = actParList.pop();
	}
	
	@Override
	public void visit(ActParList_e actParList_e) {
		finalActParList = actParList.pop();
	}
}
