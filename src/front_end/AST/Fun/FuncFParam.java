package front_end.AST.Fun;

import front_end.AST.Exp.ConstExp;
import front_end.AST.Node;
import front_end.AST.TokenNode;
import front_end.ErrUseSymbols.ErrUseSymbolManager;
import front_end.ErrorCollector;
import front_end.RW;
import mid_end.llvm_ir.IRBuilder;
import mid_end.llvm_ir.Instrs.AllocIr;
import mid_end.llvm_ir.Instrs.StoreIr;
import mid_end.llvm_ir.Param;
import mid_end.llvm_ir.Value;
import mid_end.llvm_ir.type.ArrayType;
import mid_end.llvm_ir.type.BaseType;
import mid_end.llvm_ir.type.LLVMType;
import mid_end.llvm_ir.type.PointerType;
import mid_end.symbols.SymbolManager;
import mid_end.symbols.VarSymbol;

import java.util.ArrayList;

public class FuncFParam extends Node {
    private final RW.TYPE type;
    public final TokenNode tokenNode;
    public final ArrayList<ConstExp> constExps;

    public final boolean hasBrack;

    public FuncFParam(RW.TYPE type,TokenNode tokenNode, boolean hasBrack) {
        this.type=type;
        this.tokenNode = tokenNode;
        this.hasBrack = hasBrack;
        constExps = new ArrayList<>();
    }

    public void addConstExp(ConstExp constExp) {
        constExps.add(constExp);
    }

    @Override
    public void show() {
        super.show();
        System.out.println("INTTK int");
        tokenNode.show();
        if (hasBrack) {
            System.out.println("LBRACK [\n" +
                    "RBRACK ]");
            for (ConstExp constExp : constExps) {
                System.out.println("LBRACK [");
                constExp.show();
                System.out.println("RBRACK ]");
            }
        }
        System.out.println("<FuncFParam>");
    }

    public int getDim() {
        if (!hasBrack) return 0;
        else {
            if (constExps.size() == 0) {
                return 1;
            } else {
                return 2;
            }
        }
    }

    public int getSecondLength() {
        return constExps.get(0).evaluate();
    }


    @Override
    public void checkError(ErrorCollector errorCollector) {
        if (ErrUseSymbolManager.SM.blockHasDefineVar(tokenNode.content())) {
            errorCollector.addError(tokenNode.getLine(), "b");
        } else if (ErrUseSymbolManager.SM.isGlobal()
        &&ErrUseSymbolManager.SM.hasDefineFunc(tokenNode.content())) {
            // 如果是全局变量并且有同名函数，报错
            errorCollector.addError(tokenNode.getLine(), "b");
        } else {
            ErrUseSymbolManager.SM.newDeclarePara(this);
            for (ConstExp constExp : constExps) {
                constExp.checkError(errorCollector);
            }
        }
    }

    @Override
    public Value getIRCode() {
        LLVMType llvmType;
        if (!hasBrack) {
            llvmType = BaseType.I32;
        } else {
            if (constExps.size() == 0) {
                llvmType = new PointerType(BaseType.I32);
            } else {
                llvmType = new PointerType(new ArrayType(constExps.get(0).queryValue(), BaseType.I32));
            }
        }
        Param param = new Param(llvmType);
        AllocIr allocIr = new AllocIr(llvmType, false);
        IRBuilder.IB.addInstrForBlock(allocIr);
        VarSymbol varSymbol = new VarSymbol(tokenNode.content(), allocIr.getAns());
        SymbolManager.SM.addVarSymbol(varSymbol);
        StoreIr storeIr = new StoreIr(param, allocIr.getAns());
        IRBuilder.IB.addInstrForBlock(storeIr);
        return param;
    }
}
