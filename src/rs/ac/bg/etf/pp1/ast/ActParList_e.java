// generated with ast extension for cup
// version 0.8
// 14/1/2025 19:24:15


package rs.ac.bg.etf.pp1.ast;

public class ActParList_e extends ActParList {

    private ActParListBegin ActParListBegin;

    public ActParList_e (ActParListBegin ActParListBegin) {
        this.ActParListBegin=ActParListBegin;
        if(ActParListBegin!=null) ActParListBegin.setParent(this);
    }

    public ActParListBegin getActParListBegin() {
        return ActParListBegin;
    }

    public void setActParListBegin(ActParListBegin ActParListBegin) {
        this.ActParListBegin=ActParListBegin;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ActParListBegin!=null) ActParListBegin.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ActParListBegin!=null) ActParListBegin.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ActParListBegin!=null) ActParListBegin.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ActParList_e(\n");

        if(ActParListBegin!=null)
            buffer.append(ActParListBegin.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ActParList_e]");
        return buffer.toString();
    }
}
