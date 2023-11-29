.data
g1 : .word 1, 0, 0, -1, -1, 0, 0, 1
g2 : .word 3
g3 : .word 1, 0, 0, -1, -1, 0, 0, 1
g4 : .word 1, 2
g5 : .word 0
g6 : .word 3, 1
str_0: .asciiz ""
str_1: .asciiz "\n"
str_2: .asciiz "Hello "
str_3: .asciiz "\n"
str_4: .asciiz ""
str_5: .asciiz "\n"
str_6: .asciiz ""
str_7: .asciiz "\n"
str_8: .asciiz "judgeB 2,3 = "
str_9: .asciiz "\n"

.text

# enter main
jal main


# leave main
j END

getDif3N:
getDif3Nb0:

# %V1 = alloca i32
addi $t0, $sp, -12
sw $t0, -16($sp)

# store i32 %P_0 , i32* %V1
lw $t0, -4($sp)
lw $t1, -16($sp)
sw $t0, 0($t1)

# %V2 = alloca i32
addi $t0, $sp, -20
sw $t0, -24($sp)

# store i32 %P_1 , i32* %V2
lw $t0, -8($sp)
lw $t1, -24($sp)
sw $t0, 0($t1)

# %V3 = alloca i32
addi $t0, $sp, -28
sw $t0, -32($sp)

# store i32 1 , i32* %V3
li $t0, 1
lw $t1, -32($sp)
sw $t0, 0($t1)

# %V4 = alloca i32
addi $t0, $sp, -36
sw $t0, -40($sp)

# store i32 1 , i32* %V4
li $t0, 1
lw $t1, -40($sp)
sw $t0, 0($t1)

# %V5 = alloca i32
addi $t0, $sp, -44
sw $t0, -48($sp)

# store i32 1 , i32* %V5
li $t0, 1
lw $t1, -48($sp)
sw $t0, 0($t1)

# %V6 = alloca i32
addi $t0, $sp, -52
sw $t0, -56($sp)

# store i32 0 , i32* %V6
li $t0, 0
lw $t1, -56($sp)
sw $t0, 0($t1)

# %V7 = alloca [2 x i32]
addi $t0, $sp, -64
sw $t0, -68($sp)

# %V8 = getelementptr inbounds [2 x i32], [2 x i32]* %V7,  i32 0 ,i32 0
lw $t0, -68($sp)
addi $t0, $t0, 0
sw $t0, -72($sp)

# store i32 1 , i32* %V8
li $t0, 1
lw $t1, -72($sp)
sw $t0, 0($t1)

# %V9 = getelementptr inbounds [2 x i32], [2 x i32]* %V7,  i32 0 ,i32 1
lw $t0, -68($sp)
addi $t0, $t0, 4
sw $t0, -76($sp)

# store i32 2 , i32* %V9
li $t0, 2
lw $t1, -76($sp)
sw $t0, 0($t1)

# br label %b1
j getDif3Nb1

getDif3Nb1:

# %V10 = load i32, i32* %V3
lw $t0, -32($sp)
lw $t0, 0($t0)
sw $t0, -80($sp)

# %V11 = icmp sle i32 %V10, 3
lw $t0, -80($sp)
li $t1, 3
sle $t2, $t0, $t1
sw $t2, -84($sp)

# br i1 %V11, label %b2, label %b4
lw $t0, -84($sp)
bne $t0, $zero, getDif3Nb2
beq $t0, $zero, getDif3Nb4
getDif3Nb2:

# store i32 1 , i32* %V4
li $t0, 1
lw $t1, -40($sp)
sw $t0, 0($t1)

# br label %b5
j getDif3Nb5

getDif3Nb5:

# %V12 = load i32, i32* %V4
lw $t0, -40($sp)
lw $t0, 0($t0)
sw $t0, -88($sp)

# %V13 = icmp sle i32 %V12, 3
lw $t0, -88($sp)
li $t1, 3
sle $t2, $t0, $t1
sw $t2, -92($sp)

# br i1 %V13, label %b6, label %b8
lw $t0, -92($sp)
bne $t0, $zero, getDif3Nb6
beq $t0, $zero, getDif3Nb8
getDif3Nb6:

# store i32 1 , i32* %V5
li $t0, 1
lw $t1, -48($sp)
sw $t0, 0($t1)

