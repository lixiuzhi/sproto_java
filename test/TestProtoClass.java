package com.lxz;

import com.lxz.sproto.*;
import java.util.List;

public class TestProtoClass extends SprotoTypeBase {
    private static int max_field_count = 5;

    public TestProtoClass(){
        super(max_field_count);
    }

    public TestProtoClass(byte[] buffer){
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

    private int _Id; // tag 1

    public boolean HasID(){
        return super.has_field.has_field(1);
    }

    public int getId() {
        return _Id;
    }

    public void setId(int v) {
        super.has_field.set_field(1, true);
        _Id = v;
    }


    private long _Level; // tag 2

    public boolean HasLevel(){
        return super.has_field.has_field(2);
    }

    public long getLevel() {
        return _Level;
    }
    public void setLevel(long v) {
        super.has_field.set_field(2,true);
        _Level = v;
    }

    private List<Integer> _IntArr; // tag 3

    public boolean HasIntArr(){
        return super.has_field.has_field(3);
    }

    public List<Integer> getIntArr() {
        return _IntArr;
    }

    public void setIntArr(List<Integer> v) {
        super.has_field.set_field(3,true);
        _IntArr = v;
    }

    private TestProtoClass2 _TestObj; // tag 4

    public boolean HasTestObj(){
        return super.has_field.has_field(4);
    }

    public TestProtoClass2 getTestObj() {
        return _TestObj;
    }

    public void setTestObj(TestProtoClass2 v) {
        super.has_field.set_field(4,true);
        _TestObj = v;
    }

    protected void decode () {
        int tag = -1;
        while (-1 != (tag = super.deserialize.read_tag ())) {
            switch (tag) {
                case 0:
                    this.setName(super.deserialize.read_string());
                    break;

                case 1:
                    this.setId(super.deserialize.read_int32());
                    break;

                case 2:
                    this.setLevel( super.deserialize.read_int64());
                    break;

                case 3:
                    this.setIntArr( super.deserialize.read_int32_list());
                    break;

                case 4:
                    this.setTestObj( super.deserialize.read_obj(()->new TestProtoClass2()));
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

        if (super.has_field.has_field (1)) {
            super.serialize.write_int32(this.getId(), 1);
        }

        if (super.has_field.has_field (2)) {
            super.serialize.write_int64(this.getLevel(), 2);
        }

        if (super.has_field.has_field (3)) {
            super.serialize.write_int32(this.getIntArr(), 3);
        }

        if (super.has_field.has_field (4)) {
            super.serialize.write_obj(this.getTestObj(), 4);
        }
        return super.serialize.close ();
    }
}
