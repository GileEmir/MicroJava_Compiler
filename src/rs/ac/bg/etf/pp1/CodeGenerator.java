package rs.ac.bg.etf.pp1;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

//import java.util.HashMap;
//import java.util.Map;
//import java.util.List;

import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

public class CodeGenerator extends VisitorAdaptor{

	private int mainPC;
	private int printSetMethodAddress;
	private int mapMethodAddress;
	
	//private Map<String,Integer> labels = new HashMap<>();
	//private Map<String,List<Integer>> patchAddrs = new HashMap<>();
	public int getMainPc() {
		return this.mainPC;
	}
	
	
    public void initialisePredeclaredMethods() {
        //'ord' and 'chr' are the same code
        Obj ordMethod = Tab.find("ord");
        Obj chrMethod = Tab.find("chr");
        ordMethod.setAdr(Code.pc);
        chrMethod.setAdr(Code.pc);
        Code.put(Code.enter);
        Code.put(1);
        Code.put(1);
        Code.put(Code.load_n);
        Code.put(Code.exit);
        Code.put(Code.return_);

        Obj lenMethod = Tab.find("len");
        lenMethod.setAdr(Code.pc);
        Code.put(Code.enter);
        Code.put(1);
        Code.put(1);
        Code.put(Code.load_n);
        Code.put(Code.arraylength);
        Code.put(Code.exit);
        Code.put(Code.return_);

        // Add the 'add' method for sets
        addMethod();
        addAllMethod();
        
        printSetMethodAddress = Code.pc;
        printSetMethod();
        
        mapMethodAddress = Code.pc;
        mapMethod();
        
    }


   private void addMethod() {
        Obj addMethod = Tab.find("add");
        addMethod.setAdr(Code.pc);

        // Begin method setup: 2 formal parameters (s, b) and 4 total slots (s, b, len, i)
        Code.put(Code.enter);
        Code.put(2);  // parameters: s, b
        Code.put(4);  // locals: s, b, len, i
        
        Code.loadConst('b');
        Code.loadConst(0);         // Width = 0
        Code.put(Code.bprint);      // Print element        

        // Compute the length of the array 's' and store it in local variable 'len'
        Code.put(Code.load_n);   // load s (local 0)
        Code.put(Code.arraylength);
        Code.put(Code.store_2);  // len = s.length

        // Initialize loop index for duplicate checking: i = 1
        Code.loadConst(1);
        Code.put(Code.store_3);  // i = 1

        int dupLoopStart = Code.pc;  // Label: start of duplicate search loop

        // Compare i with s[0] (current set size)
        Code.put(Code.load_3);       // load i
        Code.put(Code.load_n);       // load s
        Code.loadConst(0);           // index 0
        Code.put(Code.aload);        // get s[0]
        Code.put(Code.jcc + Code.ge); // if (i >= s[0]) then exit duplicate loop
        int dupLoopExit = Code.pc;
        Code.put2(0);                // reserve space for jump offset

        // Check if the element at s[i] equals b (the element to add)
        Code.put(Code.load_n);       // load s
        Code.put(Code.load_3);       // load i
        Code.put(Code.aload);        // s[i]
        Code.put(Code.load_1);       // load b
        Code.put(Code.jcc + Code.eq); // if (s[i] == b) then element is already present
        int dupFound = Code.pc;
        Code.put2(0);                // reserve space for jump offset

        // Increment i (i++) and jump back to the beginning of the duplicate search loop
        Code.put(Code.load_3);
        Code.loadConst(1);
        Code.put(Code.add);
        Code.put(Code.store_3);
        Code.putJump(dupLoopStart);

        // Patch jump to exit the loop when no duplicate is found
        Code.fixup(dupLoopExit);

        // Now, if we’re here, the element is not already in the set.
        // Retrieve the current set size from s[0] and store it in i.
        Code.put(Code.load_n);   // load s
        Code.loadConst(0);       // index 0
        Code.put(Code.aload);    // load s[0]
        Code.put(Code.store_3);  // i = s[0]

        // Check if the set is full: if (i >= len) then skip adding
        Code.put(Code.load_3);   // load i (current size)
        Code.put(Code.load_2);   // load len (array length)
        Code.put(Code.jcc + Code.ge); // if (i >= len) then the set is full
        int fullLabel = Code.pc;
        Code.put2(0);            // reserve space for jump offset

        // Insert element b into the set at index i: s[i] = b
        Code.put(Code.load_n);   // load s
        Code.put(Code.load_3);   // load i
        Code.put(Code.load_1);   // load b
        Code.put(Code.astore);   // s[i] = b

        // Increment the set size stored in s[0]: s[0] = s[0] + 1
        Code.put(Code.load_n);   // load s
        Code.loadConst(0);       // index 0
        Code.put(Code.load_3);   // load i (which is s[0])
        Code.loadConst(1);
        Code.put(Code.add);
        Code.put(Code.astore);   // s[0]++

        // Patch jump offsets for duplicate found and full set cases
        Code.fixup(dupFound);
        Code.fixup(fullLabel);

        // Exit the method
        Code.put(Code.exit);
        Code.put(Code.return_);
    }
    
