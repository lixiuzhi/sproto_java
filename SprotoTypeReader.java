package com.lxz.sproto;

public class SprotoTypeReader {
    public byte[] buffer;
    private int begin;
    private int pos;
    private int size;


    public int getPosition() {
         return this.pos - this.begin;
    }

    public int getOffset() {
        return this.pos;
    }

    public int getLength() {
        return this.size - this.begin;
    }

    public SprotoTypeReader (byte[] buffer, int offset, int size) {
        this.Init(buffer, offset, size);
    }

    public SprotoTypeReader() {
    }


    public void Init(byte[] buffer, int offset, int size) {
        this.begin = offset;
        this.pos = offset;
        this.buffer = buffer;
        this.size = offset + size;
        this.check ();
    }


    private void check() {
        if(this.pos > this.size || this.begin > this.pos) {
            SprotoTypeSize.error("invalid pos.");
        }
    }

    public byte ReadByte () {
        this.check();
        return this.buffer [this.pos++];
    }

    public void Seek (int offset) {
        this.pos = this.begin + offset;
        this.check ();
    }

    public void Read(byte[] data, int offset, int size) {
        int cur_pos = this.pos;
        this.pos += size;
        check ();

        for (int i = cur_pos; i < this.pos; i++) {
            data [offset + i - cur_pos] = this.buffer [i];
        }
    }
}
