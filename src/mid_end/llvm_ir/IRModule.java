package mid_end.llvm_ir;

import back_end.Mips.MipsBuilder;
import mid_end.llvm_ir.Instrs.IO.GetInt;
import mid_end.llvm_ir.Instrs.IO.PutCh;
import mid_end.llvm_ir.Instrs.IO.PutInt;
import mid_end.llvm_ir.Instrs.IO.PutStr;

import java.util.ArrayList;
import java.util.HashSet;

public class IRModule extends Value {
    private final ArrayList<StringLiteral> stringLiterals;
    public final ArrayList<Function> functions;
    private final ArrayList<GlobalVar> globalVars;


    public IRModule() {
        functions = new ArrayList<>();
        globalVars = new ArrayList<>();
        stringLiterals = new ArrayList<>();
    }

    public void finish() {
        for (Function function : functions) {
            function.ensureReturnExist();
            function.cleanInstrAfterOutForBlock();
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

    //----------------------------------------------------------------CFG

    public void doCFG() {
        refillFlowChart();
        cleanUnReachableBlock();
        queryDominates();
        queryImmDomTree();
        queryDf();
    }

    public void reQueryDom() {
        // 清空计算的dom
        for (Function function : functions) {
            for (BasicBlock block : function.basicBlocks) {
                block.whoDomMe.clear();
                block.meDomWho.clear();
                block.immDomer = null;
                block.beImmDom.clear();
                block.df.clear();
            }
        }
        // 用新的前后关系计算一遍
        queryDominates();
        queryImmDomTree();
        queryDf();
    }

    private void refillFlowChart() {
        for (Function function : functions) {
            function.refillFlowChart();
        }
    }

    private void queryDominates() {
        System.out.println("--------------dominates------------------");
        for (Function function : functions) {
            System.out.println(function.name);
            function.queryDom();
        }
    }

    public void queryImmDomTree() {
        System.out.println("--------------immDomTree------------------");
        for (Function function : functions) {
            System.out.println(function.name);
            function.queryImmDomTree();
        }
    }

    public void queryDf() {
        System.out.println("-------------------df-------------------------");
        for (Function function : functions) {
            System.out.println(function.name);
            function.queryDf();
        }
    }

    public void cleanUnReachableBlock() {
        for (Function function : functions) {
            function.cleanUnReachableBlock();
        }
    }

}
