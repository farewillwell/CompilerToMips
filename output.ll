declare i32 @getint()
declare void @putch(i32)
declare void @putint(i32)

@G_a = dso_local global i32 1

define dso_local i32 @F_func (){
b0:
  store i32 2 , i32* @G_a
  ret i32 1
}
define dso_local i32 @F_func2 (){
b0:
  store i32 4 , i32* @G_a
  ret i32 10
}
define dso_local i32 @F_func3 (){
b0:
  store i32 3 , i32* @G_a
  ret i32 0
}
define dso_local i32 @main() {
b0:
  %V1 = alloca i32
  store i32 1 , i32* %V1
  %V2 = alloca i32
  store i32 1 , i32* %V2
  %V3 = alloca i32
  store i32 1 , i32* %V3
  %V4 = icmp ne i32 0, 0
  br i1 %V4, label %b1, label %b3
b3:
  %V5 = call i32 @F_func ()
  %V6 = icmp ne i32 %V5, 0
  br i1 %V6, label %b5, label %b4
b5:
  %V7 = call i32 @F_func3 ()
  %V8 = icmp ne i32 %V7, 0
  br i1 %V8, label %b1, label %b4
b4:
  %V9 = call i32 @F_func2 ()
  %V10 = icmp ne i32 %V9, 0
  br i1 %V10, label %b1, label %b2
b1:
  br label %b2
b2:
  %V11 = load i32, i32* @G_a
  store i32 %V11 , i32* %V1
  %V12 = icmp ne i32 1, 0
  br i1 %V12, label %b6, label %b8
b8:
  %V13 = call i32 @F_func3 ()
  %V14 = icmp ne i32 %V13, 0
  br i1 %V14, label %b6, label %b7
b6:
  br label %b7
b7:
  %V15 = load i32, i32* @G_a
  store i32 %V15 , i32* %V2
  %V16 = icmp ne i32 0, 0
  br i1 %V16, label %b9, label %b11
b11:
  %V17 = call i32 @F_func3 ()
  %V18 = icmp ne i32 %V17, 0
  br i1 %V18, label %b9, label %b12
b12:
  %V19 = call i32 @F_func ()
  %V20 = call i32 @F_func2 ()
  %V21 = icmp slt i32 %V19, %V20
  br i1 %V21, label %b9, label %b10
b9:
  br label %b10
b10:
  %V22 = load i32, i32* @G_a
  store i32 %V22 , i32* %V3
  %V23 = load i32, i32* %V2
  ret i32 %V23
}

