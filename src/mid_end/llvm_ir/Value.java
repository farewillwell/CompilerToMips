package mid_end.llvm_ir;


import mid_end.llvm_ir.type.LLVMType;

import java.util.ArrayList;

public class Value {

    public LLVMType type;

    public Value() {
        type = null;
    }

    public Value(LLVMType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public void genMipsCode() {

    }

    private final ArrayList<Instr> userInstr = new ArrayList<>();

    public void addUser(Instr instr) {
        //System.out.println(instr);
        userInstr.add(instr);
    }

    public boolean userEmpty() {
        return userInstr.size() == 0;
    }
    public void removeUser(Instr instr) {
        userInstr.remove(instr);
    }

    // 这里应当无论在哪个块，都狠狠标记上，这样可以跨块搜索
    public void userReplaceMeWith(Value newValue) {
        // 防止删除的时候爆concurrent？赋值一份再删即可
        ArrayList<Instr> copy = new ArrayList<>(userInstr);
        for (Instr instr : copy) {
            instr.replace(this, newValue);
        }
    }
}
