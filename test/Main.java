package com.lxz;

import com.lxz.sproto.SprotoPack;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        TestProtoClass actorInfo = new TestProtoClass();
        actorInfo.setName("测试wenbenasdfasdf asdf ");
        actorInfo.setLevel(-999999999);
        actorInfo.setId(2131236);
        List<Integer> dd  = new ArrayList<>();
        dd.add(2);
        dd.add(445);
        dd.add(-223);
        dd.add(-2332223);
        actorInfo.setIntArr(dd);

        byte[] data = actorInfo.encode();

        SprotoPack packer = new SprotoPack();
        data = packer.pack(data,0);
         data = packer.unpack(data,0);

        TestProtoClass actorInfo1 = new TestProtoClass(data);

        System.out.println(actorInfo1.getName());
        System.out.println(actorInfo1.getLevel());
        System.out.println(actorInfo1.getId());
        System.out.println(actorInfo1.getIntArr().toString());
    }
}
