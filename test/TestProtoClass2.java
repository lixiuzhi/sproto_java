
package com.lxz;

import com.lxz.sproto.*;
import java.util.List;
import java.util.function.Supplier;

public class TestProtoClass2 extends SprotoTypeBase {

	private static int max_field_count = 3;
	public static Supplier<TestProtoClass2> proto_supplier = ()->new TestProtoClass2();

	public TestProtoClass2(){
			super(max_field_count);
	}
	
	public TestProtoClass2(byte[] buffer){
			super(max_field_count, buffer);
			this.decode ();
	} 

	 
	private byte[] _A; // tag 0
	public boolean HasA(){
		return super.has_field.has_field(0);
	}
	public byte[] getA() {
		return _A;
	}
	public void setA(byte[] value){
		super.has_field.set_field(0,true);
		_A = value;
	}
 
	 
	private TestProtoClass1 _B; // tag 1
	public boolean HasB(){
		return super.has_field.has_field(1);
	}
	public TestProtoClass1 getB() {
		return _B;
	}
	public void setB(TestProtoClass1 value){
		super.has_field.set_field(1,true);
		_B = value;
	}
 
	 
	private List<TestProtoClass1> _C; // tag 2
	public boolean HasC(){
		return super.has_field.has_field(2);
	}
	public List<TestProtoClass1> getC() {
		return _C;
	}
	public void setC(List<TestProtoClass1> value){
		super.has_field.set_field(2,true);
		_C = value;
	}
 
	
	protected void decode () {
		int tag = -1;
		while (-1 != (tag = super.deserialize.read_tag ())) {
			switch (tag) {	
	
			case 0:
				this.setA(super.deserialize.read_bytes());
				break;
	
			case 1:
				this.setB(super.deserialize.read_obj(TestProtoClass1.proto_supplier));
				break;
	
			case 2:
				this.setC(super.deserialize.read_obj_list(TestProtoClass1.proto_supplier));
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
				super.serialize.write_bytes(this._A, 0);
			} 
	
			if (super.has_field.has_field (1)) {
				super.serialize.write_obj(this._B, 1);
			} 
	
			if (super.has_field.has_field (2)) {
				super.serialize.write_obj(this._C, 2);
			} 
	
			return super.serialize.close ();
	}
}
