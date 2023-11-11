package front_end.AST.Fun;

import front_end.AST.Exp.Exp;
import front_end.AST.Node;
import front_end.ErrorCollector;
import mid_end.llvm_ir.Value;

import java.util.ArrayList;

public class FuncRParams extends Node {
    public final Exp exp;
    public final ArrayList<Exp> otherExps;

    public FuncRParams(Exp exp) {
        this.exp = exp;
        this.otherExps = new ArrayList<>();
    }

    public void addExp(Exp exp) {
        otherExps.add(exp);
    }

    public int getSize() {
        return 1 + otherExps.size();
    }

    @Override
    public void show() {
        super.show();
        exp.show();
        for (Exp exp1 : otherExps) {
            System.out.println("COMMA ,");
            exp1.show();
        }
        System.out.println("<FuncRParams>");
    }

    @Override
    public void checkError(ErrorCollector errorCollector) {
        super.checkError(errorCollector);
        exp.checkError(errorCollector);
        for (Exp exp1 : otherExps) {
            exp1.checkError(errorCollector);
        }
    }

    public ArrayList<Value> getRealParas() {
        ArrayList<Value> rps = new ArrayList<>();
        rps.add(exp.getIRCode());
        for (Exp exp1 : otherExps) {
            rps.add(exp1.getIRCode());
        }
        return rps;
    }
}
