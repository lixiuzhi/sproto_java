
package com.lxz;

import com.lxz.sproto.*;
import java.util.List;
import java.util.function.Supplier;

public class TestProtoClass1 extends SprotoTypeBase {

	private static int max_field_count = 6;
	public static Supplier<TestProtoClass1> proto_supplier = ()->new TestProtoClass1();

	public TestProtoClass1(){
			super(max_field_count);
	}
	
	public TestProtoClass1(byte[] buffer){
			super(max_field_count, buffer);
			this.decode ();
	} 

	 
	private int _A; // tag 0
	public boolean HasA(){
		return super.has_field.has_field(0);
	}
	public int getA() {
		return _A;
	}
	public void setA(int value){
		super.has_field.set_field(0,true);
		_A = value;
	}
 
	 
	private boolean _B; // tag 1
	public boolean HasB(){
		return super.has_field.has_field(1);
	}
	public boolean getB() {
		return _B;
	}
	public void setB(boolean value){
		super.has_field.set_field(1,true);
		_B = value;
	}
 
	 
	private long _C; // tag 2
	public boolean HasC(){
		return super.has_field.has_field(2);
	}
	public long getC() {
		return _C;
	}
	public void setC(long value){
		super.has_field.set_field(2,true);
		_C = value;
	}
 
	 
	private int _D; // tag 3
	public boolean HasD(){
		return super.has_field.has_field(3);
	}
	public int getD() {
		return _D;
	}
	public void setD(int value){
		super.has_field.set_field(3,true);
		_D = value;
	}
 
	 
	private List<Long> _E; // tag 4
	public boolean HasE(){
		return super.has_field.has_field(4);
	}
	public List<Long> getE() {
		return _E;
	}
	public void setE(List<Long> value){
		super.has_field.set_field(4,true);
		_E = value;
	}
 
	 
	private String _F; // tag 5
	public boolean HasF(){
		return super.has_field.has_field(5);
	}
	public String getF() {
		return _F;
	}
	public void setF(String value){
		super.has_field.set_field(5,true);
		_F = value;
	}
 
	
	protected void decode () {
		int tag = -1;
		while (-1 != (tag = super.deserialize.read_tag ())) {
			switch (tag) {	
	
			case 0:
				this.setA(super.deserialize.read_int32());
				break;
	
			case 1:
				this.setB(super.deserialize.read_boolean());
				break;
	
			case 2:
				this.setC(super.deserialize.read_int64());
				break;
	
			case 3:
				this.setD(super.deserialize.read_int32());
				break;
	
			case 4:
				this.setE(super.deserialize.read_int64_list());
				break;
	
			case 5:
				this.setF(super.deserialize.read_string());
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
				super.serialize.write_int32(this._A, 0);
			} 
	
			if (super.has_field.has_field (1)) {
				super.serialize.write_boolean(this._B, 1);
			} 
	
			if (super.has_field.has_field (2)) {
				super.serialize.write_int64(this._C, 2);
			} 
	
			if (super.has_field.has_field (3)) {
				super.serialize.write_int32(this._D, 3);
			} 
	
			if (super.has_field.has_field (4)) {
				super.serialize.write_int64(this._E, 4);
			} 
	
			if (super.has_field.has_field (5)) {
				super.serialize.write_string(this._F, 5);
			} 
	
			return super.serialize.close ();
	}
}
