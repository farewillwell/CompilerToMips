package front_end.AST.Stmt;

import front_end.AST.Exp.Exp;
import front_end.AST.TokenNode;
import front_end.ErrorCollector;
import mid_end.llvm_ir.Constant;
import mid_end.llvm_ir.IRBuilder;
import mid_end.llvm_ir.Instrs.IO.PutCh;
import mid_end.llvm_ir.Instrs.IO.PutInt;
import mid_end.llvm_ir.Instrs.IO.PutStr;
import mid_end.llvm_ir.StringLiteral;
import mid_end.llvm_ir.Value;


import java.util.ArrayList;

public class PrintStmt extends Stmt {
    private final TokenNode formatString;
    private final ArrayList<Exp> exps;

    private final int printLine;

    public PrintStmt(TokenNode tokenNode, int printLine) {
        this.formatString = tokenNode;
        this.exps = new ArrayList<>();
        this.printLine = printLine;
    }

    public void addExp(Exp exp) {
        exps.add(exp);
    }

    @Override
    public void show() {
        System.out.println("PRINTFTK printf\n" +
                "LPARENT (");
        formatString.show();
        for (Exp exp : exps) {
            System.out.println("COMMA ,");
            exp.show();
        }
        System.out.println("RPARENT )\n" +
                "SEMICN ;");
        super.show();
    }

    private boolean wrongStringCode() {
        String string = formatString.content();
        string = string.substring(1, string.length() - 1);
        boolean formatError = false;
        for (int i = 0; i < string.length(); i++) {
            int value = string.charAt(i);
            if (value == 92 && (i + 1 >= string.length() || string.charAt(i + 1) != 'n')) {
                formatError = true;
                break;
            } else if (value == 37 && (i + 1 >= string.length() || string.charAt(i + 1) != 'd')) {
                formatError = true;
                break;
            } else if (!(value == 32 || value == 33 || (value >= 40 && value <= 126) || value == 37)) {
                formatError = true;
                break;
            }
        }
        return formatError;
    }

    private boolean checkStringParaNum() {
        String string = formatString.content();
        int sum = 0;
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == '%' && string.charAt(i + 1) == 'd') {
                sum++;
            }
        }
        return sum != exps.size();
    }

    @Override
    public void checkError(ErrorCollector errorCollector) {
        // check formatString
        if (wrongStringCode()) {
            errorCollector.addError(formatString.getLine(), "a");
        }
        if (checkStringParaNum()) {
            errorCollector.addError(printLine, "l");
        }
    }

    @Override
    public Value getIRCode() {
        ArrayList<Value> values = new ArrayList<>();
        for (Exp exp : exps) {
            values.add(exp.getIRCode());
        }
        String str = formatString.content();
        int cur = 0;
        // bug:没有处理换行符
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < str.length() - 1; i++) {
            if (str.charAt(i) != '%') {
                if (str.charAt(i) == '\\') {
                    //IRBuilder.IB.addInstrForBlock(new PutCh(new Constant('\n')));
                    sb.append("\\n");
                    //-----------------------------------------------------------------------------//
                    //注意，在llvm中这个不会体现\n,而是当作字符输出的，但是mips是正常的
                    //-----------------------------------------------------------------------------//
                    i++;
                } else {
                    //IRBuilder.IB.addInstrForBlock(new PutCh(new Constant(str.charAt(i))));
                    sb.append(str.charAt(i));
                }
            } else {
                StringLiteral st = new StringLiteral(sb.length(), sb.toString());
                IRBuilder.IB.moduleAddString(st);
                IRBuilder.IB.addInstrForBlock(new PutStr(st));
                sb = new StringBuilder();
                i++;
                IRBuilder.IB.addInstrForBlock(new PutInt(values.get(cur++)));
            }
        }
        if (sb.length() != 0) {
            StringLiteral st = new StringLiteral(sb.length(), sb.toString());
            IRBuilder.IB.moduleAddString(st);
            IRBuilder.IB.addInstrForBlock(new PutStr(st));
        }
        return null;
    }
}
