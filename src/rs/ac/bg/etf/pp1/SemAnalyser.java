package rs.ac.bg.etf.pp1;
//import java.util.HashSet;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

public class SemAnalyser extends VisitorAdaptor {

	private boolean errorDetected = false;
	private Obj currentProgram ;

	Logger log = Logger.getLogger(getClass());
	private Struct currentType;
	private int constant;
	private Struct constantType;
	
	/*
	private HashSet<String> setOfLabels = null;
	private HashSet<String> setOfGotoLabels = null;
	*/
	private Struct boolType = Tab.find("bool").getType();
	public static final Struct setType = Tab.find("set").getType();
	private Obj mainMethod = null;
	private Obj currentMethod;
	private boolean returnHappend;
	private int loopCnt = 0;
	int nVars;
	
	/* LOG MESSAGES */ 
	public void report_error(String message, SyntaxNode info) {
		errorDetected   = true;
		StringBuilder msg = new StringBuilder(message);
		int line = (info == null) ? 0: info.getLine();
		if (line != 0)
			msg.append (" na liniji ").append(line);
		log.error(msg.toString());
	}

	public void report_info(String message, SyntaxNode info) {
		StringBuilder msg = new StringBuilder(message); 
		int line = (info == null) ? 0: info.getLine();
		if (line != 0)
			msg.append (" na liniji ").append(line);
		log.info(msg.toString());
	}
	
	public boolean passed() {
		return !errorDetected;
	}
	
	
	/* SEMANTIC PASS CODE*/ 
	@Override
	public void visit(ProgramName programName) {
		currentProgram = Tab.insert(Obj.Prog, programName.getI1(),Tab.noType);
		Tab.openScope();
	}
	
	
	@Override
	public void visit(Program program) {
		nVars = Tab.currentScope.getnVars();
		Tab.chainLocalSymbols(currentProgram);
		Tab.closeScope();
		currentProgram = null;
		
		if (mainMethod == null || mainMethod.getLevel() > 0) {
			report_error("Ne postoji void main funkcija sa praznim parametrima" , program);
		}
		
	}
	
	
	/*CONST DECLARATIONS */
	@Override
	public void visit(ConstDecl constDecl) {
		Obj constObj = Tab.find(constDecl.getI1());
		if (constObj != Tab.noObj) {
			report_error("Dvostruka definicija konstante : " + constDecl.getI1() , constDecl);
		}
		else {
			if (constantType.assignableTo(currentType)) {
				constObj = Tab.insert(Obj.Con, constDecl.getI1(), currentType);
				constObj.setAdr(constant);
			}else {
				report_error("Neadekvatna dodela konstanti : " + constDecl.getI1(), constDecl);
			}
		}
		
	}
	
	@Override
	public void visit(Constant_number constant_number) {
		constant = constant_number.getN1();
		constantType =  Tab.intType;
	}
	
	@Override
	public void visit(Constant_char constant_char) {
		constant = constant_char.getC1();
		constantType = Tab.charType;
	}
	
	@Override
	public void visit(Constant_bool constant_bool) {
		constant = constant_bool.getB1();
		constantType = boolType;
	}
	
	
	/*VAR DECLARATIONS*/
	@Override 
	public void visit(VarDecl_var varDecl_var) {
		Obj varObj = null;
		if (currentMethod == null) /*/* && currentRecord == null*/ {
			varObj = Tab.find(varDecl_var.getI1());
		}
		else 
			varObj = Tab.currentScope().findSymbol(varDecl_var.getI1());
		if (varObj == null || varObj == Tab.noObj) {
			varObj = Tab.insert(Obj.Var, varDecl_var.getI1(), currentType);
			/*	if(currentRecord == null)
				varObj = Tab.insert(Obj.Var, varDecl_var.getI1(), currentType);
			else {
				varObj = Tab.insert(Obj.Fld, varDecl_var.getI1(), currentType);
				varObj.setLevel(2);
			}* OVO JE AKO DODAM REKORD DA MOGU DA SE POKLAPAJU FIELDOVI SA GLOBALNIM PROMENLJIVAMA  */
		}
		else {
			report_error("Dvostruka definicija promenljive : " + varDecl_var.getI1() , varDecl_var);
		}
	}
	
