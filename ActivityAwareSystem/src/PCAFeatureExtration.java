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

public class PCAFeatureExtration {
	
	private String IFileName;
	private String OFileName;
	private String PCAFileName;
	private String Path = "";
	private double Threashold;
	
	private Vector<Vector<Double>> Instances = new Vector<Vector<Double>>();
	Acceleration A = new Acceleration();
	Orientation O = new Orientation();
	Features F = new Features();
	Label L = new Label();
	
	boolean withGravity = false;
	boolean SM = false;
	
	Vector<String> Timestamp = new Vector<String>();
	
	public PCAFeatureExtration(String filePath, String iFilename, String pcaFilename, double threashold) {
		// TODO Auto-generated constructor stub
		Path = filePath;
		IFileName = iFilename;
		PCAFileName = pcaFilename;
		OFileName = "PCA"+iFilename;
		Threashold = threashold;
		F.setOrienThreashold(360);
		
		readRawData();
	}
	
	public void readRawData(){
		try {
			FileReader fr = new FileReader(Path+"/Features/"+IFileName);
			BufferedReader br = new BufferedReader(fr);
			
			FileReader frPCA = new FileReader(Path+"/Features/"+PCAFileName);
			BufferedReader brPCA = new BufferedReader(frPCA);
			
			try {
				FileWriter fw = new FileWriter(Path+"/Features/"+OFileName);
				String linePCA = "";
				String line = "";
				
				try {
					try {
						Vector<Double> EigenValue = new Vector<Double>();
						Vector<Vector<Double>> PrincipalComponents = new Vector<Vector<Double>>();
						double accumulate = 0;
						int topNum = 0;
						while((linePCA = brPCA.readLine())!=null){
							linePCA.replace(" ", "");
							String[] eigenVector = linePCA.split(",");
							double eigenValue = Double.valueOf(eigenVector[0]);
							accumulate += eigenValue;
							
							EigenValue.add(eigenValue);
							Vector<Double> principalComponent = new Vector<Double>();
							for(int i=1; i<eigenVector.length; i++)
								principalComponent.add(Double.valueOf(eigenVector[i]));
							PrincipalComponents.add(principalComponent);
							
						}
						double topAccumulation = 0;
						for(int i=0; i<EigenValue.size(); i++){
							topAccumulation += EigenValue.get(i);
							if((topAccumulation/accumulate)<Threashold){
								topNum++;
							}
						}
						brPCA.close();
						frPCA.close();
						
						/*
						Vector<Double> MAX = new Vector<Double>();
						Vector<Double> MIN = new Vector<Double>();
						Vector<String> GroundTruth = new Vector<String>();
						
						for(int i=0; i<PrincipalComponents.size(); i++){
							MAX.add(-100000.0);
							MIN.add(100000.0);
						}
						*/
						
						while((line = br.readLine())!=null){
							
							String[] feature = line.split(",");
							//int Num = feature.length-1;
							Vector<Double> featureVector = new Vector<Double>();
							for(int i=0; i<feature.length-1; i++){
								double f = Double.valueOf(feature[i]);
								featureVector.add(f);
								//MAX.set(i, max(f, MAX.get(i)));
								//MIN.set(i, min(f, MIN.get(i)));
							}
							for(int i=0; i<topNum; i++){
								double pcF = 0;
								for(int j=0; j<feature.length-1; j++){
									pcF += featureVector.get(j) * PrincipalComponents.get(i).get(j);
								}
								fw.write(pcF+",");
								System.out.print(pcF+",");
							}
							//Instances.add(featureVector);
							fw.write(feature[feature.length-1]+"\n");
							System.out.print(feature[feature.length-1]+"\n");
							//GroundTruth.add(feature[feature.length-1]);
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

	public void printFeature(){
		try {
			new File(Path).mkdirs();
			FileWriter fw = new FileWriter(Path+"/"+OFileName,true);
			
			
			fw.flush();
			fw.close();
			
			
			L.clearACT();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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