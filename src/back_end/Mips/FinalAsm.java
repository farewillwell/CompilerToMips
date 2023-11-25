package back_end.Mips;

import back_end.Mips.AsmInstrs.*;
import back_end.Mips.MipsHead.Header;

import java.util.ArrayList;

public class FinalAsm {
    private final ArrayList<Header> headers;
    private final ArrayList<AsmInstr> asmInstrs;

    public FinalAsm() {
        headers = new ArrayList<>();
        asmInstrs = new ArrayList<>();
    }
    // 如何调整 main和函数的func位置？
    // 不需要的，只要保证先跳到main再跳到end就行了

    public void addHead(Header header) {
        headers.add(header);
    }

    public void addInstr(AsmInstr asmInstr) {
        asmInstrs.add(asmInstr);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(".data\n");
        for (Header header : headers) {
            stringBuilder.append(header).append("\n");
        }
        stringBuilder.append("\n.text\n");
        for (AsmInstr asmInstr : asmInstrs) {
            stringBuilder.append(asmInstr).append("\n");
            if (asmInstr instanceof JumpAsm) {
                stringBuilder.append("\n");
            }
        }
        return stringBuilder.toString();
    }
}
