package mid_end.llvm_ir;

import java.util.ArrayList;

public class BasicBlock extends Value {
    private final ArrayList<Instr> instrList;
    public final String name;

    public BasicBlock() {
        super();
        this.name = IRBuilder.IB.getBasicBlockName();
        instrList = new ArrayList<>();
    }

    public void addInstr(Instr instr) {
        instrList.add(instr);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        // substring 1 : without %
        stringBuilder.append(name.substring(1)).append(":\n");
        for (Instr instr : instrList) {
            stringBuilder.append("  ").append(instr.toString()).append("\n");
        }
        return stringBuilder.toString();
    }
}
