declare i32 @getint()
declare void @putch(i32)
declare void @putint(i32)

@G_a = dso_local global [6 x i32] [i32 1,i32 2,i32 3,i32 4,i32 5,i32 6]

@G_b = dso_local global [3 x [3 x i32]] [[3 x i32] [i32 3,i32 8,i32 5],[3 x i32] [i32 1,i32 2,i32 0],[3 x i32] [i32 0,i32 0,i32 0]]

define dso_local i32 @F_a2 (i32 %P_0,i32* %P_1){
%V1 = alloca i32
store i32 %P_0 , i32* %V1
%V2 = alloca i32*
store i32* %P_1 , i32** %V2
%V3 = load i32, i32* %V1
%V4 = load i32*, i32** %V2
%V5 = getelementptr inbounds i32,i32* %V4, i32 2
%V6 = load i32, i32* %V5
%V7 = add  i32 %V3, %V6
ret i32 %V7
}
define dso_local i32 @F_a3 (i32 %P_0,i32* %P_1,[3 x i32]* %P_2){
%V1 = alloca i32
store i32 %P_0 , i32* %V1
%V2 = alloca i32*
store i32* %P_1 , i32** %V2
%V3 = alloca [3 x i32]*
store [3 x i32]* %P_2 , [3 x i32]** %V3
%V4 = load i32, i32* %V1
%V5 = load i32*, i32** %V2
%V6 = getelementptr inbounds i32,i32* %V5, i32 1
%V7 = load i32, i32* %V6
%V8 = mul  i32 %V4, %V7
%V9 = load [3 x i32]*, [3 x i32]** %V3
%V10 = getelementptr inbounds [3 x i32],[3 x i32]* %V9, i32 2
%V11 = getelementptr inbounds [3 x i32], [3 x i32]* %V10,  i32 0 ,i32 1
%V12 = load i32, i32* %V11
%V13 = sub  i32 %V8, %V12
ret i32 %V13
}
define dso_local i32 @main() {
%V1 = alloca [2 x [3 x i32]]
%V2 = getelementptr inbounds [2 x [3 x i32]], [2 x [3 x i32]]* %V1,  i32 0 ,i32 0
%V3 = getelementptr inbounds [3 x i32], [3 x i32]* %V2,  i32 0 ,i32 0
store i32 1 , i32* %V3
%V4 = getelementptr inbounds [3 x i32], [3 x i32]* %V2,  i32 0 ,i32 1
store i32 2 , i32* %V4
%V5 = getelementptr inbounds [3 x i32], [3 x i32]* %V2,  i32 0 ,i32 2
store i32 3 , i32* %V5
%V6 = getelementptr inbounds [2 x [3 x i32]], [2 x [3 x i32]]* %V1,  i32 0 ,i32 1
%V7 = getelementptr inbounds [3 x i32], [3 x i32]* %V6,  i32 0 ,i32 0
store i32 0 , i32* %V7
%V8 = getelementptr inbounds [3 x i32], [3 x i32]* %V6,  i32 0 ,i32 1
store i32 0 , i32* %V8
%V9 = getelementptr inbounds [3 x i32], [3 x i32]* %V6,  i32 0 ,i32 2
store i32 0 , i32* %V9
%V10 = alloca i32
%V11 = getelementptr inbounds [6 x i32], [6 x i32]* @G_a,  i32 0 ,i32 4
%V12 = load i32, i32* %V11
%V13 = getelementptr inbounds [6 x i32], [6 x i32]* @G_a,  i32 0 ,i32 0
%V14 = call i32 @F_a2 (i32 %V12 , i32* %V13)
store i32 %V14 , i32* %V10
%V15 = alloca i32
%V16 = getelementptr inbounds [3 x [3 x i32]], [3 x [3 x i32]]* @G_b,  i32 0 ,i32 0
%V17 = getelementptr inbounds [3 x i32], [3 x i32]* %V16,  i32 0 ,i32 1
%V18 = load i32, i32* %V17
%V19 = getelementptr inbounds [3 x [3 x i32]], [3 x [3 x i32]]* @G_b,  i32 0 ,i32 1
%V20 = getelementptr inbounds [3 x i32], [3 x i32]* %V19,  i32 0 ,i32 0
%V21 = getelementptr inbounds [3 x [3 x i32]], [3 x [3 x i32]]* @G_b,  i32 0 ,i32 0
%V22 = call i32 @F_a3 (i32 %V18 , i32* %V20 , [3 x i32]* %V21)
store i32 %V22 , i32* %V15
%V23 = load i32, i32* %V10
%V24 = load i32, i32* %V15
%V25 = add  i32 %V23, %V24
ret i32 %V25
}

