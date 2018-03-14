package com.lxz;

import com.lxz.sproto.SprotoPack;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        TestProtoClass protoObj = new TestProtoClass();
        protoObj.setName("测试wenbenasdfasdf asdf ");
        protoObj.setLevel(-999999999);
        protoObj.setId(2131236);
        List<Integer> dd  = new ArrayList<>();
        dd.add(2);
        dd.add(445);
        dd.add(-223);
        dd.add(-2332223);
        protoObj.setIntArr(dd);

        TestProtoClass2 protoObj21 = new TestProtoClass2();
        protoObj21.setName("打算阿道夫啊发顺丰");

        protoObj.setTestObj(protoObj21);

        byte[] data = protoObj.encode();

        SprotoPack packer = new SprotoPack();
        data = packer.pack(data,0);
         data = packer.unpack(data,0);

        TestProtoClass protoObj2 = new TestProtoClass(data);

        System.out.println(protoObj2.getName());
        System.out.println(protoObj2.getLevel());
        System.out.println(protoObj2.getId());
        System.out.println(protoObj2.getIntArr().toString());
        System.out.println(protoObj2.getTestObj().getName());
    }
}