	@Override 
	public void visit(VarDecl_array varDecl_array) {
		Obj varObj = null;
		if (currentMethod == null) /* && currentRecord == null*/ {
			varObj = Tab.find(varDecl_array.getI1());
		}
		else 
			varObj = Tab.currentScope().findSymbol(varDecl_array.getI1());
		
		if (varObj == null || varObj == Tab.noObj) {
			varObj = Tab.insert(Obj.Var,varDecl_array.getI1(),new Struct(Struct.Array,currentType));
			/*if(currentRecord == null)
			varObj = Tab.insert(Obj.Var, varDecl_array.getI1(), new Struct(Struct.Array, currentType));
		else {
			varObj = Tab.insert(Obj.Fld, varDecl_array.getI1(), new Struct(Struct.Array, currentType));
			varObj.setLevel(2);
		} OVO JE AKO DODAM REKORD DA MOGU DA SE POKLAPAJU FIELDOVI SA GLOBALNIM PROMENLJIVAMA  */
		}
		else {
			report_error("Dvostruka definicija promenljive : " + varDecl_array.getI1() , varDecl_array);
		}
	}

	

	
	
	/*METHOD DECLARATIONS*/
	
	@Override
	public void visit(MethodReturnAndName_type methodReturnAndName_type) {
		methodReturnAndName_type.obj = currentMethod = Tab.insert(Obj.Meth,methodReturnAndName_type.getI2() , currentType);
		Tab.openScope();
		/*
		setOfLabels = new HashSet<>();
		setOfGotoLabels = new HashSet<>(); ZA LABELE*/
	}
	
	
	
	@Override
	public void visit(MethodReturnAndName_void methodReturnAndName_void) {
		methodReturnAndName_void.obj = currentMethod = Tab.insert(Obj.Meth,methodReturnAndName_void.getI1() , Tab.noType);
		Tab.openScope();
		if (methodReturnAndName_void.getI1().equalsIgnoreCase("main")) {
			mainMethod = currentMethod;
		}
		methodReturnAndName_void.obj = currentMethod;
		/*		
		mainMethod = currentMethod;
		setOfLabels = new HashSet<>();
		setOfGotoLabels = new HashSet<>();  OVO JE ZA LABELE*/
	}
	
	@Override
	public void visit(MethodDecl methodDecl) {
		Tab.chainLocalSymbols(currentMethod);
		Tab.closeScope();
		
		if(currentMethod.getType() != Tab.noType && !returnHappend ) {
			report_error("Ne postoji iskaz return unutar metode:" + currentMethod.getName(), methodDecl);
		}	
		
		returnHappend = false;
		currentMethod = null;
		/*
		if(!setOfLabels.containsAll(setOfGotoLabels))
			report_error("Postoji neadekvatan goto statement u tekucoj metodi: " + currentMethod.getName(), methodDecl);
		if(currentMethod.getType() != Tab.noType && !returnHappend)
			report_error("Ne postoji iskaz return unutar metode: " + currentMethod.getName(), methodDecl);
		
		setOfLabels = null;
		setOfGotoLabels = null;
		currentMethod = null;
		returnHappend = false;  ZA LABELE*/
	}
	
	/*FORMPAR DECLARATIONS*/
	@Override
	public void visit(FormPar_var formPar_var) {
		Obj varObj = null;
		if (currentMethod == null) {
			report_error("Semanticka greska : [FormPar_var]", formPar_var);
		}
		else 
			varObj = Tab.currentScope().findSymbol(formPar_var.getI2());
		
		if (varObj == null || varObj == Tab.noObj) {
			varObj = Tab.insert(Obj.Var, formPar_var.getI2(), currentType);
			varObj.setFpPos(1);
			currentMethod.setLevel(currentMethod.getLevel() + 1);
		}
		else {
			report_error("Dvostruka definicija formalnog parametra : " + formPar_var.getI2() , formPar_var);
		}
	}
	
