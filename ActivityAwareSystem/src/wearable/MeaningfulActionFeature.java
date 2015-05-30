package wearable;

import java.util.Vector;

public class MeaningfulActionFeature {
	private int timewindow = 60;
	private Vector<Integer> Histogram = new Vector<Integer>();
	private int CluNum;
	
	
	public MeaningfulActionFeature(int ClusterNum){
		CluNum = ClusterNum;
		for(int i =0; i< CluNum; i++) Histogram.add(0);
	}
	
	public void setHistogram(Vector<Integer> T){
		int len = T.size();
		for(int i=0; i<len; i++){
			Histogram.set(T.get(i), Histogram.get(T.get(i))+1);
		}
	}
	
	public int getHistogram(int index){
		return Histogram.get(index);
	}
	
	public String getHistogramStr(){
		String str = "";
		for(int i=0; i<Histogram.size()-1; i++){
			str += getHistogram(i) + ",";
		}
		str += getHistogram(Histogram.size()-1);
		return str;
	}
	
}
