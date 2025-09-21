// generated with ast extension for cup
// version 0.8
// 17/8/2025 19:54:7


package rs.ac.bg.etf.pp1.ast;

public class Statement_for extends Statement {

    private For For;
    private DesignatorStatement DesignatorStatement;
    private ForJump ForJump;
    private Condition Condition;
    private SemiFor SemiFor;
    private DesignatorStatement DesignatorStatement1;
    private Rparen Rparen;
    private Statement Statement;
    private JumpToForStatement JumpToForStatement;

    public Statement_for (For For, DesignatorStatement DesignatorStatement, ForJump ForJump, Condition Condition, SemiFor SemiFor, DesignatorStatement DesignatorStatement1, Rparen Rparen, Statement Statement, JumpToForStatement JumpToForStatement) {
        this.For=For;
        if(For!=null) For.setParent(this);
        this.DesignatorStatement=DesignatorStatement;
        if(DesignatorStatement!=null) DesignatorStatement.setParent(this);
        this.ForJump=ForJump;
        if(ForJump!=null) ForJump.setParent(this);
        this.Condition=Condition;
        if(Condition!=null) Condition.setParent(this);
        this.SemiFor=SemiFor;
        if(SemiFor!=null) SemiFor.setParent(this);
        this.DesignatorStatement1=DesignatorStatement1;
        if(DesignatorStatement1!=null) DesignatorStatement1.setParent(this);
        this.Rparen=Rparen;
        if(Rparen!=null) Rparen.setParent(this);
        this.Statement=Statement;
        if(Statement!=null) Statement.setParent(this);
        this.JumpToForStatement=JumpToForStatement;
        if(JumpToForStatement!=null) JumpToForStatement.setParent(this);
    }

    public For getFor() {
        return For;
    }

    public void setFor(For For) {
        this.For=For;
    }

    public DesignatorStatement getDesignatorStatement() {
        return DesignatorStatement;
    }

    public void setDesignatorStatement(DesignatorStatement DesignatorStatement) {
        this.DesignatorStatement=DesignatorStatement;
    }

    public ForJump getForJump() {
        return ForJump;
    }

    public void setForJump(ForJump ForJump) {
        this.ForJump=ForJump;
    }

    public Condition getCondition() {
        return Condition;
    }

    public void setCondition(Condition Condition) {
        this.Condition=Condition;
    }

    public SemiFor getSemiFor() {
        return SemiFor;
    }

    public void setSemiFor(SemiFor SemiFor) {
        this.SemiFor=SemiFor;
    }

    public DesignatorStatement getDesignatorStatement1() {
        return DesignatorStatement1;
    }

    public void setDesignatorStatement1(DesignatorStatement DesignatorStatement1) {
        this.DesignatorStatement1=DesignatorStatement1;
    }

    public Rparen getRparen() {
        return Rparen;
    }

    public void setRparen(Rparen Rparen) {
        this.Rparen=Rparen;
    }

    public Statement getStatement() {
        return Statement;
    }

    public void setStatement(Statement Statement) {
        this.Statement=Statement;
    }

    public JumpToForStatement getJumpToForStatement() {
        return JumpToForStatement;
    }

    public void setJumpToForStatement(JumpToForStatement JumpToForStatement) {
        this.JumpToForStatement=JumpToForStatement;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(For!=null) For.accept(visitor);
        if(DesignatorStatement!=null) DesignatorStatement.accept(visitor);
        if(ForJump!=null) ForJump.accept(visitor);
        if(Condition!=null) Condition.accept(visitor);
        if(SemiFor!=null) SemiFor.accept(visitor);
        if(DesignatorStatement1!=null) DesignatorStatement1.accept(visitor);
        if(Rparen!=null) Rparen.accept(visitor);
        if(Statement!=null) Statement.accept(visitor);
        if(JumpToForStatement!=null) JumpToForStatement.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(For!=null) For.traverseTopDown(visitor);
        if(DesignatorStatement!=null) DesignatorStatement.traverseTopDown(visitor);
        if(ForJump!=null) ForJump.traverseTopDown(visitor);
        if(Condition!=null) Condition.traverseTopDown(visitor);
        if(SemiFor!=null) SemiFor.traverseTopDown(visitor);
        if(DesignatorStatement1!=null) DesignatorStatement1.traverseTopDown(visitor);
        if(Rparen!=null) Rparen.traverseTopDown(visitor);
        if(Statement!=null) Statement.traverseTopDown(visitor);
        if(JumpToForStatement!=null) JumpToForStatement.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(For!=null) For.traverseBottomUp(visitor);
        if(DesignatorStatement!=null) DesignatorStatement.traverseBottomUp(visitor);
        if(ForJump!=null) ForJump.traverseBottomUp(visitor);
        if(Condition!=null) Condition.traverseBottomUp(visitor);
        if(SemiFor!=null) SemiFor.traverseBottomUp(visitor);
        if(DesignatorStatement1!=null) DesignatorStatement1.traverseBottomUp(visitor);
        if(Rparen!=null) Rparen.traverseBottomUp(visitor);
        if(Statement!=null) Statement.traverseBottomUp(visitor);
        if(JumpToForStatement!=null) JumpToForStatement.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("Statement_for(\n");

        if(For!=null)
            buffer.append(For.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(DesignatorStatement!=null)
            buffer.append(DesignatorStatement.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ForJump!=null)
            buffer.append(ForJump.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Condition!=null)
            buffer.append(Condition.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(SemiFor!=null)
            buffer.append(SemiFor.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(DesignatorStatement1!=null)
            buffer.append(DesignatorStatement1.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Rparen!=null)
            buffer.append(Rparen.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(Statement!=null)
            buffer.append(Statement.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(JumpToForStatement!=null)
            buffer.append(JumpToForStatement.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [Statement_for]");
        return buffer.toString();
    }
}
