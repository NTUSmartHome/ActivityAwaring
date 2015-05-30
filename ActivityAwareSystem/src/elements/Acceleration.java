package elements;

import java.util.Vector;

public class Acceleration {
	Vector<Double> X = new Vector<Double>();
	Vector<Double> Y = new Vector<Double>();
	Vector<Double> Z = new Vector<Double>();
	
	public Acceleration(){
		
	}
	
	public double get(int axis, int i){
		if(axis == 0)
			return X.get(i);
		else if(axis == 1)
			return Y.get(i);
		else if(axis == 2)
			return Z.get(i);
		return 0;
	}
	public void set(int axis, int i, double value){
		if(axis == 0)
			X.set(i, value);
		else if(axis == 1)
			Y.set(i, value);
		else if(axis == 2)
			Z.set(i, value);
	}
	
	public double getX(int i){ return X.get(i); }
	public double getY(int i){ return Y.get(i); }
	public double getZ(int i){ return Z.get(i); }
	
	public void setX(int i, double value){ X.set(i, value); }
	public void setY(int i, double value){ Y.set(i, value); }
	public void setZ(int i, double value){ Z.set(i, value); }
	
	public void add(int axis, double value){
		if(axis == 0)
			X.add(value);
		else if(axis == 1)
			Y.add(value);
		else if(axis == 2)
			Z.add(value);
	}
	public void addX(double value){ X.add(value); }
	public void addY(double value){ Y.add(value); }
	public void addZ(double value){ Z.add(value); }
	
	public void removeAllElements(){
		X.removeAllElements();
		Y.removeAllElements();
		Z.removeAllElements();
	}
	
	public int size(){ return X.size(); }
	
}