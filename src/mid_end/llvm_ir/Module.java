package mid_end.llvm_ir;

import back_end.Mips.MipsBuilder;
import mid_end.llvm_ir.Instrs.IO.GetInt;
import mid_end.llvm_ir.Instrs.IO.PutCh;
import mid_end.llvm_ir.Instrs.IO.PutInt;
import mid_end.llvm_ir.Instrs.IO.PutStr;

import java.util.ArrayList;

public class Module extends Value {
    private final ArrayList<StringLiteral> stringLiterals;
    private final ArrayList<Function> functions;
    private final ArrayList<GlobalVar> globalVars;


    public Module() {
        functions = new ArrayList<>();
        globalVars = new ArrayList<>();
        stringLiterals = new ArrayList<>();
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

    public void addString(StringLiteral stringLiteral) {
        stringLiterals.add(stringLiteral);
    }

    @Override
    public String toString() {
        finish();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(GetInt.defineHead);
        stringBuilder.append(PutCh.defineHead);
        stringBuilder.append(PutInt.defineHead);
        stringBuilder.append(PutStr.defineHead);
        stringBuilder.append("\n");
        for (GlobalVar globalVar : globalVars) {
            stringBuilder.append(globalVar.forShow());
            stringBuilder.append("\n");
        }
        for (StringLiteral stringLiteral : stringLiterals) {
            stringBuilder.append(stringLiteral.toString());
            stringBuilder.append("\n");
        }
        for (Function function : functions) {
            stringBuilder.append(function.toString());
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    @Override
    public void genMipsCode() {
        for (GlobalVar globalVar : globalVars) {
            globalVar.genMipsCode();
        }
        for (StringLiteral stringLiteral : stringLiterals) {
            stringLiteral.genMipsCode();
        }
        MipsBuilder.MB.headerFinish();
        for (Function function : functions) {
            function.genMipsCode();
        }
        MipsBuilder.MB.textFinish();
    }
}
