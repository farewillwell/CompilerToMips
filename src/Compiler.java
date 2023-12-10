import back_end.Mips.MipsBuilder;
import front_end.AST.CompUnit;
import front_end.ErrorCollector;
import front_end.Lexer;
import front_end.Parser;
import front_end.TokenStream;
import mid_end.llvm_ir.IRModule;
import optimization.*;

import java.io.*;


public class Compiler {
    // 是否进行代码检查
    private static final boolean CHECK_ERROR = true;
    // 是否开优化
    private static final boolean DO_OPTIMIZE = true;

    private static final boolean MAKE_MIPS = true;

    private static final PrintStream stdout = System.out;

    private static void setOut(String file) {
        try {
            PrintStream printStream = new PrintStream(file);
            System.setOut(printStream);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

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
            irUnit = (IRModule) compUnit.getIRCode();
            irUnit.finish();
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
            new DeadCodeRemove().solve(irUnit);
            setOut("llvm_ir.txt");
            System.out.println(irUnit);
            setOut("active_set.txt");
            new ActiveAnalysis().solve(irUnit);
            setOut("reg_set.txt");
            new RegAlloc().solve(irUnit);
        }
        //--------------------------------------------------------------------------------------------
        // mips make
        if (irUnit != null && MAKE_MIPS) {
            setOut("mips.txt");
            irUnit.genMipsCode();
            System.out.println(MipsBuilder.MB.mips());
        }
    }
}