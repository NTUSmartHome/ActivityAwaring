package elements;

import java.util.Vector;

public class Label {
	Vector<String> LABEL = new Vector<String>(); 
	Vector<Boolean> activeLabel = new Vector<Boolean>();
	public Label(){
		LABEL.add("Exercise");
		LABEL.add("Sweep");
		LABEL.add("Walk");
		LABEL.add("Meal");
		LABEL.add("WearShoes");
		LABEL.add("WashDishes");
		LABEL.add("PlayPad");
		LABEL.add("WatchTV");
		LABEL.add("Read");
		LABEL.add("Sleep");
		LABEL.add("Other");
		//LABEL.add("+1");
		//LABEL.add("-1");
		for(int i=0; i<LABEL.size(); i++) activeLabel.add(false);
	}

	public void clearACT(){
		for(int i=0; i<LABEL.size(); i++) activeLabel.set(i,false);
	}
	
	public int size(){
		return LABEL.size();
	}
	
	public void setACT(String str){
		for(int i=0; i<LABEL.size(); i++){
			if(str.contains(LABEL.get(i))){
				activeLabel.set(i, true);
			}
		}
	}
	public String getACT(){
		String str = "";
		for(int i=0; i<LABEL.size(); i++){
			if(activeLabel.get(i)){
				str += LABEL.get(i) + "";
			}
		}
		return str;
	}
	public String get(int i){
		return LABEL.get(i);
	}
	public int get(String label){
		for(int i=0; i<LABEL.size(); i++){
			if(label.contains(LABEL.get(i))){
				return i;
			}
		}
		return -1;
	}
	
	public boolean containLabel(String groundTruth, int Label){
		if(groundTruth.contains(get(Label)))
			return true;
		else
			return false;
	}
}