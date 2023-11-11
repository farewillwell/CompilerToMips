package mid_end.symbols;

import mid_end.llvm_ir.Function;

public class FuncSymbol extends Symbol {
    public Function function;


    public FuncSymbol(String name, Function function) {
        super(name);
        this.function = function;
    }
}
