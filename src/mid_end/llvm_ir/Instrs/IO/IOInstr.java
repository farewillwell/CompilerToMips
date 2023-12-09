package mid_end.llvm_ir.Instrs.IO;

import mid_end.llvm_ir.Instr;

public class IOInstr extends Instr {
    // 1 输出整数  $a0 : 待输出的
    // 4 输出字符串 $a0:待输出字符串地址
    // 5 读入整数 $v0 读入的整数   ***************************这读入的整数是v0!!!!
    // 10 exit
    // 11 输出char $a0 : char

    @Override
    public void genMipsCode() {
        super.genMipsCode();
    }
    // 事实上,不用在乎putCh和putStr,因为并不是真的用了
}
