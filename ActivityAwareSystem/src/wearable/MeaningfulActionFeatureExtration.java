package wearable;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import elements.Label;
import elements.SparseMatrix;


public class MeaningfulActionFeatureExtration {
	//int maxCluNum = 0;
	private Label L = new Label();
	private Vector<Vector<Integer>> Statistic = new Vector<Vector<Integer>>();
	private Vector<Vector<Integer>> Instances = new Vector<Vector<Integer>>();
	
	private String Path;
	private int Timewindow = 60;
	private int Overlap = 40;
	private String IFileName;
	private String OFileName;
	
	// Offline
	public MeaningfulActionFeatureExtration(String path, String iFilename, String oFilename, int timewindow, int overlap) {
		// TODO Auto-generated constructor stub
		Path = path;
		IFileName = iFilename;
		OFileName = oFilename;
		Timewindow = timewindow;
		Overlap = overlap;
		
		for(int i=0; i<L.size(); i++) Statistic.add(new Vector<Integer>());
		
		loadResult();
		//printReport();
		generateHistogramFeature();
		
	}
	
	public void loadResult(){
		try {
			FileReader fr = new FileReader(Path+"/DPMM/"+IFileName);
			BufferedReader br = new BufferedReader(fr);
			String line = "";
			try {
				int maxCluNum = -1;
				while((line = br.readLine())!=null){
					String[] tmp = line.split("\t");
										
					int index = Integer.valueOf(tmp[1]);
					if(index>maxCluNum){ 
						maxCluNum = index;
						for(int i=0; i<L.size(); i++)
							for(int j=Statistic.get(i).size(); j<=maxCluNum; j++)
								Statistic.get(i).add(0);
					}

					for(int i=0; i<L.size(); i++){
						if(L.containLabel(tmp[0],i)){
							Statistic.get(i).set(index, Statistic.get(i).get(index) + 1);
							break;
						}
					}
					Instances.add(new Vector<Integer>());
					Instances.lastElement().add(L.get(tmp[0]));
					Instances.lastElement().add(index);
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
	
	public void generateHistogramFeature(){
		FileWriter fw;
		try {
			new File(Path).mkdirs();
			new File(Path+"/Features").mkdirs();
			fw = new FileWriter(Path+"/Features/"+OFileName);
			
			int[] histogram = new int[Statistic.get(0).size()];
			int[] findGT = new int[Statistic.size()];
			
			int tInterval = Timewindow - Overlap;
			int times = (int)((Instances.size()-Overlap)/tInterval)-1;
			for(int i=0; i<times; i++){
				initArray(histogram);
				initArray(findGT);
				
				for(int t=(i*tInterval); t<((i*tInterval)+Timewindow); t++){
					findGT[Instances.get(t).get(0)]++;
					histogram[Instances.get(t).get(1)]++;
				}
				
				int max = 0, secondMax = 0;
				int GT=L.get("Other"); 
				for(int j=0; j<findGT.length; j++){
					if(findGT[j]>max){
						max = findGT[j];
						GT = j;
					}
					else if(findGT[j]>secondMax){
						secondMax = findGT[j];
					}
				}
				double threadsoldGT = 0.35*Timewindow;
				if( secondMax>threadsoldGT)
					GT = L.get("Other");
				
				for(int j=0; j<histogram.length; j++) 
					fw.write(histogram[j]+",");
				fw.write(L.get(GT));
				fw.write("\n");
			}				
			fw.flush();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initArray(int[] array){
		for(int i=0; i<array.length; i++)
			array[i] = 0;
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
