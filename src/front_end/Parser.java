package front_end;

import front_end.AST.Block;
import front_end.AST.CompUnit;
import front_end.AST.Exp.*;
import front_end.AST.Exp.ForStmt;
import front_end.AST.Exp.Number;
import front_end.AST.Fun.*;
import front_end.AST.Stmt.*;
import front_end.AST.TokenNode;
import front_end.AST.Var.*;

public class Parser {
    private final TokenStream ts;

    public Parser(TokenStream ts) {
        this.ts = ts;
    }

    public CompUnit parserComUnit() {
        CompUnit compUnit = new CompUnit();
        while ((ts.now().type == RW.TYPE.CONSTTK || ts.now().type == RW.TYPE.INTTK)
                && ts.next().type != RW.TYPE.MAINTK && ts.preNext().type != RW.TYPE.LPARENT) {
            if (ts.now().type == RW.TYPE.CONSTTK) {
                compUnit.addDecl(parserConstDecl());   // constDecl
            } else {
                compUnit.addDecl(parserValDecl());    //varDecl
            }
        }
        while ((ts.now().type == RW.TYPE.INTTK || ts.now().type == RW.TYPE.VOIDTK) && ts.next().type != RW.TYPE.MAINTK) {
            compUnit.addFuncDef(parserFuncDef());    //funcDecl
        }
        compUnit.setMainFuncDef(parserMain());    // mainDef
        return compUnit;
    }

    public ConstDecl parserConstDecl() {
        ts.checkError(RW.TYPE.CONSTTK);   // const
        ts.move();
        ts.checkError(RW.TYPE.INTTK);   //int
        ts.move();
        ConstDecl constDecl = new ConstDecl(parserConstDef());   // constDef
        while (ts.now().type == RW.TYPE.COMMA) {
            ts.move();    // ,
            constDecl.addConstDef(parserConstDef());    //constDef
        }
        if (ts.now().type != RW.TYPE.SEMICN) {
            ErrorCollector.EC.addError(ts.lastTokenLineNum(), "i");
        } else {
            ts.move();
        }
        return constDecl;
    }

    public ConstDef parserConstDef() {
        ts.checkError(RW.TYPE.IDENFR);   //indent
        ConstDef constDef = new ConstDef(new TokenNode(ts.now()));
        ts.move();
        while (ts.now().type == RW.TYPE.LBRACK) {    // [
            ts.move();
            constDef.addConstExps(parserConstExp());   // constExp
            if (ts.now().type != RW.TYPE.RBRACK) {
                ErrorCollector.EC.addError(ts.lastTokenLineNum(), "k");
            } else {
                ts.move();
            }
        }
        ts.checkError(RW.TYPE.ASSIGN);    // =
        ts.move();
        constDef.setConstInitVal(parserConstInitVal());   //  constInitVal
        return constDef;
    }

    public ConstInitVal parserConstInitVal() {
        if (ts.now().type != RW.TYPE.LBRACE) {           // ConstExp
            return new ConstInitVal(parserConstExp());
        }
        ts.move();     //{
        ConstInitVal constInitVal = new ConstInitVal();
        if (ts.now().type != RW.TYPE.RBRACE) {    // constInitVal
            constInitVal.setConstInitVal(parserConstInitVal());
            while (ts.now().type == RW.TYPE.COMMA) {   //,
                ts.move();
                constInitVal.addOtherConstInitVals(parserConstInitVal());   //other constInitVal
            }
        }
        ts.checkError(RW.TYPE.RBRACE);
        ts.move();
        return constInitVal;
    }

    public ValDecl parserValDecl() {
        ts.checkError(RW.TYPE.INTTK);  // int
        ts.move();
        ValDecl valDecl = new ValDecl(parserVarDef()); //varDef
        while (ts.now().type == RW.TYPE.COMMA) {
            ts.move();   // ,
            valDecl.addOtherVarDefs(parserVarDef());  //varDef
        }
        if (ts.now().type != RW.TYPE.SEMICN) {
            ErrorCollector.EC.addError(ts.lastTokenLineNum(), "i");
        } else {
            ts.move();
        }
        return valDecl;
    }

    public VarDef parserVarDef() {
        ts.checkError(RW.TYPE.IDENFR);  //ident
        VarDef varDef = new VarDef(new TokenNode(ts.now()));
        ts.move();
        while (ts.now().type == RW.TYPE.LBRACK) {
            ts.move();
            varDef.addConstExps(parserConstExp());   //constExp
            if (ts.now().type != RW.TYPE.RBRACK) {
                ErrorCollector.EC.addError(ts.lastTokenLineNum(), "k");
            } else {
                ts.move();
            }
        }
        if (ts.now().type == RW.TYPE.ASSIGN) {    // =
            ts.move();
            varDef.setInitVal(parserInitVal());   //InitVal
        }
        return varDef;
    }

