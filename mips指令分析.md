# mips指令分析

## 指令划分

mips又很多指令，如何对其进行针对性建类？

下面列出各种指令的表

### 加载

LB 加载字节 

LBU加载字节(无符号)

LH 加载半字 

LHU加载半字(无符号)

LW 加载字 

#### 分析：

不需要非整字有关的指令，因此只需要保留

##### lw: 加载字

lw rt, offset(base)

rt:取出来的数值放的寄存器，base，一个寄存器存地址，offset，一个立即数

### 保存

SB 存储字节 

SH 存储半字 

SW 存储字 

#### 分析

显然不需要非整字有关的指令

##### sw:存字

sw rt, offset(base)

rt:要存到地址中的寄存器，base，一个寄存器地址，offset，一个立即数。

当然sw也有扩展的算法，不过需要

### R-R运算

ADD 加 

ADDU 无符号加 

SUB 减 

SUBU 无符号减 

MULT 乘 

MULTU 乘(无符号) 

DIV 除 

DIVU 除(无符号) 

SLT 小于置 1 

SLTU小于置 1(无符号)

SLL 逻辑左移 

SRL 逻辑右移 

SRA 算术右移 

SLLV 逻辑可变左移 

SRLV 逻辑可变右移 

SRAV 算术可变右移 

AND 与 

OR 或 

XOR 异或 

NOR 或非 

#### 分析：

异或和或非不需要

AND和OR必须要，移位除了左移（计算偏移量）之外都没用。

ADD，SUB，MUL，DIV需要

slt 是需要的，但是，这个很特殊的会返回1，所以要单开。

同时我了解到，s系列还有

sle:小于等于置1

sge:大于等于置1

sgt:大于置1

seq:等于置1

sne:不等于置1

### R-I 

ADDI 加立即数 加立即数(无符号)

ANDI 

ORI 或立即数 

XORI 

LUI 立即数加载至高位

SLTI 小于立即数置 1 

SLTIU 小于立即数置 1(无符号)

#### 分析

除了ADDI好像都不需要，ADDI在移动地址的时候需要，offset

### 分支

BEQ 等于转移 

BNE 不等转移 

BLEZ 小于等于 0 转移 

BGTZ 大于 0 转移 

BLTZ 小于 0 转移 

#### 分析

好像和0没啥关系，所以后三个也不需要，只需要前俩

当然这前俩也能按照label使用，不一定要是offset

### 跳转

J 跳转 

JAL 

跳转并链接 

JALR 跳转并链接寄存器

JR 跳转寄存器 

#### 分析

j直接跳，是必要的，比如那个if的

jal需要返回的地址，Jr也是必要的，用在函数调用，所以必须要

jalr的功能可以用上面的两个实现，为了防止复杂还是拆开好

#### j

j label，跳到label名里

### jr

jr rd，这个rd存的值显然是返回值，跳到rd存的值里头吧，这些label啥的都会由mars自动跑，因此不用担心

#### jal

jal label ，跳到label名里，然后将返回值（下一条指令的地址存到ra里面）

(注意保存！！！可能多跳两下ra的东西就没了)

### 传输

MFHI 读 HI 寄存器 

MFLO 读 LO 寄存器 

MTHI 写 HI 寄存器 

MTLO 写 LO 寄存器 

#### 分析

显然是不需要写hi/lo寄存器的，因此只需要俩就好了。

#### MFHI

mfhi rd ，将hi的数值存到RD中

#### MFLO

mflo rd ，将lo的数值存到RD中

### 其他

SYSCALL 系统调用，这个必须有，显然

### 伪指令

一些伪指令很好用，但是他们用的都是一号寄存器$at，所以切记不要用$at存东西。

### la

将地址加载到寄存器中，使用的是标签

la rd label

### li

将立即数加载到寄存器中

li rd imm

### move

将一个数值从一个复制到另一个寄存器内

move rs rt

（rt到rs）





![image-20231115160022233](D:\A大三课程资料\编译\Compile_mips\images\image-20231115160022233.png)

需要实现的指令表：

### lw: 加载字,sw:存字

lw rt, offset(base)

rt:取出来的数值放的寄存器，base，一个寄存器存地址，offset，一个立即数

sw rt, offset(base)

rt:要存到地址中的寄存器，base，一个寄存器地址，offset，一个立即数

### AND,OR,ADD,SUB,

### MUL，DIV

### ADDI ,sll

### BEQ 等于转移 

### BNE 不等转移 

### SLT..

sle:小于等于置1

sge:大于等于置1

sgt:大于置1

seq:等于置1

sne:不等于置1

### j,jr,jal

### syscall

### li,la,move

这些是扩展指令，需要好好考虑。

li会根据不同的操作有不同的翻译，因此我们不需要自己完成这部分的逻辑，只需要放一个li即可。

la是存进了来一个地址，主要用来

### MFHI 读 HI 寄存器 

### MFLO 读 LO 寄存器 
