package front_end.AST.Exp;

import front_end.AST.Node;
import front_end.ErrorCollector;
import mid_end.llvm_ir.Value;

public class PrimaryExp extends Node {
    private Number number;

    private int mode;//exp=1, lVal exp=2,number=3
    private Exp exp;
    private LVal lVal;

    public PrimaryExp(Number number) {
        this.number = number;
        this.lVal = null;
        this.exp = null;
        this.mode = 3;
    }

    public PrimaryExp(Exp exp) {
        this.number = null;
        this.lVal = null;
        this.exp = exp;
        this.mode = 1;
    }

    public PrimaryExp(LVal lVal) {
        this.number = null;
        this.lVal = lVal;
        this.exp = null;
        this.mode = 2;
    }

    @Override
    public void show() {
        if (mode == 1) {
            System.out.println("LPARENT (");
            exp.show();
            System.out.println("RPARENT )");
        } else if (mode == 2) {
            lVal.show();
        } else {
            number.show();
        }
        System.out.println("<PrimaryExp>");
        super.show();
    }

    public int getDim() {
        if (mode == 1) {
            assert exp != null;
            return exp.getDim();
        } else if (mode == 2) {
            assert lVal != null;
            return lVal.getDim();
        } else {
            return 0;
        }
    }

    @Override
    public void checkError(ErrorCollector errorCollector) {
        super.checkError(errorCollector);
        if (mode == 1) {
            assert exp != null;
            exp.checkError(errorCollector);
        } else if (mode == 2) {
            assert lVal != null;
            lVal.checkError(errorCollector);
        } else {
            assert number != null;
            number.checkError(errorCollector);
        }
    }

    public int evaluate() {
        if (mode == 1) {
            assert exp != null;
            return exp.evaluate();
        } else if (mode == 2) {
            assert lVal != null;
            /*需要默认lval在这里是数字*/
            return lVal.evaluate();
        } else {
            assert number != null;
            return number.evaluate();
        }
    }

    public int getSec() {
        if (mode == 1) {
            assert exp != null;
            return exp.getSec();
        } else if (mode == 2) {
            assert lVal != null;
            return lVal.getSec();
        } else {
            assert number != null;
            return number.getSec();
        }
    }

    @Override
    public Value getIRCode() {
        if (mode == 1) {
            return exp.getIRCode();
        } else if (mode == 2) {
            return lVal.getIRCode();
        } else {
            return number.getIRCode();
        }
    }

    @Override
    public int queryValue() {
        if (mode == 1) {
            return exp.queryValue();
        } else if (mode == 2) {
            return lVal.queryValue();
        } else {
            return number.queryValue();
        }
    }
}
