package optimization;

import mid_end.llvm_ir.*;
import mid_end.llvm_ir.Instrs.*;
import mid_end.llvm_ir.type.BaseType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;


public class PhiRemove {
    public void phi2Pc(IRModule irModule) {
        for (Function function : irModule.functions) {
            IRBuilder.IB.enterFunc(function);
            turnPhiToPC(function);
        }
    }

    public void pc2Move(IRModule irModule) {
        for (Function function : irModule.functions) {
            IRBuilder.IB.enterFunc(function);
            turnPCToMove(function);
        }
    }

    //
    private void turnPhiToPC(Function function) {
        // 因为后续要添加中间block，因此不希望在原本上遍历
        ArrayList<BasicBlock> blocks = new ArrayList<>(function.basicBlocks);
        // 插入pc
        for (BasicBlock block : blocks) {
            // 如果只有一个jump,说明可以直接插入
            if (block.lastInstr() instanceof JumpIr) {
                JumpIr jumpIr = (JumpIr) block.lastInstr();
                BasicBlock target = jumpIr.getTargetBlock();
                for (Instr instr : target.instrList) {
                    if (instr instanceof PhiIr) {
                        Value value = ((PhiIr) instr).getValueByBlock(block);
                        if (value instanceof Constant && ((Constant) value).isUndefine) {
                            continue;
                        }
                        block.insertAtLast(new PallCopyIr(instr.getAns(), value));
                    } else {
                        break;
                    }
                }
            }
            // 考虑到每个phi指令必然对于每一个该块的前驱块都有对应取值。
            // 因此只要B是A的后继节点，必然可以在B的所有phi里面找到对应的from，获取其中的值
            // 所以我们可以直接找到最后的指令判断它的后继
            else if (block.lastInstr() instanceof BranchIr) {
                BranchIr branchIr = (BranchIr) block.lastInstr();
                BasicBlock thenBlock = branchIr.getThenBlock();
                if (thenBlock.hasPhi()) {
                    BasicBlock midForThen = new BasicBlock();
                    function.basicBlocks.add(midForThen);
                    block.insertBlock(thenBlock, midForThen);
                    branchIr.changeThen(midForThen);
                    midForThen.addInstr(new JumpIr(thenBlock));
                    for (Instr instr : thenBlock.instrList) {
                        if (instr instanceof PhiIr) {
                            Value value = ((PhiIr) instr).getValueByBlock(block);
                            if (value instanceof Constant && ((Constant) value).isUndefine) {
                                continue;
                            }
                            midForThen.insertAtLast(new PallCopyIr(instr.getAns(), value));
                        } else {
                            break;
                        }
                    }
                }
                BasicBlock elseBlock = branchIr.getElseBlock();
                if (elseBlock.hasPhi()) {
                    BasicBlock midForElse = new BasicBlock();
                    function.basicBlocks.add(midForElse);
                    block.insertBlock(elseBlock, midForElse);
                    // 该insert处实现了修改前后块的功能，因此不用担心光改指令
                    branchIr.changeElse(midForElse);
                    midForElse.addInstr(new JumpIr(elseBlock));
                    for (Instr instr : elseBlock.instrList) {
                        if (instr instanceof PhiIr) {
                            Value value = ((PhiIr) instr).getValueByBlock(block);
                            if (value instanceof Constant && ((Constant) value).isUndefine) {
                                continue;
                            }
                            midForElse.insertAtLast(new PallCopyIr(instr.getAns(), value));
                        } else {
                            break;
                        }
                    }
                }
            }
            // 如果最后是一个ret,那么不用管
        }
        for (BasicBlock block : function.basicBlocks) {
            block.removePhi();
        }
    }

    private void turnPCToMove(Function function) {
        ArrayList<BasicBlock> blocks = function.basicBlocks;
        for (BasicBlock block : blocks) {
            // 对于每一个块进行消pcopy,当然,不在同一个块内的没有访问的必要
            ArrayList<MoveIr> moves = new ArrayList<>();
            LinkedList<PallCopyIr> edges = new LinkedList<>();
            Iterator<Instr> iterator = block.instrList.iterator();
            while (iterator.hasNext()) {
                Instr instr = iterator.next();
                if (instr instanceof PallCopyIr) {
                    edges.add((PallCopyIr) instr);
                    instr.isDeleted = true;
                    iterator.remove();
                }
            }
            while (edges.size() != 0) {
                PallCopyIr now = edges.removeFirst();
                if (now.getMoveIn() == now.getTarget()) {
                    continue;
                }
                boolean targetAsMoveIn = false;
                for (PallCopyIr pc : edges) {
                    if (pc.getMoveIn() == now.getTarget()) {
                        targetAsMoveIn = true;
                        break;
                    }
                }
                // 该指令的赋值不会修改其他指令要用到的值
                if (!targetAsMoveIn) {
                    // 暴力开关,事实证明如果开了会有一个点错误
                    // 所以不要开!
                    if (false) {
                        if (now.getMoveIn() instanceof Constant || now.getMoveIn() instanceof Param) {
                            moves.add(new MoveIr(now.getTarget(), now.getMoveIn()));
                        }
                        //不选择生成move,而选择用moveIn的代替所有target
                        else {
                            now.getMoveIn().allReplaceWith(now.getTarget());
                        }
                    } else {
                        moves.add(new MoveIr(now.getTarget(), now.getMoveIn()));
                    }
                } else {
                    // 该指令的赋值会影响其他move的值
                    // 那么需要把这个值给换掉
                    // 当然,从顺序上,之前的指令已经被翻译为move的，就不会出现这个问题
                    // 然后剩下所有的pc，如果其输入为这个被赋值的value,那么直接换成输入为这个temp就好
                    // 注意，不能直接扔掉，因为这个剩下的pc有可能其target是下面某一个的moveIn
                    LocalVar temp = new LocalVar(BaseType.I32, false);
                    moves.add(new MoveIr(temp, now.getMoveIn()));
                    moves.add(new MoveIr(now.getTarget(), now.getMoveIn()));
                    for (PallCopyIr pc : edges) {
                        if (pc.getMoveIn() == now.getTarget()) {
                            pc.setMoveIn(temp);
                        }
                    }
                }
            }
            for (MoveIr moveIr : moves) {
                block.insertAtLast(moveIr);
            }
        }
    }

}
