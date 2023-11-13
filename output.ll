declare i32 @getint()
declare void @putch(i32)
declare void @putint(i32)

@G_num = dso_local global i32 10

define dso_local i32 @main() {
b0:
  %V1 = alloca i32
  store i32 0 , i32* %V1
  %V2 = alloca i32
  store i32 0 , i32* %V2
  store i32 0 , i32* %V1
  br label %b1
b1:
  %V3 = load i32, i32* %V1
  %V4 = load i32, i32* @G_num
  %V5 = icmp sle i32 %V3, %V4
  br i1 %V5, label %b2, label %b4
b2:
  %V6 = load i32, i32* %V1
  %V7 = icmp eq i32 %V6, 5
  br i1 %V7, label %b5, label %b6
b5:
  br label %b3
  br label %b6
b6:
  %V8 = load i32, i32* %V1
  %V9 = icmp eq i32 %V8, 10
  br i1 %V9, label %b7, label %b8
b7:
  br label %b4
  br label %b8
b8:
  %V10 = load i32, i32* %V2
  %V11 = load i32, i32* %V1
  %V12 = add  i32 %V10, %V11
  store i32 %V12 , i32* %V2
  br label %b3
b3:
  %V13 = load i32, i32* %V1
  %V14 = add  i32 %V13, 1
  store i32 %V14 , i32* %V1
  br label %b1
b4:
  %V15 = load i32, i32* %V2
  ret i32 %V15
}

