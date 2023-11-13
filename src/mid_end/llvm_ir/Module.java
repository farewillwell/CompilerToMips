package mid_end.llvm_ir;

import mid_end.llvm_ir.Instrs.IO.GetInt;
import mid_end.llvm_ir.Instrs.IO.PutCh;
import mid_end.llvm_ir.Instrs.IO.PutInt;

import java.util.ArrayList;

public class Module extends Value {
    private final ArrayList<Function> functions;
    private final ArrayList<GlobalVar> globalVars;


    public Module() {
        functions = new ArrayList<>();
        globalVars = new ArrayList<>();
    }

    public void finish() {
        for (Function function : functions) {
            function.ensureReturnExist();
        }
    }

    public void addFunction(Function function) {
        functions.add(function);
    }

    public void addGlobalVar(GlobalVar globalVar) {
        globalVars.add(globalVar);
    }

    @Override
    public String toString() {
        finish();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(GetInt.defineHead);
        stringBuilder.append(PutCh.defineHead);
        stringBuilder.append(PutInt.defineHead);
        stringBuilder.append("\n");
        for (GlobalVar globalVar : globalVars) {
            stringBuilder.append(globalVar.forShow());
            stringBuilder.append("\n");
        }
        for (Function function : functions) {
            stringBuilder.append(function.toString());
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}
