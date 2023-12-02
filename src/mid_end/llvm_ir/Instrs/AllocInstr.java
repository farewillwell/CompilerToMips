package mid_end.llvm_ir.Instrs;

import back_end.Mips.AsmInstrs.AluR2IAsm;
import back_end.Mips.AsmInstrs.AnnotationAsm;
import back_end.Mips.AsmInstrs.MemAsm;
import back_end.Mips.MipsSymbol;
import back_end.Mips.MipsBuilder;
import back_end.Mips.Register;
import mid_end.llvm_ir.Instr;
import mid_end.llvm_ir.LocalVar;
import mid_end.llvm_ir.type.BaseType;
import mid_end.llvm_ir.type.LLVMType;
import mid_end.llvm_ir.type.PointerType;

public class AllocInstr extends Instr {

    public AllocInstr(LLVMType type, boolean isConst) {
        super(type);
        setAns(new LocalVar(new PointerType(type), isConst));
    }

    @Override
    public String toString() {
        return getAns().toString() + " = alloca " + type.toString();
    }

    @Override
    public void genMipsCode() {
        // 仅有非全局变量才需要alloca，很好
        super.genMipsCode();
        int offset = MipsBuilder.MB.allocOnStack(type.getSize());
        // 在内存上分配一个这个空间
        new AluR2IAsm(AluR2IAsm.ADDI,Register.T0,Register.SP,offset);
        // 计算这个地址值
        int pointOffset =MipsBuilder.MB.queryOffset(getAns());
        // 开一个指针存大小
        new MemAsm(MemAsm.SW,Register.T0,Register.SP,pointOffset);
        // 把指针值存到那个地址里面
    }
}