# br label %b9
j getDif3Nb9

getDif3Nb9:

# %V14 = load i32, i32* %V5
lw $t0, -48($sp)
lw $t0, 0($t0)
sw $t0, -96($sp)

# %V15 = icmp sle i32 %V14, 3
lw $t0, -96($sp)
li $t1, 3
sle $t2, $t0, $t1
sw $t2, -100($sp)

# br i1 %V15, label %b10, label %b12
lw $t0, -100($sp)
bne $t0, $zero, getDif3Nb10
beq $t0, $zero, getDif3Nb12
getDif3Nb10:

# %V16 = load i32, i32* %V3
lw $t0, -32($sp)
lw $t0, 0($t0)
sw $t0, -104($sp)

# %V17 = load i32, i32* %V4
lw $t0, -40($sp)
lw $t0, 0($t0)
sw $t0, -108($sp)

# %V18 = icmp ne i32 %V16, %V17
lw $t0, -104($sp)
lw $t1, -108($sp)
sne $t2, $t0, $t1
sw $t2, -112($sp)

# br i1 %V18, label %b15, label %b14
lw $t0, -112($sp)
bne $t0, $zero, getDif3Nb15
beq $t0, $zero, getDif3Nb14
getDif3Nb15:

# %V19 = load i32, i32* %V3
lw $t0, -32($sp)
lw $t0, 0($t0)
sw $t0, -116($sp)

# %V20 = load i32, i32* %V5
lw $t0, -48($sp)
lw $t0, 0($t0)
sw $t0, -120($sp)

# %V21 = icmp ne i32 %V19, %V20
lw $t0, -116($sp)
lw $t1, -120($sp)
sne $t2, $t0, $t1
sw $t2, -124($sp)

# br i1 %V21, label %b16, label %b14
lw $t0, -124($sp)
bne $t0, $zero, getDif3Nb16
beq $t0, $zero, getDif3Nb14
getDif3Nb16:

# %V22 = load i32, i32* %V4
lw $t0, -40($sp)
lw $t0, 0($t0)
sw $t0, -128($sp)

# %V23 = load i32, i32* %V5
lw $t0, -48($sp)
lw $t0, 0($t0)
sw $t0, -132($sp)

# %V24 = icmp ne i32 %V22, %V23
lw $t0, -128($sp)
lw $t1, -132($sp)
sne $t2, $t0, $t1
sw $t2, -136($sp)

# br i1 %V24, label %b13, label %b14
lw $t0, -136($sp)
bne $t0, $zero, getDif3Nb13
beq $t0, $zero, getDif3Nb14
getDif3Nb13:

# %V25 = load i32, i32* %V6
lw $t0, -56($sp)
lw $t0, 0($t0)
sw $t0, -140($sp)

# %V26 = add i32 %V25, 1
lw $t0, -140($sp)
li $t1, 1
addu $t2, $t0, $t1
sw $t2, -144($sp)

# store i32 %V26 , i32* %V6
lw $t0, -144($sp)
lw $t1, -56($sp)
sw $t0, 0($t1)

# br label %b14
j getDif3Nb14

getDif3Nb14:

# %V27 = load i32, i32* %V5
lw $t0, -48($sp)
lw $t0, 0($t0)
sw $t0, -148($sp)

# %V28 = add i32 %V27, 1
lw $t0, -148($sp)
li $t1, 1
addu $t2, $t0, $t1
sw $t2, -152($sp)

# store i32 %V28 , i32* %V5
lw $t0, -152($sp)
lw $t1, -48($sp)
sw $t0, 0($t1)

# br label %b11
j getDif3Nb11

getDif3Nb11:

# br label %b9
j getDif3Nb9

getDif3Nb12:

# %V29 = load i32, i32* %V4
lw $t0, -40($sp)
lw $t0, 0($t0)
sw $t0, -156($sp)

# %V30 = add i32 %V29, 1
lw $t0, -156($sp)
li $t1, 1
addu $t2, $t0, $t1
sw $t2, -160($sp)

# store i32 %V30 , i32* %V4
lw $t0, -160($sp)
lw $t1, -40($sp)
sw $t0, 0($t1)

# br label %b7
j getDif3Nb7

getDif3Nb7:

# br label %b5
j getDif3Nb5