    public InitVal parserInitVal() {
        if (ts.now().type != RW.TYPE.LBRACE) {           // Exp
            return new InitVal(parserExp());
        }
        ts.move();     //{
        InitVal initVal = new InitVal();
        if (ts.now().type != RW.TYPE.RBRACE) {    // constInitVal
            initVal.setInitVal(parserInitVal());
            while (ts.now().type == RW.TYPE.COMMA) {   //,
                ts.move();
                initVal.addOtherInitVals(parserInitVal());   //other constInitVal
            }
        }
        ts.checkError(RW.TYPE.RBRACE);
        ts.move();
        return initVal;
    }

    public FuncDef parserFuncDef() {
        FuncType funcType = parserFuncType();
        TokenNode tokenNode = new TokenNode(ts.now());
        ts.move();
        ts.checkError(RW.TYPE.LPARENT); // (
        ts.move();   // paras or )
        if (ts.now().type == RW.TYPE.RPARENT) {
            ts.move(); //  to {
            return new FuncDef(funcType, tokenNode, parserBlock());
        } else if (ts.now().type == RW.TYPE.LBRACE) {   // is { .no )
            ErrorCollector.EC.addError(ts.lastTokenLineNum(), "j");
            return new FuncDef(funcType, tokenNode, parserBlock());
        } else {
            FuncFParams funcFParams = parserFuncFormalParams();
            if (ts.now().type != RW.TYPE.RPARENT) {
                ErrorCollector.EC.addError(ts.lastTokenLineNum(), "j");
            } else {
                ts.move();
            }
            return new FuncDef(funcType, tokenNode, funcFParams, parserBlock());
        }
    }

    public FuncType parserFuncType() {
        if (ts.now().type != RW.TYPE.VOIDTK && ts.now().type != RW.TYPE.INTTK) {
            throw new RuntimeException("not match type of teh funcType , we got " + ts.now().type);
        }
        TokenNode tokenNode = new TokenNode(ts.now());
        ts.move();
        return new FuncType(tokenNode);
    }

    public MainFuncDef parserMain() {
        ts.checkError(RW.TYPE.INTTK);
        ts.move();
        ts.checkError(RW.TYPE.MAINTK);
        ts.move();
        ts.checkError(RW.TYPE.LPARENT);
        ts.move();
        if (ts.now().type != RW.TYPE.RPARENT) {
            ErrorCollector.EC.addError(ts.lastTokenLineNum(), "j");
        } else {
            ts.move();
        }
        return new MainFuncDef(parserBlock());
    }

    public FuncRParams parserFuncRealParams() {
        FuncRParams funcRParams = new FuncRParams(parserExp());
        while (ts.now().type == RW.TYPE.COMMA) {
            ts.move();
            funcRParams.addExp(parserExp());
        }
        return funcRParams;
    }

    public FuncFParam parserFuncFormalParam() {
        ts.checkError(RW.TYPE.INTTK);
        ts.move();
        ts.checkError(RW.TYPE.IDENFR);
        TokenNode tokenNode = new TokenNode(ts.now());
        ts.move();
        if (ts.now().type != RW.TYPE.LBRACK) {
            return new FuncFParam(RW.TYPE.INTTK, tokenNode, false);
        }
        ts.move();
        if (ts.now().type != RW.TYPE.RBRACK) {
            ErrorCollector.EC.addError(ts.lastTokenLineNum(), "k");
        } else {
            ts.move();
        }
        FuncFParam funcFParam = new FuncFParam(RW.TYPE.INTTK, tokenNode, true);
        while (ts.now().type == RW.TYPE.LBRACK) {
            ts.move();
            funcFParam.addConstExp(parserConstExp());
            if (ts.now().type != RW.TYPE.RBRACK) {
                ErrorCollector.EC.addError(ts.lastTokenLineNum(), "k");
            } else {
                ts.move();
            }
        }
        return funcFParam;
    }

    public FuncFParams parserFuncFormalParams() {
        FuncFParams funcFParams = new FuncFParams(parserFuncFormalParam());
        while (ts.now().type == RW.TYPE.COMMA) {
            ts.move();
            funcFParams.addFuncFormalParams(parserFuncFormalParam());
        }
        return funcFParams;
    }

