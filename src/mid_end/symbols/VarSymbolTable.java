package mid_end.symbols;

import java.util.HashMap;

public class VarSymbolTable {
    private HashMap<String, VarSymbol> table;

    public VarSymbolTable() {
        table = new HashMap<>();
    }

    public void addSymbol(VarSymbol symbol) {
        table.put(symbol.name, symbol);
    }

    public boolean hasDefine(String name) {
        return table.containsKey(name);
    }

    public VarSymbol getSymbol(String name) {
        return table.get(name);
    }
}
