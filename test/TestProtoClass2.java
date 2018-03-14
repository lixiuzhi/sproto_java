package com.lxz;

import com.lxz.sproto.*;
import java.util.List;

public class TestProtoClass2 extends SprotoTypeBase {
    private static int max_field_count = 1;

    public TestProtoClass2(){
        super(max_field_count);
    }

    public TestProtoClass2(byte[] buffer){
        super(max_field_count, buffer);
        this.decode ();
    }

    private String _Name; // tag 0

    public boolean HasName(){
        return super.has_field.has_field(0);
    }

    public String getName() {
        return _Name;
    }
    public void setName(String value){
        super.has_field.set_field(0,true);
        _Name = value;
    }


    protected void decode () {
        int tag = -1;
        while (-1 != (tag = super.deserialize.read_tag ())) {
            switch (tag) {
                case 0:
                    this.setName(super.deserialize.read_string());
                    break;
                default:
                    super.deserialize.read_unknow_data ();
                    break;
            }
        }
    }

    public int encode (SprotoStream stream) {
        super.serialize.open (stream);

        if (super.has_field.has_field (0)) {
            super.serialize.write_string(this.getName(), 0);
        }

        return super.serialize.close ();
    }
}
