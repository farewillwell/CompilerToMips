package mid_end.llvm_ir;

import mid_end.llvm_ir.type.BaseType;

public class MainFunction extends Function {
    public MainFunction() {
        super(BaseType.I32, "main");
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (BasicBlock block : basicBlocks) {
            stringBuilder.append(block.toString());
        }
        return "define dso_local i32 @main() {\n" +
                stringBuilder + "}";
    }
}
