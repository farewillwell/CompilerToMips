.data

.text

# enter main
jal main


# leave main
j END

main:
mainb0:

# %V1 = alloca i32
addi $t0, $sp, -4
sw $t0, -8($sp)

# store i32 1 , i32* %V1
li $t0, 1
lw $t1, -8($sp)
sw $t0, 0($t1)

# %V2 = alloca i32
addi $t0, $sp, -12
sw $t0, -16($sp)

# store i32 1 , i32* %V2
li $t0, 1
lw $t1, -16($sp)
sw $t0, 0($t1)

# %V3 = load i32, i32* %V2
lw $t0, -16($sp)
lw $t0, 0($t0)
sw $t0, -20($sp)

# %V4 = icmp eq i32 3, 0
li $t0, 3
li $t1, 0
seq $t2, $t0, $t1
sw $t2, -24($sp)

# %V5 = zext i1 %V4 to i32

# %V6 = icmp ne i32 %V3, %V5
lw $t0, -20($sp)
lw $t1, -24($sp)
sne $t2, $t0, $t1
sw $t2, -28($sp)

# br i1 %V6, label %b1, label %b2
lw $t0, -28($sp)
bne $t0, $zero, mainb1
beq $t0, $zero, mainb2
mainb1:

# store i32 0 , i32* %V1
li $t0, 0
lw $t1, -8($sp)
sw $t0, 0($t1)

# br label %b2
j mainb2

mainb2:

# %V7 = load i32, i32* %V1
lw $t0, -8($sp)
lw $t0, 0($t0)
sw $t0, -32($sp)

# call void @putint(i32 %V7)
lw $a0, -32($sp)
li $v0, 1
syscall

# ret i32 0
li $v0, 0
jr $ra

END:
li $v0, 10
syscall

