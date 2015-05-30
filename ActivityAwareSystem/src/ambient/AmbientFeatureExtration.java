package ambient;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

public class AmbientFeatureExtration {
	//private String[] ACTIVITY = new String[4];
	Vector<String> ACTIVITY = new Vector<String>();
	private String IAmbientFileName = "Mchess_4_7.csv";
	private String ITimeFileName = "timestampForHighActFeature.txt";
	private String OFileName = "ambient_feature.csv";
	
	Vector<String> sequenceData = new Vector<String>();
	Vector<Integer> Timestamp = new Vector<Integer>();
	Vector<Integer> AmbientTimestamp = new Vector<Integer>();
	
	int on = 10;
	int standby = 1;
	int off = 0;
	
	public AmbientFeatureExtration(String iFilename, String oFileName) {
		// TODO Auto-generated constructor stub
		IAmbientFileName = iFilename;
		OFileName = oFileName;
		
		ACTIVITY.add("COOK");
		ACTIVITY.add("MEAL");
		ACTIVITY.add("NA");
		ACTIVITY.add("NOTEBOOK");
		ACTIVITY.add("READ");
		ACTIVITY.add("SLEEP");
		ACTIVITY.add("STUDY");
		ACTIVITY.add("SWEEP");
		ACTIVITY.add("TV");
	}
	public void loadAmbientData(){
		try {
			FileReader fr = new FileReader(IAmbientFileName);
			BufferedReader br = new BufferedReader(fr);
			String line = "";
			try {
				while((line = br.readLine())!=null){
					String[] tmp = line.split(",");
					
					String featureStr = "";
					for(int i=2; i<tmp.length; i++){
						//int j = i-2;
						if(tmp[i].contains("on")){
							featureStr += on;
						}
						else if(tmp[i].contains("standby")){
							featureStr += standby;
						}
						else if(tmp[i].contains("off")){
							featureStr += off;
						}
						else{
							featureStr += tmp[i];
						}
						featureStr += ",";
					}
					//System.out.println(featureStr+"0");
					String[] timeTmp = tmp[1].split("-");
					int ambientTimestamp = Integer.valueOf(timeTmp[timeTmp.length-3])*60 + Integer.valueOf(timeTmp[timeTmp.length-2]);
					
					AmbientTimestamp.add(Integer.valueOf(ambientTimestamp));
					sequenceData.add(featureStr);					
				}
				br.close();
				fr.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void loadTimestamp(){
		try {
			FileReader fr = new FileReader(ITimeFileName);
			BufferedReader br = new BufferedReader(fr);
			String line = "";
			try {
				while((line = br.readLine())!=null){
					Timestamp.add(Integer.valueOf(line));
				}
				br.close();
				fr.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void printAmbientFreature(){
		try {
			FileWriter fw = new FileWriter(OFileName);	
			int k=0;
			for(int i=0, j=0; i<AmbientTimestamp.size() && j<Timestamp.size(); i++){
				System.out.println(AmbientTimestamp.get(i) +" vs. " + Timestamp.get(j));
				if(AmbientTimestamp.get(i).equals(Timestamp.get(j))){
					System.out.println("calculated");
					fw.write(sequenceData.get(i)+"\n");
					j++;
					k++;
				}
				else if(AmbientTimestamp.get(i) > Timestamp.get(j)){
					fw.write(sequenceData.get(i)+"\n");
					k++;
					j++;
				}
			}
			
			System.out.println(k);
			
			fw.flush();
			fw.close();
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}