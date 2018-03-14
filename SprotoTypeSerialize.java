package com.lxz.sproto;

import sun.security.ssl.Debug;

import java.util.List;

public class SprotoTypeSerialize {
    private int header_idx;
    private int header_sz;
    private int header_cap = SprotoTypeSize.sizeof_header;

    private SprotoStream data;
    private int data_idx;

    private int lasttag = -1;
    private int index = 0;

    public SprotoTypeSerialize (int max_field_count) {
        this.header_sz = SprotoTypeSize.sizeof_header + max_field_count * SprotoTypeSize.sizeof_field;
    }

    private void set_header_fn(int fn) {
        this.data.setByIndex(this.header_idx - 2, (byte) (fn & 0xff));
        this.data.setByIndex(this.header_idx - 1, (byte) ((fn >> 8) & 0xff));
    }

    private void write_header_record(int record) {
        this.data.setByIndex(this.header_idx + this.header_cap-2, (byte)(record & 0xff));
        this.data.setByIndex(this.header_idx + this.header_cap-1, (byte)((record >> 8) & 0xff));

        this.header_cap += 2;
        this.index++;
    }

    private void write_uint32_to_uint64_sign(boolean is_negative) {
        byte v = (byte)((is_negative)?(0xff):(0));

        this.data.WriteByte (v);
        this.data.WriteByte (v);
        this.data.WriteByte (v);
        this.data.WriteByte (v);
    }

    private void write_tag(int tag, int value) {
        int stag = tag - this.lasttag - 1;
        if (stag > 0) {
            // skip tag
            stag = (stag - 1) * 2 + 1;
            if (stag > 0xffff)
                SprotoTypeSize.error ("tag is too big.");

            this.write_header_record (stag);
        }

        this.write_header_record (value);
        this.lasttag = tag;
    }

    private void write_int32(int v) {
        this.data.WriteByte ((byte)(v & 0xff));
        this.data.WriteByte ((byte)((v >> 8) & 0xff));
        this.data.WriteByte ((byte)((v >> 16) & 0xff));
        this.data.WriteByte ((byte)((v >> 24) & 0xff));
    }

    private void write_int64(long v) {
        this.data.WriteByte ((byte)(v & 0xff));
        this.data.WriteByte ((byte)((v >> 8) & 0xff));
        this.data.WriteByte ((byte)((v >> 16) & 0xff));
        this.data.WriteByte ((byte)((v >> 24) & 0xff));
        this.data.WriteByte ((byte)((v >> 32) & 0xff));
        this.data.WriteByte ((byte)((v >> 40) & 0xff));
        this.data.WriteByte ((byte)((v >> 48) & 0xff));
        this.data.WriteByte ((byte)((v >> 56) & 0xff));
    }

    private void fill_size(int sz) {
        if (sz < 0)
            SprotoTypeSize.error ("fill invalid size.");

        this.write_int32(sz);
    }

    private int encode_integer(int v) {
        this.fill_size (4);

        this.write_int32 (v);
        return SprotoTypeSize.sizeof_length + 4;
    }

    private int encode_uint64(long v) {
        this.fill_size (8);

        this.write_int64 (v);
        return SprotoTypeSize.sizeof_length + 8;
    }

    private int encode_string(String str){
        byte[] s = str.getBytes();
        this.fill_size (s.length);
        this.data.Write (s, 0, s.length);

        return SprotoTypeSize.sizeof_length + s.length;
    }
    private int encode_bytes(byte[] s)
    {
        this.fill_size(s.length);
        this.data.Write(s, 0, s.length);

        return SprotoTypeSize.sizeof_length + s.length;
    }


    private int encode_struct(SprotoTypeBase obj){
        int sz_pos = this.data.getPosition();

        this.data.Seek (SprotoTypeSize.sizeof_length, SeekOrigin.Current);
        int len = obj.encode (this.data);
        int cur_pos = this.data.getPosition();

        this.data.Seek (sz_pos, SeekOrigin.Begin);
        this.fill_size (len);
        this.data.Seek (cur_pos, SeekOrigin.Begin);

        return SprotoTypeSize.sizeof_length + len;
    }

