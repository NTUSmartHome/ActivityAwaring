import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

public class MergeAWFeature {
	String Wearable;
	String Ambient;
	String Path;
	String Merge;
	int Timewindow;
	int Overlap;
	public MergeAWFeature(String path, String wearableName, String ambientName, String mergeName, int timewindow, int overlap){
		Path = path;
		Wearable = wearableName;
		Ambient = ambientName;
		Merge = mergeName;
		Timewindow = timewindow;
		Overlap = overlap;
		
		printMergeFile();
	}
	public void printMergeFile(){
		new File(Path).mkdirs();
		new File(Path+"/Features").mkdirs();
		try {
			FileReader frW = new FileReader(Path+"/DPMM/"+Wearable);
			FileReader frA = new FileReader(Path+"/DPMM/"+Ambient);
			BufferedReader brW =  new BufferedReader(frW);
			BufferedReader brA =  new BufferedReader(frA);
			String readWearable = "";
			
			try {
				FileWriter fw = new FileWriter(Path+"/Features/"+Merge);
			
			
				Vector<Integer> Ambient = new Vector<Integer>();
				int clusterNum = 0;
				try {
					for(int i=0; i<Timewindow; i++){
						String[] tmp = brA.readLine().split("	");
						Ambient.add(Integer.valueOf(tmp[1]));
						if(Integer.valueOf(tmp[1])>clusterNum){
							clusterNum = Integer.valueOf(tmp[1]);
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					while((readWearable = brW.readLine())!=null){
						//String readAmbient = "";
						int shift = Timewindow-Overlap;
						for(int i=0; i<shift; i++){
							String[] tmp = brA.readLine().split("	");
							Ambient.add(Integer.valueOf(tmp[1]));
							Ambient.remove(0);
							if(Integer.valueOf(tmp[1])>clusterNum){
								clusterNum = Integer.valueOf(tmp[1]);
							}
						}
						int[] cluster = new int[clusterNum+1];
						for(int i=0; i<Timewindow; i++){
							cluster[Ambient.get(i)]++;
						}
						int maxId = -1;
						int max = 0;
						for(int i=0; i<cluster.length; i++){
							if(cluster[i]>max){
								max = cluster[i];
								maxId = i;
							}
						}
						fw.write(maxId+",");
						String[] wearableStr = readWearable.split("	");
						System.out.println(wearableStr[1]);
						
						fw.write(wearableStr[1]+","+wearableStr[0]+"\n");
		
						//int maxAmbient = 0;
						
					}
					fw.flush();
					fw.close();
					brA.close();
					brW.close();
					frA.close();
					frW.close();
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
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