	@Override
	public void visit(FormPar_array formPar_array) {
		Obj varObj = null;
		if (currentMethod == null) {
			report_error("Semanticka greska : [FormPar_array] ", formPar_array);
		}
		else 
			varObj = Tab.currentScope().findSymbol(formPar_array.getI2());
		
		if (varObj == null || varObj == Tab.noObj) {
			varObj = Tab.insert(Obj.Var, formPar_array.getI2(), new Struct(Struct.Array,currentType));
			varObj.setFpPos(1);
			currentMethod.setLevel(currentMethod.getLevel() + 1);
			//report_info("Koriscenje formalnog argumenta funkcije: " + formPar_array.getI2(), formPar_array);
		}
		else {
			report_error("Dvostruka definicija formalnog parametra : " + formPar_array.getI2(), formPar_array);
		}
	}
	
	
	
	
	
	
	/* RECORD DECLARATIONS */
	/*@Override
	public void visit(RecDeclName recDeclName) {
		Obj recObj = Tab.find(recDeclName.getI1());
		if(recObj != Tab.noObj) {
			report_error("Dvostruka definicija rekorda: " + recDeclName.getI1(), recDeclName);
		}
		else {
			currentRecord = new Struct(Struct.Class);
			recObj = Tab.insert(Obj.Type, recDeclName.getI1(), currentRecord);
			Tab.openScope();
		}
	}*/
	
	/*@Override
	public void visit(RecDecl recDecl) {
		Tab.chainLocalSymbols(currentRecord);
		Tab.closeScope();
		currentRecord = null;
	}*/
	


	
	
	@Override
	public void visit(Type type) {
		Obj typeObj = Tab.find(type.getI1());
		if (typeObj == Tab.noObj) {
			report_error("Nepostojeci tip podatka : " + type.getI1(), type);
			type.struct = currentType = Tab.noType;
		}
		else if(typeObj.getKind() != Obj.Type) {
			report_error("Neadekvatan tip podatka : " + type.getI1(), type);
			
		}
		else
			type.struct = currentType = typeObj.getType();
	}
	
	
	/*CONTEXT CONDITIONS*/
	//Designator
	public void visit(Designator_var designator_var) {
		Obj varObj = Tab.find(designator_var.getI1());
		if (varObj == Tab.noObj) {
			report_error("Pristup nedefinisanoj promeljivoj:" + designator_var.getI1(), designator_var);
			designator_var.obj = Tab.noObj;
		}else if(varObj.getKind() != Obj.Var && varObj.getKind() != Obj.Con && varObj.getKind() != Obj.Meth){
			designator_var.obj = Tab.noObj;
		}else{
			designator_var.obj = varObj;
			if(designator_var.obj.getLevel() == 0) { 
				if(designator_var.obj.getKind() == Obj.Con) {
					report_info("Pristup simbolickoj konstanti : " + designator_var.getI1() + "[Kind : " + designator_var.obj.getKind() + "]", designator_var);
					}
				else{
					report_info("Pristup globalnoj promeljivoj: " + designator_var.getI1() + "[Kind : " + designator_var.obj.getKind() + "]", designator_var);
					}
				}
			else
				if(designator_var.obj.getFpPos() == 1)
					report_info("Koriscenje formalnog parametra: " + designator_var.getI1() + "[Kind : " + designator_var.obj.getKind() + "]", designator_var);
				else
					report_info("Pristup lokalnoj promeljivoj: " + designator_var.getI1() + "[Kind : " + designator_var.obj.getKind() + "]", designator_var);
		}
	}
	
	public void visit(DesignatorArrayName designatorArrayName) { 
		Obj arrObj = Tab.find(designatorArrayName.getI1());
		if (arrObj == Tab.noObj) {
			report_error("Pristup nedefinisanoj promeljivoj niza :" + designatorArrayName.getI1(), designatorArrayName);
			designatorArrayName.obj = Tab.noObj;
		}else if(arrObj.getKind() != Obj.Var || arrObj.getType().getKind() != Struct.Array){
			report_error("Neadekvatna promenljiva niza :" + designatorArrayName.getI1(), designatorArrayName);
			designatorArrayName.obj = Tab.noObj;
		}else{
			designatorArrayName.obj = arrObj;
		}
	}
	
	public void visit(Designator_elem designator_elem) {
		Obj arrObj = designator_elem.getDesignatorArrayName().obj;
		if (arrObj == Tab.noObj)
			designator_elem.obj = arrObj;
		else if(!designator_elem.getExpr().struct.equals(Tab.intType)){
			report_error("Indeksiranje sa NE int vrednosti :" + designator_elem.getDesignatorArrayName().getI1(), designator_elem);
			designator_elem.obj = arrObj;
		}
		else {
			designator_elem.obj = new Obj(Obj.Elem,arrObj.getName() + "[$]",arrObj.getType().getElemType());
			report_info("Pristup elementu niza :" + arrObj.getName() + "[Kind: " + designator_elem.obj.getKind() + "]", designator_elem);
		}
	}
	