    private void clear() {
        this.index = 0;
        this.header_idx = 2;
        this.lasttag = -1;
        this.data = null;
        this.header_cap = SprotoTypeSize.sizeof_header;
    }

    public void write_int32(int integer, int tag)
    {
        write_integer((long)integer, tag);
    }

    public void write_float32(float f, int tag)
    {
        write_int32((int)(f * 1000), tag);
    }

    public void write_double(double f, int tag)
    {
        write_int64((long)(f * 1000), tag);
    }

    public void write_int64(long integer, int tag)
    {
        write_integer(integer, tag);
    }

    // API
    public void write_integer(long integer, int tag) {
        long vh = integer >> 31;
        int sz = (vh == 0 || vh == -1)?4:8;
        int value = 0;

        if (sz == 4) {
            int v = (int)integer;
            if (v>0 && v < 0x7fff) {
                value = (int)((v + 1) * 2);
                sz = 2;
            } else {
                sz = this.encode_integer (v);
            }

        } else if (sz == 8) {
            long v = integer;
            sz = this.encode_uint64 (v);

        } else {
            SprotoTypeSize.error("invalid integer size.");
        }

        this.write_tag (tag, value);
    }


    public void write_int32(List<Integer> integer_list, int tag)
    {
        if (integer_list == null || integer_list.size() <= 0)
            return;

        int sz_pos = this.data.getPosition();
        this.data.Seek(sz_pos + SprotoTypeSize.sizeof_length, SeekOrigin.Begin);

        int begin_pos = this.data.getPosition();
        int intlen = 4;
        this.data.Seek(begin_pos + 1, SeekOrigin.Begin);

        for (int index = 0; index < integer_list.size(); index++)
        {
            int v = integer_list.get(index);

            this.write_int32(v);
            if (intlen == 8)
            {
                boolean is_negative = ((v & 0x80000000) == 0) ? (false) : (true);
                this.write_uint32_to_uint64_sign(is_negative);
            }
        }

        // fill integer size
        int cur_pos = this.data.getPosition();
        this.data.Seek(begin_pos, SeekOrigin.Begin);
        this.data.WriteByte((byte)(intlen&0xFF));

        // fill array size
        int size = (int)(cur_pos - begin_pos);
        this.data.Seek(sz_pos, SeekOrigin.Begin);
        this.fill_size(size);

        this.data.Seek(cur_pos, SeekOrigin.Begin);
        this.write_tag(tag, 0);
    }

    public void write_int64(List<Long> integer_list, int tag)
    {
        write_integer(integer_list, tag);
    }

    public void write_integer(List<Long> integer_list, int tag) {
        if (integer_list == null || integer_list.size() <= 0)
            return;

        int sz_pos = this.data.getPosition();
        this.data.Seek (sz_pos + SprotoTypeSize.sizeof_length, SeekOrigin.Begin);

        int begin_pos = this.data.getPosition();
        int intlen = 4;
        this.data.Seek (begin_pos + 1, SeekOrigin.Begin);

        for (int index = 0; index < integer_list.size(); index++) {
            long v = integer_list.get(index);
            long vh = v >> 31;
            int sz = (vh == 0 || vh == -1)?4:8;

            if (sz == 4) {
                this.write_int32 ((int)v);
                if (intlen == 8) {
                    boolean is_negative = ((v & 0x80000000) == 0) ? (false) : (true);
                    this.write_uint32_to_uint64_sign (is_negative);
                }

            } else if (sz == 8) {
                if (intlen == 4) {
                    this.data.Seek (begin_pos+1, SeekOrigin.Begin);
                    for (int i = 0; i < index; i++) {
                        long value = (long)(integer_list.get(i));
                        this.write_int64 (value);
                    }
                    intlen = 8;
                }
                this.write_int64 (v);

            } else {
                SprotoTypeSize.error ("invalid integer size(" + sz + ")");
            }
        }

        // fill integer size
        int cur_pos = this.data.getPosition();
        this.data.Seek (begin_pos, SeekOrigin.Begin);
        this.data.WriteByte ((byte)(intlen&0xFF));

        // fill array size
        int size = (int)(cur_pos - begin_pos);
        this.data.Seek (sz_pos, SeekOrigin.Begin);
        this.fill_size (size);

        this.data.Seek (cur_pos, SeekOrigin.Begin);
        this.write_tag (tag, 0);
    }