   private void addAllMethod() {
	    // Look up the addAll method and set its starting address.
	    Obj addAllMethod = Tab.find("addAll");
	    addAllMethod.setAdr(Code.pc);
	
	    // Set up the activation record:
	    // Parameters: a (target set) is in local 0, b (array to add) is in local 1.
	    // Locals: len (array length) will be stored in local 2, and loop index i in local 3.
	    Code.put(Code.enter);
	    Code.put(2);  // Two parameters (a and b)
	    Code.put(4);  // Total number of slots: 2 parameters + 2 locals
	
	    // Compute len = b.length and store it in local 2.
	    Code.put(Code.load_1);      // load parameter b (local 1)
	    Code.put(Code.arraylength); // compute b.length
	    Code.put(Code.store_2);     // store the length in local slot 2
	
	    // Initialize the loop index: i = 0 (stored in local 3)
	    Code.loadConst(0);
	    Code.put(Code.store_3);     // i = 0
	    
	    // Print the value of i
	    Code.put(Code.load_3);      // load i from local 3
	    Code.loadConst(5);          // push width (assuming width 5 for printing)
	    Code.put(Code.bprint);      // print i with width 5
	
	
	    // Mark the beginning of the loop.
	    int loopStart = Code.pc;
	
	    // Check loop condition: if (i >= len) then exit the loop.
	    Code.put(Code.load_3);      // load i from local 3
	    Code.put(Code.load_2);      // load len from local 2
	    Code.putFalseJump(Code.lt, 0); // if (i >= len) then jump to loop exit
	    int exitJump = Code.pc - 2;
	
	    // Prepare the call to addMethod(a, b[i]):
	    // 1. Load the target set (parameter a) from local 0.
	    Code.put(Code.load_n);      // push a (local 0)
	    // 2. Load the array b (parameter b) from local 1,
	    //    then load the current index i (local 3) and retrieve b[i].
	    Code.put(Code.load_1);      // push b (local 1)
	    Code.put(Code.load_3);      // push i (local 3)
	    Code.put(Code.aload);       // b[i]
	
	    // Call addMethod with parameters (a, b[i])
	    int addMethodAddress = Tab.find("add").getAdr();
	    int relativeAddress = addMethodAddress - (Code.pc); // 3 bytes for the call instruction
	    Code.put(Code.call);
	    Code.put2(relativeAddress);
	
	    // Increment the loop index: i = i + 1.
	    Code.put(Code.load_3);      // load current i
	    Code.loadConst(1);          // push constant 1
	    Code.put(Code.add);         // compute i + 1
	    Code.put(Code.store_3);     // store updated i back into local 3
	
	    // Jump back to the beginning of the loop.
	    Code.putJump(loopStart);
	    Code.fixup(exitJump);       // fix the jump offset for the exit condition
	
	    // End of method: exit and return.
	    Code.put(Code.exit);
	    Code.put(Code.return_);
}
	   
