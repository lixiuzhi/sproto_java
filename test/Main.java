package com.lxz;

import com.lxz.sproto.SprotoPack;
import java.util.ArrayList;
import java.util.List;

public class Main {


    public static void PrintfBytes(byte[] data)
    {
        String s = "";
        for(byte b:data){

            s+=b+" ";
        }
        System.out.println(s);
    }


    public static void main(String[] args) {

        TestProtoClass1 b = new TestProtoClass1();
        b.setA(-32423423);
        b.setB(true);
        b.setC(989898989);
        b.setD(2);
        List<Long> e = new ArrayList<>();
        e.add(34l);
        e.add(-23l);
        b.setE(e);
        b.setF("asdfasdfasdfsdffadssdaa阿斯顿发斯蒂芬sdfa");

        List<TestProtoClass1> c = new ArrayList<>();
        c.add(b);
        c.add(b);

        TestProtoClass2 protoObj = new TestProtoClass2();
        byte[] a = new byte[]{-1,1,2,-3};
        //PrintfBytes(a);
        protoObj.setA(a);
        protoObj.setB(b);
        protoObj.setC(c);

        byte[] data = protoObj.encode();

        SprotoPack packer = new SprotoPack();
        data = packer.pack(data,0);
         data = packer.unpack(data,0);

        TestProtoClass2 protoObj2 = new TestProtoClass2(data);

        PrintfBytes(protoObj2.getA());
        System.out.println(protoObj2.getB().getA());
        System.out.println(protoObj2.getB().getB());
        System.out.println(protoObj2.getB().getC());
        System.out.println(protoObj2.getB().getD());
        System.out.println(protoObj2.getB().getE().toString());
        System.out.println(protoObj2.getB().getF());
        System.out.println(protoObj2.getC().size());
    }
}