    public Block parserBlock() {
        Block block = new Block();
        ts.checkError(RW.TYPE.LBRACE);
        ts.move();
        while (ts.now().type != RW.TYPE.RBRACE) {
            if (ts.now().type == RW.TYPE.INTTK) {
                block.addNode(parserValDecl());
            } else if (ts.now().type == RW.TYPE.CONSTTK) {
                block.addNode(parserConstDecl());
            } else {
                block.addNode(parserStmt());
            }
        }
        int line = ts.now().getLine();
        block.setRBraceLine(line);
        ts.move();
        return block;
    }

    public Stmt parserStmt() {
        RW.TYPE type = ts.now().type;
        if (type == RW.TYPE.IFTK) {
            return parserIfStmt();
        } else if (type == RW.TYPE.LBRACE) {
            return parserBlockStmt();
        } else if (type == RW.TYPE.BREAKTK) {
            return parserBreakStmt();
        } else if (type == RW.TYPE.CONTINUETK) {
            return parserContinueStmt();
        } else if (type == RW.TYPE.RETURNTK) {
            return parserReturnStmt();
        } else if (type == RW.TYPE.PRINTFTK) {
            return parserPrintStmt();
        } else if (type == RW.TYPE.SEMICN) {
            ts.move();
            return new ExpStmt();
        } else if (type == RW.TYPE.FORTK) {
            return parserBigForStmt();
        } else {
            int mark = ts.getPos();
            Exp exp = parserExp();
            if (ts.now().type == RW.TYPE.ASSIGN) {
                if (ts.next().type == RW.TYPE.GETINTTK) {
                    ts.backToMark(mark);
                    return parserGetIntStmt();
                } else {
                    ts.backToMark(mark);
                    return parserAssignStmt();
                }
            } else {
                if (ts.now().type != RW.TYPE.SEMICN) {
                    ErrorCollector.EC.addError(ts.lastTokenLineNum(), "i");
                } else {
                    ts.move();
                }
                return new ExpStmt(exp);
            }

        }
    }

    public ForStmt parserForStmt() {
        LVal lVal = parserLValExp();
        ts.checkError(RW.TYPE.ASSIGN);
        ts.move();
        Exp exp = parserExp();
        return new ForStmt(lVal, exp);
    }

    public BigForStmt parserBigForStmt() {
        ts.checkError(RW.TYPE.FORTK);
        ts.move();
        ts.checkError(RW.TYPE.LPARENT);
        ts.move();
        BigForStmt bigForStmt = new BigForStmt();
        if (ts.now().type != RW.TYPE.SEMICN) {
            bigForStmt.addForExpLeft(parserForStmt());
        }
        ts.checkError(RW.TYPE.SEMICN);
        ts.move();
        if (ts.now().type != RW.TYPE.SEMICN) {
            bigForStmt.addCond(parserCondExp());
        }
        ts.checkError(RW.TYPE.SEMICN);
        ts.move();
        if (ts.now().type != RW.TYPE.RPARENT) {
            bigForStmt.addForExpRight(parserForStmt());
        }
        ts.checkError(RW.TYPE.RPARENT);
        ts.move();
        bigForStmt.addStmt(parserStmt());
        return bigForStmt;
    }

    public AssignStmt parserAssignStmt() {
        LVal lVal = parserLValExp();
        ts.checkError(RW.TYPE.ASSIGN);
        ts.move();
        Exp exp = parserExp();
        if (ts.now().type != RW.TYPE.SEMICN) {
            ErrorCollector.EC.addError(ts.lastTokenLineNum(), "i");
        } else {
            ts.move();
        }
        return new AssignStmt(lVal, exp);
    }

    public BlockStmt parserBlockStmt() {
        return new BlockStmt(parserBlock());
    }

    public IfStmt parserIfStmt() {
        ts.checkError(RW.TYPE.IFTK);
        ts.move();
        ts.checkError(RW.TYPE.LPARENT);
        ts.move();
        Cond cond = parserCondExp();
        if (ts.now().type != RW.TYPE.RPARENT) {
            ErrorCollector.EC.addError(ts.lastTokenLineNum(), "j");
        } else {
            ts.move();
        }
        Stmt stmt = parserStmt();
        if (ts.now().type != RW.TYPE.ELSETK) {
            return new IfStmt(cond, stmt);
        }
        ts.move();
        return new IfStmt(cond, stmt, parserStmt());
    }

    public BreakStmt parserBreakStmt() {
        ts.checkError(RW.TYPE.BREAKTK);
        int line = ts.now().getLine();
        ts.move();
        if (ts.now().type != RW.TYPE.SEMICN) {
            ErrorCollector.EC.addError(ts.lastTokenLineNum(), "i");
        } else {
            ts.move();
        }
        return new BreakStmt(line);
    }

