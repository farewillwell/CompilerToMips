package mid_end.llvm_ir;

import mid_end.llvm_ir.type.BaseType;

import java.util.ArrayList;

public class Function extends User {

    public Function(BaseType type, String name) {
        super(type);
        basicBlocks = new ArrayList<>();
        params = new ArrayList<>();
        this.name = IRBuilder.IB.getFuncName(name);
    }

    public final ArrayList<Param> params;

    public final ArrayList<BasicBlock> basicBlocks;

    public final String name;

    public void addPara(Param param) {
        this.params.add(param);
    }

    public void addBasicBlock(BasicBlock block) {
        basicBlocks.add(block);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("define dso_local ");
        stringBuilder.append(type.toString());
        stringBuilder.append(" ");
        stringBuilder.append(name);
        stringBuilder.append(" (");
        for (int i = 0; i < params.size(); i++) {
            stringBuilder.append(params.get(i).type);
            if (i < params.size() - 1) {
                stringBuilder.append(",");
            }
            stringBuilder.append(" ");
            stringBuilder.append(params.get(i));
        }
        stringBuilder.append("){\n");
        for (BasicBlock block : basicBlocks) {
            stringBuilder.append(block);
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
