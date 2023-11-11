package mid_end.symbols;

import java.util.Stack;

public class SymbolManager {

    public static final SymbolManager SM = new SymbolManager();
    private final Stack<VarSymbolTable> tables;

    private final FuncTable funcTable;

    public boolean isGlobal() {
        return tables.size() <= 1;
    }

    // 第一个表是全局变量表

    public SymbolManager() {
        tables = new Stack<>();
        tables.push(new VarSymbolTable());
        this.funcTable = new FuncTable();
    }

    public void enterBlock() {
        tables.push(new VarSymbolTable());
    }

    public void exitBlock() {
        tables.pop();
    }

    public void addVarSymbol(VarSymbol varSymbol) {
        tables.peek().addSymbol(varSymbol);
    }

    public VarSymbol getVarSymbol(String name) {
        // System.out.println("query "+name);
        for (int i = tables.size() - 1; i >= 0; i--) {
            if (tables.get(i).hasDefine(name)) {
                //System.out.println("get "+tables.get(i).getSymbol(name).value);
                return tables.get(i).getSymbol(name);
            }
        }
        throw new RuntimeException("not define the var !!!");
    }

    public FuncSymbol getFuncSymbol(String name) {
        return funcTable.getSymbol(name);

    }

    public void addFuncSymbol(FuncSymbol funcSymbol) {
        funcTable.addSymbol(funcSymbol);
    }
}
