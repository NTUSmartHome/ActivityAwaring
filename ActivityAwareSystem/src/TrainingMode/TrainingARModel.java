package TrainingMode;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

public class TrainingARModel {
	String Wearable;
	String Ambient;
	String Path;
	String Merge;
	Double TFThreshol; 
	Vector<String> labelCluster = new Vector<String>();
	Vector<Integer> ambientCluster = new Vector<Integer>();
	Vector<Integer> wearableCluster = new Vector<Integer>();
	Vector<Integer> waCluster = new Vector<Integer>();
	public TrainingARModel(String path, String wearableName, String ambientName, double tfThreshol){
		Path = path;
		Wearable = wearableName;
		Ambient = ambientName;
		TFThreshol = tfThreshol;
		printMergeFile();
	}
	
	public void printMergeFile(){
		new File(Path).mkdirs();
		new File(Path+"/BasedClustering").mkdirs();
		new File(Path).mkdirs();
		new File(Path+"/Features").mkdirs();
		try {
			FileReader frW = new FileReader(Path+"/DPMM/"+Wearable);
			BufferedReader brW =  new BufferedReader(frW);
			String readWearable = "";
			try {
				int totalCluster = 0;
				while((readWearable = brW.readLine())!=null){
					String[] tmpStr = readWearable.split("	"); 
					labelCluster.add(tmpStr[0]);
					wearableCluster.add(Integer.valueOf(tmpStr[1]));
					if(Integer.valueOf(tmpStr[1])>totalCluster){
						totalCluster = Integer.valueOf(tmpStr[1]);
					}
				}
				totalCluster += 1;
				
				int wearableClusterNum = totalCluster;
				int ambientClusterNum = findAmbientCluster();
				
				Vector<Vector<Integer>> clusterHead = new Vector<Vector<Integer>>();
				for(int i=0; i<wearableClusterNum; i++){
					for(int j=0; j<ambientClusterNum; j++){
						for(int n=0; n<wearableCluster.size(); n++){
							if(wearableCluster.get(n)==i && ambientCluster.get(n)==j){
								Vector<Integer> head = new Vector<Integer>(); 
								head.add(i);
								head.add(j);
								clusterHead.add(head);
								break;
							}
						}
					}
				}
				
				for(int n=0; n<wearableCluster.size(); n++){
					waCluster.add(-1);
				}
				
				Vector<Integer> waTF = new Vector<Integer>(); 
				int ID = 0;
				for(int i=0; i<clusterHead.size(); i++){
					int count = 0;
					for(int n=0; n<wearableCluster.size(); n++){
						
						if(clusterHead.get(i).get(0)==wearableCluster.get(n)){
							if(clusterHead.get(i).get(1)==ambientCluster.get(n)){
								count++;
								waCluster.set(n,i);
						
							}
						}
					}
					waTF.add(count);
				}
				
				int totoalInstance = 0;
				Vector<Boolean> Head = new Vector<Boolean>(); 
				for(int i=0; i<waTF.size(); i++){
					totoalInstance += waTF.get(i);
				}
				int existHeadNum = 0;
				for(int i=0; i<waTF.size(); i++){
					double TFRate = (double)waTF.get(i)/(double)totoalInstance;
					if(TFRate>=TFThreshol){
						Head.add(true);
						existHeadNum++;
					}
					else{
						Head.add(false);
					}
				}
				
				for(int i=0; i<clusterHead.size(); i++){
					if(!Head.get(i)){
						for(int j=0; j<waCluster.size(); j++){
							if(waCluster.get(j)==i){
								waCluster.set(j, -1);
							}
						}
					}
				}
				
				for(int i=0; i<Head.size();i++){
					if(!Head.get(i)){
						int replacedId = -1;
						for(int k=(Head.size()-1); k>0; k--){
							if(Head.get(k)){
								replacedId = k;
								break;
							}
						}
						if(replacedId>i){
							for(int j=0; j<waCluster.size(); j++){
								if(waCluster.get(j)==replacedId){
									waCluster.set(j, i);								
								}						
							}
							Head.remove(replacedId);
							
						}
					}
				}
				
				new File(Path).mkdirs();
				new File(Path+"/BasedClustering").mkdirs();
				FileWriter fw = new FileWriter(Path+"/BasedClustering/"+"WAOrdinaryResult");
				for(int i=0; i<waCluster.size(); i++){
					fw.write(labelCluster.get(i)+"	");
					fw.write(waCluster.get(i)+"\r\n");
				}
				fw.flush();
				fw.close();
				
				System.out.println("Number of clusters: "+existHeadNum);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private int findAmbientCluster(){
		FileReader frA;
		int ambientClusterNum = 0;
		try {
			frA = new FileReader(Path+"/Features/"+Ambient);
			BufferedReader brA =  new BufferedReader(frA);
			String readAmbient = "";
			Vector<Vector<Boolean>> Ambient = new Vector<Vector<Boolean>>();
			try {
				while((readAmbient = brA.readLine())!=null){
					String[] tmp = readAmbient.split(",");
					Vector<Boolean> ambientInstance = new Vector<Boolean>();
					for(int i=0; i<tmp.length-1; i++){
						if(Integer.valueOf(tmp[i])==1)
							ambientInstance.add(true);
						else {
							ambientInstance.add(false);
						}
					}
					Ambient.add(ambientInstance);
				}
				Vector<Integer> ambientTF = new Vector<Integer>();
				
				Vector<Vector<Boolean>> ambientClusterHead = new Vector<Vector<Boolean>>();
				ambientClusterHead.add(Ambient.get(0));
				
				for(int i=0; i<Ambient.size(); i++){
					boolean same = false;
					for(int k=0; k<ambientClusterHead.size(); k++){
						boolean sameHead = true;
						for(int j=0; j<Ambient.get(i).size(); j++){
							if(Ambient.get(i).get(j) != ambientClusterHead.get(k).get(j)){
								sameHead = false;
								break;
							}
						}
						if(sameHead){
							same = true;
							break;
						}
					}
					if(!same){
						ambientClusterHead.add(Ambient.get(i));
					}
				}

				for(int n=0; n<Ambient.size(); n++){
					ambientCluster.add(-1);
				}
				for(int i=0; i<ambientClusterHead.size(); i++){
					int count = 0;
					for(int n=0; n<ambientCluster.size(); n++){
						boolean same = true;
						for(int j=0; j<ambientClusterHead.get(i).size(); j++){
							if(ambientClusterHead.get(i).get(j)!=Ambient.get(n).get(j)){
								same=false;
								break;
							}
						}
						if(same){
							count++;
							ambientCluster.set(n,i);
						}
						
					}
					ambientTF.add(count);
				}
				Ambient = ambientClusterHead;
				
				
				int totalInstance = 0;
				for(int i=0; i<ambientTF.size(); i++){
					totalInstance += ambientTF.get(i);
				}
				Vector<Boolean> clusterHead = new Vector<Boolean>();
				for(int i=0; i<Ambient.size(); i++){
					double TFRate = (double)ambientTF.get(i)/(double)totalInstance;
					if(TFRate>=TFThreshol){
						clusterHead.add(true);
					}
					else{
						clusterHead.add(false);
					}
					
				}
				
				for(int i=0; i<clusterHead.size(); i++){
					if(!clusterHead.get(i)){
						int belongId = mostSimilar(Ambient,clusterHead,i);
						for(int j=0; j<ambientCluster.size(); j++){
							if(ambientCluster.get(j)==i){
								ambientCluster.set(j, belongId);
							}
						}
					}
				}
				
				for(int i=0; i<Ambient.size();i++){
					if(!clusterHead.get(i)){
						for(int j=0; j<ambientCluster.size(); j++){
							if(ambientCluster.get(j)==(Ambient.size()-1)){
								ambientCluster.set(j, i);								
							}						
						}
						Ambient.remove(Ambient.size()-1);
						//clusterHead.remove(i);
					}
				}
				
				new File(Path).mkdirs();
				new File(Path+"/BasedClustering").mkdirs();
				FileWriter fw = new FileWriter(Path+"/Features/"+"AmbientResult");
				for(int i=0; i<ambientCluster.size(); i++){
					fw.write(labelCluster.get(i)+"	");
					fw.write(ambientCluster.get(i)+"\r\n");
				}
				fw.flush();
				fw.close();
				fw = new FileWriter(Path+"/BasedClustering/"+"AmbientResult");
				for(int i=0; i<ambientCluster.size(); i++){
					fw.write(labelCluster.get(i)+"	");
					fw.write(ambientCluster.get(i)+"\r\n");
				}
				fw.flush();
				fw.close();
				
				brA.close();
				frA.close();
				
				ambientClusterNum = Ambient.size();
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ambientClusterNum;
	}
	
	private int mostSimilar(Vector<Vector<Boolean>> ambient, Vector<Boolean> clusterHead, int a){
		int maxd = 0;
		int similarCluster = -1;
		for(int i=0; i<ambient.size(); i++){
			if(i!=a && clusterHead.get(i)){
				int d = distance(ambient.get(a),ambient.get(i));
				if(d>maxd){
					maxd = d;
					similarCluster = i;
				}
			}
		}
		return similarCluster;
	}
	
	
	private int distance(Vector<Boolean> a, Vector<Boolean> b){
		int d = 0;
		for(int i=0; i<a.size(); i++){
			if(a.get(i)!=b.get(i))
				d++;
		}
		return d;
	}
	
}