	//FactorElement
	@Override
	public void visit(FactorElement_character factorElement_character) {
		factorElement_character.struct = Tab.charType;
	}
	
	@Override
	public void visit(FactorElement_number factorElement_number) {
		factorElement_number.struct = Tab.intType;
	}
	
	@Override
	public void visit(FactorElement_bool factorElement_bool) {
		factorElement_bool.struct = boolType;
	}
	
	@Override
	public void visit(FactorElement_var factorElement_var) {
		factorElement_var.struct = factorElement_var.getDesignator().obj.getType();
	}
	
	@Override
	public void visit(FactorElement_new_array factorElement_new_array) {
	    if (!factorElement_new_array.getExpr().struct.equals(Tab.intType)) {
	        report_error("Velicina niza nije int tipa [FactorElement_new_array]", factorElement_new_array);
	        factorElement_new_array.struct = Tab.noType;
	    } else {
	        if (currentType == setType) {
	            factorElement_new_array.struct = setType;
	        } else {
	            factorElement_new_array.struct = new Struct(Struct.Array, currentType);
	        }
	    }
	}
	
	@Override
	public void visit(FactorElement_method factorElement_method) {
	    String methodName = factorElement_method.getDesignator().obj.getName();

	    // General method call checks
	    if (factorElement_method.getDesignator().obj.getKind() != Obj.Meth) {
	        report_error("Poziv neadekvatne metode: " + factorElement_method.getDesignator().obj.getName(), factorElement_method);
	        factorElement_method.struct = Tab.noType;
	    } else {
	        report_info("Poziv funkcije:" + factorElement_method.getDesignator().obj.getName() + "[Kind: " + Tab.find(factorElement_method.getDesignator().toString()).getKind() + "]", factorElement_method);
	        factorElement_method.struct = factorElement_method.getDesignator().obj.getType();
	        List<Struct> fpList = new ArrayList<>();
	        for (Obj local : factorElement_method.getDesignator().obj.getLocalSymbols()) {
	            if (local.getKind() == Obj.Var && local.getFpPos() == 1) {
	                fpList.add(local.getType());
	            }
	        }

	        ActParCounter apc = new ActParCounter();
	        factorElement_method.getActParList().traverseBottomUp(apc);
	        List<Struct> apList = apc.finalActParList;
	        //report_info("Fp list size:" + fpList.size(), factorElement_method);
	        //report_info("Ap list size:" + apList.size(), factorElement_method);
	        try {
	            if (fpList.size() != apList.size()) {
	                throw new Exception("Greska velicina");
	            }
	            for (int i = 0; i < fpList.size(); i++) {
	                Struct fps = fpList.get(i);
	                Struct aps = apList.get(i);
	                if (fps == setType && aps != setType) {
	                    report_error("Neodgovarajuci tipovi u pozivu funkcije: ocekuje se setType", factorElement_method);
	                } else if (fps != setType && aps == setType) {
	                    report_error("Neodgovarajuci tipovi u pozivu funkcije: ne ocekuje se setType", factorElement_method);
	                } else if (!fps.assignableTo(aps)) {
	                    report_error("Neodgovarajuci tipovi u pozivu funkcije", factorElement_method);
	                }
	            }
	        } catch (Exception e) {
	            report_error(e.getMessage(), factorElement_method);
	        }
	    }
	}
	
	
	/*@Override
	public void visit(FactorElement_new_record factorElement_new_record) {
		factorElement_new_record.struct = new Struct(Struct.Class,currentType.getMembersTable());
	}*/
	
	@Override
	public void visit(FactorElement_expr factorElement_expr) {
		factorElement_expr.struct = factorElement_expr.getExpr().struct;
	}
	
	//Factor
	@Override 
	public void visit(Factor_minus factor_minus) {
		if (factor_minus.getFactorElement().struct.equals(Tab.intType)) {
			factor_minus.struct = Tab.intType;
		}else {
			report_error("Negacija na NE int vrednost", factor_minus);
			factor_minus.struct = Tab.noType;
			}
	}
	@Override 
	public void visit(Factor_plus factor_plus) {
		factor_plus.struct = factor_plus.getFactorElement().struct;
	}
	
	
	//Expression
	
	@Override
	public void visit(MulopFactorList_factor mulopFactorList_factor) {
		mulopFactorList_factor.struct = mulopFactorList_factor.getFactor().struct;
	}
	
