package mid_end.llvm_ir.Instrs;

import mid_end.llvm_ir.IRBuilder;
import mid_end.llvm_ir.Instr;
import mid_end.llvm_ir.LocalVar;
import mid_end.llvm_ir.Value;
import mid_end.llvm_ir.type.ArrayType;
import mid_end.llvm_ir.type.LLVMType;
import mid_end.llvm_ir.type.PointerType;

public class GEPInstr extends Instr {
    //指针指的是否还是指针.
    private final boolean isMemberPointer;

    // name是输出用的，不需要查表
    // 传进来的是一个指针,就是那个实际变量地址的指针。
    public GEPInstr(Value tempPoint, Value offset) {
        if (!(tempPoint.type instanceof PointerType)) {
            throw new RuntimeException("use a un point to gep");
        }
        LLVMType objType = ((PointerType) tempPoint.type).objType;
        // 如果指针指的对象是数组，那么就是i32 = 0 的偏移方式
        if (objType instanceof ArrayType) {
            // 显然新变量的类型是数组的成员的指针，例如进来的是[10]的指针，那么对象就是[10]
            // 得到的结果类型就是int的指针，进来的是[1][1]的指针，对象就是[1][1]
            // 得到的就是int [1]的指针。
            isMemberPointer = false;
            addValue(tempPoint);
            addValue(offset);
            addValue(new LocalVar(new PointerType(((ArrayType) objType).memberType), false));
        }
        // 如果指针指的对象是指针，那么就是 只有一个i32的偏移方式。
        // 先取出来这个对象指针，例如 int a[],那么传进来的就是这个指针的指针(a是一个地址，这个地址所代表的内存空间
        // 存着这个指针的值)
        // 也就是说我只需要返回一个和这个变量的同级别的就可以，比如返回一个i32的指针(a[]本身就是整数指针)
        else {
            // int a[] ,先获取到 这个数组首地址，每一个元素是一个int
            // int a[][3] ,也是先获取到这个数组首地址，每一个元素是一个 [ ]
            isMemberPointer = true;
            LoadInstr loadInstr = new LoadInstr(tempPoint);
            IRBuilder.IB.addInstrForBlock(loadInstr);
            addValue(loadInstr.getAns());
            addValue(offset);
            addValue(new LocalVar(loadInstr.getAns().type, false));
        }
    }

    public Value getPointer() {
        return paras.get(0);
    }

    public Value getOffset() {
        return paras.get(1);
    }

    @Override
    public String toString() {
        Value pointer = getPointer();
        // 变量地址指针,它的类型是一个指针
        Value offset = getOffset();
        // 实际有效指针偏移
        PointerType varType = (PointerType) pointer.type;
        // 变量地址指针的类型，是指针确定了
        LLVMType targetType = varType.objType;
        // 变量地址指针指向的东西的类型，
        // 数组和指针状态不同，int a [1] 的就是一个数组,
        // int a[]的就是一个int，是元素的类型，总之target不会是一个指针
        // 判断条件？如果是 a [] [1]的话，其基本元素也是数组（和int同级）。就会错误的使用了i32 0的输出方式
        //所以看起来在预先处理的时候解决好是有必要的。
        if (isMemberPointer) {
            return getAns() + " = getelementptr inbounds "
                    + targetType + ","
                    + varType + " " + pointer + ", "
                    + offset.type + " " + offset;
        } else {
            return getAns() + " = getelementptr inbounds "
                    + targetType + ", "            // 指针指的东西
                    + varType + " " + pointer + ", "
                    // 这个指针本身的类型和它的名字，例如a[],数组a的指针
                    + " i32 0 ," + offset.type + " " + offset;
        }       //

    }
}
