// generated with ast extension for cup
// version 0.8
// 17/8/2025 19:54:7


package rs.ac.bg.etf.pp1.ast;

public class MethodSignature implements SyntaxNode {

    private SyntaxNode parent;
    private int line;
    private MethodReturnAndName MethodReturnAndName;
    private FormParList FormParList;

    public MethodSignature (MethodReturnAndName MethodReturnAndName, FormParList FormParList) {
        this.MethodReturnAndName=MethodReturnAndName;
        if(MethodReturnAndName!=null) MethodReturnAndName.setParent(this);
        this.FormParList=FormParList;
        if(FormParList!=null) FormParList.setParent(this);
    }

    public MethodReturnAndName getMethodReturnAndName() {
        return MethodReturnAndName;
    }

    public void setMethodReturnAndName(MethodReturnAndName MethodReturnAndName) {
        this.MethodReturnAndName=MethodReturnAndName;
    }

    public FormParList getFormParList() {
        return FormParList;
    }

    public void setFormParList(FormParList FormParList) {
        this.FormParList=FormParList;
    }

    public SyntaxNode getParent() {
        return parent;
    }

    public void setParent(SyntaxNode parent) {
        this.parent=parent;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line=line;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(MethodReturnAndName!=null) MethodReturnAndName.accept(visitor);
        if(FormParList!=null) FormParList.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(MethodReturnAndName!=null) MethodReturnAndName.traverseTopDown(visitor);
        if(FormParList!=null) FormParList.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(MethodReturnAndName!=null) MethodReturnAndName.traverseBottomUp(visitor);
        if(FormParList!=null) FormParList.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("MethodSignature(\n");

        if(MethodReturnAndName!=null)
            buffer.append(MethodReturnAndName.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(FormParList!=null)
            buffer.append(FormParList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [MethodSignature]");
        return buffer.toString();
    }
}
