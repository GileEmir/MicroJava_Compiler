package rs.ac.bg.etf.pp1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import java_cup.runtime.*;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import rs.ac.bg.etf.pp1.ast.*;
import rs.ac.bg.etf.pp1.util.Log4JUtils;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.*;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

public class Compiler {

	static {
		DOMConfigurator.configure(Log4JUtils.instance().findLoggerConfigFile());
		Log4JUtils.instance().prepareLogFile(Logger.getRootLogger());
	}
	


	
	public static void main(String[] args) throws Exception {
		
		Logger log = Logger.getLogger(Compiler.class);
		
		Reader br = null;
		try {
			File sourceCode = new File("test/program.mj");
			log.info("Compiling source file: " + sourceCode.getAbsolutePath());
			
			br = new BufferedReader(new FileReader(sourceCode));
			Yylex lexer = new Yylex(br);
			
			/*Formiranje AST*/
			MJParser p = new MJParser(lexer);
	        Symbol s = p.parse();  
	        
	        Program prog = (Program)(s.value); 
	        
			/* Ispis AST /*
			log.info(prog.toString(""));
			log.info("=====================================================================");
			
			/* Inicijalizacija tabele simbola */
			Tab.init();
			Struct boolType = new Struct(Struct.Bool);
			Obj boolObj = Tab.insert(Obj.Type, "bool", boolType);
			boolObj.setAdr(-1);
			boolObj.setLevel(-1);
			
			Struct setType = new Struct(Struct.Array,Tab.intType);
			Obj setObj = Tab.insert(Obj.Type, "set", setType);
			setObj.setAdr(-1);
			setObj.setLevel(-1);
			
			Obj addObj = Tab.insert(Obj.Meth, "add", Tab.noType);
            addObj.setAdr(0);
            addObj.setLevel(2); // Set the level to 1 for one parameter
            {
                Tab.openScope();
                Obj a = Tab.insert(Obj.Var, "a", setType);
                a.setFpPos(1);
                a.setLevel(1);
                Obj b =  Tab.insert(Obj.Var, "b", Tab.intType);
                b.setFpPos(1);
                b.setLevel(1);
                addObj.setLocals(Tab.currentScope().getLocals());
                Tab.closeScope();
            }
            
            Obj addAllObj = Tab.insert(Obj.Meth, "addAll", Tab.noType);
            addAllObj.setAdr(0);
            addAllObj.setLevel(2); // Set the level to 1 for one parameter
            {
                Tab.openScope();
                Obj a = Tab.insert(Obj.Var, "a", setType);
                a.setFpPos(1);
                a.setLevel(1);
                Struct b = new Struct(Struct.Array,Tab.intType);
                Obj bObj = Tab.insert(Obj.Var, "b", b);
                bObj.setFpPos(1);
                bObj.setLevel(1);
                addAllObj.setLocals(Tab.currentScope().getLocals());
                Tab.closeScope();
            }
			
			
			
			List<String> uni_meths = new ArrayList<>();
			uni_meths.add("chr");
			uni_meths.add("ord");
			uni_meths.add("len");
			for (String meth: uni_meths)
				for(Obj fp: Tab.find(meth).getLocalSymbols())
					fp.setFpPos(1);
			
			/*Semanticka analiza */
			log.info("=====================================================================");
			SemAnalyser sa = new SemAnalyser();
			prog.traverseBottomUp(sa);
			

			
			/*Ispis tabele simbola*/
			log.info("=====================================================================");
			Tab.dump();
			
			
			if(!p.errorDetected && sa.passed()){
				/*generisanje koda*/
				File objFile = new File("test/program.obj");
				if(objFile.exists()) objFile.delete();
				
				CodeGenerator codeGenerator = new CodeGenerator();
				prog.traverseBottomUp(codeGenerator);
				Code.dataSize = sa.nVars;
				Code.mainPc = codeGenerator.getMainPc();
				Code.write(new FileOutputStream(objFile));
				
				log.info("Generisanje  uspesno zavrseno!");
			}else{
				log.error("Parsiranje NIJE uspesno zavrseno!");
			}
			
		} 
		finally {
			if (br != null) try { br.close(); } catch (IOException e1) { log.error(e1.getMessage(), e1); }
		}

	}
	
	
}