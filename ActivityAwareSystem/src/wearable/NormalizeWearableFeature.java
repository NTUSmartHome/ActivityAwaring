package wearable;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

import org.omg.CORBA.portable.ValueBase;

import elements.Acceleration;
import elements.Features;
import elements.Label;
import elements.Orientation;

public class NormalizeWearableFeature<E> {
	
	private String IFileName;
	private String OFileName;
	private String Path = "";
	
	private Vector<Vector<Double>> Instances = new Vector<Vector<Double>>();
	Acceleration A = new Acceleration();
	Orientation O = new Orientation();
	Features F = new Features();
	Label L = new Label();
	
	boolean withGravity = false;
	boolean SM = false;
	boolean WithLabel = true;
	
	Vector<String> Timestamp = new Vector<String>();
	
	public NormalizeWearableFeature(String filePath, String iFilename) {
		// TODO Auto-generated constructor stub
		Path = filePath;
		IFileName = iFilename;
		OFileName = "Normalize"+iFilename;
		WithLabel = false;
		readRawData();
	}
	public NormalizeWearableFeature(String filePath, String iFilename, String oFilename, boolean withLabel ) {
		// TODO Auto-generated constructor stub
		Path = filePath;
		IFileName = iFilename;
		OFileName = "Normalize"+iFilename;
		WithLabel = withLabel;
		readRawData();
	}
	public void readRawData(){
		try {
			FileReader fr = new FileReader(Path+"/Features/"+IFileName);
			BufferedReader br = new BufferedReader(fr);
		
			try {
				FileWriter fw = new FileWriter(Path+"/Features/"+OFileName);
			
				String line = "";
				try {
					try {
						Vector<Double> MAX = new Vector<Double>();
						Vector<Double> MIN = new Vector<Double>();
						Vector<String> GroundTruth = new Vector<String>();
						
						for(int i=0; i<12; i++){
							MAX.add(-100000.0);
							MIN.add(100000.0);
						}
						
						while((line = br.readLine())!=null){
							
							String[] feature = line.split(",");
							Vector<Double> featureVector = new Vector<Double>();
							for(int i=0; i<feature.length-1; i++){
								double f = Double.valueOf(feature[i]);
								featureVector.add(f);
								MAX.set(i, max(f, MAX.get(i)));
								MIN.set(i, min(f, MIN.get(i)));
							}
							Instances.add(featureVector);
							GroundTruth.add(feature[feature.length-1]);
						}
						
						Instances = normalize(Instances, MAX, MIN);
						if(!WithLabel){
							for(int i=0; i<Instances.size()-1; i++){
								for(int k=0; k<Instances.get(i).size()-1; k++){
									 fw.write( Instances.get(i).get(k) +" ");
								}
								fw.write(Instances.get(i).get(Instances.get(i).size()-1)+"\n");
							}
							int last = Instances.size()-1;
							for(int k=0; k<Instances.get(last).size()-1; k++){
								 fw.write( Instances.get(last).get(k) +" ");
							}
							fw.write(String.valueOf(Instances.get(last).get(Instances.get(last).size()-1)));
						}
						else {
							for(int i=0; i<Instances.size(); i++){
								for(int k=0; k<Instances.get(i).size(); k++){
									 fw.write( Instances.get(i).get(k) +",");
								}
								fw.write(GroundTruth.get(i)+"\n");
							}
						}
						
					} catch (Exception e) {
						// TODO: handle exception
					
					}
					br.close();
					fr.close();
					
					
					fw.flush();
					fw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		
			
			
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private double round(double value){
		double result = (double) (Math.round(value*100000)/100000.0);
		if(result>0.001 || result<-0.001)
			return result;
		return 0;
	}

	
	
	private Vector<Vector<Double>> normalize(Vector<Vector<Double>> V, Vector<Double> Max, Vector<Double> Min){
		for(int i=0; i<V.get(0).size(); i++){
			double numerator;
			double denominator = Max.get(i) - Min.get(i);
			for(int j=0; j<V.size(); j++){
				numerator = V.get(j).get(i) - Min.get(i);
				V.get(j).set(i, (numerator*100)/denominator);
			}
		}
		return V;
	}

	private double max(double value, double preMax){
		if(value>preMax) return value;
		return preMax;
	}
	private double min(double value, double preMin){
		if(value<preMin) return value;
		return preMin;
	}
}