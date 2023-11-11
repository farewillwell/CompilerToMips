package front_end.AST.Var;

import front_end.AST.Exp.ConstExp;
import front_end.AST.Node;
import front_end.AST.TokenNode;
import front_end.ErrUseSymbols.ErrUseSymbolManager;
import front_end.ErrorCollector;
import mid_end.llvm_ir.*;
import mid_end.llvm_ir.Instrs.AllocInstr;
import mid_end.llvm_ir.Instrs.StoreInstr;
import mid_end.llvm_ir.type.ArrayType;
import mid_end.llvm_ir.type.BaseType;
import mid_end.llvm_ir.type.LLVMType;
import mid_end.llvm_ir.type.PointerType;
import mid_end.symbols.SymbolManager;
import mid_end.symbols.VarSymbol;

import java.util.ArrayList;

public class VarDef extends Node {

    private final TokenNode indent;
    public final ArrayList<ConstExp> constExps;
    private InitVal initVal;

    public VarDef(TokenNode indent) {
        this.indent = indent;
        constExps = new ArrayList<>();
        initVal = null;
    }

    public String getName() {
        return indent.content();
    }

    public int getDim() {
        return constExps.size();
    }

    public void addConstExps(ConstExp constExp) {
        constExps.add(constExp);
    }

    public void setInitVal(InitVal initVal) {
        this.initVal = initVal;
    }

    @Override
    public void show() {
        super.show();
        indent.show();
        for (ConstExp constExp : constExps) {
            System.out.println("LBRACK [");
            constExp.show();
            System.out.println("RBRACK ]");
        }
        if (initVal != null) {
            System.out.println("ASSIGN =");
            initVal.show();
        }
        System.out.println("<VarDef>");
    }

    @Override
    public void checkError(ErrorCollector errorCollector) {
        super.checkError(errorCollector);
        super.checkError(errorCollector);
        if (ErrUseSymbolManager.SM.blockHasDefineVar(indent.content())) {
            errorCollector.addError(indent.getLine(), "b");
        } else {
            ErrUseSymbolManager.SM.newDeclareVar(this);
        }
        if (constExps.size() > 1) {
            constExps.get(1).checkError(errorCollector);
        }
        if (initVal != null) {
            initVal.checkError(errorCollector);
        }
    }
    @Override
    public Value getIRCode() {
        LLVMType llvmType;
        if (constExps.size() == 0) {
            llvmType = BaseType.I32;
        } else if (constExps.size() == 1) {
            llvmType = new ArrayType(constExps.get(0).queryValue(), BaseType.I32);
        } else {
            llvmType = new ArrayType(constExps.get(0).queryValue(),
                    new ArrayType(constExps.get(1).queryValue(), BaseType.I32));
        }
        if (SymbolManager.SM.isGlobal()) {
            GlobalVar globalVar;
            if (initVal != null) {
                Initial initial = (Initial) initVal.getIRCode();
                globalVar = new GlobalVar(indent.content(), llvmType, initial, false);
            } else {
                globalVar = new GlobalVar(indent.content(), new PointerType(llvmType), new Initial(), false);
            }
            IRBuilder.IB.moduleAddGlobal(globalVar);
            VarSymbol varSymbol = new VarSymbol(indent.content(), globalVar);
            SymbolManager.SM.addVarSymbol(varSymbol);
        } else {
            AllocInstr allocInstr = new AllocInstr(llvmType, false);
            IRBuilder.IB.addInstrForBlock(allocInstr);
            VarSymbol varSymbol = new VarSymbol(indent.content(), allocInstr.getAns());
            if (initVal != null) {
                Value value = initVal.getIRCode();
                StoreInstr storeInstr = new StoreInstr(value, allocInstr.getAns());
                IRBuilder.IB.addInstrForBlock(storeInstr);
            } else {
                StoreInstr storeInstr = new StoreInstr(new Constant(0), allocInstr.getAns());
                IRBuilder.IB.addInstrForBlock(storeInstr);
            }
            SymbolManager.SM.addVarSymbol(varSymbol);
        }
        /*符号一定要再已经完成声明后，走完全部流程（包括初始化）再放入符号表*/
        return null;
    }
}
