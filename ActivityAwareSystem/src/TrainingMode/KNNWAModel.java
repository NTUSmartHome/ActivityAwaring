package TrainingMode;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

public class KNNWAModel {
	String Wearable;
	String Ambient;
	String Path;
	String WAHead;
	Double TFThreshol; 
	int Timewindow;
	int Overlap;
	int K;
	Vector<Vector<Integer>> wearableFeature = new Vector<Vector<Integer>>();
	Vector<Vector<Integer>> ambientFeature = new Vector<Vector<Integer>>();
	Vector<String> labelCluster = new Vector<String>();
	Vector<Integer> ambientCluster = new Vector<Integer>();
	Vector<Integer> wearableCluster = new Vector<Integer>();
	Vector<Integer> waCluster = new Vector<Integer>();
	Vector<Boolean> waHead = new Vector<Boolean>();
	public KNNWAModel(String path, String wearableFeatureName, String ambientClusterName, String waHead, int k, int timewindow, int overlap){
		Path = path;
		Wearable = wearableFeatureName;
		Ambient = ambientClusterName;
		WAHead = waHead;
		Timewindow = timewindow;
		Overlap = overlap;
		K = k;
		printMergeFile();
	}
	
	public void printMergeFile(){
		new File(Path).mkdirs();
		new File(Path+"/BasedClustering").mkdirs();
		new File(Path).mkdirs();
		new File(Path+"/Features").mkdirs();
		try {
			FileReader frWA = new FileReader(Path+"/BasedClustering/"+WAHead);
			BufferedReader brWA =  new BufferedReader(frWA);
			FileReader frW = new FileReader(Path+"/Features/"+Wearable);
			BufferedReader brW =  new BufferedReader(frW);
			FileReader frA = new FileReader(Path+"/Features/"+Ambient);
			BufferedReader brA =  new BufferedReader(frA);
			String line = "";
			try {
				int totalAmbientCluster = 0;
				int totalCluster = 0;
				while((line = brW.readLine())!=null){
					//load wearable features
					String[] tmpStr = line.split(","); 
					labelCluster.add(tmpStr[tmpStr.length-1]);
					Vector<Integer> wearableInstance = new Vector<Integer>();
					for(int i=0; i<(tmpStr.length-1); i++){
						wearableInstance.add(Integer.valueOf(tmpStr[i]));
					}
					wearableFeature.add(wearableInstance);
					
					//load ambient features
					line = brA.readLine();
					tmpStr = line.split("	");
					ambientCluster.add(Integer.valueOf(tmpStr[1]));
					if(Integer.valueOf(tmpStr[1])>totalAmbientCluster){
						totalAmbientCluster = Integer.valueOf(tmpStr[1]);
					}
					
					//load wa head information
					String readWA = brWA.readLine();
					tmpStr = readWA.split("	");
					int waId = Integer.valueOf(tmpStr[1]);
					waCluster.add(waId);
					if(waId>totalCluster){
						totalCluster = waId;
					}
					if(waId==-1){
						waHead.add(false);
					}
					else{
						waHead.add(true);
					}
				}
				totalCluster += 1;
				totalAmbientCluster += 1;
				
				int wearableFeatureNum = wearableFeature.get(0).size();
				int ambientClusterNum = totalAmbientCluster;
				
				//generate ambient feature
				int shift = Timewindow-Overlap;
				int times = (int)(Timewindow/shift);
				for(int i=0; i<(times-1); i++){
					Vector<Integer> ambientInstance = new Vector<Integer>();
					for(int j=0; j<ambientClusterNum; j++) ambientInstance.add(0);
					int generateId = ambientCluster.get(i);
					ambientInstance.set(generateId, Timewindow);
					ambientFeature.add(ambientInstance);
				}
				for(int i=(times-1); i<wearableFeature.size(); i++){
					Vector<Integer> ambientInstance = new Vector<Integer>();
					for(int j=0; j<ambientClusterNum; j++) ambientInstance.add(0);
					for(int j=i; j>(i-times); j--){
						int generateId = ambientCluster.get(j);
						ambientInstance.set(generateId, shift);
					}
					ambientFeature.add(ambientInstance);
				}
				
				//predict unclassify instance
				for(int i=0; i<waHead.size(); i++){
					if(!waHead.get(i)){
						int waId = knn(i);
						waCluster.set(i, waId);
						//System.out.println(i+" label:"+labelCluster.get(i)+", predict:"+waId);
					}
				}
				
				new File(Path).mkdirs();
				new File(Path+"/BasedClustering").mkdirs();
				FileWriter fw = new FileWriter(Path+"/BasedClustering/"+"WAResult");
				for(int i=0; i<ambientCluster.size(); i++){
					fw.write(labelCluster.get(i)+"	");
					fw.write(waCluster.get(i)+"\r\n");
				}
				fw.flush();
				fw.close();
				
				
				fw = new FileWriter(Path+"/Features/"+"WAFeature.txt");
				for(int i=0; i<ambientCluster.size(); i++){
					for(int j=0; j<wearableFeature.get(i).size(); j++){
						fw.write(wearableFeature.get(i).get(j)+",");
					}
					for(int j=0; j<ambientFeature.get(i).size(); j++){
						fw.write(ambientFeature.get(i).get(j)+",");
					}
					fw.write(labelCluster.get(i)+"\r\n");
				}
				fw.flush();
				fw.close();
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private int knn(int a){
		Vector<Integer> kDistance = new Vector<Integer>();
		Vector<Integer> kNeighbor = new Vector<Integer>();
		for(int i=0; i<K; i++){
			kDistance.add(Timewindow*2);
			kNeighbor.add(-1);
		}
		
		for(int i=0; i<waHead.size(); i++){
			if(i!=a && waHead.get(i)){
				int d = distance(a, i);
				for(int j=0; j<kDistance.size(); j++){
					if(d<kDistance.get(j)){
						//System.out.println(d+","+waCluster.get(i));
						kDistance.add(j, d);
						kNeighbor.add(j,waCluster.get(i));
						kDistance.remove(K);
						kNeighbor.remove(K);
						break;
					}
					
				}
			}
		}
		Vector<Integer> NeighborId = new Vector<Integer>();
		Vector<Integer> NeighborTF = new Vector<Integer>();
		NeighborId.add(kNeighbor.get(0));
		NeighborTF.add(1);
		for(int i=1; i<kNeighbor.size(); i++){
			boolean seen = false;
			for(int j=0; j<NeighborId.size(); j++){
				if(kNeighbor.get(i).equals(NeighborId.get(j))){
					NeighborTF.set(j, NeighborTF.get(j)+1);
					seen = true;
					break;
				}
			}
			if(!seen){
				NeighborId.add(kNeighbor.get(i));
				NeighborTF.add(1);
			}
		}
		
		int mostKId = -1;
		int maxTF = -1;
		for(int i=0; i<NeighborTF.size(); i++){
			//System.out.print(NeighborId.get(i)+":"+NeighborTF.get(i)+", ");
			if(NeighborTF.get(i)>maxTF){
				maxTF = NeighborTF.get(i);
				mostKId = NeighborId.get(i);
			}
		}
		//System.out.println("");
		
		return mostKId;
	}
	
	
	private int distance(int a, int b){
		int d = 0;
		for(int i=0; i<ambientFeature.get(a).size(); i++){
			d += Math.abs(ambientFeature.get(a).get(i) - ambientFeature.get(b).get(i));
		}
		for(int i=0; i<wearableFeature.get(a).size(); i++){
			d += Math.abs(wearableFeature.get(a).get(i) - wearableFeature.get(b).get(i));
		}
		return d;
	}
	
}
