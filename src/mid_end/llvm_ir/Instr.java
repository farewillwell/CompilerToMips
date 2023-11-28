package mid_end.llvm_ir;

import back_end.Mips.AsmInstrs.AnnotationAsm;
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

    @Override
    public void genMipsCode() {
        new AnnotationAsm(toString());
        // 一定要分清楚，全局和开在栈上的取值方式是不一样的!!!
    }

    // 将第x个参数换成value
    // 例如value = add 1+2 可以把这个参数删掉.

    public void replace(Value toReplace, Value value) {
        this.paras.replaceAll(para -> {
            if (para == toReplace) return value;
            else return para;
        });
    }
}
