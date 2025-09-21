// generated with ast extension for cup
// version 0.8
// 17/8/2025 19:54:8


package rs.ac.bg.etf.pp1.ast;

public class Factor_plus extends Factor {

    private FactorElement FactorElement;

    public Factor_plus (FactorElement FactorElement) {
        this.FactorElement=FactorElement;
        if(FactorElement!=null) FactorElement.setParent(this);
    }

    public FactorElement getFactorElement() {
        return FactorElement;
    }

    public void setFactorElement(FactorElement FactorElement) {
        this.FactorElement=FactorElement;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(FactorElement!=null) FactorElement.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(FactorElement!=null) FactorElement.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(FactorElement!=null) FactorElement.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("Factor_plus(\n");

        if(FactorElement!=null)
            buffer.append(FactorElement.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [Factor_plus]");
        return buffer.toString();
    }
}
