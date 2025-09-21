// generated with ast extension for cup
// version 0.8
// 17/8/2025 19:54:7


package rs.ac.bg.etf.pp1.ast;

public class Statement_return_void extends Statement {

    public Statement_return_void () {
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
        buffer.append("Statement_return_void(\n");

        buffer.append(tab);
        buffer.append(") [Statement_return_void]");
        return buffer.toString();
    }
}