getDif3Nb8:

# %V31 = load i32, i32* %V3
lw $t0, -32($sp)
lw $t0, 0($t0)
sw $t0, -164($sp)

# %V32 = add i32 %V31, 1
lw $t0, -164($sp)
li $t1, 1
addu $t2, $t0, $t1
sw $t2, -168($sp)

# store i32 %V32 , i32* %V3
lw $t0, -168($sp)
lw $t1, -32($sp)
sw $t0, 0($t1)

# br label %b3
j getDif3Nb3

getDif3Nb3:

# br label %b1
j getDif3Nb1

getDif3Nb4:

# %V33 = load i32, i32* %V6
lw $t0, -56($sp)
lw $t0, 0($t0)
sw $t0, -172($sp)

# ret i32 %V33
lw $v0, -172($sp)
jr $ra

judgeB:
judgeBb0:

# %V1 = alloca i32
addi $t0, $sp, -12
sw $t0, -16($sp)

# store i32 %P_0 , i32* %V1
lw $t0, -4($sp)
lw $t1, -16($sp)
sw $t0, 0($t1)

# %V2 = alloca i32
addi $t0, $sp, -20
sw $t0, -24($sp)

# store i32 %P_1 , i32* %V2
lw $t0, -8($sp)
lw $t1, -24($sp)
sw $t0, 0($t1)

# %V3 = load i32, i32* %V1
lw $t0, -16($sp)
lw $t0, 0($t0)
sw $t0, -28($sp)

# %V4 = load i32, i32* %V2
lw $t0, -24($sp)
lw $t0, 0($t0)
sw $t0, -32($sp)

# %V5 = icmp sle i32 %V3, %V4
lw $t0, -28($sp)
lw $t1, -32($sp)
sle $t2, $t0, $t1
sw $t2, -36($sp)

# br i1 %V5, label %b1, label %b2
lw $t0, -36($sp)
bne $t0, $zero, judgeBb1
beq $t0, $zero, judgeBb2
judgeBb1:

# %V6 = load i32, i32* %V1
lw $t0, -16($sp)
lw $t0, 0($t0)
sw $t0, -40($sp)

# %V7 = load i32, i32* %V2
lw $t0, -24($sp)
lw $t0, 0($t0)
sw $t0, -44($sp)

# %V8 = icmp slt i32 %V6, %V7
lw $t0, -40($sp)
lw $t1, -44($sp)
slt $t2, $t0, $t1
sw $t2, -48($sp)

# br i1 %V8, label %b3, label %b4
lw $t0, -48($sp)
bne $t0, $zero, judgeBb3
beq $t0, $zero, judgeBb4
judgeBb3:

# %V9 = load i32, i32* %V1
lw $t0, -16($sp)
lw $t0, 0($t0)
sw $t0, -52($sp)

# %V10 = load i32, i32* %V2
lw $t0, -24($sp)
lw $t0, 0($t0)
sw $t0, -56($sp)

# %V11 = sub i32 %V9, %V10
lw $t0, -52($sp)
lw $t1, -56($sp)
subu $t2, $t0, $t1
sw $t2, -60($sp)

# ret i32 %V11
lw $v0, -60($sp)
jr $ra

judgeBb4:

# %V12 = load i32, i32* %V1
lw $t0, -16($sp)
lw $t0, 0($t0)
sw $t0, -64($sp)

# %V13 = load i32, i32* %V2
lw $t0, -24($sp)
lw $t0, 0($t0)
sw $t0, -68($sp)

# %V14 = icmp eq i32 %V12, %V13
lw $t0, -64($sp)
lw $t1, -68($sp)
seq $t2, $t0, $t1
sw $t2, -72($sp)

# br i1 %V14, label %b6, label %b7
lw $t0, -72($sp)
bne $t0, $zero, judgeBb6
beq $t0, $zero, judgeBb7
judgeBb6:

# ret i32 0
li $v0, 0
jr $ra

judgeBb7:

# br label %b5
j judgeBb5

judgeBb5:

# br label %b8
j judgeBb8

judgeBb2:

# %V15 = load i32, i32* %V1
lw $t0, -16($sp)
lw $t0, 0($t0)
sw $t0, -76($sp)

# %V16 = load i32, i32* %V2
lw $t0, -24($sp)
lw $t0, 0($t0)
sw $t0, -80($sp)

