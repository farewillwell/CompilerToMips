import back_end.Mips.MipsBuilder;
import front_end.AST.CompUnit;
import front_end.ErrorCollector;
import front_end.Lexer;
import front_end.Parser;
import front_end.TokenStream;
import mid_end.llvm_ir.IRModule;
import optimization.GVN;
import optimization.Mem2Reg;
import optimization.PhiRemove;

import java.io.*;


public class Compiler {
    // 是否进行代码检查
    private static final boolean CHECK_ERROR = false;
    // 是否开优化
    private static final boolean DO_OPTIMIZE = true;

    private static final boolean MAKE_MIPS = true;

    private static final PrintStream stdout = System.out;

    public static void main(String[] args) {
        //-----------------------------------------------------------------------------------------
        // read in
        File file = new File("testfile.txt");
        long fileLengthLong = file.length();
        byte[] fileContent = new byte[(int) fileLengthLong + 100];
        try {
            FileInputStream inputStream = new FileInputStream("testfile.txt");
            int length = inputStream.read(fileContent);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String string = new String(fileContent);
        //-----------------------------------------------------------------------------------------
        // parser work
        Lexer lexer = new Lexer(string);
        ErrorCollector errorCollector = ErrorCollector.EC;
        TokenStream tokenStream = new TokenStream(lexer);
        Parser parser = new Parser(tokenStream);
        CompUnit compUnit = parser.parserComUnit();
        //--------------------------------------------------------------------------------------
        // error check
        if (CHECK_ERROR) {
            try {
                compUnit.checkError(errorCollector);
                errorCollector.setAllLine(lexer.getLineNum());
                PrintStream printStream = new PrintStream("error.txt");
                System.setOut(printStream);
                errorCollector.printErrors();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        //---------------------------------------------------------------------------------------
        // llvm make
        IRModule irUnit = null;
        if (!CHECK_ERROR || errorCollector.noError()) {
            try {
                PrintStream printStream = new PrintStream("llvm_origin.txt");
                irUnit = (IRModule) compUnit.getIRCode();
                irUnit.finish();
                System.setOut(printStream);
                System.out.println(irUnit);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        //-------------------------------------------------------------------------------------------
        // optimize
        /*TODO 优化待施工*/
        if (irUnit != null && DO_OPTIMIZE) {
            System.setOut(stdout);
            irUnit.doCFG();
            new Mem2Reg().solve(irUnit);
            new PhiRemove().solve(irUnit);
            new GVN().solve(irUnit);
            try {
                PrintStream printStream = new PrintStream("llvm_GVN.txt");
                System.setOut(printStream);
                System.out.println(irUnit);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        //--------------------------------------------------------------------------------------------
        // mips make
        if (irUnit != null && MAKE_MIPS) {
            try {
                PrintStream printStream = new PrintStream("mips.txt");
                System.setOut(printStream);
                irUnit.genMipsCode();
                System.out.println(MipsBuilder.MB.mips());
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}