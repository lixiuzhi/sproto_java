# sproto_java
云风sproto的java版本,直接翻译C#版本https://github.com/lvzixun/sproto-Csharp

# 导出工具   
https://github.com/lixiuzhi/gotools

# 针对sproto schema的修改
不支持内部类   
数组用[]    
枚举将被导出成静态整数类    
## 测试协议如下：
```
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
  OK    //成功
  ERROR //error
  OTHER //其他
}
```
导出的java代码参考test目录对应的几个Test类.

## 序列化 pack unpack 反序列化 示例
```  
TestProtoClass2 protoObj = new TestProtoClass2();
byte[] data = protoObj.encode();

SprotoPack packer = new SprotoPack();
data = packer.pack(data,0);

data = packer.unpack(data,0);

TestProtoClass2 protoObj2 = new TestProtoClass2(data);
```