# %V17 = icmp sge i32 %V15, %V16
lw $t0, -76($sp)
lw $t1, -80($sp)
sge $t2, $t0, $t1
sw $t2, -84($sp)

# br i1 %V17, label %b9, label %b10
lw $t0, -84($sp)
bne $t0, $zero, judgeBb9
beq $t0, $zero, judgeBb10
judgeBb9:

# %V18 = load i32, i32* %V1
lw $t0, -16($sp)
lw $t0, 0($t0)
sw $t0, -88($sp)

# %V19 = load i32, i32* %V2
lw $t0, -24($sp)
lw $t0, 0($t0)
sw $t0, -92($sp)

# %V20 = icmp sgt i32 %V18, %V19
lw $t0, -88($sp)
lw $t1, -92($sp)
sgt $t2, $t0, $t1
sw $t2, -96($sp)

# br i1 %V20, label %b11, label %b12
lw $t0, -96($sp)
bne $t0, $zero, judgeBb11
beq $t0, $zero, judgeBb12
judgeBb11:

# %V21 = load i32, i32* %V1
lw $t0, -16($sp)
lw $t0, 0($t0)
sw $t0, -100($sp)

# %V22 = load i32, i32* %V2
lw $t0, -24($sp)
lw $t0, 0($t0)
sw $t0, -104($sp)

# %V23 = sub i32 %V21, %V22
lw $t0, -100($sp)
lw $t1, -104($sp)
subu $t2, $t0, $t1
sw $t2, -108($sp)

# ret i32 %V23
lw $v0, -108($sp)
jr $ra

judgeBb12:

# %V24 = load i32, i32* %V1
lw $t0, -16($sp)
lw $t0, 0($t0)
sw $t0, -112($sp)

# %V25 = load i32, i32* %V2
lw $t0, -24($sp)
lw $t0, 0($t0)
sw $t0, -116($sp)

# %V26 = icmp eq i32 %V24, %V25
lw $t0, -112($sp)
lw $t1, -116($sp)
seq $t2, $t0, $t1
sw $t2, -120($sp)

# br i1 %V26, label %b14, label %b15
lw $t0, -120($sp)
bne $t0, $zero, judgeBb14
beq $t0, $zero, judgeBb15
judgeBb14:

# ret i32 0
li $v0, 0
jr $ra

judgeBb15:

# br label %b13
j judgeBb13

judgeBb13:

# br label %b10
j judgeBb10

judgeBb10:

# br label %b8
j judgeBb8

judgeBb8:

# ret i32 0
li $v0, 0
jr $ra

printArr:
printArrb0:

# %V1 = alloca i32*
addi $t0, $sp, -8
sw $t0, -12($sp)

# store i32* %P_0 , i32** %V1
lw $t0, -4($sp)
lw $t1, -12($sp)
sw $t0, 0($t1)

# %V2 = alloca i32
addi $t0, $sp, -16
sw $t0, -20($sp)

# store i32 0 , i32* %V2
li $t0, 0
lw $t1, -20($sp)
sw $t0, 0($t1)

# br label %b1
j printArrb1

printArrb1:

# %V3 = load i32, i32* %V2
lw $t0, -20($sp)
lw $t0, 0($t0)
sw $t0, -24($sp)

# %V4 = icmp slt i32 %V3, 2
lw $t0, -24($sp)
li $t1, 2
slt $t2, $t0, $t1
sw $t2, -28($sp)

# br i1 %V4, label %b2, label %b4
lw $t0, -28($sp)
bne $t0, $zero, printArrb2
beq $t0, $zero, printArrb4
printArrb2:

# %V5 = load i32, i32* %V2
lw $t0, -20($sp)
lw $t0, 0($t0)
sw $t0, -32($sp)

# %V6 = load i32*, i32** %V1
lw $t0, -12($sp)
lw $t0, 0($t0)
sw $t0, -36($sp)

# %V7 = getelementptr inbounds i32,i32* %V6, i32 %V5
lw $t0, -36($sp)
lw $t1, -32($sp)
sll $t1, $t1, 2
addu $t0, $t1, $t0
sw $t0, -40($sp)

# %V8 = load i32, i32* %V7
lw $t0, -40($sp)
lw $t0, 0($t0)
sw $t0, -44($sp)

