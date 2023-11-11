package front_end.AST.Fun;

import front_end.AST.Node;
import front_end.ErrorCollector;
import mid_end.llvm_ir.Param;
import mid_end.llvm_ir.Value;

import java.util.ArrayList;

public class FuncFParams extends Node {
    public final FuncFParam funcFParam;
    public final ArrayList<FuncFParam> otherFuncFParams;

    public FuncFParams(FuncFParam funcFParam) {
        this.funcFParam = funcFParam;
        this.otherFuncFParams = new ArrayList<>();
    }

    public int getParaNum() {
        return 1 + otherFuncFParams.size();
    }

    public void addFuncFormalParams(FuncFParam funcFParam) {
        this.otherFuncFParams.add(funcFParam);
    }

    @Override
    public void show() {
        super.show();
        funcFParam.show();
        for (FuncFParam funcFParam1 : otherFuncFParams) {
            System.out.println("COMMA ,");
            funcFParam1.show();
        }
        System.out.println("<FuncFParams>");
    }

    public ArrayList<Param> getParas() {
        ArrayList<Param> paras = new ArrayList<>();
        paras.add((Param) funcFParam.getIRCode());
        for (FuncFParam funcFParam1 : otherFuncFParams) {
            paras.add((Param) funcFParam1.getIRCode());
        }
        return paras;
    }

    @Override
    public void checkError(ErrorCollector errorCollector) {
        funcFParam.checkError(errorCollector);
        for (FuncFParam funcFParam1 : otherFuncFParams) {
            funcFParam1.checkError(errorCollector);
        }
    }
}
