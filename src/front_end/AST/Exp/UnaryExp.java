package front_end.AST.Exp;

import front_end.AST.Fun.FuncRParams;
import front_end.AST.Node;
import front_end.AST.TokenNode;
import front_end.ErrUseSymbols.ErrUseFuncErrUseSymbol;
import front_end.ErrUseSymbols.ErrUseSymbolManager;
import front_end.ErrorCollector;
import mid_end.llvm_ir.Constant;
import mid_end.llvm_ir.Function;
import mid_end.llvm_ir.IRBuilder;
import mid_end.llvm_ir.Instrs.ALUIr;
import mid_end.llvm_ir.Instrs.CallIr;
import mid_end.llvm_ir.Instrs.IcmpIr;
import mid_end.llvm_ir.Value;
import mid_end.llvm_ir.type.BaseType;
import mid_end.symbols.SymbolManager;

import java.util.ArrayList;

public class UnaryExp extends Node {
    private final int mode; // 1= primaryExp 2= ident 3= unary
    public PrimaryExp primaryExp;
    public TokenNode tokenNode;
    public FuncRParams funcRParams;
    public UnaryOp unaryOp;
    public UnaryExp unaryExp;

    public UnaryExp(PrimaryExp primaryExp) {
        this.primaryExp = primaryExp;
        this.tokenNode = null;
        this.funcRParams = null;
        this.unaryOp = null;
        this.unaryExp = null;
        this.mode = 1;
    }

    public UnaryExp(TokenNode tokenNode) {
        this.primaryExp = null;
        this.tokenNode = tokenNode;
        this.funcRParams = null;
        this.unaryOp = null;
        this.unaryExp = null;
        this.mode = 2;
    }

    public UnaryExp(TokenNode tokenNode, FuncRParams funcRParams) {
        this.primaryExp = null;
        this.tokenNode = tokenNode;
        this.funcRParams = funcRParams;
        this.unaryOp = null;
        this.unaryExp = null;
        this.mode = 2;
    }

    public UnaryExp(UnaryOp unaryOp, UnaryExp unaryExp) {
        this.primaryExp = null;
        this.tokenNode = null;
        this.funcRParams = null;
        this.unaryOp = unaryOp;
        this.unaryExp = unaryExp;
        this.mode = 3;
    }

    @Override
    public void show() {
        if (mode == 1) {
            primaryExp.show();
        } else if (mode == 2) {
            tokenNode.show();
            System.out.println("LPARENT (");
            if (funcRParams != null) {
                funcRParams.show();
            }
            System.out.println("RPARENT )");
        } else {
            unaryOp.show();
            unaryExp.show();
        }
        System.out.println("<UnaryExp>");
        super.show();
    }

    @Override
    public void checkError(ErrorCollector errorCollector) {
        super.checkError(errorCollector);
        assert tokenNode != null;
        if (mode == 1) {
            assert primaryExp != null;
            primaryExp.checkError(errorCollector);
        } else if (mode == 2) {
            if (ErrUseSymbolManager.SM.notDefineFuncYet(tokenNode.content())) {
                errorCollector.addError(tokenNode.getLine(), "c");
            } else {
                int paraNum = 0;
                if (funcRParams != null) {
                    paraNum = funcRParams.getSize();
                    funcRParams.checkError(errorCollector);
                }
                if (!ErrUseSymbolManager.SM.paraNumOk(tokenNode.content(), paraNum)) {
                    errorCollector.addError(tokenNode.getLine(), "d");
                } else if (!ErrUseSymbolManager.SM.rightParasType(this)) {
                    errorCollector.addError(tokenNode.getLine(), "e");
                }
            }
        } else {
            assert unaryExp != null;
            unaryExp.checkError(errorCollector);
        }
    }

    public int evaluate() {
        if (mode == 1) {
            assert primaryExp != null;
            return primaryExp.evaluate();
        } else if (mode == 2) {
            //throw new RuntimeException("use a func value");
            return 0;
        } else {
            assert unaryOp != null && unaryExp != null;
            if (unaryOp.isNot()) {
                return unaryExp.evaluate() == 0 ? 1 : 0;
            }
            return unaryExp.evaluate() * unaryOp.evaluate();
            // 由于!仅出现在条件表达式，因此不会在这种情况下出现取值的情况。
        }
    }

    /* 统一编号，==3：void 错误，属于类型不匹配，未定义错误应该用异常来解决 */
    public int getDim() throws RuntimeException {
        if (mode == 1) {
            assert primaryExp != null;
            return primaryExp.getDim();
        } else if (mode == 2) {
            // 如果事void 函数怎么办？直接当成3阶.
            assert tokenNode != null;
            ErrUseFuncErrUseSymbol errUseFuncSymbol = ErrUseSymbolManager.SM.getFuncSymbol(tokenNode.content());
            if (errUseFuncSymbol == null) {
                ErrorCollector.EC.addError(tokenNode.getLine(), "c");
                throw new RuntimeException("undefined");
            } else if (errUseFuncSymbol.returnIsVoid) {
                return 3;
            }
            return 0;
        } else {
            assert unaryExp != null;
            return unaryExp.getDim();
        }
    }

    public int getSec() {
        if (mode == 1) {
            assert primaryExp != null;
            return primaryExp.getSec();
        } else if (mode == 2) {
            return 0;
        } else {
            assert unaryExp != null;
            return unaryExp.getSec();
        }
    }

    @Override
    public Value getIRCode() {
        if (mode == 1) {
            return primaryExp.getIRCode();
        } else if (mode == 2) {
            Function function = SymbolManager.SM.getFuncSymbol(tokenNode.content()).function;
            ArrayList<Value> rps;
            if (funcRParams != null) {
                rps = funcRParams.getRealParas();
            } else {
                rps = new ArrayList<>();
            }
            CallIr callIr = new CallIr(function, rps);
            IRBuilder.IB.addInstrForBlock(callIr);
            if (function.type != BaseType.Void) {
                return callIr.getAns();
            } else {
                return null;
            }
            /* TODO 函数有关的 */
        } else {
            Value get = unaryExp.getIRCode();
            if (get instanceof Constant) {
                if (unaryOp.isNegative()) {
                    return new Constant(-1 * ((Constant) get).getValue());
                } else if (unaryOp.isPositive()) {
                    return get;
                } else {
                    if (unaryOp.isNot()) {
                        return new Constant(((Constant) get).getValue() == 0 ? 1 : 0);
                    } else {
                        return get;
                    }
                }
            } else {
                if (unaryOp.isNegative()) {
                    ALUIr aluIr = new ALUIr(ALUIr.SUB, new Constant(0), get);
                    Value value = aluIr.getAns();
                    IRBuilder.IB.addInstrForBlock(aluIr);
                    return value;
                } else if (unaryOp.isPositive()) {
                    return get;
                } else {
                    if (unaryOp.isNot()) {
                        IcmpIr icmpIr = new IcmpIr
                                (IcmpIr.EQ, get, new Constant(0));
                        IRBuilder.IB.addInstrForBlock(icmpIr);
                        return icmpIr.getAns();
                    } else {
                        return get;
                    }
                }
            }
        }
    }

    @Override
    public int queryValue() {
        if (mode == 1) {
            return primaryExp.queryValue();
        } else if (mode == 2) {
            /*TODO*/
            throw new RuntimeException("query value of a func");
        } else {
            if (unaryOp.isNot()) {
                return unaryExp.queryValue() == 0 ? 1 : 0;
            }
            return (unaryOp.isPositive() ? 1 : -1) * unaryExp.queryValue();
        }
    }
}