	@Override
	public void visit(MulopFactorList_mul mulopFactorList_mul) {
		Struct left = mulopFactorList_mul.getMulopFactorList().struct;
		Struct right = mulopFactorList_mul.getFactor().struct;
		if(left.equals(Tab.intType) && right.equals(Tab.intType)) {
			mulopFactorList_mul.struct = Tab.intType;
		}else {
			report_error("Mnozenje NE int vrednosti ",mulopFactorList_mul);
			mulopFactorList_mul.struct = Tab.noType;
		}
	}
	
	@Override
	public void visit(Term term) {
		term.struct = term.getMulopFactorList().struct;
	}
	
	@Override
	public void visit(AddopTermList_term addopTermList_term) {
		addopTermList_term.struct = addopTermList_term.getTerm().struct;
	}
	
	@Override
	public void visit(AddopTermList_add addopTermList_add) {
		Struct left = addopTermList_add.getAddopTermList().struct;
		Struct right = addopTermList_add.getTerm().struct;
		if(left.equals(Tab.intType) && right.equals(Tab.intType)) {
			addopTermList_add.struct = Tab.intType;
		}else {
			report_error("Dodavanje NE int vrednosti ",addopTermList_add);
			addopTermList_add.struct = Tab.noType;
		}
	}
	
	@Override
	public void visit(Expr_addop expr_addop) {
		expr_addop.struct = expr_addop.getAddopTermList().struct;
	}
	
	@Override
	public void visit(Expr_designator expr_designator) {
		Obj LeviDesignator = expr_designator.getDesignator().obj;
		Obj DesniDesignator = expr_designator.getDesignator1().obj;
		if(LeviDesignator.getKind() != Obj.Meth) {
			report_error("Designator sa leve strane mora biti metoda", expr_designator);
			expr_designator.struct = Tab.noType;
		}
		else{
			if(!LeviDesignator.getType().equals(Tab.intType)) {
				report_error("Designator sa leve strane MAP operatora mora biti metoda sa povratnom vrednoscu int ", expr_designator);
				expr_designator.struct = Tab.noType;
			}else {
				if(LeviDesignator.getLevel()!=1 ) {
					report_error("Designator sa leve strane MAP operatora mora biti metoda sa jednim parametrom tipa int", expr_designator);
					expr_designator.struct = Tab.noType;
				}else {
					for(Obj local: LeviDesignator.getLocalSymbols())
						if(local.getKind() == Obj.Var && local.getFpPos() == 1) {
							if( local.getType() != Tab.intType) {
								report_error("Designator sa leve strane MAP operatora mora biti metoda sa jednim parametrom tipa int", expr_designator);
								expr_designator.struct = Tab.noType;								
								break;
							}
							
						}
					
				}
			}
		}
		//(arrObj.getKind() != Obj.Var || arrObj.getType().getKind() != Struct.Array){
		if(DesniDesignator.getKind() != Obj.Var || DesniDesignator.getType().getKind()!=Struct.Array) {
				report_error("Designator sa desne strane MAP operatora mora biti tipa Array[]",expr_designator);
				expr_designator.struct = Tab.noType;
		}else {
			if(DesniDesignator.getType().getElemType() != Tab.intType) {
				report_error("Designator sa desne strane MAP operatora mora biti tipa intArray []",expr_designator);
				expr_designator.struct = Tab.noType;
			}else {
				if(DesniDesignator.getType() == setType) {
					report_error("Designator sa desne strane MAP operatora ne sme biti tipa set,vec tipa intArray []",expr_designator);
					expr_designator.struct = Tab.noType;
				}
				report_info("", expr_designator);
			}
		}
		expr_designator.struct = Tab.intType;
	}
	
	
	//Designator statements
	@Override
	public void visit(DesignatorStatement_assign designatorStatement_assign) {
	    Struct destType = designatorStatement_assign.getDesignator().obj.getType();
	    Struct srcType = designatorStatement_assign.getExpr().struct;

	    if (destType == setType && srcType != setType) {
	        report_error("Ne moze se dodeliti vrednost koja nije tipa setType", designatorStatement_assign);
	    } else if (destType != setType && srcType == setType) {
	        report_error("Ne moze se dodeliti vrednost tipa setType", designatorStatement_assign);
	    } else {
	        // Normal assignment check
	        if (!destType.assignableTo(srcType)) {
	            report_error("Neodgovarajuci tipovi u dodeli vrednosti", designatorStatement_assign);
	        }
	    }
	}
	
