package rs.ac.bg.etf.pp1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
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
	
	
	private void emitCall(Obj m) {
	    Code.put(Code.call);
	    Code.put2(m.getAdr() - Code.pc + 1);   // +1 so target = enter
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
        
        addAllSetMethod();   // for set ∪ set (deep-copy helper)
        cloneSetMethod();    // create independent copy of a set
        
        printSetMethodAddress = Code.pc;
        printSetMethod();
        
        mapMethodAddress = Code.pc;
        mapMethod();
        
        Obj maxElem = Tab.find("maxElem");
    	maxElem.setAdr(Code.pc);
    	Code.put(Code.enter);
		Code.put(1);
		Code.put(3);
		
		Code.loadConst(2);
		Code.put(Code.store_1);
		
		Code.put(Code.load_n);
		Code.loadConst(1);
		Code.put(Code.aload);
		Code.put(Code.store_2);
		
		int loop = Code.pc;
		
		Code.put(Code.load_1);
		Code.put(Code.load_n);
		Code.loadConst(0);
		Code.put(Code.aload);
		
		Code.putFalseJump(Code.lt, 0);
		int end = Code.pc - 2;
		
		Code.put(Code.load_n);
		Code.put(Code.load_1);
		Code.put(Code.aload);
		
		Code.put(Code.load_2);
		
		Code.putFalseJump(Code.gt, 0);
		int skipIf = Code.pc - 2;
		
		Code.put(Code.load_n);
		Code.put(Code.load_1);
		Code.put(Code.aload);
		Code.put(Code.store_2);
		
		Code.fixup(skipIf);
		
		Code.put(Code.load_1);
		Code.loadConst(1);
		Code.put(Code.add);
		Code.put(Code.store_1);
		
		Code.putJump(loop);
		Code.fixup(end);
		
		Code.put(Code.load_2);

		Code.put(Code.exit);
		Code.put(Code.return_);
        
        
        
       // maxElem();
        
    }
    
    private void maxElem() {
    	Obj maxElem = Tab.find("maxElem");
    	maxElem.setAdr(Code.pc);
        
        Collection<Obj> locals = maxElem.getLocalSymbols();
		Iterator<Obj> it = locals.iterator();
		
		Code.put(Code.enter);
		Code.put(1);
		Code.put(3);
		Code.loadConst(1);
		Code.put(Code.store_1);
		Code.loadConst(0);
		Code.put(Code.aload);
		Code.put(Code.store_2);
		Code.loadConst(0);
		
		Code.put(Code.aload);
		Code.loadConst(0);
		Code.putFalseJump(Code.gt, Code.pc + 6);
		Code.putJump(Code.pc + 6);
		Code.putJump(Code.pc + 33);
		Code.put(Code.load_1);
		Code.put(Code.aload);
		Code.put(Code.load_2);
		
		Code.putFalseJump(Code.gt, Code.pc + 6);
		Code.putJump(Code.pc + 6);
		Code.putJump(Code.pc + 6);
		
		Code.put(Code.load_1);
		Code.put(Code.aload);
		Code.put(Code.store_2);
		Code.put(Code.load_1);
		Code.loadConst(0);
		Code.put(Code.aload);
		
		Code.putFalseJump(Code.lt, Code.pc + 6);
		Code.putJump(Code.pc + 6);
		Code.putJump(Code.pc + 6);
		Code.putJump(Code.pc - 27);
		Code.put(Code.load_2);
		Code.put(Code.exit);
		Code.put(Code.return_);
		Code.put(Code.exit);
		Code.put(Code.return_);
		
    }
    
    private void addAllSetMethod() {
        Obj m = Tab.find("addAllSet");
        if (m == Tab.noObj) {
            m = Tab.insert(Obj.Meth, "addAllSet", Tab.noType);
            m.setLevel(2); // two params
        }
        m.setAdr(Code.pc);

        // Params: a(0), b(1); Locals: lenHeader(2), i(3)
        Code.put(Code.enter);
        Code.put(2);
        Code.put(4);

        // lenHeader = b[0]   (next free index; logical elements are 1..lenHeader-1)
        Code.put(Code.load_1);
        Code.loadConst(0);
        Code.put(Code.aload);
        Code.put(Code.store_2);

        // i = 1
        Code.loadConst(1);
        Code.put(Code.store_3);

        int loopStart = Code.pc;
        // while (i < lenHeader)
        Code.put(Code.load_3);
        Code.put(Code.load_2);
        Code.putFalseJump(Code.lt, 0);
        int exitFix = Code.pc - 2;

        // add(a, b[i])
        Code.put(Code.load_n);   // a
        Code.put(Code.load_1);   // b
        Code.put(Code.load_3);   // i
        Code.put(Code.aload);    // b[i]
        int addAdr = Tab.find("add").getAdr();
        Code.put(Code.call);
        Code.put2(addAdr - Code.pc);

        // i++
        Code.put(Code.load_3);
        Code.loadConst(1);
        Code.put(Code.add);
        Code.put(Code.store_3);

        Code.putJump(loopStart);
        Code.fixup(exitFix);

        Code.put(Code.exit);
        Code.put(Code.return_);
}
	    
	private void cloneSetMethod() {
	    Obj m = Tab.find("cloneSet");
	    if (m == Tab.noObj) {
	        // Return type = setType (so call leaves result)
	        m = Tab.insert(Obj.Meth, "cloneSet", SemAnalyser.setType);
	        m.setLevel(1); // one parameter
	    }
	    m.setAdr(Code.pc);
	
	    // Params: src(0) ; Locals: newArr(1), sizeHeader(2), i(3)
	    Code.put(Code.enter);
	    Code.put(1);
	    Code.put(4);
	
	    // newArr = new int[src.length]
	    Code.put(Code.load_n);
	    Code.put(Code.arraylength);
	    Code.put(Code.newarray);
	    Code.put(1);             // word elements
	    Code.put(Code.store_1);   // newArr
	
	    // sizeHeader = src[0]
	    Code.put(Code.load_n);
	    Code.loadConst(0);
	    Code.put(Code.aload);
	    Code.put(Code.store_2);
	
	    // i = 0
	    Code.loadConst(0);
	    Code.put(Code.store_3);
	
	    int loopStart = Code.pc;
	    // while (i < sizeHeader)
	    Code.put(Code.load_3);
	    Code.put(Code.load_2);
	    Code.putFalseJump(Code.lt, 0);
	    int exitFix = Code.pc - 2;
	
	    // newArr[i] = src[i]
	    Code.put(Code.load_1);     // newArr
	    Code.put(Code.load_3);     // i
	    Code.put(Code.load_n);     // src
	    Code.put(Code.load_3);     // i
	    Code.put(Code.aload);      // src[i]
	    Code.put(Code.astore);     // newArr[i] = src[i]
	
	    // i++
	    Code.put(Code.load_3);
	    Code.loadConst(1);
	    Code.put(Code.add);
	    Code.put(Code.store_3);
	
	    Code.putJump(loopStart);
	    Code.fixup(exitFix);
	
	    // return newArr
	    Code.put(Code.load_1);
	    Code.put(Code.exit);
	    Code.put(Code.return_);
	}

	@Override
	public void visit(DesignatorStatement_assign_designator n) {
	    Obj dst   = n.getDesignator().obj;      // b
	    Obj left  = n.getDesignator1().obj;     // b1
	    Obj right = n.getDesignator2().obj;     // b2
	    Obj addM  = Tab.find("add");

	    // new int[left.length + right.length]
	    Code.load(left);
	    Code.put(Code.arraylength);
	    Code.load(right);
	    Code.put(Code.arraylength);
	    Code.put(Code.add);
	    Code.put(Code.newarray);
	    Code.put(1);            // word elems

	    // header = 1
	    Code.put(Code.dup);
	    Code.loadConst(0);
	    Code.loadConst(1);
	    Code.put(Code.astore);

	    // store into destination designator (b)
	    Code.store(dst);

	    // -------- copy left ----------
	    // limitLeft = left[0]
	    Code.load(left);
	    Code.loadConst(0);
	    Code.put(Code.aload);
	    Code.put(Code.store_1);     // local1 = limit
	    // i = 1
	    Code.loadConst(1);
	    Code.put(Code.store_n);     // local0 = i

	    int Lloop = Code.pc;
	    Code.put(Code.load_n);
	    Code.put(Code.load_1);
	    Code.putFalseJump(Code.lt, 0);
	    int Lexit = Code.pc - 2;

	    // add(dst, left[i])
	    Code.load(dst);
	    Code.load(left);
	    Code.put(Code.load_n);
	    Code.put(Code.aload);
	    emitCall(addM);

	    // i++
	    Code.put(Code.load_n);
	    Code.loadConst(1);
	    Code.put(Code.add);
	    Code.put(Code.store_n);
	    Code.putJump(Lloop);
	    Code.fixup(Lexit);

	    // -------- copy right ----------
	    Code.load(right);
	    Code.loadConst(0);
	    Code.put(Code.aload);
	    Code.put(Code.store_1);     // limit = right[0]
	    Code.loadConst(1);
	    Code.put(Code.store_n);     // i = 1

	    int Rloop = Code.pc;
	    Code.put(Code.load_n);
	    Code.put(Code.load_1);
	    Code.putFalseJump(Code.lt, 0);
	    int Rexit = Code.pc - 2;

	    // add(dst, right[i])
	    Code.load(dst);
	    Code.load(right);
	    Code.put(Code.load_n);
	    Code.put(Code.aload);
	    emitCall(addM);

	    // i++
	    Code.put(Code.load_n);
	    Code.loadConst(1);
	    Code.put(Code.add);
	    Code.put(Code.store_n);
	    Code.putJump(Rloop);
	    Code.fixup(Rexit);
	}
	
   private void addMethod() {
        Obj addMethod = Tab.find("add");
        addMethod.setAdr(Code.pc);

        // Begin method setup: 2 formal parameters (s, b) and 4 total slots (s, b, len, i)
        Code.put(Code.enter);
        Code.put(2);  // parameters: s, b
        Code.put(4);  // locals: s, b, len, i
        
        //Code.loadConst('b');
        //Code.loadConst(0);         // Width = 0
        //Code.put(Code.bprint);      // Print element        

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

        // i = 1 (prvi realni element)
        Code.loadConst(1);
        Code.put(Code.store_1);

        int loopStart = Code.pc;

        // while (i < s[0])
        Code.put(Code.load_1);     // i
        Code.put(Code.load_n);     // s
        Code.loadConst(0);
        Code.put(Code.aload);      // s[0]
        Code.put(Code.jcc + Code.ge);
        int loopExit = Code.pc;
        Code.put2(0);

        // print s[i]
        Code.put(Code.load_n);     // s
        Code.put(Code.load_1);     // i
        Code.put(Code.aload);      // s[i]
        Code.loadConst(0);
        Code.put(Code.print);

        // ako (i + 1 >= s[0]) preskoci separator
        Code.put(Code.load_1);     // i
        Code.loadConst(1);
        Code.put(Code.add);        // i+1
        Code.put(Code.load_n);     // s
        Code.loadConst(0);
        Code.put(Code.aload);      // s[0]
        Code.put(Code.jcc + Code.ge);
        int skipSep = Code.pc;
        Code.put2(0);

        // print '\t' separator
        Code.loadConst('\t');
        Code.loadConst(0);
        Code.put(Code.bprint);

        Code.fixup(skipSep);

        // i++
        Code.put(Code.load_1);
        Code.loadConst(1);
        Code.put(Code.add);
        Code.put(Code.store_1);

        Code.putJump(loopStart);
        Code.fixup(loopExit);

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
    	int f = expr_designator.getDesignator().obj.getAdr();
		Obj arr = expr_designator.getDesignator1().obj;

		// 0 as start sum
		Code.loadConst(0);

		// load last index
		Code.load(arr);
		Code.put(Code.arraylength);
		
		// return here
		int begin = Code.pc;
		
		Code.loadConst(1);
		Code.put(Code.sub);
		Code.put(Code.dup);
		
		Code.loadConst(0);

		// jump if index < 0
		Code.putFalseJump(Code.ge, 0);
		
		int jmp = Code.pc - 2;
		
		Code.load(arr);
		
		Code.put(Code.dup2);
		
		Code.put(Code.pop);
		
		Code.put(Code.aload);
		
		Code.put(Code.call);
		Code.put2(f - Code.pc + 1);
		
		Code.put(Code.dup_x2);
		
		Code.put(Code.pop);
		
		Code.put(Code.dup_x2);
		
		Code.put(Code.pop);
		
		Code.put(Code.add);
		
		Code.put(Code.dup_x1);
		
		Code.put(Code.pop);
		
		Code.putJump(begin);
		
		Code.fixup(jmp);
		
		Code.put(Code.pop);
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
	     Code.loadConst('\n');
	     Code.loadConst(0);
	     Code.put(Code.bprint);
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
	
	@Override
	public void visit(FactorElement_bool factorElement_bool) {
	    // When visiting a boolean literal (true/false)
	    if (factorElement_bool.getB1().equals("true"))
	        Code.loadConst(1);  // true = 1
	    else
	        Code.loadConst(0);  // false = 0
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
