package mid_end.symbols;

import mid_end.llvm_ir.Value;

import java.util.ArrayList;

public class VarSymbol extends Symbol {
    private int firstDim;
    private ArrayList<Integer> secondDim;

    public Value value;


    public VarSymbol(String name, Value value) {
        super(name);
        this.value = value;
    }
}