	@Override
	public void visit(DesignatorStatement_assign_designator designatorStatement_assign_designator) {
	    Struct destType = designatorStatement_assign_designator.getDesignator().obj.getType();
	    Struct src1Type = designatorStatement_assign_designator.getDesignator1().obj.getType();
	    Struct src2Type = designatorStatement_assign_designator.getDesignator2().obj.getType();
	    if (destType == setType && src1Type == setType && src2Type == setType) {
	    	//report_info("ALL GOOD",designatorStatement_assign_designator);
	    } else {
	        // Normal assignment check
	        report_error("Neodgovarajuci tipovi u dodeli vrednosti,svi tipovi designatora moraju da budu Setovi", designatorStatement_assign_designator);
	    }
	}
	
	@Override
	public void visit(DesignatorStatement_incr designatorStatement_incr) {
		int kind = designatorStatement_incr.getDesignator().obj.getKind();
		if( kind != Obj.Var && kind != Obj.Elem) {
			report_error("Inkrement neadekvatne promeljive: " + designatorStatement_incr.getDesignator().obj.getName(), designatorStatement_incr);
		}
		else if (!designatorStatement_incr.getDesignator().obj.getType().equals(Tab.intType)){
			report_error("Inkrement Ne int promenljive: " + designatorStatement_incr.getDesignator().obj.getName(), designatorStatement_incr);
		} 
	}
	
	
	@Override
	public void visit(DesignatorStatement_decr designatorStatement_decr) {
		int kind = designatorStatement_decr.getDesignator().obj.getKind();
		if( kind != Obj.Var && kind != Obj.Elem ) {
			report_error("Dekrement neadekvatne promeljive: " + designatorStatement_decr.getDesignator().obj.getName(), designatorStatement_decr);
		}
		else if (!designatorStatement_decr.getDesignator().obj.getType().equals(Tab.intType)){
			report_error("Dekrement Ne int promenljive: " + designatorStatement_decr.getDesignator().obj.getName(), designatorStatement_decr);
		} 
	}
	
	@Override
	public void visit(DesignatorStatement_meth designatorStatement_meth) {
		String methodName = designatorStatement_meth.getDesignator().obj.getName();
		if( designatorStatement_meth.getDesignator().obj.getKind() != Obj.Meth) {
			report_error("Poziv neadekvatne metode: " + designatorStatement_meth.getDesignator().obj.getName(), designatorStatement_meth);
		}
		else {
			
			
	        report_info("Poziv funkcije: " + designatorStatement_meth.getDesignator().obj.getName() + "[Kind: " + designatorStatement_meth.getDesignator().obj.getKind() + "]", designatorStatement_meth);
			List<Struct> fpList = new ArrayList<>();
			for(Obj local: designatorStatement_meth.getDesignator().obj.getLocalSymbols())
				if(local.getKind() == Obj.Var && local.getLevel() == 1 && local.getFpPos() == 1)
					fpList.add(local.getType());
			
			ActParCounter apc = new ActParCounter();
			designatorStatement_meth.getActParList().traverseBottomUp(apc);
			List<Struct> apList = apc.finalActParList;
			//report_info("Fp list size:" + fpList.size() + fpList.toString() , designatorStatement_meth);
			//report_info("Ap list size:" + apList.size() + apList.toString(), designatorStatement_meth);
			//report_info("Fp list size:" + fpList.size() + fpList.toString() , designatorStatement_meth);
			//report_info("Ap list size:" + apList.size() + apList.toString(), designatorStatement_meth);
			try {
				
				if(fpList.size() != apList.size())
					throw new Exception("Greska velicina");
				for(int i = 0 ; i < fpList.size(); i++) {
					Struct fps = fpList.get(i);
					Struct aps = apList.get(i);
					if (fps == setType && aps != setType) {
	                    report_error("Neodgovarajuci tipovi u pozivu funkcije: ocekuje se setType", designatorStatement_meth);
	                } else if (fps != setType && aps == setType) {
	                    report_error("Neodgovarajuci tipovi u pozivu funkcije: ne ocekuje se setType", designatorStatement_meth);
	                } else if (!fps.assignableTo(aps)) {
	                    report_error("Neodgovarajuci tipovi u pozivu funkcije", designatorStatement_meth);
	                }
				}
			}catch(Exception ex) {
				report_error("[" + ex.getMessage() + "] Nekompatibilnost parametara pri pozivu metode: " + designatorStatement_meth.getDesignator().obj.getName(), designatorStatement_meth);
			}
		}
	}
	
	
	//Statements
	
	
	/*	@Override
	public void visit(Label label) {
		if(!setOfLabels.add(label.getI1()))
			report_error("Dvostruka definicija labele: " + label.getI1(), label);
	} 
		@Override
	public void visit(SingleStatement_goto singleStatement_goto) {
		setOfGotoLabels.add(singleStatement_goto.getI1());
	} 
	
	ZA LABELE */
	
