message TestProtoClass1{
    A int32 //注释测试1
    B bool
    C int64
    D int32
    E []int64
    F string
}

message TestProtoClass2{
    A binary //二进制测试
    B TestProtoClass1
    C []TestProtoClass1
}

//枚举测试
enum Test12 {
	OK					// 成功
	ERROR			    //error
    OTHER			// 其他
}