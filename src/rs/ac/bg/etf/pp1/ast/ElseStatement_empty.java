// generated with ast extension for cup
// version 0.8
// 17/8/2025 19:54:7


package rs.ac.bg.etf.pp1.ast;

public class ElseStatement_empty extends ElseStatement {

    public ElseStatement_empty () {
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ElseStatement_empty(\n");

        buffer.append(tab);
        buffer.append(") [ElseStatement_empty]");
        return buffer.toString();
    }
}