	@Override
	public void visit(Statement_read statement_read) {
		int kind = statement_read.getDesignator().obj.getKind();
		Struct type = statement_read.getDesignator().obj.getType();
		if(kind != Obj.Var && kind != Obj.Elem) {
			report_error("Read neadekvatne promeljive :" + statement_read.getDesignator().obj.getName(), statement_read);	
		}else if(!type.equals(Tab.intType) && !type.equals(Tab.charType) && !type.equals(boolType)) {
			report_error("Read ne Int/Char/Bool promeljive : " + statement_read.getDesignator().obj.getName(), statement_read);
		}
	}
	
	@Override
	public void visit(Statement_print statement_print) {
		Struct type = statement_print.getExpr().struct;
		if(!type.equals(Tab.intType) && !type.equals(Tab.charType) && !type.equals(boolType) && type != setType)
			report_error("Print operacija ne int/char/bool izraza", statement_print);
	}
	
	@Override
	public void visit(Statement_print_number statement_print_number) {
		Struct type = statement_print_number.getExpr().struct;
		if(!type.equals(Tab.intType) && !type.equals(Tab.charType) && !type.equals(boolType))
			report_error("Print operacija ne int/char/bool izraza", statement_print_number);
	}
	
	
	@Override
	public void visit(Statement_return_void statement_return_void) {
		returnHappend = true;
		if(currentMethod.getType() != Tab.noType) {
			report_error("Dogodio se nevalidan return u metodi " + currentMethod.getName(), statement_return_void);
		}
	}
	
	@Override
	public void visit(Statement_return_expr statement_return_expr) {
		returnHappend = true;
		if(!currentMethod.getType().equals(statement_return_expr.getExpr().struct)) {
			report_error("Dogodio se nevalidan return u metodi " + currentMethod.getName(), statement_return_expr);
		}
	}
	
	@Override
	public void visit(DoNonTerm doNonTerm) {
		loopCnt++;
	}
	
	@Override
	public void visit(Statement_do_empty statement_do_empty) {
		loopCnt--;
	}
	
	@Override
	public void visit(Statement_do_condition statement_do_condition) {
		loopCnt--;
	}
	
	@Override
	public void visit(Statement_do_condition_designator statement_do_condition_designator) {
		loopCnt--;
	}
	
	@Override
	public void visit(Statement_continue statement_continue) {
		if( loopCnt == 0 ) {
			report_error("Break naredba se ne nalazi unutar tela petlje ", statement_continue);
		}
	}
	
	@Override
	public void visit(Statement_break statement_break) {
		if( loopCnt == 0 ) {
			report_error("Break naredba se ne nalazi unutar tela petlje ", statement_break);
		}
	}
	
	//Condition
	@Override
	public void visit(CondFact_expr condFact_expr) {
		if(!condFact_expr.getExpr().struct.equals(boolType)) {
			report_error("Logicki operand nije tipa bool.", condFact_expr);
			condFact_expr.struct = Tab.noType;
		}
		else {
			condFact_expr.struct = boolType;
		}
	}
	
	@Override
	public void visit(CondFact_expr_r_expr condFact_expr_r_expr) {
		Struct left = condFact_expr_r_expr.getExpr().struct;
		Struct right = condFact_expr_r_expr.getExpr1().struct;
		if(left.compatibleWith(right)) {
			if(left.isRefType() || right.isRefType()) { // isRefType vraca da kki je nesto ili klasa ili array
				if(condFact_expr_r_expr.getRelop() instanceof Relop_eq || condFact_expr_r_expr.getRelop() instanceof Relop_ne)
					condFact_expr_r_expr.struct = boolType;
				else {
					report_error("Poredjenje ref tipova sa ne adekvatnim relacionim operatorom.", condFact_expr_r_expr);
					condFact_expr_r_expr.struct = Tab.noType;
				}
			}
			else
				condFact_expr_r_expr.struct = boolType;
		}
		else {
			report_error("Logicki operandi nisu kompatibilni.", condFact_expr_r_expr);
			condFact_expr_r_expr.struct = Tab.noType;
		}
	}
	
