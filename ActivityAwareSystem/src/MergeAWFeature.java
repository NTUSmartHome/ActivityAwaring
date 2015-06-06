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
