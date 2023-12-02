package mid_end.llvm_ir;


import java.util.Stack;

public class IRBuilder {

    // 实际的llvm中中间代码都是 %i 形式的，而且命名都是直接@name之类的，我们为了测试方便，选择自行加一个前缀。

    public static final String GLOBAL_VAR_PRE = "@G_";
    public static final String STRING_VAR_PRE = "@str_";
    public static final String LOCAL_VAR_PRE = "%V";

    public static final String PARAM_PRE = "%P_";

    // jump to the basic block 为了方便划分，单独在基本块内部设计这个。
    public static final String BLOCK_PRE = "b";


    public static final IRBuilder IB = new IRBuilder();

    private final Stack<Loop> loopStack;

    public IRBuilder() {
        paraCnt = 0;
        stringCnt = 0;
        IRModule = new IRModule();
        loopStack = new Stack<>();
    }

    // 保证只在compUnit处获得
    public IRModule getModule() {
        return this.IRModule;
    }

    private final IRModule IRModule;

    private int paraCnt;
    private int stringCnt;

    public int globalCnt;

    public String getGlobalVarName(String name) {
        globalCnt++;
        return GLOBAL_VAR_PRE + name;
    }

    public String getParamName() {
        return PARAM_PRE + paraCnt++;
    }

    public String getStringName() {
        return STRING_VAR_PRE + stringCnt++;
    }

    public String getLocalVarName() {
        return LOCAL_VAR_PRE + nowFunc.localCnt++;
    }

    private BasicBlock nowBlock;

    private Function nowFunc;

    public void addInstrForBlock(Instr instr) {
        nowBlock.addInstr(instr);
    }

    public void moduleAddGlobal(GlobalVar globalVar) {
        IRModule.addGlobalVar(globalVar);
    }

    public void moduleAddFunc(Function function) {
        IRModule.addFunction(function);
    }

    public void moduleAddString(StringLiteral stringLiteral) {
        IRModule.addString(stringLiteral);
    }

    public void enterBlock(BasicBlock block) {
        this.nowFunc.addBasicBlock(block);
        this.nowBlock = block;
    }

    public void enterFunc(Function function) {
        paraCnt = 0;
        this.nowFunc = function;
    }

    public String getBasicBlockName() {
        return BLOCK_PRE + nowFunc.bbCnt++;
    }

    public String getNowFuncName() {
        return nowFunc.name;
    }

    public void enterLoop(Loop loop) {
        loopStack.push(loop);
    }

    public void exitLoop() {
        loopStack.pop();
    }

    public Loop getCurLoop() {
        return loopStack.peek();
    }

}
