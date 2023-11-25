package mid_end.llvm_ir.Instrs;

import back_end.Mips.AsmInstrs.*;
import back_end.Mips.MipsBuilder;
import back_end.Mips.MipsSymbol;
import back_end.Mips.Register;
import mid_end.llvm_ir.*;
import mid_end.llvm_ir.type.BaseType;

public class ALUInstr extends Instr {

    //  and  /  or become jump of basic block
    public static final String ADD = "add";
    public static final String SUB = "sub";
    public static final String MUL = "mul";
    public static final String DIV = "sdiv";
    public static final String SREM = "srem";

    private final String opcode;

    // 可扩展？i32 或者什么？

    public ALUInstr(String op, Value para1, Value para2) {
        this.opcode = op;
        this.addValue(para1);
        this.addValue(para2);
        this.addValue(new LocalVar(BaseType.I32, false));
    }

    @Override
    public String toString() {
        return this.getAns().toString() + " = " + opcode + " " + getAns().type.toString() + " " +
                paras.get(0).toString() + ", " + paras.get(1).toString();
    }

    @Override
    public void genMipsCode() {
        super.genMipsCode();
        Value p0 = paras.get(0);
        Value p1 = paras.get(1);
        int newOffset = MipsBuilder.MB.allocOnStack(getAns().type.getSize());
        MipsSymbol mipsSymbol = new MipsSymbol(getAns(), newOffset);
        MipsBuilder.MB.addVarSymbol(mipsSymbol);
        if (p0 instanceof Constant) {
            new LiAsm(((Constant) p0).getValue(), Register.T0);
        } else if (p0 instanceof LocalVar) {
            int offset = MipsBuilder.MB.queryOffset(p0);
            new MemAsm(MemAsm.LW, Register.T0, Register.SP, offset);
        } else {
            new MemAsm(MemAsm.LW, Register.T0, ((GlobalVar) p0).nameInMips(), 0);
        }
        if (p1 instanceof Constant) {
            new LiAsm(((Constant) p1).getValue(), Register.T1);
        } else if (p1 instanceof LocalVar) {
            int offset = MipsBuilder.MB.queryOffset(p1);
            new MemAsm(MemAsm.LW, Register.T1, Register.SP, offset);
        } else {
            new MemAsm(MemAsm.LW, Register.T1, ((GlobalVar) p1).nameInMips(), 0);
        }
        if (opcode.equals(ADD)) {
            new AluR2RAsm(AluR2RAsm.ADDU, Register.T2, Register.T0, Register.T1);
        } else if (opcode.equals(SUB)) {
            new AluR2RAsm(AluR2RAsm.SUBU, Register.T2, Register.T0, Register.T1);
        } else {
            if (opcode.equals(MUL)) {
                new MulDivAsm(MulDivAsm.MUL, Register.T0, Register.T1);
                new HiLoGetterAsm(HiLoGetterAsm.MFLO, Register.T2);
            } else if (opcode.equals(DIV)) {
                new MulDivAsm(MulDivAsm.DIV, Register.T0, Register.T1);
                new HiLoGetterAsm(HiLoGetterAsm.MFLO, Register.T2);
            } else {
                new MulDivAsm(MulDivAsm.DIV, Register.T0, Register.T1);
                new HiLoGetterAsm(HiLoGetterAsm.MFHI, Register.T2);
            }
        }
        new MemAsm(MemAsm.SW, Register.T2, Register.SP, newOffset);
    }
}
