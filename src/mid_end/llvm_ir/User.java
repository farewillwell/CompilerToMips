package mid_end.llvm_ir;

import mid_end.llvm_ir.type.LLVMType;

import java.util.ArrayList;

public class User extends Value {
    public ArrayList<Value> paras;

    public User() {
        super();
        paras = new ArrayList<>();
    }

    public User(LLVMType type) {
        super((type));
        paras = new ArrayList<>();
    }

    public void addValue(Value value) {
        this.paras.add(value);
        if (this instanceof Instr) {
            value.addUser((Instr) this);
        }
    }


}
