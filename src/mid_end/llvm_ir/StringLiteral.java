package mid_end.llvm_ir;

import back_end.Mips.MipsBuilder;
import back_end.Mips.MipsHead.Asciiz;

public class StringLiteral extends Value {
    // 如果要优化速度的话还是要整这个，不然每次输出一个字符都要系统调用一次，真的难绷。
    //
    public final String name;

    public final int size;

    private final String content;

    public StringLiteral(int size, String content) {
        this.name = IRBuilder.IB.getStringName();
        this.size = size + 1;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public String getMipsName() {
        return name.substring(1);
    }

    @Override
    public String toString() {
        return name + " = " + "constant [" + size + " x i8] c\"" + content + "\\00\"";
    }

    @Override
    public void genMipsCode() {
        MipsBuilder.MB.addHead(new Asciiz(getMipsName(), content));
    }
}