   private void printSetMethod() {
        Code.put(Code.enter);
        Code.put(1);  // param: s (local 0)
        Code.put(2);  // locals: s (0), i (1)

        // Initialize i = 1
        Code.loadConst(1);
        Code.put(Code.store_1);    // i = 1

        int check = Code.pc;

        // Loop condition: i < s[0]
        Code.put(Code.load_1);     // Load i
        Code.put(Code.load_n);     // Load s
        Code.loadConst(0);
        Code.put(Code.aload);      // Load s[0]
        Code.put(Code.jcc + Code.ge); // Exit loop if i >= s[0]
        int exitLoop = Code.pc;
        Code.put2(0);

        // Print s[i]
        Code.put(Code.load_n);     // Load s
        Code.put(Code.load_1);     // Load i
        Code.put(Code.aload);      // Load s[i]
        Code.loadConst(0);         // Width = 0
        Code.put(Code.print);      // Print element

        // Increment i
        Code.put(Code.load_1);
        Code.loadConst(1);
        Code.put(Code.add);
        Code.put(Code.store_1);    // i++
        Code.putJump(check);

        Code.fixup(exitLoop);
        Code.put(Code.exit);
        Code.put(Code.return_);
    }
   
   	
   private void mapMethod() {
       // Enter the method
       Code.put(Code.enter);
       Code.put(0); // Number of parameters
       Code.put(1); // Number of local variables

       // Example operation in the method
       Code.loadConst('T');
       Code.loadConst(0);
       Code.put(Code.print);

       // Exit the method
       Code.put(Code.exit);
       Code.put(Code.return_);
   }
   
    CodeGenerator() {
        this.initialisePredeclaredMethods();
    }
    
    //MAP METHOD 
    
    @Override
    public void visit(Expr_designator expr_designator) {
        Obj designatorObj = expr_designator.getDesignator().obj;
        
        if (designatorObj.getType().getKind() == Struct.Array) {
            // Load the array reference
            Code.load(designatorObj);
            
            // Get the array length
            Code.put(Code.arraylength);
            
            // Initialize the sum to 0
            Code.loadConst(0);
            
            // Initialize the index to 0
            Code.loadConst(0);
            int loopStart = Code.pc;
            
            // Duplicate index and array length for comparison
            Code.put(Code.dup2);
            Code.putFalseJump(Code.lt, 0);
            int loopEndJump = Code.pc - 2;
            
            // Load the array element
            Code.put(Code.dup2); // Duplicate array reference and index
            Code.put(Code.aload); // Load array element
            
            // Call the mapMethod
            int offset = mapMethodAddress - Code.pc;
            Code.put(Code.call);
            Code.put2(offset);
            
            // Add the result to the sum
            Code.put(Code.add);
            
            // Increment the index
            Code.put(Code.const_1);
            Code.put(Code.add);
            
            // Jump to the start of the loop
            Code.putJump(loopStart);
            
            // Fix the jump address for loop end
            Code.fixup(loopEndJump);
            
            // Clean up the stack (remove array reference and index)
            Code.put(Code.pop);
            Code.put(Code.pop);
        } else {
            // If it's not an array, just call the mapMethod once
            int offset = mapMethodAddress - Code.pc;
            Code.put(Code.call);
            Code.put2(offset);
        }
    }
	
	/*METHOD DECLARATIONS*/
	@Override 
	public void visit(MethodReturnAndName_type methodReturnAndName_type) {
		
		methodReturnAndName_type.obj.setAdr(Code.pc);
		if(methodReturnAndName_type.getI2().equalsIgnoreCase("main"))
			this.mainPC = Code.pc;
		
		Code.put(Code.enter);
		Code.put(methodReturnAndName_type.obj.getLevel());
		Code.put(methodReturnAndName_type.obj.getLocalSymbols().size());
	}
	
	@Override
	public void visit(MethodReturnAndName_void methodReturnAndName_void) {
		methodReturnAndName_void.obj.setAdr(Code.pc);
		if(methodReturnAndName_void.getI1().equalsIgnoreCase("main"))
			this.mainPC = Code.pc;
		Code.put(Code.enter);
		Code.put(methodReturnAndName_void.obj.getLevel());
		Code.put(methodReturnAndName_void.obj.getLocalSymbols().size());
	}
	
	@Override
	public void visit(MethodDecl methodDecl) {
		Code.put(Code.exit);
		Code.put(Code.return_);
	}
	
	/*STATEMENTS*/
	
	//DESIGNATOR STATEMENTS
	@Override
	public void visit(DesignatorStatement_assign designatorStatement_assign) {
		Code.store(designatorStatement_assign.getDesignator().obj);
	}
	
