// generated with ast extension for cup
// version 0.8
// 17/8/2025 19:54:7


package rs.ac.bg.etf.pp1.ast;

public class Statement_do_empty extends Statement {

    private DoNonTerm DoNonTerm;
    private Statement Statement;
    private WhileNonTerm WhileNonTerm;

    public Statement_do_empty (DoNonTerm DoNonTerm, Statement Statement, WhileNonTerm WhileNonTerm) {
        this.DoNonTerm=DoNonTerm;
        if(DoNonTerm!=null) DoNonTerm.setParent(this);
        this.Statement=Statement;
        if(Statement!=null) Statement.setParent(this);
        this.WhileNonTerm=WhileNonTerm;
        if(WhileNonTerm!=null) WhileNonTerm.setParent(this);
    }

    public DoNonTerm getDoNonTerm() {
        return DoNonTerm;
    }

    public void setDoNonTerm(DoNonTerm DoNonTerm) {
        this.DoNonTerm=DoNonTerm;
    }

    public Statement getStatement() {
        return Statement;
    }

    public void setStatement(Statement Statement) {
        this.Statement=Statement;
    }

    public WhileNonTerm getWhileNonTerm() {
        return WhileNonTerm;
    }

    public void setWhileNonTerm(WhileNonTerm WhileNonTerm) {
        this.WhileNonTerm=WhileNonTerm;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(DoNonTerm!=null) DoNonTerm.accept(visitor);
        if(Statement!=null) Statement.accept(visitor);
        if(WhileNonTerm!=null) WhileNonTerm.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(DoNonTerm!=null) DoNonTerm.traverseTopDown(visitor);
        if(Statement!=null) Statement.traverseTopDown(visitor);
        if(WhileNonTerm!=null) WhileNonTerm.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(DoNonTerm!=null) DoNonTerm.traverseBottomUp(visitor);
        if(Statement!=null) Statement.traverseBottomUp(visitor);
        if(WhileNonTerm!=null) WhileNonTerm.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("Statement_do_empty(\n");

        if(DoNonTerm!=null)
            buffer.append(DoNonTerm.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Statement!=null)
            buffer.append(Statement.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(WhileNonTerm!=null)
            buffer.append(WhileNonTerm.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [Statement_do_empty]");
        return buffer.toString();
    }
}
