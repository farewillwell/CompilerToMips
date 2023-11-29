package mid_end.llvm_ir.Instrs;

import back_end.Mips.AsmInstrs.*;
import back_end.Mips.MipsBuilder;
import back_end.Mips.MipsSymbol;
import back_end.Mips.Register;
import mid_end.llvm_ir.*;
import mid_end.llvm_ir.type.ArrayType;
import mid_end.llvm_ir.type.LLVMType;
import mid_end.llvm_ir.type.PointerType;

public class GEPInstr extends Instr {
    //********************************************************************************
    // 注意一个事情,无论构造的时候传进来的是*(a[1])还是*(a*),最后得到的都是一个直接指向一个对象的指针
    // 因此最后得到的指针就是直接的,不用再找一层
    //********************************************************************************
    private final boolean isInitMemberPointer;
    //在构造函数时传进来的时候指针指的是否还是指针.

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
            isInitMemberPointer = false;
            addValue(tempPoint);
            addValue(offset);
            setAns(new LocalVar(new PointerType(((ArrayType) objType).memberType), false));
        }
        // 如果指针指的对象是指针，那么就是 只有一个i32的偏移方式。
        // 先取出来这个对象指针，例如 int a[],那么传进来的就是这个指针的指针(a是一个地址，这个地址所代表的内存空间
        // 存着这个指针的值)
        // 也就是说我只需要返回一个和这个变量的同级别的就可以，比如返回一个i32的指针(a[]本身就是整数指针)
        else {
            // int a[] ,先获取到 这个数组首地址，每一个元素是一个int
            // int a[][3] ,也是先获取到这个数组首地址，每一个元素是一个 [ ]
            isInitMemberPointer = true;
            LoadInstr loadInstr = new LoadInstr(tempPoint);
            IRBuilder.IB.addInstrForBlock(loadInstr);
            addValue(loadInstr.getAns());
            addValue(offset);
            setAns(new LocalVar(loadInstr.getAns().type, false));
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
        // 所以看起来在预先处理的时候解决好是有必要的。
        if (isInitMemberPointer) {
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

    // 根据传进来时候意义的不同,offset应该对应两个意义
    // 如果传进来的时候直接指的是对象,那么offset的意义是数组中的第几个元素
    // 如果传进来的时候指的是指针,那么offset的意义是从这个指针开始偏移多少个单位
    //|-----------a0a1a2--------|sp|
    // 两者的区别仅仅在objsize的时候，剩下时候都可以统一处理，因为初始化之后其中的pointer参数指向的就是一个非指针对象了
    @Override
    public void genMipsCode() {
        super.genMipsCode();
        Value pointer = getPointer();
        // 变量地址指针,它的类型是一个指针,记住这里,假如是指针a[],那么传到
        Value numberOffset = getOffset();
        // 实际有效指针偏移
        LLVMType targetType = ((PointerType) pointer.type).objType;
        // 变量地址指针的类型，是指针确定了
        int objSize = isInitMemberPointer ? targetType.getSize() : ((ArrayType) targetType).memberType.getSize();
        // 求出指针的位置
        if (getPointer() instanceof GlobalVar) {
            new LaAsm(((GlobalVar) getPointer()).nameInMips(), Register.T0);
        } else {
            int pointerOffset = MipsBuilder.MB.queryOffset(getPointer());
            // 求出指针的值，就是元素列的首地址
            new MemAsm(MemAsm.LW, Register.T0, Register.SP, pointerOffset);
        }
        if (numberOffset instanceof Constant) {
            // 假如这个位置是a[10]的起始点？
            // 获取需要偏移的位数
            int length = ((Constant) numberOffset).getValue();
            // 获得需要的指针位置
            new AluR2IAsm(AluR2IAsm.ADDI, Register.T0, Register.T0, length * objSize);
            // 新开一个存这个得到的指针
            int ansOffset = MipsBuilder.MB.allocOnStack(4);
            // 把这个地址存到符号表里面
            MipsBuilder.MB.addVarSymbol(new MipsSymbol(getAns(), ansOffset));
            // 把得到的结果指针存到地址中
            new MemAsm(MemAsm.SW, Register.T0, Register.SP, ansOffset);
        } else {
            // 另一种很困难了，因为你根本不知道这个地址是多少
            // a[i][10] ? => 又nm要用到乘了，对性能影响大啊，很大啊
            // 先不管乘了，这个可以考虑在代码优化里面解决
            // 获取这个偏移元素的数目
            new MemAsm(MemAsm.LW, Register.T1, Register.SP, MipsBuilder.MB.queryOffset(numberOffset));
            // 把元素的长度存起来
            // 长度不会是负数，因此没问题
            if ((objSize & (objSize - 1)) == 0) {
                int shift = Integer.toBinaryString(objSize).length() - 1;
                new AluR2IAsm(AluR2IAsm.SLL, Register.T1, Register.T1, shift);
            } else {
                new LiAsm(objSize, Register.T2);
                // 相乘计算大小
                new MulDivAsm(MulDivAsm.MUL, Register.T2, Register.T1);
                // 获取最后的偏移值,存到t1里面
                new HiLoGetterAsm(HiLoGetterAsm.MFLO, Register.T1);
            }
            // 计算得到ans指针变量的值
            new AluR2RAsm(AluR2RAsm.ADDU, Register.T0, Register.T1, Register.T0);
            // 新开一个存指针变量值的空间
            int ansOffset = MipsBuilder.MB.allocOnStack(4);
            MipsBuilder.MB.addVarSymbol(new MipsSymbol(getAns(), ansOffset));
            // 把得到的指针变量值存进去
            new MemAsm(MemAsm.SW, Register.T0, Register.SP, ansOffset);
        }
    }
}