# call void @putstr(i8* getelementptr inbounds ([1 x i8], [1 x i8]* @str_0, i32 0, i32 0))
la $a0, str_0
li $v0, 4
syscall

# call void @putint(i32 %V8)
lw $a0, -44($sp)
li $v0, 1
syscall

# %V9 = load i32, i32* %V2
lw $t0, -20($sp)
lw $t0, 0($t0)
sw $t0, -48($sp)

# %V10 = add i32 %V9, 1
lw $t0, -48($sp)
li $t1, 1
addu $t2, $t0, $t1
sw $t2, -52($sp)

# store i32 %V10 , i32* %V2
lw $t0, -52($sp)
lw $t1, -20($sp)
sw $t0, 0($t1)

# br label %b3
j printArrb3

printArrb3:

# br label %b1
j printArrb1

printArrb4:

# call void @putstr(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @str_1, i32 0, i32 0))
la $a0, str_1
li $v0, 4
syscall

# ret void
jr $ra

printArr2:
printArr2b0:

# %V1 = alloca [2 x i32]*
addi $t0, $sp, -8
sw $t0, -12($sp)

# store [2 x i32]* %P_0 , [2 x i32]** %V1
lw $t0, -4($sp)
lw $t1, -12($sp)
sw $t0, 0($t1)

# %V2 = alloca i32
addi $t0, $sp, -16
sw $t0, -20($sp)

# store i32 0 , i32* %V2
li $t0, 0
lw $t1, -20($sp)
sw $t0, 0($t1)

# br label %b1
j printArr2b1

printArr2b1:

# %V3 = load i32, i32* %V2
lw $t0, -20($sp)
lw $t0, 0($t0)
sw $t0, -24($sp)

# %V4 = icmp slt i32 %V3, 4
lw $t0, -24($sp)
li $t1, 4
slt $t2, $t0, $t1
sw $t2, -28($sp)

# br i1 %V4, label %b2, label %b4
lw $t0, -28($sp)
bne $t0, $zero, printArr2b2
beq $t0, $zero, printArr2b4
printArr2b2:

# %V5 = load i32, i32* %V2
lw $t0, -20($sp)
lw $t0, 0($t0)
sw $t0, -32($sp)

# %V6 = load [2 x i32]*, [2 x i32]** %V1
lw $t0, -12($sp)
lw $t0, 0($t0)
sw $t0, -36($sp)

# %V7 = getelementptr inbounds [2 x i32],[2 x i32]* %V6, i32 %V5
lw $t0, -36($sp)
lw $t1, -32($sp)
sll $t1, $t1, 3
addu $t0, $t1, $t0
sw $t0, -40($sp)

# %V8 = getelementptr inbounds [2 x i32], [2 x i32]* %V7,  i32 0 ,i32 0
lw $t0, -40($sp)
addi $t0, $t0, 0
sw $t0, -44($sp)

# call void @F_printArr (i32* %V8)
sw $ra, -48($sp)
lw $t0, -44($sp)
sw $t0, -52($sp)
addi $sp, $sp, -48
jal printArr

addi $sp, $sp, 48
lw $ra, -48($sp)

# %V10 = load i32, i32* %V2
lw $t0, -20($sp)
lw $t0, 0($t0)
sw $t0, -56($sp)

# %V11 = add i32 %V10, 1
lw $t0, -56($sp)
li $t1, 1
addu $t2, $t0, $t1
sw $t2, -60($sp)

# store i32 %V11 , i32* %V2
lw $t0, -60($sp)
lw $t1, -20($sp)
sw $t0, 0($t1)

# br label %b3
j printArr2b3

printArr2b3:

# br label %b1
j printArr2b1

printArr2b4:

# ret void
jr $ra

printHello:
printHellob0:

# %V1 = alloca i32
addi $t0, $sp, -4
sw $t0, -8($sp)

# %V2 = call i32 @getint()
li $v0, 5
syscall
sw $v0, -12($sp)

# store i32 %V2 , i32* %V1
lw $t0, -12($sp)
lw $t1, -8($sp)
sw $t0, 0($t1)

# %V3 = load i32, i32* %V1
lw $t0, -8($sp)
lw $t0, 0($t0)
sw $t0, -16($sp)

