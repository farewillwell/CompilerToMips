package mid_end.llvm_ir;

import back_end.Mips.AsmInstrs.AnnotationAsm;
import back_end.Mips.AsmInstrs.LiAsm;
import back_end.Mips.AsmInstrs.MemAsm;
import back_end.Mips.MipsBuilder;
import back_end.Mips.MipsSymbol;
import back_end.Mips.Register;
import mid_end.llvm_ir.type.LLVMType;

public class Instr extends User {

    public Instr(LLVMType type) {
        super(type);
    }

    public Instr() {
        super();
    }

    private Value ans = null;

    public void setAns(Value value) {
        ans = value;
    }

    public Value getAns() {
        return ans;
    }

    @Override
    public void genMipsCode() {
        new AnnotationAsm(toString());
        // 一定要分清楚，全局和开在栈上的取值方式是不一样的!!!
    }

    // 将第x个参数换成value
    // 例如value = add 1+2 可以把这个参数删掉.

    public void replace(Value oldOne, Value newOne) {
        this.paras.replaceAll(para -> {
            if (para == oldOne) {
                oldOne.removeUser(this);
                newOne.addUser(this);
                return newOne;
            } else return para;
        });
    }

    public static void getValueInReg(Register dst, Value value) {
        if (value instanceof Constant) {
            new LiAsm(((Constant) value).getValue(), dst);
        } else if (value instanceof GlobalVar) {
            new MemAsm(MemAsm.LW, dst, ((GlobalVar) value).nameInMips(), 0);
        } else {
            int inOffset = MipsBuilder.MB.queryOffset(value);
            new MemAsm(MemAsm.LW, dst, Register.SP, inOffset);
        }
    }

    public void allocSelf() {
        if (getAns() == null) {
            return;
        }
        int offset = MipsBuilder.MB.allocOnStack(4);
        MipsBuilder.MB.addVarSymbol(new MipsSymbol(getAns(), offset));
    }
}
