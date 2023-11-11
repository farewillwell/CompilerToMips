package mid_end.llvm_ir;

import mid_end.llvm_ir.type.LLVMType;

public class Instr extends User {

    public Instr(LLVMType type) {
        super(type);
    }

    public Instr() {
        super();
    }

    public Value getAns() {
        if (paras.size() == 0) {
            throw new RuntimeException("got a none value in user");
        }
        return paras.get(paras.size() - 1);
    }
}
