package mid_end.llvm_ir;

import java.util.ArrayList;

public class BasicBlock extends Value {
    private final ArrayList<Instr> instrList;

    public BasicBlock() {
        super();
        instrList = new ArrayList<>();
    }

    public void addInstr(Instr instr) {
        instrList.add(instr);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Instr instr : instrList) {
            stringBuilder.append(instr.toString()).append("\n");
        }
        return stringBuilder.toString();
    }
}
