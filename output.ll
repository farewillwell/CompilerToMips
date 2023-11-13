declare i32 @getint()
declare void @putch(i32)
declare void @putint(i32)

@G_a = dso_local global i32 10

@G_b = dso_local global [1 x i32] zeroinitializer

define dso_local i32 @main() {
b0:
  %V1 = load i32, i32* @G_a
  %V2 = icmp ne i32 %V1, 0
  br i1 %V2, label %b1, label %b2
b1:
  br label %b2
b2:
  %V3 = load i32, i32* @G_a
  %V4 = icmp eq i32 %V3, 0
  br i1 %V4, label %b3, label %b4
b3:
  br label %b4
b4:
}

