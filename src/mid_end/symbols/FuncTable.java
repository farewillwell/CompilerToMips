package mid_end.symbols;

import java.util.HashMap;

public class FuncTable {
    private HashMap<String, FuncSymbol> table;

    public FuncTable() {
        this.table = new HashMap<>();
    }

    public void addSymbol(FuncSymbol symbol) {
        table.put(symbol.name, symbol);
    }

    public FuncSymbol getSymbol(String name) {
        return table.get(name);
    }
}