# call void @putstr(i8* getelementptr inbounds ([7 x i8], [7 x i8]* @str_2, i32 0, i32 0))
la $a0, str_2
li $v0, 4
syscall

# call void @putint(i32 %V3)
lw $a0, -16($sp)
li $v0, 1
syscall

# call void @putstr(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @str_3, i32 0, i32 0))
la $a0, str_3
li $v0, 4
syscall

# ret void
jr $ra

add:
addb0:

# %V1 = alloca i32
addi $t0, $sp, -8
sw $t0, -12($sp)

# store i32 %P_0 , i32* %V1
lw $t0, -4($sp)
lw $t1, -12($sp)
sw $t0, 0($t1)

# %V2 = alloca i32
addi $t0, $sp, -16
sw $t0, -20($sp)

# %V3 = load i32, i32* %V1
lw $t0, -12($sp)
lw $t0, 0($t0)
sw $t0, -24($sp)

# %V4 = add i32 %V3, 3
lw $t0, -24($sp)
li $t1, 3
addu $t2, $t0, $t1
sw $t2, -28($sp)

# store i32 %V4 , i32* %V2
lw $t0, -28($sp)
lw $t1, -20($sp)
sw $t0, 0($t1)

# %V5 = load i32, i32* %V2
lw $t0, -20($sp)
lw $t0, 0($t0)
sw $t0, -32($sp)

# ret i32 %V5
lw $v0, -32($sp)
jr $ra

opp:
oppb0:

# ret void
jr $ra

main:
mainb0:

# %V1 = alloca i32
addi $t0, $sp, -4
sw $t0, -8($sp)

# store i32 3 , i32* %V1
li $t0, 3
lw $t1, -8($sp)
sw $t0, 0($t1)

# %V2 = alloca i32
addi $t0, $sp, -12
sw $t0, -16($sp)

# store i32 0 , i32* %V2
li $t0, 0
lw $t1, -16($sp)
sw $t0, 0($t1)

# %V3 = alloca i32
addi $t0, $sp, -20
sw $t0, -24($sp)

# store i32 1 , i32* %V3
li $t0, 1
lw $t1, -24($sp)
sw $t0, 0($t1)

# %V4 = load i32, i32* %V3
lw $t0, -24($sp)
lw $t0, 0($t0)
sw $t0, -28($sp)

# %V5 = icmp eq i32 %V4, 0
lw $t0, -28($sp)
li $t1, 0
seq $t2, $t0, $t1
sw $t2, -32($sp)

# br i1 %V5, label %b1, label %b2
lw $t0, -32($sp)
bne $t0, $zero, mainb1
beq $t0, $zero, mainb2
mainb1:

# store i32 0 , i32* %V3
li $t0, 0
lw $t1, -24($sp)
sw $t0, 0($t1)

# br label %b2
j mainb2

mainb2:

# store i32 1 , i32* %V3
li $t0, 1
lw $t1, -24($sp)
sw $t0, 0($t1)

# store i32 -1 , i32* %V3
li $t0, -1
lw $t1, -24($sp)
sw $t0, 0($t1)

# %V6 = alloca i32
addi $t0, $sp, -36
sw $t0, -40($sp)

# %V7 = load i32, i32* %V2
lw $t0, -16($sp)
lw $t0, 0($t0)
sw $t0, -44($sp)

# %V8 = add i32 %V7, 1
lw $t0, -44($sp)
li $t1, 1
addu $t2, $t0, $t1
sw $t2, -48($sp)

# %V9 = sdiv i32 18, %V8
li $t0, 18
lw $t1, -48($sp)
div $t0, $t1
mflo $t2
sw $t2, -52($sp)

# %V10 = getelementptr inbounds [4 x [2 x i32]], [4 x [2 x i32]]* @G_next,  i32 0 ,i32 0
la $t0, g1
addi $t0, $t0, 0
sw $t0, -56($sp)

# %V11 = getelementptr inbounds [2 x i32], [2 x i32]* %V10,  i32 0 ,i32 0
lw $t0, -56($sp)
addi $t0, $t0, 0
sw $t0, -60($sp)

# %V12 = load i32, i32* %V11
lw $t0, -60($sp)
lw $t0, 0($t0)
sw $t0, -64($sp)

