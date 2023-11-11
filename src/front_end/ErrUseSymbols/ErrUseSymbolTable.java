package front_end.ErrUseSymbols;

import java.util.HashMap;

public class ErrUseSymbolTable {
    private final HashMap<String, ErrUseSymbol> table;

    public ErrUseSymbolTable() {
        table = new HashMap<>();
    }

    public void debugPrint() {
        int count = 0;
        System.out.println("-------------------------------");
        for (String name : table.keySet()) {
            count++;
            System.out.println("symbol " + count + " " + name + " | " + table.get(name).getClass());
        }
        System.out.println("--------------------------------");
    }

    public void addSymbol(ErrUseSymbol errUseSymbol) {
        table.put(errUseSymbol.name, errUseSymbol);
    }


    public boolean hasDefine(String name) {
        return table.containsKey(name);
    }

    public ErrUseSymbol getSymbol(String name) {
        return table.get(name);
    }

}
