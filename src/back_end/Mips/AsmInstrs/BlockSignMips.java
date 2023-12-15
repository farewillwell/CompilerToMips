package back_end.Mips.AsmInstrs;

public class BlockSignMips extends MipsInstr {
    private final String label;

    public BlockSignMips(String label) {
        super("LABEL");
        this.label = label;
    }

    @Override
    public String toString() {
        return label + ":";
    }
}
