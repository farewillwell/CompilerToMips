package back_end.Mips.AsmInstrs;

public class BlockSignAsm extends AsmInstr {
    private final String label;

    public BlockSignAsm(String label) {
        super("LABEL");
        this.label = label;
    }

    @Override
    public String toString() {
        return label + ":";
    }
}