	@Override
	public void visit(DesignatorStatement_meth designatorStatement_meth) {
		int offset = designatorStatement_meth.getDesignator().obj.getAdr() - Code.pc;
		Code.put(Code.call);
		Code.put2(offset);
		
		if(designatorStatement_meth.getDesignator().obj.getType()!= Tab.noType) {
			Code.put(Code.pop);
		}
	}
	
	
	@Override
	public void visit(DesignatorStatement_incr designatorStatement_incr) {
		if(designatorStatement_incr.getDesignator().obj.getKind() == Obj.Elem) {
			Code.put(Code.dup2); //ovde je dup2 jer se cuva i indeks i adresa oba nam trebaju za inkrementovanje vrednosti niza 
		}else if(designatorStatement_incr.getDesignator().obj.getKind() == Obj.Fld) {
			Code.put(Code.dup); // ovo je u slucaju fieldova npr o.x++,nakon sto sabere 1 sa tom vrednoscu treba mu adresa polja x
		}
		Code.load(designatorStatement_incr.getDesignator().obj);
		Code.loadConst(1);
		Code.put(Code.add);
		Code.store(designatorStatement_incr.getDesignator().obj);
	}
	
	@Override
	public void visit(DesignatorStatement_decr designatorStatement_decr) {
		if(designatorStatement_decr.getDesignator().obj.getKind() == Obj.Elem) {
			Code.put(Code.dup2); //ovde je dup2 jer se cuva i indeks i adresa oba nam trebaju za inkrementovanje vrednosti niza 
		}else if(designatorStatement_decr.getDesignator().obj.getKind() == Obj.Fld) {
			Code.put(Code.dup); // ovo je u slucaju fieldova npr o.x++,nakon sto sabere 1 sa tom vrednoscu treba mu adresa polja x
		}
		Code.load(designatorStatement_decr.getDesignator().obj);
		Code.loadConst(1);
		Code.put(Code.sub);
		Code.store(designatorStatement_decr.getDesignator().obj);
	}
	
	@Override
	public void visit(Statement_return_void statement_return_void) {
		Code.put(Code.exit);
		Code.put(Code.return_);
	} 
	
	@Override
	public void visit(Statement_return_expr statement_return_expr) {
		Code.put(Code.exit);
		Code.put(Code.return_);
	}
	
	@Override
	public void visit(Statement_read statement_read) {
		if(statement_read.getDesignator().obj.getType().equals(Tab.charType)) {
			Code.put(Code.bread);
		}else {
			Code.put(Code.read);
		}
		Code.store(statement_read.getDesignator().obj);
	} 
	
	//SINGLE STATEMENTS
	
	@Override
	public void visit(Statement_print statement_print) {
	    if (statement_print.getExpr().struct == SemAnalyser.setType) {
	        // The address of the set array is already on the stack
	        // Call the printSetMethod
	        int offset = printSetMethodAddress - Code.pc;
	        Code.put(Code.call);
	        Code.put2(offset);
	    } else {
	    	Code.loadConst(0);
	    	if (statement_print.getExpr().struct.equals(Tab.charType))
	            Code.put(Code.bprint);
	        else
	            Code.put(Code.print);
	    }
	}
	
	@Override 
	public void visit(Statement_print_number statement_print_number) {
		Code.loadConst(statement_print_number.getN2());
		if(statement_print_number.getExpr().struct.equals(Tab.charType))
			Code.put(Code.bprint);
		else
			Code.put(Code.print);
	}
	
	@Override
	public void visit(FactorElement_number factorElement_number) {
		Code.loadConst(factorElement_number.getN1());
	}
	
