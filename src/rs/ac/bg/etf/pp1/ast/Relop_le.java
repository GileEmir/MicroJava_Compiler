// generated with ast extension for cup
// version 0.8
// 17/8/2025 19:54:8


package rs.ac.bg.etf.pp1.ast;

public class Relop_le extends Relop {

    public Relop_le () {
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
        buffer.append("Relop_le(\n");

        buffer.append(tab);
        buffer.append(") [Relop_le]");
        return buffer.toString();
    }
}
