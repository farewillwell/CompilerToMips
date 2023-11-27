.data
str_0: .asciiz "djaslkjdal\nhdskajdlsa)"
str_1: .asciiz "1"

.text

# enter main
jal main


# leave main
j END

main:
mainb0:

# call void @putstr(i8* getelementptr inbounds ([24 x i8], [24 x i8]* @str_0, i32 0, i32 0))
la $a0, str_0
li $v0, 4
syscall

# call void @putint(i32 1)
li $a0, 1
li $v0, 1
syscall

# call void @putstr(i8* getelementptr inbounds ([2 x i8], [2 x i8]* @str_1, i32 0, i32 0))
la $a0, str_1
li $v0, 4
syscall

# ret i32 0
li $v0, 0
jr $ra

END:
li $v0, 10
syscall

