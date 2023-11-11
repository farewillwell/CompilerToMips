package mid_end.llvm_ir.Instrs;

import mid_end.llvm_ir.GlobalVar;
import mid_end.llvm_ir.Instr;
import mid_end.llvm_ir.LocalVar;
import mid_end.llvm_ir.type.LLVMType;
import mid_end.symbols.SymbolManager;
import mid_end.symbols.VarSymbol;

public class LoadInstr extends Instr {

    public LoadInstr(String name, LLVMType type) {
        super(type);
        VarSymbol varSymbol = SymbolManager.SM.getVarSymbol(name);
        addValue(varSymbol.value);
        addValue(new LocalVar(type, false));
    }

    @Override
    public String toString() {
        String from = paras.get(0).type +
                " " + paras.get(0).toString();
        return getAns().toString() + " = load " + type.toString() + ", " + from;
    }
}
