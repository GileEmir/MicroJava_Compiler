// generated with ast extension for cup
// version 0.8
// 14/1/2025 19:24:15


package rs.ac.bg.etf.pp1.ast;

public class VarDeclListRec_e extends VarDeclListRec {

    public VarDeclListRec_e () {
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
        buffer.append("VarDeclListRec_e(\n");

        buffer.append(tab);
        buffer.append(") [VarDeclListRec_e]");
        return buffer.toString();
    }
}
