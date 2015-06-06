package adaption;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import elements.Label;

public class SimilarityFunction {
	private String Path;
	private String FeatureFile;
	private String TestFile; 
	private Vector<String> GroundTruth = new Vector<String>();
	private Vector<Double> MostSimilarity = new Vector<Double>();
	private Vector<Vector<Double>> Cluster = new Vector<Vector<Double>>();
	private Vector<Vector<Double>> Instance = new Vector<Vector<Double>>();
	private double Threadhold;
	
	// for off-line detection
	public SimilarityFunction(String path, String featureFile, String testFile, double threadhole){
		Path = path;
		FeatureFile = featureFile;
		TestFile = testFile;
		Threadhold = threadhole;
		
		loadFile(featureFile,testFile);
		findUnseenInstance();
		
	}
	
	
	// for on-line detection
	public SimilarityFunction(String path, String featureFile, double threadhole){
		Path = path;
		FeatureFile = featureFile;
		Threadhold = threadhole;
		loadFile(featureFile);
		
	}
	
	public boolean reasoningUnseen(Object[] instance){
		Vector<Double> Inst = new Vector<Double>();
		for(int i=0;i<instance.length; i++){
			Inst.add((double)instance[i]);
		}
		int[] result = SimilarityFun(Inst);
		System.out.println("Neareast Cluster Head is "+result[1]);
		System.out.println("Distance is "+result[2]);
		if(result[0]==0){
			return true;
		}
		return false;
	}
	
	public void findUnseenInstance(){
		for(int i=0; i<Instance.size(); i++){
			int[] result = SimilarityFun(i);
			System.out.println(i+" is most similar to "+result[1]+",\tDistance is "+result[2]);
			if(result[0]==0){
				System.out.println("\t It's too various to all known cases..");
			}
		}
	}
	
	public int[] SimilarityFun(Vector<Double> instance){
		Vector<Double> sum = new Vector<Double>();
		for(int i=0; i<Cluster.size(); i++){
			double s = 0;
			for(int j=0; j<Cluster.get(i).size(); j++){
				s += delta(Cluster.get(i).get(j), instance.get(j));
			}
			sum.add(s);
		}		
		double[] result = min(sum);
		int[] returnResult = new int[3];
		returnResult[1] = (int)result[0];
		returnResult[2] = (int)result[1];
		if(result[1]>Threadhold){
			returnResult[0] = 0;
		}
		else{ 
			returnResult[0] = 1;
		}
		return returnResult;
	}
	
	public int[] SimilarityFun(int index){
		Vector<Double> sum = new Vector<Double>();
		for(int i=0; i<Cluster.size(); i++){
			double s = 0;
			for(int j=0; j<Cluster.get(i).size(); j++){
				s += delta(Cluster.get(i).get(j), Instance.get(index).get(j));
			}
			sum.add(s);
		}		
		double[] result = min(sum);
		int[] returnResult = new int[3];
		returnResult[1] = (int)result[0];
		returnResult[2] = (int)result[1];
		if(result[1]>Threadhold){
			returnResult[0] = 0;
		}
		else{ 
			returnResult[0] = 1;
		}
		return returnResult;
	}
	
	private void loadFile(String FeatureFile){
		new File(Path).mkdirs();
		new File(Path+"/Reasoning").mkdirs();
		try {
			FileReader frF = new FileReader(Path+"/Reasoning/"+FeatureFile);
			BufferedReader brF =  new BufferedReader(frF);
			String line;
			try {
				while((line = brF.readLine())!=null){
					String[] fMean = line.split(",");
					Vector<Double> f = new Vector<Double>();
					for(int i=0; i<fMean.length; i++){
						f.add(Double.valueOf(fMean[i]));
					}
					Cluster.add(f);				
				}
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void loadFile(String FeatureFile, String TestFile){
		new File(Path).mkdirs();
		new File(Path+"/Reasoning").mkdirs();
		try {
			FileReader frF = new FileReader(Path+"/Reasoning/"+FeatureFile);
			FileReader frT = new FileReader(Path+"/Reasoning/"+TestFile);
			BufferedReader brF =  new BufferedReader(frF);
			BufferedReader brT =  new BufferedReader(frT);
			String line;
			try {
				while((line = brF.readLine())!=null){
					String[] fMean = line.split(",");
					Vector<Double> f = new Vector<Double>();
					for(int i=0; i<fMean.length; i++){
						f.add(Double.valueOf(fMean[i]));
					}
					Cluster.add(f);				
				}
				
				while((line = brT.readLine())!=null){
					String[] fMean = line.split(",");
					Vector<Double> f = new Vector<Double>();
					for(int i=0; i<fMean.length-1; i++){
						f.add(Double.valueOf(fMean[i]));
					}
					Instance.add(f);
					
					GroundTruth.add(fMean[fMean.length-1]);
					
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	private double delta(double cluster, double instance){
		return Math.abs(cluster-instance);
	}
	
	private double[] min(Vector<Double> varySum){
		double[] result = new double[2];
		double min = 100000;
		int similarID = 0;
		for(int i=0; i<varySum.size(); i++){
			if(min>varySum.get(i)){
				min = varySum.get(i);
				similarID = i;
			}
		}
		result[0] = Double.valueOf(similarID);
		result[1] = min;
		return result;
	}
	
	
}
