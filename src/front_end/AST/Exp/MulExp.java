package front_end.AST.Exp;

import front_end.AST.Node;
import front_end.AST.TokenNode;
import front_end.ErrorCollector;
import front_end.RW;
import mid_end.llvm_ir.Constant;
import mid_end.llvm_ir.IRBuilder;
import mid_end.llvm_ir.Instrs.ALUIr;
import mid_end.llvm_ir.Value;

import java.util.ArrayList;

public class MulExp extends Node {
    private final UnaryExp unaryExp;
    private final ArrayList<UnaryExp> otherUnaryExps;
    private final ArrayList<TokenNode> tokenNodes;

    public MulExp(UnaryExp unaryExp) {
        this.unaryExp = unaryExp;
        this.otherUnaryExps = new ArrayList<>();
        tokenNodes = new ArrayList<>();
    }

    public void addUnaryExp(TokenNode tokenNode, UnaryExp unaryExp) {
        tokenNodes.add(tokenNode);
        otherUnaryExps.add(unaryExp);
    }

    @Override
    public void show() {
        unaryExp.show();
        System.out.println("<MulExp>");
        for (int i = 0; i < otherUnaryExps.size(); i++) {
            tokenNodes.get(i).show();
            otherUnaryExps.get(i).show();
            System.out.println("<MulExp>");
        }

        super.show();
    }

    @Override
    public void checkError(ErrorCollector errorCollector) {
        unaryExp.checkError(errorCollector);
        for (UnaryExp unaryExp1 : otherUnaryExps) {
            unaryExp1.checkError(errorCollector);
        }
    }

    public int evaluate() {
        int temp = unaryExp.evaluate();
        for (int i = 0; i < tokenNodes.size(); i++) {
            if (tokenNodes.get(i).type() == RW.TYPE.MULT) {
                temp *= otherUnaryExps.get(i).evaluate();
            } else if (tokenNodes.get(i).type() == RW.TYPE.DIV) {
                temp /= otherUnaryExps.get(i).evaluate();
            } else {
                temp %= otherUnaryExps.get(i).evaluate();
            }
        }
        return temp;
    }

    public int getDim() {
        int dim = unaryExp.getDim();
        for (UnaryExp unaryExp1 : otherUnaryExps) {
            dim = Integer.max(dim, unaryExp1.getDim());
        }
        return dim;
    }

    public int getSec() {
        int sec = unaryExp.getSec();
        for (UnaryExp unaryExp1 : otherUnaryExps) {
            sec = Integer.min(sec, unaryExp1.getSec());
        }
        return sec;
    }

    @Override
    public Value getIRCode() {
        Value value = unaryExp.getIRCode();
        if (value instanceof Constant && ((Constant) value).getValue() == 0) {
            return new Constant(0);
        }
        ArrayList<Value> otherFactors = new ArrayList<>();
        /* a*b*c 可行 a/b*c 不可计算 b*c,因此如果有a/b*c而bc为常数,无法进行常数优化*/
        /*只有前缀全为常数才可进行类似优化*/
        for (UnaryExp otherUnaryExp : otherUnaryExps) {
            otherFactors.add(otherUnaryExp.getIRCode());
        }
        for (Value factor : otherFactors) {
            if (factor instanceof Constant && ((Constant) factor).getValue() == 0) {
                return new Constant(0);
            }
        }
        for (int i = 0; i < otherFactors.size(); i++) {
            String opcode;
            RW.TYPE type = tokenNodes.get(i).type();
            if (type == RW.TYPE.MULT) {
                opcode = ALUIr.MUL;
            } else if (type == RW.TYPE.DIV) {
                opcode = ALUIr.DIV;
            } else {
                opcode = ALUIr.SREM;
            }
            Value factor = otherFactors.get(i);
            if (value instanceof Constant && factor instanceof Constant) {
                int ans;
                int left = ((Constant) value).getValue();
                int right = ((Constant) factor).getValue();
                if (opcode.equals(ALUIr.MUL)) {
                    ans = left * right;
                } else if (opcode.equals(ALUIr.DIV)) {
                    ans = left / right;
                } else {
                    ans = left % right;
                }
                value = new Constant(ans);
            } else {
                ALUIr aluIr = new ALUIr(opcode, value, otherFactors.get(i));
                IRBuilder.IB.addInstrForBlock(aluIr);
                value = aluIr.getAns();
            }
        }
        return value;
    }

    @Override
    public int queryValue() {
        int ans = unaryExp.queryValue();
        for (int i = 0; i < otherUnaryExps.size(); i++) {
            if (tokenNodes.get(i).type() == RW.TYPE.MULT) {
                ans *= otherUnaryExps.get(i).queryValue();
            } else if (tokenNodes.get(i).type() == RW.TYPE.DIV) {
                ans /= otherUnaryExps.get(i).queryValue();
            } else {
                ans %= otherUnaryExps.get(i).queryValue();
            }
        }
        return ans;
    }
}
