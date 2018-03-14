package com.lxz.sproto;

public class SprotoPack {
    private SprotoStream buffer;
    private byte[] tmp;

    public SprotoPack () {
        this.buffer = new SprotoStream();
        this.tmp = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};
    }

    private void write_ff(byte[] src, int offset, int pos, int n) {
        int align8_n = (n+7)&(~7);
        int cur_pos = this.buffer.getPosition();

        this.buffer.Seek (pos, SeekOrigin.Begin);

        this.buffer.WriteByte ((byte)0xff);
        this.buffer.WriteByte ((byte)((align8_n/8 - 1)&0xff));

        this.buffer.Write (src, offset, n);
        for (int i = 0; i < align8_n-n; i++) {
            this.buffer.WriteByte ((byte)0);
        }

        this.buffer.Seek (cur_pos, SeekOrigin.Begin);
    }

    private int pack_seg(byte[] src, int offset,  int ff_n) {
        byte header = 0;
        int notzero = 0;

        int header_pos = this.buffer.getPosition();
        this.buffer.Seek (1, SeekOrigin.Current);

        for (int i = 0; i < 8; i++) {
            if (src [offset + i] != 0) {
                notzero++;
                header |= (byte)(1 << i);
                this.buffer.WriteByte (src[offset + i]);
            }
        }

        if ((notzero == 7 || notzero == 6) &&ff_n > 0) {
            notzero = 8;
        }
        if (notzero == 8) {
            if (ff_n > 0) {
                this.buffer.Seek (header_pos, SeekOrigin.Begin);
                return 8;
            } else {
                this.buffer.Seek (header_pos, SeekOrigin.Begin);
                return 10;
            }
        }

        this.buffer.Seek (header_pos, SeekOrigin.Begin);
        this.buffer.WriteByte (header);
        this.buffer.Seek (header_pos, SeekOrigin.Begin);
        return notzero + 1;
    }

    public byte[] pack (byte[] data, int len) {
        this.clear ();

        int srcsz = (len==0)?(data.length):(len);
        byte[] ff_src = null;
        int   ff_srcstart = 0;
        int   ff_desstart = 0;

        int ff_n = 0;

        byte[] src = data;
        int offset = 0;

        for (int i = 0; i < srcsz; i += 8) {
            offset = i;

            int padding = i + 8 - srcsz;
            if (padding > 0) {
                for (int j = 0; j < 8 - padding; j++) {
                    this.tmp [j] = src [i + j];
                }
                for (int j = 0; j < padding; j++) {
                    this.tmp [7 - j] = 0;
                }

                src = this.tmp;
                offset = 0;
            }

            int n = this.pack_seg (src, offset,  ff_n);
            if (n == 10) {
                // first FF
                ff_src = src;
                ff_srcstart = offset;
                ff_desstart = this.buffer.getPosition();
                ff_n = 1;
            } else if (n == 8 && ff_n > 0) {
                ++ff_n;
                if (ff_n == 256) {
                    this.write_ff (ff_src, ff_srcstart, ff_desstart, 256*8);
                    ff_n = 0;
                }
            } else {
                if (ff_n > 0) {
                    this.write_ff (ff_src, ff_srcstart, ff_desstart, ff_n*8);
                    ff_n = 0;
                }
            }

            this.buffer.Seek (n, SeekOrigin.Current);
        }

        if (ff_n == 1) {
            this.write_ff (ff_src, ff_srcstart, ff_desstart, 8);
        } else if (ff_n > 1) {
            int length = (ff_src == data)?(srcsz):(ff_src.length);
            this.write_ff (ff_src, ff_srcstart, ff_desstart, length - ff_srcstart);
        }

        long maxsz = (srcsz + 2047) / 2048 * 2 + srcsz + 2;
        if (maxsz < this.buffer.getPosition()) {
            SprotoTypeSize.error ("packing error, return size="+this.buffer.getPosition());
        }

        byte[] pack_buffer = new byte[this.buffer.getPosition()];
        this.buffer.Seek (0, SeekOrigin.Begin);
        this.buffer.Read (pack_buffer, 0, pack_buffer.length);

        return pack_buffer;
    }


    public byte[] unpack (byte[] data, int len) {
        this.clear ();

        len = (len==0)?(data.length):(len);
        int srcsz = len;

        while (srcsz > 0) {
            byte header = data [len - srcsz];
            --srcsz;

            if (header == (byte)0xff) {
                if (srcsz < 0) {
                    SprotoTypeSize.error ("invalid unpack stream.");
                }

                int n = (data [len - srcsz] + 1) * 8;

                if (srcsz < n + 1) {
                    SprotoTypeSize.error ("invalid unpack stream.");
                }

                this.buffer.Write (data, len - srcsz + 1, n);
                srcsz -= n + 1;
            } else {
                for (int i = 0; i < 8; i++) {
                    int nz = (header >> i) & 1;
                    if (nz == 1) {
                        if (srcsz < 0) {
                            SprotoTypeSize.error ("invalid unpack stream.");
                        }
                        this.buffer.WriteByte (data [len - srcsz]);
                        --srcsz;
                    } else {
                        this.buffer.WriteByte ((byte)0);
                    }
                }
            }
        }

        byte[] unpack_data = new byte[this.buffer.getPosition()];
        this.buffer.Seek (0, SeekOrigin.Begin);

        this.buffer.Read (unpack_data, 0, unpack_data.length);
        return unpack_data;
    }

    private void clear() {
        this.buffer.Seek (0, SeekOrigin.Begin);

        for (int i = 0; i < this.tmp.length; i++) {
            this.tmp [i] = 0;
        }
    }
}
