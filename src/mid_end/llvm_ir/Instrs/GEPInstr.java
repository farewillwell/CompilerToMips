package mid_end.llvm_ir.Instrs;

import mid_end.llvm_ir.Instr;
import mid_end.llvm_ir.LocalVar;
import mid_end.llvm_ir.Value;
import mid_end.llvm_ir.type.ArrayType;
import mid_end.llvm_ir.type.LLVMType;
import mid_end.llvm_ir.type.PointerType;

public class GEPInstr extends Instr {

    // 重要特点，只要是全局的数组，那么可以直接全把东西堆进去，假如不是全局的，那么它本身和中间的也都是局部的，所以两个道路完全不一样。
    // name是输出用的，不需要查表
    // 一般传进来的是一个数组的指针，如果不是数组指针的话也没必要用这个指令，因此得到的返回值类型是指针指的数组的元素的指针。
    // 可扩展，就光当成数组就好了
    // 大道至简，把要被取地址的作为一个参数也。
    public GEPInstr(Value tempPoint, Value offset) {
        addValue(tempPoint);
        addValue(offset);
        // 这个是传进来的指针所指的数组的组成元素
        // 注意这个层次,首先传进来的是指针。
        // 指针的类型的所指类型，是一个数组
        // 要取出来的是数组的元素
        ArrayType containerType = (ArrayType) (((PointerType) tempPoint.type).objType);
        LLVMType wantType = containerType.memberType;
        addValue(new LocalVar(new PointerType(wantType), false));
    }
    // paras[0] : 要取东西的地方
    // paras[1] : 偏移量
    @Override
    public String toString() {
        // 来自的对象的指针
        PointerType point = (PointerType) paras.get(0).type;
        return getAns().toString() + " = getelementptr inbounds "
                + (point).objType + ", "            // 指针指的东西
                + paras.get(0).type + " " + paras.get(0) + ", "
                // 这个指针本身的类型和它的名字，例如a[],数组a的指针
                + " i32 0 ," + paras.get(1).type + " " + paras.get(1);        //

    }
}
