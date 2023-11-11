package front_end.AST;

import front_end.ErrorCollector;
import mid_end.llvm_ir.Value;

public class Node {

    public Node() {

    }

    public void show() {

    }

    public void checkError(ErrorCollector errorCollector) {

    }

    public Value getIRCode() {
        return null;
    }
    /**TODO 原本是evaluate，但是为了无痛迁移错误处理，所以这个要换一个名字，防止出问题*/
    public int queryValue() {
        throw new RuntimeException("evaluate un valuable node");
    }


}
