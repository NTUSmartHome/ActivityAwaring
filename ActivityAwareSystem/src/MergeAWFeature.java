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
	public MergeAWFeature(String path, String wearableName, String ambientName, String mergeName, int timewindow, int overlap, boolean seperate){
		Path = path;
		Wearable = wearableName+"_Mean.txt";
		Ambient = ambientName;
		Merge = mergeName;
		printMergeFile();
	}
	
	int WC;
	String AmbientFilename;
	String WearableMeanFilename;
	Vector<String> wearableMean = new Vector<String>();
	Vector<String> ambientMean = new Vector<String>();
	
	public MergeAWFeature(String path, String wearableMeanFilename){
		
		Path = path;
		WearableMeanFilename = wearableMeanFilename;
		
		try {
			FileReader frW = new FileReader(Path+"/Reasoning/"+WearableMeanFilename);
			BufferedReader brW =  new BufferedReader(frW);
			for(int i=0; i<WC; i++){
				try {
					wearableMean.add(brW.readLine());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	
	public void setC(int wC){
		WC = wC;
	}
	public String generateWAActionFeatureOnline(int actionResult){
		String WAFeature = "";
		for(int i=0; i<wearableMean.size(); i++){
			if(actionResult == i){
				WAFeature = wearableMean.get(i);
			}
		}
		return WAFeature;		
	}
	
	public void printMergeFile(){
		
		new File(Path).mkdirs();
		new File(Path+"/Features").mkdirs();
		try {
			FileReader frW = new FileReader(Path+"/Features/"+Wearable);
			FileReader frA = new FileReader(Path+"/Features/"+Ambient);
			BufferedReader brW =  new BufferedReader(frW);
			BufferedReader brA =  new BufferedReader(frA);
			String readWearable = "";
			String readAmbient = "";
			
			try {
				FileWriter fw = new FileWriter(Path+"/Features/"+Merge);			
				Vector<Vector<Integer>> Ambient = new Vector<Vector<Integer>>();
				try {
					while((readWearable = brW.readLine())!=null){
						readAmbient = brA.readLine();
						fw.write(readWearable+","+readAmbient+"\n");
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