    public ContinueStmt parserContinueStmt() {
        ts.checkError(RW.TYPE.CONTINUETK);
        int line = ts.now().getLine();
        ts.move();
        if (ts.now().type != RW.TYPE.SEMICN) {
            ErrorCollector.EC.addError(ts.lastTokenLineNum(), "i");
        } else {
            ts.move();
        }
        return new ContinueStmt(line);
    }

    /*TODO return语句如何处理缺少分号的情况？需要给出first集*/
    /* LVal ==> Ident {'[' Exp ']'}
       PrimaryExp ==> '(' Exp ')' | LVal | Number
       UnaryExp ==> PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp
       : ( ,Ident,Number,UnaryOp
       */

    private boolean isFirstExp() {
        RW.TYPE type = ts.now().type;
        return type == RW.TYPE.IDENFR || type == RW.TYPE.LPARENT || type == RW.TYPE.INTCON
                || type == RW.TYPE.PLUS || type == RW.TYPE.MINU || type == RW.TYPE.NOT;
    }

    public ReturnStmt parserReturnStmt() {
        ts.checkError(RW.TYPE.RETURNTK);
        int line = ts.now().getLine();
        ts.move();
        if (ts.now().type == RW.TYPE.SEMICN) {
            ts.move();
            return new ReturnStmt(line);
        } else {
            Exp exp = null;
            if (isFirstExp()) {
                exp = parserExp();
            }
            if (ts.now().type != RW.TYPE.SEMICN) {
                ErrorCollector.EC.addError(ts.lastTokenLineNum(), "i");
            } else {
                ts.move();
            }
            return new ReturnStmt(exp, line);
        }
    }

    /*注意，缺少小括号和分号两者需要都判断一下*/
    public GetIntStmt parserGetIntStmt() {
        LVal lVal = parserLValExp();
        ts.checkError(RW.TYPE.ASSIGN);
        ts.move();
        ts.checkError(RW.TYPE.GETINTTK);
        ts.move();
        ts.checkError(RW.TYPE.LPARENT);
        ts.move();
        if (ts.now().type != RW.TYPE.RPARENT) {
            ErrorCollector.EC.addError(ts.lastTokenLineNum(), "j");
        } else {
            ts.move();
        }
        if (ts.now().type != RW.TYPE.SEMICN) {
            ErrorCollector.EC.addError(ts.lastTokenLineNum(), "i");
        } else {
            ts.move();
        }
        return new GetIntStmt(lVal);
    }

    public PrintStmt parserPrintStmt() {
        ts.checkError(RW.TYPE.PRINTFTK);
        int line = ts.now().getLine();
        ts.move();
        ts.checkError(RW.TYPE.LPARENT);
        ts.move();
        ts.checkError(RW.TYPE.STRCON);
        PrintStmt printStmt = new PrintStmt(new TokenNode(ts.now()), line);
        ts.move();
        while (ts.now().type == RW.TYPE.COMMA) {
            ts.move();
            Exp exp = parserExp();
            printStmt.addExp(exp);
        }
        if (ts.now().type != RW.TYPE.RPARENT) {
            ErrorCollector.EC.addError(ts.lastTokenLineNum(), "j");
        } else {
            ts.move();
        }
        if (ts.now().type != RW.TYPE.SEMICN) {
            ErrorCollector.EC.addError(ts.lastTokenLineNum(), "i");
        } else {
            ts.move();
        }
        return printStmt;
    }

    public LVal parserLValExp() {
        TokenNode tokenNode = new TokenNode(ts.now());
        LVal lVal = new LVal(tokenNode);
        ts.move();
        while (ts.now().type == RW.TYPE.LBRACK) {
            ts.move();
            lVal.addExp(parserExp());
            if (ts.now().type != RW.TYPE.RBRACK) {
                ErrorCollector.EC.addError(ts.lastTokenLineNum(), "k");
            } else {
                ts.move();
            }
        }
        return lVal;
    }

    public PrimaryExp parserPrimaryExp() {
        if (ts.now().type == RW.TYPE.INTCON) {
            return new PrimaryExp(parserNumber());
        } else if (ts.now().type == RW.TYPE.LPARENT) {
            ts.move();
            Exp exp = parserExp();
            ts.checkError(RW.TYPE.RPARENT);
            ts.move();
            return new PrimaryExp(exp);
        } else {
            return new PrimaryExp(parserLValExp());
        }
    }

