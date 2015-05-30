package ambient;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class APFeautreGenerator {
	String IAmbientFilename = "";
	String IAccelFilename = "";
	String ITimestampFilename = "";
	String OFilename = "";
	APFeautreGenerator(String iAmbientFilename, String iAccelFilename, String oFilename){
		IAmbientFilename = iAmbientFilename;
		IAccelFilename = iAccelFilename;
		OFilename = oFilename;
	}
	
	public void loadFeature(){
		try {
			FileReader amfr = new FileReader(IAmbientFilename);
			BufferedReader ambr = new BufferedReader(amfr);
			FileReader acfr = new FileReader(IAccelFilename);
			BufferedReader acbr = new BufferedReader(acfr);
			FileWriter fw;
			
			String line = "";
			try {
				fw = new FileWriter(OFilename);
				while((line = ambr.readLine())!=null){
					String[] tmp = acbr.readLine().split("\t");
					for(int i=1; i<tmp.length; i++){
						line += tmp[i]+",";
					}
					line += tmp[0];
					fw.write(line+"\n");
				}
				ambr.close();
				amfr.close();
				acbr.close();
				acfr.close();
				fw.flush();
				fw.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
