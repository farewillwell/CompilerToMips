package mid_end.llvm_ir;

import back_end.Mips.AsmInstrs.*;
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

    public boolean isDeleted = false;

    public void setAns(Value value) {
        ans = value;
        value.definer=this;
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
    // 如果是需要传值,那么也许直接把参数换掉是一个好文明
    // 就是,如果值在寄存器里面,那么就直接用寄存器,否则就用传进来的需要存的

    public static Register moveValueIntoReg(Register dst, Value value) {
        // 注意,如果已经在寄存器里了，返回的不是传入的寄存器而是已经在的寄存器
        if (value instanceof Constant) {
            new LiAsm(((Constant) value).getValue(), dst);
            return dst;
        } else if (value instanceof GlobalVar) {
            new MemAsm(MemAsm.LW, dst, ((GlobalVar) value).nameInMips(), 0);
            return dst;
        } else {
            if (MipsBuilder.MB.hasRegFor(value)) {
                return MipsBuilder.MB.queryReg(value);
            } else {
                int inOffset = MipsBuilder.MB.queryOffset(value);
                new MemAsm(MemAsm.LW, dst, Register.SP, inOffset);
                return dst;
            }
        }
    }

    // 这个是如果需要存的value已经被分配寄存器的更好用的方法
    // 不应当增添更多功能
    public static Register targetSRegorNull(Value value) {
        if (MipsBuilder.MB.hasRegFor(value)) {
            return MipsBuilder.MB.queryReg(value);
        }
        return null;
    }

    // 强调,该方法一定要是目标不被分配寄存器,否则可以更节约的使用
    public static void storeMemFromReg(Register src, Value value) {
        if (value instanceof GlobalVar) {
            new MemAsm(MemAsm.SW, src, ((GlobalVar) value).nameInMips(), 0);
        } else {
            int offset = MipsBuilder.MB.queryOffset(value);
            new MemAsm(MemAsm.SW, src, Register.SP, offset);
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