	@Override
	public void visit(CondFactList_cf condFactList_cf) {
		condFactList_cf.struct = condFactList_cf.getCondFact().struct;
	}
	
	@Override
	public void visit(CondFactList_and condFactList_and) {
		Struct left = condFactList_and.getCondFactList().struct;
		Struct right = condFactList_and.getCondFact().struct;
		if(left.equals(boolType) && right.equals(boolType))
			condFactList_and.struct = boolType;
		else {
			report_error("And operacija ne bool vrednosti.", condFactList_and);
			condFactList_and.struct = Tab.noType;
		}
	}
	
	@Override
	public void visit(CondTerm condTerm) {
		condTerm.struct = condTerm.getCondFactList().struct;
	}
	
	@Override
	public void visit(CondTermList_ct condTermList_ct) {
		condTermList_ct.struct = condTermList_ct.getCondTerm().struct;
	}
	
	@Override
	public void visit(CondTermList_or condTermList_or) {
		Struct left = condTermList_or.getCondTermList().struct;
		Struct right = condTermList_or.getCondTerm().struct;
		if(left.equals(boolType) && right.equals(boolType))
			condTermList_or.struct = boolType;
		else {
			report_error("Or operacija ne bool vrednosti.", condTermList_or);
			condTermList_or.struct = Tab.noType;
		}
	}
	
	@Override
	public void visit(Condition condition) {
		condition.struct = condition.getCondTermList().struct;
		if(!condition.struct.equals(boolType))
			report_error("Uslov nije tipa bool.", condition);
	}
	
	
/*
	Semanticka obrada:

	Implementacija:
		addToLocals,openScope,closeScope,setLocals
		CHAINLOCALSYMBOLS 
		insert - dodaje na kraj scope-a,automatski se level polje postavlja,field mora sam da se namesta
		find - trazi po scope-ovima 
		Tab.currentScope.findSymbol()-vraca null umesto NoObj		
		report_error i report_info 
		
		U struct classi:
		equals: da li su 2 strukturna cvora ekvivalentna (moze i array-ove da poredi,tacnije njihove elemente)
		compatible with: za logicka poredjenja( <,>,<=,>=,==,!=)
		assignableTo: da li je nesto dodeljivo u nesto Src.assignableTo(Dest) 


1. Scope node
	Parent (ref. Scope): - pokazivac na Scope oca, okruzujuci ospeg
	Locals (ref. Obj): - pokazivac na listu Object cvorova koji se nalaze u istom opsegu
	nVars  (int): - broj Var promenljivih u scope-u
	
2. Obj node
	Kind:
		Var		(int a, c[])
		Meth	(void main() {})
		Con		(const int b = 5)
		Type	(int)
		Field	(rec a { int b; })
		Prog	(program Test)
		Elem	(c[5]) // ne ide u tabelu simbola
	Name:
		... // ne sme da se ponovi u jednom opsegu
	Type (ref. Struct):
		Var,Con,Field,Elem - tip promenljive (prost ili slozen)
		Meth - povratni tip metode
		Type - structurni cvor za taj tip
	Next (ref. Object):
		... ulancavanje u listu
	Adr:
		Var, Field - offset od pocetka scope-a (inc)
		Con - vrednost konstante
		#4 Meth - adresa pocetka metode (trenutni PC)
	Level:
		Var, Con - 0 (globalna) ili 1 (lokalna)
		Field - 2
		Meth - broj formalnih parametara
	fpPos:
		Var - 1 (formalni parametar metode)
	Locals (ref. Obj):
		pokazivac na sve objektne cvorove ovog scope-a
		Prog, Meth
		
3. Struct node
	Kind:
		None (void)
		Int
		Char
		Bool
		Array
		Class
		Enum
		Interface
	Elem type (ref. Struct):
		pozivac na structurni cvor odredjenog tipa elementa niza
	N:
		broj polja klase
	Fields(Members) (ref. Obj):
		Lolals za klasu
		Type*/
	
}