# %V13 = sub i32 %V9, 14
lw $t0, -52($sp)
li $t1, 14
subu $t2, $t0, $t1
sw $t2, -68($sp)

# %V14 = add i32 %V13, %V12
lw $t0, -68($sp)
lw $t1, -64($sp)
addu $t2, $t0, $t1
sw $t2, -72($sp)

# store i32 %V14 , i32* %V6
lw $t0, -72($sp)
lw $t1, -40($sp)
sw $t0, 0($t1)

# br label %b3
j mainb3

mainb3:

# %V15 = load i32, i32* %V2
lw $t0, -16($sp)
lw $t0, 0($t0)
sw $t0, -76($sp)

# %V16 = icmp sle i32 %V15, 5
lw $t0, -76($sp)
li $t1, 5
sle $t2, $t0, $t1
sw $t2, -80($sp)

# br i1 %V16, label %b4, label %b6
lw $t0, -80($sp)
bne $t0, $zero, mainb4
beq $t0, $zero, mainb6
mainb4:

# %V17 = load i32, i32* %V2
lw $t0, -16($sp)
lw $t0, 0($t0)
sw $t0, -84($sp)

# %V18 = icmp eq i32 %V17, 3
lw $t0, -84($sp)
li $t1, 3
seq $t2, $t0, $t1
sw $t2, -88($sp)

# br i1 %V18, label %b7, label %b8
lw $t0, -88($sp)
bne $t0, $zero, mainb7
beq $t0, $zero, mainb8
mainb7:

# %V19 = load i32, i32* %V2
lw $t0, -16($sp)
lw $t0, 0($t0)
sw $t0, -92($sp)

# %V20 = add i32 %V19, 1
lw $t0, -92($sp)
li $t1, 1
addu $t2, $t0, $t1
sw $t2, -96($sp)

# store i32 %V20 , i32* %V2
lw $t0, -96($sp)
lw $t1, -16($sp)
sw $t0, 0($t1)

# br label %b5
j mainb5

mainb8:

# %V21 = load i32, i32* %V2
lw $t0, -16($sp)
lw $t0, 0($t0)
sw $t0, -100($sp)

# %V22 = icmp eq i32 %V21, 5
lw $t0, -100($sp)
li $t1, 5
seq $t2, $t0, $t1
sw $t2, -104($sp)

# br i1 %V22, label %b9, label %b10
lw $t0, -104($sp)
bne $t0, $zero, mainb9
beq $t0, $zero, mainb10
mainb9:

# br label %b6
j mainb6

mainb10:

# %V23 = alloca i32
addi $t0, $sp, -108
sw $t0, -112($sp)

# %V24 = load i32, i32* %V2
lw $t0, -16($sp)
lw $t0, 0($t0)
sw $t0, -116($sp)

# store i32 %V24 , i32* %V23
lw $t0, -116($sp)
lw $t1, -112($sp)
sw $t0, 0($t1)

# br label %b11
j mainb11

mainb11:

# %V25 = load i32, i32* %V2
lw $t0, -16($sp)
lw $t0, 0($t0)
sw $t0, -120($sp)

# %V26 = add i32 %V25, 1
lw $t0, -120($sp)
li $t1, 1
addu $t2, $t0, $t1
sw $t2, -124($sp)

# store i32 %V26 , i32* %V2
lw $t0, -124($sp)
lw $t1, -16($sp)
sw $t0, 0($t1)

# br label %b5
j mainb5

mainb5:

# br label %b3
j mainb3

mainb6:

# %V27 = call i32 @getint()
li $v0, 5
syscall
sw $v0, -128($sp)

# store i32 %V27 , i32* %V1
lw $t0, -128($sp)
lw $t1, -8($sp)
sw $t0, 0($t1)

# %V28 = load i32, i32* %V1
lw $t0, -8($sp)
lw $t0, 0($t0)
sw $t0, -132($sp)

# %V29 = call i32 @F_add (i32 %V28)
sw $ra, -136($sp)
lw $t0, -132($sp)
sw $t0, -140($sp)
addi $sp, $sp, -136
jal add

addi $sp, $sp, 136
lw $ra, -136($sp)
sw $v0, -144($sp)

# call void @putstr(i8* getelementptr inbounds ([1 x i8], [1 x i8]* @str_4, i32 0, i32 0))
la $a0, str_4
li $v0, 4
syscall

