package com.lxz.sproto;

public class SprotoStream {
    private int size;
    private int pos;
    private byte[] buffer;

    public int getPosition() {
        return this.pos;
    }

    public byte[] getBuffer() {
        return this.buffer;
    }

    public SprotoStream () {
        super();
        this.size = 128;
        this.pos = 0;
        this.buffer = new byte[this.size];
    }

    private void _expand(int sz) {
        if(this.size - this.pos  < sz) {
            long bak_sz = this.size;
            while (this.size - this.pos < sz) {
                this.size = this.size * 2;
            }

            if (this.size >= SprotoTypeSize.encode_max_size) {
                SprotoTypeSize.error ("object is too large (>" + SprotoTypeSize.encode_max_size + ")");
            }

            byte[] new_buffer = new byte[this.size];
            for (int i = 0; i < bak_sz; i++) {
                new_buffer [i] = this.buffer [i];
            }
            this.buffer = new_buffer;
        }
    }


    public void WriteByte(byte v) {
        this._expand(1);
        this.buffer [this.pos++] = v;
    }


    public void Write(byte[] data, int offset, int count) {
        this._expand(count);
        for (int i = 0; i < count; i++) {
            this.buffer [this.pos++] = data [offset + i];
        }
    }

    public int Seek(int offset, SeekOrigin loc) {

        if (loc == SeekOrigin.Begin) {
            this.pos = offset;
        } else if (loc == SeekOrigin.Current) {
            this.pos += offset;
        } else if (loc == SeekOrigin.End) {
            this.pos = this.size + offset;
        }
        this._expand(0);
        return this.pos;
    }

    public void Read(byte[] buf, int offset, int count) {
        for (int i = 0; i < count; i++) {
            buf[offset+i] = this.buffer[this.pos++];
        }
    }


    public void MoveUp(int position, int up_count) {
        if (up_count <= 0)
            return;

        long count = this.pos - position;
        for (int i = 0; i < count; i++) {
            this.buffer [position - up_count + i] = this.buffer [position + i];
        }
        this.pos -= up_count;
    }

    public byte getByIndex(int i){
        return this.buffer [i];
    }

    public void setByIndex(int i,byte v){
        this.buffer [i] = v;
    }
}
