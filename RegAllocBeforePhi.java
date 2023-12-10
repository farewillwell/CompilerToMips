package optimization;

import back_end.Mips.Register;
import back_end.Mips.VarManager;
import mid_end.llvm_ir.*;
import mid_end.llvm_ir.Instrs.PhiInstr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class RegAllocBeforePhi {

    private VarManager manager = null;

    public void solve(IRModule irModule) {
        for (Function function : irModule.functions) {
            manager = function.varManager;
            alloc(function);
        }
    }

    private void alloc(Function function) {
        ArrayList<Param> params = function.funcParams;
        for (int i = 0; i < params.size() && manager.hashMap.keySet().size() != manager.usableRegs.size(); i++) {
            Register register = allocReg();
            manager.varSReg.put(params.get(i), register);
            manager.hashMap.put(register, params.get(i));
        }
        //这里应该仅仅对入口使用,否则会在支配树的dfs中额外多出很多操作
        System.out.println("-------------alloc for " + function.name + " ---------------");
        basicAlloc(function.basicBlocks.get(0));
    }

    private void basicAlloc(BasicBlock block) {
        ArrayList<Instr> instrList = block.instrList;
        HashSet<Value> dead = new HashSet<>();
        HashMap<Value, Value> lastUse = new HashMap<>();
        for (Instr instr : instrList) {
            for (Value value : instr.paras) {
                if ((value instanceof LocalVar || value instanceof Param)) {
                    lastUse.put(value, instr);
                }
            }
        }
        for (Instr instr : instrList) {
            if (!(instr instanceof PhiInstr)) {
                for (Value value : instr.paras) {
                    if (lastUse.get(value) == instr && !block.out.contains(value) && manager.varSReg.containsKey(value)) {
                        manager.hashMap.remove(manager.varSReg.get(value));
                        dead.add(value);
                    }
                }
            }
            if (instr.getAns() != null) {
                if (manager.hashMap.keySet().size() != manager.usableRegs.size()) {
                    Register register = allocReg();
                    if (manager.hashMap.containsKey(register)) {
                        manager.varSReg.remove(manager.hashMap.get(register));
                    }
                }
            }
        }
        for (BasicBlock basicBlock : block.beImmDom) {
            HashMap<Register, Value> stack = new HashMap<>();
            for (Register register : manager.hashMap.keySet()) {
                Value value = manager.hashMap.get(register);
                if (!basicBlock.in.contains(value)) {
                    stack.put(register, value);
                }
            }
            for (Register reg : stack.keySet()) {
                manager.hashMap.remove(reg);
            }
            basicAlloc(basicBlock);
            for (Register reg : stack.keySet()) {
                manager.hashMap.put(reg, stack.get(reg));
            }
        }
        for (Value value : block.def) {
            if (manager.varSReg.containsKey(value)) {
                manager.hashMap.remove(manager.varSReg.get(value));
            }
        }
        for (Value value : dead) {
            if (manager.varSReg.containsKey(value) && !block.def.contains(value)) {
                manager.hashMap.put(manager.varSReg.get(value), value);
            }
        }
    }


    private Register allocReg() {
        LinkedList<Register> canBeUsed = new LinkedList<>(manager.usableRegs);
        canBeUsed.removeAll(manager.hashMap.keySet());
        return canBeUsed.getFirst();
    }
}
