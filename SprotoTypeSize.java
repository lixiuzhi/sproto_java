package com.lxz.sproto;

import java.lang.System;

public class SprotoTypeSize {
    public static final int sizeof_header = 2;
    public static final int sizeof_length = 4;
    public static final int sizeof_field  = 2;
    public static final int encode_max_size = 0x1000000;

    public static void error(String info) {
        System.out.println("info");
    }
}
