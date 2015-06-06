package adaption;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

public class KNNModel {
	String version = "";
	Vector<Vector<Vector<Double>>> modelVector = new Vector<Vector<Vector<Double>>>();
	Vector<Double> NearestDistance;
	Vector<Integer> NearestCluser;
	int K = 0;
	
	public KNNModel(String Path, int k){
		K = k;
		version = Path;
		loadModel();
	}
	
	public void predict(Double[] inst){
		Vector<Double> instance = new Vector<Double>();
		for(int i=0; i<inst.length; i++) instance.add(inst[i]);
		NearestDistance = new Vector<Double>();
		NearestCluser = new Vector<Integer>(); 
		for(int i=0; i<K; i++){
			NearestCluser.add(0);
			NearestDistance.add(1000000.0);
		}
		for(int i=0; i<modelVector.size(); i++){
			for(int j=0; j<modelVector.get(i).size(); j++){
				
				double d = getOneNormDistance(instance, modelVector.get(i).get(j));
				for(int k=K-1; k>=0; k--){
					if(d<NearestDistance.get(k)){
						for(int l=0; l<k && l<K-1; l++){
							NearestDistance.set(l, NearestDistance.get(l+1));
							NearestCluser.set(l, NearestCluser.get(l+1));
						}
						NearestDistance.set(k, d);
						NearestCluser.set(k,i);
					}
				}
			}
		}
			

	}
	
	public int getNeareast(){
		int mostRecord = 0;
		int mostId = 0;
		Vector<Integer> recordID = new Vector<Integer>();
		for(int j=0; j<modelVector.size(); j++){
			recordID.add(0);
		}
		for(int i=0; i<NearestCluser.size(); i++){
			for(int j=0; j<recordID.size(); j++){
				if(NearestCluser.get(i) == j){
					recordID.set(j, recordID.get(j)+1);
				}
			}
		}
		for(int i=0; i<recordID.size(); i++){
			if(recordID.get(i)>mostRecord){
				mostRecord = recordID.get(i);
				mostId = i;
			}
		}	
		return mostId;
	}
	
	public void clear(){
		NearestDistance.removeAllElements();
		NearestCluser.removeAllElements();
	}
	
	
	private double getOneNormDistance(Vector<Double> inst, Vector<Double> modelInst){
		double d = 0;
		for(int i=0; i<modelInst.size(); i++){
			d += Math.abs(inst.get(i)-modelInst.get(i));			
		}	
		return d;
	}
	
	
	public void loadModel(){
		new File(version+"/KNN").mkdirs();
		try {
			FileReader fr = new FileReader(version+"/KNN/"+"ModelParameter.txt");
			BufferedReader br = new BufferedReader(fr);			
		try {
			String[] tmp = br.readLine().split(":");
			int numC = Integer.valueOf(tmp[1]);
			tmp = br.readLine().split(":");
			int numF = Integer.valueOf(tmp[1]);
			br.close();
			fr.close();
			
			for(int i=0; i<numC; i++){
				Vector<Vector<Double>> cluVector = new Vector<Vector<Double>>();
				fr = new FileReader(version+"/KNN/"+"Model"+(i+1)+".txt");
				br = new BufferedReader(fr);
				String line;
				while((line=br.readLine())!=null){
					Vector<Double> feature = new Vector<Double>();
					String[] fea = line.split(",");
					for(int j=0; j<fea.length; j++){
						double f = Double.valueOf(fea[j]);
						feature.add(f);
					}
					cluVector.add(feature);
				}
				modelVector.add(cluVector);
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
	
}
