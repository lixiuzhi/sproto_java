package com.lxz.sproto;

public class SprotoTypeFieldOP {

    static final int slot_bits_size = 32;
    public int[] has_bits;

    public SprotoTypeFieldOP (int max_field_count) {
        int slot_count = max_field_count / slot_bits_size;
        if(max_field_count % slot_bits_size >0) {
            slot_count++;
        }

        this.has_bits = new int[slot_count];
    }

    private int _get_array_idx(int bit_idx){
        int size = has_bits.length;
        int array_idx = bit_idx / slot_bits_size;

        return array_idx;
    }

    private int _get_slotbit_idx(int bit_idx){
        int size = has_bits.length;
        int slotbit_idx = bit_idx % slot_bits_size;

        return slotbit_idx;
    }

    public boolean has_field(int field_idx){
        int array_idx = this._get_array_idx(field_idx);
        int slotbit_idx = this._get_slotbit_idx (field_idx);

        int slot = this.has_bits [array_idx];
        int mask = (1) << (slotbit_idx);

        return (slot & mask)!=0;
    }

    public void set_field(int field_idx, boolean is_has){
        int array_idx = this._get_array_idx(field_idx);
        int slotbit_idx = this._get_slotbit_idx (field_idx);

        int slot = this.has_bits [array_idx];
        if (is_has) {
            int mask = (1) << slotbit_idx;
            this.has_bits [array_idx] = slot | mask;
        } else {
            int mask = ~((1) << slotbit_idx);
            this.has_bits [array_idx] = slot & mask;
        }
    }

    public void clear_field(){
        for(int i=0; i< this.has_bits.length; i++){
            this.has_bits [i] = 0;
        }
    }
}
