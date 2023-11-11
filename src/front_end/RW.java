package front_end;

public class RW {
    //根据输入的字符串判断是保留字还是id

    public static void filePrint()
    {

    }
    public static TYPE getType(String string) {
        switch (string) {
            case "main":
                return TYPE.MAINTK;
            case "const":
                return TYPE.CONSTTK;
            case "int":
                return TYPE.INTTK;
            case "break":
                return TYPE.BREAKTK;
            case "continue":
                return TYPE.CONTINUETK;
            case "if":
                return TYPE.IFTK;
            case "else":
                return TYPE.ELSETK;
            case "!":
                return TYPE.NOT;
            case "&&":
                return TYPE.AND;
            case "||":
                return TYPE.OR;
            case "+":
                return TYPE.PLUS;
            case "-":
                return TYPE.MINU;
            case "*":
                return TYPE.MULT;
            case "/":
                return TYPE.DIV;
            case "%":
                return TYPE.MOD;
            case "<":
                return TYPE.LSS;
            case "<=":
                return TYPE.LEQ;
            case ">":
                return TYPE.GRE;
            case ">=":
                return TYPE.GEQ;
            case "==":
                return TYPE.EQL;
            case "!=":
                return TYPE.NEQ;
            case "=":
                return TYPE.ASSIGN;
            case ";":
                return TYPE.SEMICN;
            case ",":
                return TYPE.COMMA;
            case "(":
                return TYPE.LPARENT;
            case ")":
                return TYPE.RPARENT;
            case "[":
                return TYPE.LBRACK;
            case "]":
                return TYPE.RBRACK;
            case "{":
                return TYPE.LBRACE;
            case "}":
                return TYPE.RBRACE;
            case "for":
                return TYPE.FORTK;
            case "getint":
                return TYPE.GETINTTK;
            case "printf":
                return TYPE.PRINTFTK;
            case "return":
                return TYPE.RETURNTK;
            case "void":
                return TYPE.VOIDTK;
            default:
                return TYPE.IDENFR;
        }

    }

    public enum TYPE {
        IDENFR,// ident
        INTCON,// 整数
        STRCON, // str
        MAINTK, // main
        CONSTTK, // const
        INTTK, // int
        BREAKTK,  // break;
        CONTINUETK, //continue
        IFTK, //if
        ELSETK, //else
        NOTE,  //note
        NOT, // !
        AND, //&&
        OR, //||
        FORTK, //for
        GETINTTK, //getInt
        PRINTFTK, //printf
        RETURNTK, //return
        PLUS, //+
        MINU, //-
        VOIDTK, // void
        MULT, // *
        DIV,  // /
        MOD, //%
        LSS, //<
        LEQ, //<=
        GRE, //>
        GEQ, //>=
        EQL,  //==
        NEQ, //!=
        ASSIGN, //=

        SEMICN, //;
        COMMA,  //,
        LPARENT, //  (
        RPARENT,// )
        LBRACK, //[
        RBRACK, //]
        LBRACE, //{
        RBRACE //}
    }
}
