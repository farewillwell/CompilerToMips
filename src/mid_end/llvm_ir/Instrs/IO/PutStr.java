package mid_end.llvm_ir.Instrs.IO;

import back_end.Mips.AsmInstrs.LaAsm;
import back_end.Mips.AsmInstrs.LiAsm;
import back_end.Mips.AsmInstrs.Syscall;
import back_end.Mips.Register;
import mid_end.llvm_ir.StringLiteral;
import mid_end.llvm_ir.Value;

public class PutStr extends IOInstr {
    private final int size;

    public PutStr(Value value) {
        super();
        addValue(value);
        size = ((StringLiteral) value).size;
    }

    public static final String defineHead = "declare void @putstr(i8*)\n";


    //expected '(' in constantexpr   ->>>>> ()
    @Override
    public String toString() {
        return "call void @putstr(i8* getelementptr inbounds ([" + size +
                " x i8], [" + size + " x i8]* " +
                ((StringLiteral) paras.get(0)).name + ", i32 0, i32 0))";
    }

    @Override
    public void genMipsCode() {
        super.genMipsCode();
        new LaAsm(((StringLiteral) paras.get(0)).getMipsName(), Register.A0);
        new LiAsm(4, Register.V0);
        new Syscall();
    }
}