    public void write_boolean(boolean b, int tag) {
        long v = (b)?(1):(0);
        this.write_integer (v, tag);
    }

    public void write_boolean(List<Boolean> b_list, int tag) {
        if (b_list == null || b_list.size() <= 0)
            return;

        this.fill_size (b_list.size());
        for (int i = 0; i < b_list.size(); i++) {
            byte v = (byte)((b_list.get(i))?(1):(0));
            this.data.WriteByte (v);
        }

        this.write_tag (tag, 0);
    }

    public void write_string(String str, int tag) {
        this.encode_string (str);
        this.write_tag (tag, 0);
    }

    public void write_string(List<String> str_list, int tag) {
        if (str_list == null || str_list.size() <= 0)
            return;

        // write size length
        int sz = 0;
        for (String v : str_list) {
            try {
                sz += SprotoTypeSize.sizeof_length + v.getBytes("UTF-8").length;
            } catch (Exception e) {

            }
        }

        this.fill_size(sz);

        // write string
        for (String v : str_list) {
            this.encode_string(v);
        }

        this.write_tag(tag, 0);
    }

    public void write_bytes(byte[] data, int tag)
    {
        this.encode_bytes(data);
        this.write_tag(tag, 0);
    }

    public void write_bytes(List<byte[]> data_list, int tag)
    {
        if (data_list == null || data_list.size() <= 0)
            return;

        // write size length
        int sz = 0;
        for (byte[] v:data_list)
        {
            sz += SprotoTypeSize.sizeof_length + v.length;
        }
        this.fill_size(sz);

        // write string
        for (byte[] v:data_list)
        {
            this.encode_bytes(v);
        }

        this.write_tag(tag, 0);
    }

    public void write_obj(SprotoTypeBase obj, int tag) {
        this.encode_struct (obj);
        this.write_tag (tag, 0);
    }

    public<T extends SprotoTypeBase > void write_obj(List<T> obj_list, int tag) {
        if (obj_list == null || obj_list.size() <= 0)
            return;

        int sz_pos = this.data.getPosition();
        this.data.Seek (SprotoTypeSize.sizeof_length, SeekOrigin.Current);

        for (SprotoTypeBase v:obj_list) {
            this.encode_struct (v);
        }

        int cur_pos = this.data.getPosition();
        int sz = (int)(cur_pos - sz_pos - SprotoTypeSize.sizeof_length);
        this.data.Seek (sz_pos, SeekOrigin.Begin);
        this.fill_size (sz);

        this.data.Seek (cur_pos, SeekOrigin.Begin);

        this.write_tag (tag, 0);
    }

    public void open(SprotoStream stream) {
        // clear state
        this.clear ();

        this.data = stream;
        this.header_idx = stream.getPosition() + this.header_cap;
        this.data_idx = this.data.Seek (this.header_sz, SeekOrigin.Current);
    }


    public int close() {
        this.set_header_fn (this.index);

        int up_count = this.header_sz - this.header_cap;
        this.data.MoveUp (this.data_idx, up_count);

        int count = this.data.getPosition() - this.header_idx + SprotoTypeSize.sizeof_header;

        // clear state
        this.clear ();

        return count;
    }
}