	@Override
	public void visit(FactorElement_character factorElement_character) {
		Code.loadConst(factorElement_character.getC1());
	}
	
	
	/*EXPR*/ 
	@Override
	public void visit(AddopTermList_add addopTermList_add) {
		if(addopTermList_add.getAddop() instanceof Addop_plus) {
			Code.put(Code.add);
		}else if (addopTermList_add.getAddop() instanceof Addop_minus) {
			Code.put(Code.sub);
		}
	}
	
	
	@Override
	public void visit(MulopFactorList_mul mulopFactorList_mul) {
		if(mulopFactorList_mul.getMulop() instanceof Mulop_mul) {
			Code.put(Code.mul);
		}else if (mulopFactorList_mul.getMulop() instanceof Mulop_div) {
			Code.put(Code.div);
		}else if (mulopFactorList_mul.getMulop() instanceof Mulop_rem) {
			Code.put(Code.rem);
			}
	}
	
	
	/*FACTOR*/ 
	@Override
	public void visit(FactorElement_var factorElement_var) {
		Code.load(factorElement_var.getDesignator().obj);
	}
	
	@Override
	public void visit(FactorElement_new_array factorElement_new_array) {
	    // Check if the type is setType
	    if (factorElement_new_array.getType().struct == SemAnalyser.setType) {
	        Code.loadConst(1);
	        Code.put(Code.add); // Increment the size n by 1
	    }

	    Code.put(Code.newarray);
	    if (factorElement_new_array.getType().struct.equals(Tab.charType)) {
	        Code.put(0); // Array of bytes (char)
	    } else {
	        Code.put(1); // Array of words (int, bool)
	    }

	    if (factorElement_new_array.getType().struct == SemAnalyser.setType) {
	        Code.put(Code.dup); // Duplicate the array reference (addr)
	        Code.loadConst(0); // Load constant 0 (index)
	        Code.loadConst(1); // Load constant 1 (val)
	        Code.put(Code.astore); // Store 1 in the first element (size field)
	    }
	}
	
	
	@Override
	public void visit(FactorElement_method factorElement_method) {
		int offset = factorElement_method.getDesignator().obj.getAdr() - Code.pc;
		Code.put(Code.call);
		Code.put2(offset);
	}
	
	@Override
	public void visit(Factor_minus factor_minus) {
		Code.put(Code.neg);
	}
	
	@Override
	public void visit(Factor_plus factor_plus) {
		//
	}
	
	/*DESIGNATOR*/
	
	@Override
	public void visit(DesignatorArrayName designatorArrayName) {
		Code.load(designatorArrayName.obj);
	}
	
	
	
	/*ZA LABELE*/
	/*@Override
	 * public void visit(Label label){
	 * 		labels.put(label.getI1(),Code.pc);
	 * 		if(patchAddrs.containsKey(label.getI1()))
	 * 			while(patchAddrs.get(label.getI1()).isEmpty())
	 * 				Code.fixup(patchAddrs.get(label.getI1()).remove(0);
	 * 		
	 * }
	 * 
	 * 
	 * GOTO
	 * @Override
	 * public void visit(Statement_goto statement_goto){
	 * 	if(labels.containsKey(statement_goto.getI1())){
	 * 		Code.putJump(labels.get(statement_goto.getI1()));	
	 * 	}
	 * else{
	 * 		Code.putJump(0);
	 * 		int patchaddr = Code.pc - 2;
	 * 		List<Integer> l;
	 * 		if (patchAddrs.containsKey(statement_goto.getI1()))
	 * 			l = patchAddrs.get(statement_goto.getI1());
	 * 		else{
	 * 			l = new ArrayList<>();
	 * 			patchAddrs.put(statement_goto.getI1(),l);
	 * 		}
	 * 		l.add(patchAddr);
	 * 		
	 * } 
	 * }
	 */
	
	
	//CONDITIONS
	private int returnRelop(Relop relop) {
		if (relop instanceof Relop_eq)
			return Code.eq;
		else if (relop instanceof Relop_ne)
			return Code.ne;
		else if (relop instanceof Relop_gt)
			return Code.gt;
		else if (relop instanceof Relop_ge)
			return Code.ge;
		else if (relop instanceof Relop_lt)
			return Code.lt;
		else if (relop instanceof Relop_le)
			return Code.le;
		else return 0;


	}
	private Stack<Integer> skipCondFact = new Stack<>();
	private Stack<Integer> skipCondition = new Stack<>();
	private Stack<Integer> skipThen = new Stack<>();
	private Stack<Integer> skipElse = new Stack<>();
	
	@Override
	public void visit(CondFact_expr condFact_expr) {
		Code.loadConst(0);
		Code.putFalseJump(Code.ne, 0); //netacna
		skipCondFact.push(Code.pc-2);
		//tacna
		
	}
	
