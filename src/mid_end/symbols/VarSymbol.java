package mid_end.symbols;

import mid_end.llvm_ir.Value;

import java.util.ArrayList;

public class VarSymbol extends Symbol {

    public boolean isGlobal = false;
    private int firstDim;
    private ArrayList<Integer> secondDim;

    public Value value;


    public VarSymbol(String name, Value value) {
        super(name);
        if (SymbolManager.SM.isGlobal()) {
            this.isGlobal = true;
        }
        this.value = value;
    }
}
