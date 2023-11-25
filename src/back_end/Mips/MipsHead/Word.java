package back_end.Mips.MipsHead;

import mid_end.llvm_ir.Instrs.IcmpInstr;

import java.util.ArrayList;

public class Word extends Header {
    private final String name;
    private final int initValue;

    private final boolean isArr;

    private final ArrayList<Integer> initValues;

    public Word(String name, int initValue) {
        isArr = false;
        this.name = name;
        this.initValue = initValue;
        this.initValues = new ArrayList<>();
    }

    public Word(String name, ArrayList<Integer> initValues) {
        isArr = true;
        this.name = name;
        this.initValues = new ArrayList<>();
        this.initValue = 0;
        this.initValues.addAll(initValues);
    }

    @Override

    public String toString() {
        if (isArr) {
            StringBuilder sb = new StringBuilder();
            sb.append(name);
            sb.append(" : .word ");
            for (int i = 0; i < initValues.size(); i++) {
                sb.append(initValues.get(i));
                if (i < initValues.size() - 1) {
                    sb.append(", ");
                }
            }
            return sb.toString();
        }
        return name + " : .word " + initValue;
    }
}
