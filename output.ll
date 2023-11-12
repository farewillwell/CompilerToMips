declare i32 @getint()
declare void @putch(i32)
declare void @putint(i32)

@G_a = dso_local global [10 x i32] zeroinitializer

define dso_local i32 @main() {
%V1 = alloca [2 x [2 x i32]]
%V2 = getelementptr inbounds [2 x [2 x i32]], [2 x [2 x i32]]* %V1,  i32 0 ,i32 0
%V3 = getelementptr inbounds [2 x i32], [2 x i32]* %V2,  i32 0 ,i32 0
store i32 1 , i32* %V3
%V4 = getelementptr inbounds [2 x i32], [2 x i32]* %V2,  i32 0 ,i32 1
store i32 2 , i32* %V4
%V5 = getelementptr inbounds [2 x [2 x i32]], [2 x [2 x i32]]* %V1,  i32 0 ,i32 1
%V6 = getelementptr inbounds [2 x i32], [2 x i32]* %V5,  i32 0 ,i32 0
store i32 3 , i32* %V6
%V7 = getelementptr inbounds [2 x i32], [2 x i32]* %V5,  i32 0 ,i32 1
store i32 4 , i32* %V7
%V8 = getelementptr inbounds [2 x [2 x i32]], [2 x [2 x i32]]* %V1,  i32 0 ,i32 1
%V9 = getelementptr inbounds [2 x i32], [2 x i32]* %V8,  i32 0 ,i32 1
%V10 = load i32, i32* %V9
ret i32 %V10
}

