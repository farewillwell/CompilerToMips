package mid_end.llvm_ir;

import back_end.Mips.AsmInstrs.BlockSignAsm;
import back_end.Mips.MipsBuilder;
import mid_end.llvm_ir.Instrs.ReturnInstr;

import java.util.ArrayList;

public class BasicBlock extends Value {
    private final ArrayList<Instr> instrList;
    public final String name;
    public final String nameInMips;

    public BasicBlock() {
        super();
        this.name = IRBuilder.IB.getBasicBlockName();
        this.nameInMips = IRBuilder.IB.getNowFuncName() + name;
        instrList = new ArrayList<>();
    }

    public void addInstr(Instr instr) {
        instrList.add(instr);
    }

    public boolean lastInstrNotRet() {
        return instrList.size() == 0 || !(instrList.get(instrList.size() - 1) instanceof ReturnInstr);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(name).append(":\n");
        for (Instr instr : instrList) {
            stringBuilder.append("  ").append(instr.toString()).append("\n");
        }
        return stringBuilder.toString();
    }

    @Override
    public void genMipsCode() {
        new BlockSignAsm(nameInMips);
        for (Instr instr : instrList) {
            instr.genMipsCode();
        }
    }
}