    /*TODO 所有基于分号的判断都应当舍去！！！！*/
    public UnaryExp parserUnaryExp() {
        RW.TYPE type = ts.now().type;
        if (type == RW.TYPE.PLUS || type == RW.TYPE.MINU || type == RW.TYPE.NOT) {
            return new UnaryExp(parserUnaryOp(), parserUnaryExp());
        } else if (ts.now().type == RW.TYPE.IDENFR && ts.next().type == RW.TYPE.LPARENT) {
            TokenNode tokenNode = new TokenNode(ts.now());
            ts.move(); // to (
            ts.move();// jump over ( ,to funcRealParams or  )
            if (ts.now().type == RW.TYPE.RPARENT) {
                ts.move();
                return new UnaryExp(tokenNode);
            } else {
                FuncRParams funcRParams = null;
                if (isFirstExp()) {
                    funcRParams = parserFuncRealParams();
                }
                if (ts.now().type != RW.TYPE.RPARENT) {
                    ErrorCollector.EC.addError(ts.lastTokenLineNum(), "j");
                } else {
                    ts.move();
                }
                return new UnaryExp(tokenNode, funcRParams);
            }
        } else {
            return new UnaryExp(parserPrimaryExp());
        }
    }

    public UnaryOp parserUnaryOp() {
        RW.TYPE type = ts.now().type;
        if (type == RW.TYPE.PLUS || type == RW.TYPE.MINU || type == RW.TYPE.NOT) {
            TokenNode tokenNode = new TokenNode(ts.now());
            ts.move();
            return new UnaryOp(tokenNode);
        } else {
            throw new RuntimeException("sign out of + - ! in unaryOp");
        }
    }

    public MulExp parserMulExp() {
        MulExp mulExp = new MulExp(parserUnaryExp());
        RW.TYPE type = ts.now().type;
        while (type == RW.TYPE.MULT || type == RW.TYPE.DIV || type == RW.TYPE.MOD) {
            TokenNode tokenNode = new TokenNode(ts.now());
            ts.move();
            mulExp.addUnaryExp(tokenNode, parserUnaryExp());
            type = ts.now().type;
        }
        return mulExp;
    }

    public AddExp parserAddExp() {
        AddExp addExp = new AddExp(parserMulExp());
        RW.TYPE type = ts.now().type;
        while (type == RW.TYPE.PLUS || type == RW.TYPE.MINU) {
            TokenNode tokenNode = new TokenNode(ts.now());
            ts.move();
            addExp.addMulExp(tokenNode, parserMulExp());
            type = ts.now().type;
        }
        return addExp;
    }

    public RelExp parserRelExp() {
        RelExp relExp = new RelExp(parserAddExp());
        RW.TYPE type = ts.now().type;
        while (type == RW.TYPE.LSS || type == RW.TYPE.LEQ || type == RW.TYPE.GRE || type == RW.TYPE.GEQ) {
            TokenNode tokenNode = new TokenNode(ts.now());
            ts.move();
            relExp.addAddExp(tokenNode, parserAddExp());
            type = ts.now().type;
        }
        return relExp;
    }

    public EqExp parserEqExp() {
        EqExp eqExp = new EqExp(parserRelExp());
        RW.TYPE type = ts.now().type;
        while (type == RW.TYPE.EQL || type == RW.TYPE.NEQ) {
            TokenNode tokenNode = new TokenNode(ts.now());
            ts.move();
            eqExp.addRelExp(tokenNode, parserRelExp());
            type = ts.now().type;
        }
        return eqExp;
    }

    public LAndExp parserLAndExp() {
        LAndExp lAndExp = new LAndExp(parserEqExp());
        RW.TYPE type = ts.now().type;
        while (type == RW.TYPE.AND) {
            ts.move();
            lAndExp.addEqExp(parserEqExp());
            type = ts.now().type;
        }
        return lAndExp;
    }

    public LOrExp parserLOrExp() {
        LOrExp lOrExp = new LOrExp(parserLAndExp());
        RW.TYPE type = ts.now().type;
        while (type == RW.TYPE.OR) {
            ts.move();
            lOrExp.addLAndExp(parserLAndExp());
            type = ts.now().type;
        }
        return lOrExp;
    }

    public ConstExp parserConstExp() {
        return new ConstExp(parserAddExp());
    }

    public Cond parserCondExp() {
        return new Cond(parserLOrExp());
    }

    public Exp parserExp() {
        return new Exp(parserAddExp());
    }

    public Number parserNumber() {
        ts.checkError(RW.TYPE.INTCON);
        Number number = new Number(new TokenNode(ts.now()));
        ts.move();
        return number;
    }

}
