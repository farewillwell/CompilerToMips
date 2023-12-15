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

public class AddExp extends Node {
    private final MulExp mulExp;
    private final ArrayList<TokenNode> tokenNodes;
    private final ArrayList<MulExp> otherMulExps;

    public AddExp(MulExp mulExp) {
        this.mulExp = mulExp;
        this.otherMulExps = new ArrayList<>();
        this.tokenNodes = new ArrayList<>();
    }

    public void addMulExp(TokenNode tokenNode, MulExp mulExp) {
        tokenNodes.add(tokenNode);
        otherMulExps.add(mulExp);
    }

    @Override
    public void checkError(ErrorCollector errorCollector) {
        mulExp.checkError(errorCollector);
        for (MulExp mulExp1 : otherMulExps) {
            mulExp1.checkError(errorCollector);
        }
    }

    @Override
    public void show() {
        mulExp.show();
        System.out.println("<AddExp>");
        for (int i = 0; i < otherMulExps.size(); i++) {
            tokenNodes.get(i).show();
            otherMulExps.get(i).show();
            System.out.println("<AddExp>");
        }
        super.show();
    }

    public int evaluate() {
        int temp = mulExp.evaluate();
        for (int i = 0; i < tokenNodes.size(); i++) {
            if (tokenNodes.get(i).type() == RW.TYPE.PLUS) {
                temp += otherMulExps.get(i).evaluate();
            } else {
                temp -= otherMulExps.get(i).evaluate();
            }
        }
        return temp;
    }

    public int getDim() {
        int dim = mulExp.getDim();
        for (MulExp mulExp1 : otherMulExps) {
            dim = Integer.max(dim, mulExp1.getDim());
        }
        return dim;
    }

    public int getSec() {
        int sec = mulExp.getSec();
        for (MulExp mulExp1 : otherMulExps) {
            sec = Integer.min(sec, mulExp1.getSec());
        }
        return sec;
    }

    /*TODO 防止多次计算getIRCode*/
    @Override
    public Value getIRCode() {
        Value value = mulExp.getIRCode();
        ArrayList<Value> otherTerm = new ArrayList<>();
        /* a+b-c 可行 a-b+c 不可计算 */
        /*只有前缀全为常数才可进行类似优化*/
        for (MulExp otherMulExp : otherMulExps) {
            otherTerm.add(otherMulExp.getIRCode());
        }
        for (int i = 0; i < otherTerm.size(); i++) {
            String opcode;
            RW.TYPE type = tokenNodes.get(i).type();
            if (type == RW.TYPE.PLUS) {
                opcode = ALUIr.ADD;
            } else {
                opcode = ALUIr.SUB;
            }
            Value term = otherTerm.get(i);
            if (value instanceof Constant && term instanceof Constant) {
                int ans;
                int left = ((Constant) value).getValue();
                int right = ((Constant) term).getValue();
                if (opcode.equals(ALUIr.ADD)) {
                    ans = left + right;
                } else {
                    ans = left - right;
                }
                value = new Constant(ans);
            } else {
                ALUIr aluIr = new ALUIr(opcode, value, otherTerm.get(i));
                IRBuilder.IB.addInstrForBlock(aluIr);
                value = aluIr.getAns();
            }
        }
        return value;
    }

    @Override
    public int queryValue() {
        int ans = mulExp.queryValue();
        for (int i = 0; i < otherMulExps.size(); i++) {
            ans += (tokenNodes.get(i).type() == RW.TYPE.PLUS ? 1 : -1) * otherMulExps.get(i).queryValue();
        }
        return ans;
    }
}
