package back_end.Mips;

public enum Register {
    ZERO("$zero"),
    AT("$at"),  // never use this !!!!!
    V0("$v0"),
    V1("$v1"),
    A0("$a0"),
    A1("$a1"),
    A2("$a2"),
    A3("$a3"),
    //-----------------------------------------------------------中转寄存器
    T0("$t0"),// 一般用t0作为指令的参数,不存东西
    T1("$t1"),// 一般用t1作为指令的参数,不存东西
    // -----------------------------------------------------------
    T2("$t2"),
    T3("$t3"),
    T4("$t4"),
    T5("$t5"),
    T6("$t6"),
    T7("$t7"),
    S0("$s0"),
    S1("$s1"),
    S2("$s2"),
    S3("$s3"),
    S4("$s4"),
    S5("$s5"),
    S6("$s6"),
    S7("$s7"),
    T8("$t8"),
    T9("$t9"),
    K0("$k0"),
    K1("$k1"),
    GP("$gp"),
    SP("$sp"),
    FP("$fp"),
    RA("$ra");
    private final String name;

    Register(String name) {
        this.name = name;
    }

    // k0k1按理来说是不该被使用的，但是考虑到并没有和操作系统进行交互，所以这俩是没用的
    // 自主可用的寄存器, t0-gp , 共22个(谨记zero不能用来load,所以只能在特殊用途)
    // 而且基础的两个操作数是t0,t1因此不用这两个来存变量.
    // fp 不知道能干啥用，但是似乎能追踪什么的，还留着不用了。
    // 结果值怎么办?首先肯定会存到寄存器里，然后立即放到地址空间里比较合适。
    @Override
    public String toString() {
        return name;
    }
    // 考虑到短路求值，事实上不会出现and运算符，所以我们可以用一个寄存器来专门指定存放的比较值.
    // 比如就用fp来存这个就是不错的行为

    public static Register getWithIndex(int index) {
        //if (index >= 11 && index <= 28) {
        return values()[index];
        //}
        // 不能用的,zero,at,v0,v1,a0-a3,sp,ra
        //throw new RuntimeException("use which shall never be used by index " + index);
    }


}
