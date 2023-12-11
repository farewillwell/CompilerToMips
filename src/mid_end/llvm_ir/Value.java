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

    public final ArrayList<Instr> definers = new ArrayList<>();

    private final ArrayList<Instr> userInstr = new ArrayList<>();

    public void addUser(Instr instr) {
        //System.out.println(instr);
        userInstr.add(instr);
    }

    public boolean userEmpty() {
        for (Instr instr : userInstr) {
            if (!instr.isDeleted) {
                return false;
            }
        }
        return true;
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

    public void allReplaceWith(Value newValue) {
        userReplaceMeWith(newValue);
        // 当然,实行这样的操作后可能会出现很多个definer,所以要用数组存
        ArrayList<Instr>define = new ArrayList<>(definers);
        for (Instr instr : define) {
            instr.reSetAns(newValue);
        }
    }
}
