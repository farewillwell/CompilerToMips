import front_end.AST.CompUnit;
import front_end.ErrorCollector;
import front_end.Lexer;
import front_end.Parser;
import front_end.TokenStream;

import java.io.*;


public class Compiler {
    // 是否进行代码检查
    private static final boolean CHECK_ERROR = false;

    public static void main(String[] args) {
        //-----------------------------------------------------------------------------------------
        // read in
        File file = new File("testfile.txt");
        long fileLengthLong = file.length();
        byte[] fileContent = new byte[(int) fileLengthLong];
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
        compUnit.checkError(errorCollector);
        errorCollector.setAllLine(lexer.getLineNum());
        //--------------------------------------------------------------------------------------
        // error check
        if (CHECK_ERROR) {
            try {
                PrintStream printStream = new PrintStream("error.txt");
                System.setOut(printStream);
                errorCollector.printErrors();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        //---------------------------------------------------------------------------------------
        // llvm make
        if (!CHECK_ERROR||errorCollector.noError()) {
            try {
                PrintStream printStream = new PrintStream("output.ll");
                System.setOut(printStream);
                System.out.println(compUnit.getIRCode().toString());
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}