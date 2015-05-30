package elements;

import java.util.Vector;

public class Orientation {
	Vector<Double> P = new Vector<Double>();
	Vector<Double> R = new Vector<Double>();
	Vector<Double> A = new Vector<Double>();
	
	public Orientation(){
		
	}
	
	public double get(int axis, int i){
		if(axis == 0)
			return P.get(i);
		else if(axis == 1)
			return R.get(i);
		else if(axis == 2)
			return A.get(i);
		return 0;
	}
	public void set(int axis, int i, double value){
		if(axis == 0)
			P.set(i, value);
		else if(axis == 1)
			R.set(i, value);
		else if(axis == 2)
			A.set(i, value);
	}
	
	public double getP(int i){ return P.get(i); }
	public double getR(int i){ return R.get(i); }
	public double getA(int i){ return A.get(i); }
	
	public void setP(int i, double value){ P.set(i, value); }
	public void setR(int i, double value){ R.set(i, value); }
	public void setA(int i, double value){ A.set(i, value); }
	
	public void add(int axis, double value){
		if(axis == 0)
			P.add(value);
		else if(axis == 1)
			R.add(value);
		else if(axis == 2)
			A.add(value);
	}
	public void addP(double value){ P.add(value); }
	public void addR(double value){ R.add(value); }
	public void addA(double value){ A.add(value); }
	
	public void removeAllElements(){
		P.removeAllElements();
		R.removeAllElements();
		A.removeAllElements();
	}
	
	public int size(){ return P.size(); }
	
}