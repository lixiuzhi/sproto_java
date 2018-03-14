package com.lxz.sproto;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SprotoTypeDeserialize {
    private SprotoTypeReader reader;
    private int begin_data_pos;
    private int cur_field_pos;

    private int fn;
    private int tag = -1;
    private int value;

    public SprotoTypeDeserialize() {
    }

    public SprotoTypeDeserialize (byte[] data) {
        this.init (data,0);
    }

    public SprotoTypeDeserialize(SprotoTypeReader reader) {
        this.init (reader);
    }

    public void init(byte[] data, int offset) {
        this.clear ();
        this.reader = new SprotoTypeReader (data, offset, data.length);
        this.init ();
    }

    public void init(SprotoTypeReader reader) {
        this.clear ();
        this.reader = reader;
        this.init ();
    }

    private void init() {
        this.fn = this.read_word ();

        int header_length = SprotoTypeSize.sizeof_header + this.fn * SprotoTypeSize.sizeof_field;
        this.begin_data_pos = header_length;
        this.cur_field_pos = this.reader.getPosition();

        if (this.reader.getLength() < header_length) {
            SprotoTypeSize.error ("invalid decode header.");
        }

        this.reader.Seek (this.begin_data_pos);
    }

    private long expand64(int value) {
        return value;
    }

    private int read_word() {
        return (int)this.reader.ReadByte ()& 0xFF |
                ((int)this.reader.ReadByte ()& 0xFF) << 8;
    }

    private int read_dword() {
        return 	(int)this.reader.ReadByte ()& 0xFF    |
                ((int)this.reader.ReadByte ()& 0xFF) << 8 |
                ((int)this.reader.ReadByte ()& 0xFF) << 16|
                ((int)this.reader.ReadByte ()& 0xFF) << 24;
    }

    private int read_array_size() {
        if (this.value >= 0)
            SprotoTypeSize.error ("invalid array value.");

        int sz = this.read_dword ();
        if (sz < 0)
            SprotoTypeSize.error ("error array size("+sz+")");

        return sz;
    }


    public int read_tag() {
        int pos = this.reader.getPosition();
        this.reader.Seek (this.cur_field_pos);

        while(this.reader.getPosition() < this.begin_data_pos){
            this.tag++;
            int value = this.read_word ();

            if( (value & 1) == 0) {
                this.cur_field_pos = this.reader.getPosition();
                this.reader.Seek(pos);
                this.value = value/2 - 1;
                return this.tag;
            }

            this.tag += value/2;
        }


        this.reader.Seek(pos);
        return -1;
    }

    public int read_int32()
    {
        return (int)read_integer();
    }

    public float read_float32( )
    {
        return (float)read_int32()  * 0.001f;
    }

    public double read_double()
    {
        return (double)read_int64() * 0.001;
    }

    public long read_int64()
    {
        return read_integer();
    }

    public long read_integer() {
        if (this.value >= 0) {
            return (long)(this.value);
        } else {
            int sz = this.read_dword ();
            if (sz == 4) {
                long v = this.expand64 (this.read_dword ());
                return (long)v;
            } else if (sz == 8) {
                int low = this.read_dword ();
                int hi  = this.read_dword ();
                long v = (long)low | (long)hi << 32;
                return (long)v;
            } else {
                SprotoTypeSize.error ("read invalid integer size (" + sz + ")");
            }
        }

        return 0;
    }

    public List<Long> read_int64_list()
    {
        return read_integer_list();
    }

    public List<Long> read_integer_list() {
        List<Long> integer_list  = new ArrayList<>();

        int sz = this.read_array_size ();
        if (sz == 0) {
            return integer_list;
        }

        int len = this.reader.ReadByte () & 0xFF;
        sz--;

        if (len == 4) {
            if (sz % 4 != 0) {
                SprotoTypeSize.error ("error array size("+sz+")@sizeof(Uint32)");
            }


            for (int i = 0; i < sz / 4; i++) {
                long v = this.expand64 (this.read_dword ());
                integer_list.add (v);
            }

        } else if (len == 8) {
            if (sz % 8 != 0) {
                SprotoTypeSize.error ("error array size("+sz+")@sizeof(Uint64)");
            }

            for (int i = 0; i < sz / 8; i++) {
                int low = this.read_dword ();
                int hi  = this.read_dword ();
                long v = (long)low | (long)hi << 32;
                integer_list.add (v);
            }

        } else {
            SprotoTypeSize.error ("error intlen("+len+")");
        }

        return integer_list;
    }

    public List<Integer> read_int32_list()
    {
        List<Integer> integer_list =new ArrayList<>();

        int sz = this.read_array_size();
        if (sz == 0)
        {
            return integer_list;
        }

        int len = this.reader.ReadByte()& 0xFF;
        sz--;

        if (len ==4)
        {
            if (sz %4 != 0)
            {
                SprotoTypeSize.error("error array size(" + sz + ")@sizeof(Uint32)");
            }


            for (int i = 0; i < sz / 4; i++)
            {
                long v = this.expand64(this.read_dword());
                integer_list.add((int)v);
            }

        }
        else if (len == 8)
        {
            if (sz %8 != 0)
            {
                SprotoTypeSize.error("error array size(" + sz + ")@sizeof(Uint64)");
            }

            for (int i = 0; i < sz /8; i++)
            {
                int low = this.read_dword();
                int hi = this.read_dword();
                long v = (long)low | (long)hi << 32;
                integer_list.add((int)v);
            }

        }
        else
        {
            SprotoTypeSize.error("error intlen(" + len + ")");
        }

        return integer_list;
    }


    public boolean read_boolean() {
        if (this.value < 0) {
            SprotoTypeSize.error ("read invalid boolean.");
            return false;
        } else {
            return (this.value ==0)?(false):(true);
        }
    }

    public List<Boolean> read_boolean_list() {
        int sz = this.read_array_size ();

        List<Boolean> boolean_list = new ArrayList<>();
        for (int i = 0; i < sz; i++) {
            boolean v = (this.reader.ReadByte() == (byte)0)?(false):(true);
            boolean_list.add (v);
        }

        return boolean_list;
    }


    public String read_string() {
        int sz = this.read_dword ();
        byte[] buffer = new byte[sz];
        this.reader.Read (buffer, 0, buffer.length);
        try{
            return new String(buffer,"utf-8");
        }catch (Exception e){

        }
        return "";
    }


    public List<String> read_string_list() {
        int sz = this.read_array_size();

        List<String> string_list = new ArrayList<>();
        for (int i = 0; sz > 0; i++) {
            if (sz < SprotoTypeSize.sizeof_length) {
                SprotoTypeSize.error("error array size.");
            }

            int hsz = this.read_dword();
            sz -= (int) SprotoTypeSize.sizeof_length;

            if (hsz > sz) {
                SprotoTypeSize.error("error array object.");
            }

            byte[] buffer = new byte[hsz];
            this.reader.Read(buffer, 0, buffer.length);
            String v = "";
            try {
                v = new String(buffer, "utf-8");
            } catch (Exception e) {
            }
            string_list.add(v);
            sz -= hsz;
        }

        return string_list;
    }


    public byte[] read_bytes()
    {
        int sz = this.read_dword();
        byte[] buffer = new byte[sz];
        this.reader.Read(buffer, 0, buffer.length);
        return buffer;
    }


    public List<byte[]> read_bytes_list()
    {
        int sz = this.read_array_size();

        List<byte[]> data_list = new ArrayList<>();
        for (int i = 0; sz > 0; i++)
        {
            if (sz < SprotoTypeSize.sizeof_length)
            {
                SprotoTypeSize.error("error array size.");
            }

            int hsz = this.read_dword();
            sz -= SprotoTypeSize.sizeof_length;

            if (hsz > sz)
            {
                SprotoTypeSize.error("error array object.");
            }

            byte[] buffer = new byte[hsz];
            this.reader.Read(buffer, 0, buffer.length);

            data_list.add(buffer);
            sz -= hsz;
        }

        return data_list;
    }


    public <T extends SprotoTypeBase> T read_obj (Supplier<T> factory) {
        int sz = (int)this.read_dword ();

        SprotoTypeReader reader = new SprotoTypeReader (this.reader.buffer, this.reader.getOffset(), sz);
        this.reader.Seek (this.reader.getPosition() + sz);

        T obj = factory.get();
        obj.init (reader);
        return obj;
    }

    private <T extends SprotoTypeBase> int read_element(T obj, SprotoTypeReader reader, int sz) {
        int read_size = 0;
        if (sz < SprotoTypeSize.sizeof_length) {
            SprotoTypeSize.error ("error array size.");
        }

        int hsz = this.read_dword ();
        sz -= (int)SprotoTypeSize.sizeof_length;
        read_size += (int)SprotoTypeSize.sizeof_length;

        if (hsz > sz) {
            SprotoTypeSize.error ("error array object.");
        }

        reader.Init(this.reader.buffer, this.reader.getOffset(), (int)hsz);
        this.reader.Seek (this.reader.getPosition() + (int)hsz);

        obj.init (reader);
        read_size += hsz;
        return read_size;
    }

    public <T extends SprotoTypeBase> List<T> read_obj_list(Supplier<T> factory){
        int sz = this.read_array_size ();

        List<T> obj_list = new ArrayList<>();
        SprotoTypeReader reader = new SprotoTypeReader ();
        for (int i = 0; sz > 0; i++) {
            int read_size;
            T obj = factory.get();
            sz-=read_element(obj,reader, sz);
            obj_list.add (obj);
        }

        return obj_list;
    }

    public void read_unknow_data() {
        if (this.value < 0) {
            int sz = (int)this.read_dword ();
            this.reader.Seek (sz + this.reader.getPosition());
        }
    }


    public int size() {
        return this.reader.getPosition();
    }

    public void clear() {
        this.fn = 0;
        this.tag = -1;
        this.value = 0;

        if (this.reader != null) {
            this.reader.Seek (0);
        }
    }
}