	@Override
	public void visit(CondFact_expr_r_expr condFact_expr_r_expr) {
		Code.putFalseJump(returnRelop(condFact_expr_r_expr.getRelop()), 0); //netacna
		skipCondFact.push(Code.pc-2);
		//tacna
		
	}
	
	@Override
	public void visit(CondTerm condTerm) {
		//tacna
		Code.putJump(0);//tacne bacamo na THEN
		skipCondition.push(Code.pc-2);
		//ovde vracamo netacne
		while(!skipCondFact.empty())
			Code.fixup(skipCondFact.pop());
		//netacne
		
	}
	
	
	@Override
	public void visit(Condition condition) {
		//netacni
		Code.putJump(0);//netacne na ELSE
		this.skipThen.push(Code.pc-2);
		//THEN
		while(!skipCondition.empty())
			Code.fixup(skipCondition.pop());
		//tacne
		
	}
	
	@Override
	public void visit(ElseStatement_empty elseStatement_empty) {
		//tacne
		Code.fixup(skipThen.pop());
		//tacne+netacne
		
	}
	
	@Override
	public void visit(Else else_) {
		//tacne
		Code.putJump(0);//tacne bacamo na kraj ELSE
		this.skipElse.push(Code.pc - 2); 
		Code.fixup(skipThen.pop());
		//netacne 
		
	}
	
	@Override
	public void visit(ElseStatement_else elseStatement_else) {
		//netacne
		Code.fixup(skipElse.pop());// vracamo tacne koje su preskocile ELSE
		//netacne + tacne
	}
	
	
	//DO WHILE
	
	private Stack<Integer> doBegin = new Stack<>();
	@Override
	public void visit(DoNonTerm doNonTerm) {
		doBegin.push(Code.pc);
		breakJumps.push(new ArrayList<Integer>());
		continueJumps.push(new ArrayList<Integer>());
	
	}
	
	@Override
	public void visit(Statement_do_empty statement_do_empty) {
		// The condition is omitted, so it is implicitly true.
		// Jump back unconditionally to the beginning of the loop.
		Code.putJump(doBegin.pop());
		while(!breakJumps.peek().isEmpty())
			Code.fixup(breakJumps.peek().remove(0));
		breakJumps.pop();
 	}
	
	@Override
	public void visit(Statement_do_condition statement_do_condition) {
		Code.putJump(doBegin.pop());
		Code.fixup(skipThen.pop());
		while(!breakJumps.peek().isEmpty())
			Code.fixup(breakJumps.peek().remove(0));
		breakJumps.pop();
	}
	
	@Override
	public void visit(Statement_do_condition_designator statement_do_condition_designator) {
	    // At this point:
	    //   - The loop’s body has been generated.
	    //   - The Condition was processed and it emitted a false jump (saved in skipThen)
	    //     so that if the condition is false, the designator statement is skipped.
	    //   - The DesignatorStatement code (the extra code to be executed at the end
	    //     of the iteration when the condition is true) has been generated.
	    
	    // Now, emit an unconditional jump to return to the beginning of the loop.
	    Code.putJump(doBegin.pop());
	    
	    // Fix up the pending false jump from the Condition:
	    // If the condition was false, execution jumps here—i.e. skipping the designator code.
	    Code.fixup(skipThen.pop());
	    while(!breakJumps.peek().isEmpty())
			Code.fixup(breakJumps.peek().remove(0));
		breakJumps.pop();
	}
	
	//break i continue
	
	private Stack<List<Integer>> breakJumps = new Stack<>();
	private Stack<List<Integer>> continueJumps = new Stack<>();

	@Override
	public void visit(Statement_break statement_break) {
		Code.putJump(0);
		breakJumps.peek().add(Code.pc - 2);
		
	}
	
	@Override
	public void visit(Statement_continue statement_continue) {
		Code.putJump(0);
		continueJumps.peek().add(Code.pc - 2);
		
	}
	
	@Override
	public void visit(WhileNonTerm whileNonTerm) {
		while(!continueJumps.peek().isEmpty())
			Code.fixup(continueJumps.peek().remove(0));
		continueJumps.pop();
	}

	
}
