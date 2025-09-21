// generated with ast extension for cup
// version 0.8
// 17/8/2025 19:54:7


package rs.ac.bg.etf.pp1.ast;

public class Statement_do_condition_designator extends Statement {

    private DoNonTerm DoNonTerm;
    private Statement Statement;
    private WhileNonTerm WhileNonTerm;
    private Condition Condition;
    private DesignatorStatement DesignatorStatement;

    public Statement_do_condition_designator (DoNonTerm DoNonTerm, Statement Statement, WhileNonTerm WhileNonTerm, Condition Condition, DesignatorStatement DesignatorStatement) {
        this.DoNonTerm=DoNonTerm;
        if(DoNonTerm!=null) DoNonTerm.setParent(this);
        this.Statement=Statement;
        if(Statement!=null) Statement.setParent(this);
        this.WhileNonTerm=WhileNonTerm;
        if(WhileNonTerm!=null) WhileNonTerm.setParent(this);
        this.Condition=Condition;
        if(Condition!=null) Condition.setParent(this);
        this.DesignatorStatement=DesignatorStatement;
        if(DesignatorStatement!=null) DesignatorStatement.setParent(this);
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

    public DesignatorStatement getDesignatorStatement() {
        return DesignatorStatement;
    }

    public void setDesignatorStatement(DesignatorStatement DesignatorStatement) {
        this.DesignatorStatement=DesignatorStatement;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(DoNonTerm!=null) DoNonTerm.accept(visitor);
        if(Statement!=null) Statement.accept(visitor);
        if(WhileNonTerm!=null) WhileNonTerm.accept(visitor);
        if(Condition!=null) Condition.accept(visitor);
        if(DesignatorStatement!=null) DesignatorStatement.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(DoNonTerm!=null) DoNonTerm.traverseTopDown(visitor);
        if(Statement!=null) Statement.traverseTopDown(visitor);
        if(WhileNonTerm!=null) WhileNonTerm.traverseTopDown(visitor);
        if(Condition!=null) Condition.traverseTopDown(visitor);
        if(DesignatorStatement!=null) DesignatorStatement.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(DoNonTerm!=null) DoNonTerm.traverseBottomUp(visitor);
        if(Statement!=null) Statement.traverseBottomUp(visitor);
        if(WhileNonTerm!=null) WhileNonTerm.traverseBottomUp(visitor);
        if(Condition!=null) Condition.traverseBottomUp(visitor);
        if(DesignatorStatement!=null) DesignatorStatement.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("Statement_do_condition_designator(\n");

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

        if(DesignatorStatement!=null)
            buffer.append(DesignatorStatement.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [Statement_do_condition_designator]");
        return buffer.toString();
    }
}
