package front_end.AST.Var;

import front_end.AST.Node;
import front_end.AST.TokenNode;
import front_end.AST.Exp.ConstExp;
import front_end.ErrUseSymbols.ErrUseSymbolManager;
import front_end.ErrorCollector;
import mid_end.llvm_ir.*;
import mid_end.llvm_ir.Instrs.AllocInstr;
import mid_end.llvm_ir.Instrs.GEPInstr;
import mid_end.llvm_ir.Instrs.StoreInstr;
import mid_end.llvm_ir.type.ArrayType;
import mid_end.llvm_ir.type.BaseType;
import mid_end.llvm_ir.type.LLVMType;
import mid_end.llvm_ir.type.PointerType;
import mid_end.symbols.SymbolManager;
import mid_end.symbols.VarSymbol;

import java.util.ArrayList;

public class ConstDef extends Node {
    private final TokenNode indent;
    public final ArrayList<ConstExp> constExps;
    public ConstInitVal constInitVal;

    public ConstDef(TokenNode indent) {
        this.indent = indent;
        constExps = new ArrayList<>();
        constInitVal = null;
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

    public void setConstInitVal(ConstInitVal constInitVal) {
        this.constInitVal = constInitVal;
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
        System.out.println("ASSIGN =");
        constInitVal.show();
        System.out.println("<ConstDef>");
    }

    @Override
    public void checkError(ErrorCollector errorCollector) {
        super.checkError(errorCollector);
        if (ErrUseSymbolManager.SM.blockHasDefineVar(indent.content())) {
            errorCollector.addError(indent.getLine(), "b");
        } else if (ErrUseSymbolManager.SM.isGlobal()
                && ErrUseSymbolManager.SM.hasDefineFunc(indent.content())) {
            // 如果是全局变量并且有同名函数，报错
            errorCollector.addError(indent.getLine(), "b");
        } else {
            ErrUseSymbolManager.SM.newDeclareConst(this);
        }
        if (constExps.size() > 1) {
            constExps.get(1).checkError(errorCollector);
        }
        constInitVal.checkError(errorCollector);
    }

    // TODO 数组的局部赋值
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
            if (constExps.size() == 0) {
                llvmType = BaseType.I32;
            } else if (constExps.size() == 1) {
                llvmType = new ArrayType(constExps.get(0).queryValue(), BaseType.I32);
            } else {
                llvmType = new ArrayType(constExps.get(0).queryValue(),
                        new ArrayType(constExps.get(1).queryValue(), BaseType.I32));
            }
            Initial initial = (Initial) constInitVal.getIRCode();
            GlobalVar globalVar = new GlobalVar(indent.content(), new PointerType(llvmType), initial, true);
            VarSymbol varSymbol = new VarSymbol(indent.content(), globalVar);
            IRBuilder.IB.moduleAddGlobal(globalVar);
            SymbolManager.SM.addVarSymbol(varSymbol);
        } else {
            AllocInstr allocInstr = new AllocInstr(llvmType, true);
            IRBuilder.IB.addInstrForBlock(allocInstr);
            ((LocalVar) allocInstr.getAns()).setConstInitial(constInitVal.getInit());
            // 给 const 赋初值,这个在编译器里用的,不体现在生成的代码里面
            VarSymbol varSymbol = new VarSymbol(indent.content(), allocInstr.getAns());
            if (getDim() == 0) {
                Value value = constInitVal.getIRCode();
                StoreInstr storeInstr = new StoreInstr(value, allocInstr.getAns());
                IRBuilder.IB.addInstrForBlock(storeInstr);
            } else if (getDim() == 1) {
                for (int i = 0; i < constExps.get(0).queryValue(); i++) {
                    GEPInstr gepInstr = new GEPInstr(allocInstr.getAns(), new Constant(i));
                    IRBuilder.IB.addInstrForBlock(gepInstr);
                    StoreInstr storeInstr = new StoreInstr(constInitVal.getIrByIndex(i), gepInstr.getAns());
                    IRBuilder.IB.addInstrForBlock(storeInstr);
                }
            } else {
                for (int i = 0; i < constExps.get(0).queryValue(); i++) {
                    GEPInstr gepInstr = new GEPInstr(allocInstr.getAns(), new Constant(i));
                    IRBuilder.IB.addInstrForBlock(gepInstr);
                    for (int j = 0; j < constExps.get(1).queryValue(); j++) {
                        GEPInstr insideGepInstr = new GEPInstr(gepInstr.getAns(), new Constant(j));
                        IRBuilder.IB.addInstrForBlock(insideGepInstr);
                        StoreInstr storeInstr =
                                new StoreInstr(constInitVal.getIrByIndex(i, j), insideGepInstr.getAns());
                        IRBuilder.IB.addInstrForBlock(storeInstr);
                    }
                }
            }
            SymbolManager.SM.addVarSymbol(varSymbol);
        }
        /*TODO 已经实现，但是符号一定要整个声明完成后再被放入符号表*/
        return null;
    }
}
