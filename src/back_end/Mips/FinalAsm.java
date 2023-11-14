package back_end.Mips;

import back_end.Mips.AsmInstrs.AsmInstr;
import back_end.Mips.MipsHead.Header;

import java.util.ArrayList;

public class FinalAsm {
    private final ArrayList<Header> headers;
    private final ArrayList<AsmInstr> asmInstrs;

    public FinalAsm() {
        headers = new ArrayList<>();
        asmInstrs = new ArrayList<>();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(".data\n");
        for (Header header : headers) {
            stringBuilder.append(header).append("\n");
        }
        stringBuilder.append("\n.text\n\n\n");
        for (AsmInstr asmInstr : asmInstrs) {
            stringBuilder.append(asmInstr).append("\n");
        }
        return stringBuilder.toString();
    }
}
