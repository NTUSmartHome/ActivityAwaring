import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import elements.Label;
import elements.SparseMatrix;

public class ReportForRuleClustering {
	int maxCluNum = 0;
	private Label L = new Label();

	private ArrayList<Integer> CluElementNum = new ArrayList<Integer>();
	SparseMatrix<Integer> CLUSTER = new SparseMatrix<Integer>(0);
	SparseMatrix<String> sequenceData = new SparseMatrix<String>("");
	private String Path; 
	private String IFileName = "testResult.txt";
	private String OFileName = "reportMeaningfulActResult.txt";
	final int MAX = 1000;
	int existCluNum = 0;
	public ReportForRuleClustering(String path, String ifilename, String ofilename) {
		// TODO Auto-generated constructor stub
		Path = path;
		IFileName = ifilename;
		OFileName = ofilename;

		
		for(int i=0; i<MAX; i++) CluElementNum.add(0);
		
		loadExpResult();
		printExpReport();
		
	}
	
	public void loadExpResult(){
		try {
			FileReader fr = new FileReader(Path+"/BasedClustering/"+IFileName);
			BufferedReader br = new BufferedReader(fr);
			String line = "";
			try {
				int findSweep = 0;
				int sequenceIndex = 0;
				while((line = br.readLine())!=null){
					String[] tmp = line.split("\t");
					int index = Integer.valueOf(tmp[1]);
					if(index>maxCluNum) maxCluNum = index;

					CluElementNum.set(index, (CluElementNum.get(index)+1));
					
					for(int i=0; i<L.size(); i++){
						if(tmp[0].equals(L.get(i))){
							CLUSTER.set(i, index, (CLUSTER.get(i, index) + 1));
							break;
						}
					}

					sequenceData.set(sequenceIndex, 0, tmp[1]);
					sequenceIndex++;
					
				}
				maxCluNum++;
				
				System.out.println(findSweep);
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
	
	
	
	public void printExpReport(){
		try {
			new File(Path).mkdirs();
			new File(Path+"/Report").mkdirs();
			FileWriter fw = new FileWriter(Path+"/Report/"+OFileName);		
			int cluNum = 0;
			for(int i=0; i<maxCluNum; i++){
				int cluElementNum = CluElementNum.get(i);
				if(cluElementNum!=0){
					cluNum++;
					fw.write( "c"+cluNum + ", " + cluElementNum + "\r\n");
					for(int j=0; j<L.size(); j++){
						double percent = Math.round((CLUSTER.get(j, i)/(double)cluElementNum)*10000)/100.0;					
						fw.write(formatStr(L.get(j), 10)+": "+ formatStr(percent, 6)+ "%,\t\t"+CLUSTER.get(j, i)+"\r\n");
						
						
					}
					fw.write("\r\n\r\n");
					
				}
			}
			fw.flush();
			fw.close();
			
			
			fw = new FileWriter(Path+"/Report/"+"ForExacel_"+OFileName);		
			for(int j=0; j<L.size(); j++){
				fw.write(L.get(j));
				for(int i=0; i<maxCluNum; i++){
					int cluElementNum = CluElementNum.get(i);
					if(cluElementNum!=0){
						fw.write(","+CLUSTER.get(j, i));
					}
				}
				fw.write("\r\n");
			}
			fw.flush();
			fw.close();
			existCluNum = cluNum;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	String formatStr(String str, int len){
		String formatStr = str;
		for(int i=formatStr.length(); i<len; i++)	formatStr += " ";
		return formatStr;
	}
	
	String formatStr(double str, int len){
		String formatStr = String.valueOf(str);
		for(int i=formatStr.length(); i<len; i++)	formatStr += " ";
		return formatStr;
	}
}
