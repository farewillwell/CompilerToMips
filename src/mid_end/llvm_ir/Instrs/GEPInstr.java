package mid_end.llvm_ir.Instrs;

import mid_end.llvm_ir.Instr;
import mid_end.llvm_ir.LocalVar;
import mid_end.llvm_ir.Value;
import mid_end.llvm_ir.type.PointerType;
import mid_end.symbols.SymbolManager;

public class GEPInstr extends Instr {

    public GEPInstr(String name,Value offset1,Value offset2)
    {
        Value value = SymbolManager.SM.getVarSymbol(name).value;
        addValue(offset1);
        addValue(offset2);
        addValue(new LocalVar(new PointerType(value.type.getElementType()),false));
    }
}