# call void @putint(i32 %V29)
lw $a0, -144($sp)
li $v0, 1
syscall

# call void @putstr(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @str_5, i32 0, i32 0))
la $a0, str_5
li $v0, 4
syscall

# %V30 = call i32 @F_getDif3N (i32 1 , i32 999)
sw $ra, -148($sp)
li $t0, 1
sw $t0, -152($sp)
li $t0, 999
sw $t0, -156($sp)
addi $sp, $sp, -148
jal getDif3N

addi $sp, $sp, 148
lw $ra, -148($sp)
sw $v0, -160($sp)

# store i32 %V30 , i32* %V1
lw $t0, -160($sp)
lw $t1, -8($sp)
sw $t0, 0($t1)

# %V31 = load i32, i32* %V1
lw $t0, -8($sp)
lw $t0, 0($t0)
sw $t0, -164($sp)

# call void @putstr(i8* getelementptr inbounds ([1 x i8], [1 x i8]* @str_6, i32 0, i32 0))
la $a0, str_6
li $v0, 4
syscall

# call void @putint(i32 %V31)
lw $a0, -164($sp)
li $v0, 1
syscall

# call void @putstr(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @str_7, i32 0, i32 0))
la $a0, str_7
li $v0, 4
syscall

# %V32 = call i32 @F_judgeB (i32 2 , i32 3)
sw $ra, -168($sp)
li $t0, 2
sw $t0, -172($sp)
li $t0, 3
sw $t0, -176($sp)
addi $sp, $sp, -168
jal judgeB

addi $sp, $sp, 168
lw $ra, -168($sp)
sw $v0, -180($sp)

# store i32 %V32 , i32* %V1
lw $t0, -180($sp)
lw $t1, -8($sp)
sw $t0, 0($t1)

# %V33 = load i32, i32* %V1
lw $t0, -8($sp)
lw $t0, 0($t0)
sw $t0, -184($sp)

# call void @putstr(i8* getelementptr inbounds ([14 x i8], [14 x i8]* @str_8, i32 0, i32 0))
la $a0, str_8
li $v0, 4
syscall

# call void @putint(i32 %V33)
lw $a0, -184($sp)
li $v0, 1
syscall

# call void @putstr(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @str_9, i32 0, i32 0))
la $a0, str_9
li $v0, 4
syscall

# call void @F_printHello ()
sw $ra, -188($sp)
addi $sp, $sp, -188
jal printHello

addi $sp, $sp, 188
lw $ra, -188($sp)

# %V35 = getelementptr inbounds [4 x [2 x i32]], [4 x [2 x i32]]* @G_next,  i32 0 ,i32 0
la $t0, g1
addi $t0, $t0, 0
sw $t0, -192($sp)

# call void @F_printArr2 ([2 x i32]* %V35)
sw $ra, -196($sp)
lw $t0, -192($sp)
sw $t0, -200($sp)
addi $sp, $sp, -196
jal printArr2

addi $sp, $sp, 196
lw $ra, -196($sp)

# %V37 = getelementptr inbounds [4 x [2 x i32]], [4 x [2 x i32]]* @G_next,  i32 0 ,i32 0
la $t0, g1
addi $t0, $t0, 0
sw $t0, -204($sp)

# %V38 = getelementptr inbounds [2 x i32], [2 x i32]* %V37,  i32 0 ,i32 0
lw $t0, -204($sp)
addi $t0, $t0, 0
sw $t0, -208($sp)

# call void @F_printArr (i32* %V38)
sw $ra, -212($sp)
lw $t0, -208($sp)
sw $t0, -216($sp)
addi $sp, $sp, -212
jal printArr

addi $sp, $sp, 212
lw $ra, -212($sp)

# %V40 = getelementptr inbounds [2 x i32], [2 x i32]* @G_arr,  i32 0 ,i32 0
la $t0, g6
addi $t0, $t0, 0
sw $t0, -220($sp)

# call void @F_printArr (i32* %V40)
sw $ra, -224($sp)
lw $t0, -220($sp)
sw $t0, -228($sp)
addi $sp, $sp, -224
jal printArr

addi $sp, $sp, 224
lw $ra, -224($sp)

# ret i32 0
li $v0, 0
jr $ra

END:
li $v0, 10
syscall

