// generated with ast extension for cup
// version 0.8
// 14/1/2025 19:24:15


package rs.ac.bg.etf.pp1.ast;

public class Statement_do_condition extends Statement {

    private DoNonTerm DoNonTerm;
    private Statement Statement;
    private WhileNonTerm WhileNonTerm;
    private Condition Condition;

    public Statement_do_condition (DoNonTerm DoNonTerm, Statement Statement, WhileNonTerm WhileNonTerm, Condition Condition) {
        this.DoNonTerm=DoNonTerm;
        if(DoNonTerm!=null) DoNonTerm.setParent(this);
        this.Statement=Statement;
        if(Statement!=null) Statement.setParent(this);
        this.WhileNonTerm=WhileNonTerm;
        if(WhileNonTerm!=null) WhileNonTerm.setParent(this);
        this.Condition=Condition;
        if(Condition!=null) Condition.setParent(this);
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

    public Condition getCondition() {
        return Condition;
    }

    public void setCondition(Condition Condition) {
        this.Condition=Condition;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(DoNonTerm!=null) DoNonTerm.accept(visitor);
        if(Statement!=null) Statement.accept(visitor);
        if(WhileNonTerm!=null) WhileNonTerm.accept(visitor);
        if(Condition!=null) Condition.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(DoNonTerm!=null) DoNonTerm.traverseTopDown(visitor);
        if(Statement!=null) Statement.traverseTopDown(visitor);
        if(WhileNonTerm!=null) WhileNonTerm.traverseTopDown(visitor);
        if(Condition!=null) Condition.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(DoNonTerm!=null) DoNonTerm.traverseBottomUp(visitor);
        if(Statement!=null) Statement.traverseBottomUp(visitor);
        if(WhileNonTerm!=null) WhileNonTerm.traverseBottomUp(visitor);
        if(Condition!=null) Condition.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("Statement_do_condition(\n");

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

        if(Condition!=null)
            buffer.append(Condition.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [Statement_do_condition]");
        return buffer.toString();
    }
}
