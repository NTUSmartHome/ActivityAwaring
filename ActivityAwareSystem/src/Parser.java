import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.print.DocFlavor;

import org.apache.commons.collections4.Factory;

import wearable.MeaningfulActReport;
import wearable.NormalizeWearableFeature;
import wearable.PCAWearableFeatureExtration;
import wearable.MeaningfulActionFeatureExtration;
import wearable.SwingMotionFeatureExtration;
import adaption.SimilarityFunction;
import ambient.SimulatedScenario;

import com.datumbox.framework.machinelearning.clustering.MultinomialDPMMTest;

import dpmm.MDPMMOnline;
import dpmm.GDPMMTrainAuto;
import dpmm.GDPMMTrainD2;
import dpmm.MDPMMTrain;
import elements.FileFormat;


public class Parser {
	
	public static void main(String args[]) {
		boolean TrainLowLevelACT = true;
		boolean PCA = false;
		boolean TrainHighLevelACT = true;
		boolean TrainAmbient = true;
		boolean TrainWA = true;
		boolean Online = false;
		String Path = "5.20.MingJe_OnlineTset";
		int timewindow = 60;
		int overlap = 55;
		
		/*
		String Path = "VehicalTestingData";
		new PCAFeatureExtration(Path,"VehicalFeature.txt","PCA_Vehical.txt",0.98);
		new GaussianDPMMTrain(Path,"VehicalFeature.txt","VehicalResult",30,1,5,100);
		buildClusteringModel(Path, "VehicalFeature.txt", "VehicalResult", 1, 30, 100);
		new Report(Path,"VehicalResult","VehicalReport.txt");
		*/
		
		FileFormat LowAct  = new FileFormat(Path,"SwingMotion");
		LowAct.setRawdata("05_20_MingJe.txt");
		FileFormat HighAct = new FileFormat(Path,"MeaningfulAction");
		FileFormat AmbientAct = new FileFormat(Path,"Ambient");
		FileFormat WAAct = new FileFormat(Path,"WA");
		
		if(TrainLowLevelACT){
			new File(Path+"/Features").mkdirs();
			LowAct.deletFile("Features/SwingMotionFeature.txt");
			new SwingMotionFeatureExtration(Path,LowAct.getRawdata(),LowAct.getFeature(), true, true);
			if(PCA){		
				LowAct.setPCA("NormailzePCAResult.txt");
				new NormalizeWearableFeature(Path,LowAct.getFeature());
				new PCAWearableFeatureExtration(Path,LowAct.getFeature(),LowAct.getPCA(),8);
				new MDPMMTrain(Path,LowAct.getPCAFeature(),LowAct.getResult(),1,5,100);
			}
			else{
				new MDPMMTrain(Path,LowAct.getFeature(),LowAct.getResult(),1,5,100);
			}
			new Report(Path,LowAct.getResult(),LowAct.getReport());
			new MeaningfulActionFeatureExtration(Path,LowAct.getResult(),HighAct.getFeature(), timewindow, overlap);	
		}
		if(TrainHighLevelACT){
			new MDPMMTrain(Path,HighAct.getFeature(),HighAct.getResult(),1,5,100);
			//buildClusteringModel(Path,HighAct.getFeature(),HighAct.getResult(),1,5,100);
			new Report(Path,HighAct.getResult(),HighAct.getReport());
		}
		if(TrainAmbient){
			new SimulatedScenario(Path, HighAct.getResult());
		}
		if(TrainWA){
			new MergeAWFeature(Path, HighAct.getResult(), AmbientAct.getFeature(), WAAct.getFeature(), timewindow, overlap,false);
			
			//new MeaningfulActReport(Path,"ManualResult.txt","ManualReport.txt");
			
			new GDPMMTrainAuto(Path,WAAct.getFeature(),WAAct.getResult(),1,0.25,100);
			new Report(Path,WAAct.getResult(),WAAct.getReport());
		}
		
		if(Online){
			SimilarityFunction SimilarityFun =new SimilarityFunction(Path,"Cluster_Mean.txt",20);
			MDPMMOnline ActionPredict = new MDPMMOnline(Path, HighAct.getResult());
			
			
			FileReader fr;
			try {
				new File(Path).mkdirs();
				new File(Path+"/Testing").mkdirs();
				fr = new FileReader(Path+"/Testing/testCase.txt");
				BufferedReader br =  new BufferedReader(fr);
				String line;
				try {
					while((line = br.readLine())!=null){
						String[] tmp = line.split(",");
						String label = tmp[tmp.length-1];
						Double[] feature = new Double[tmp.length-1];
						for(int i=0; i<feature.length; i++){
							feature[i] = Double.valueOf(tmp[i]); 
						}
						boolean unseen = SimilarityFun.reasoningUnseen(feature, label);
						if(unseen){
							System.out.println("An unseen instance found!");
						}
						else{
							int predictId = ActionPredict.predict(feature, label);
							System.out.println("Predicted result is "+predictId);
						}
						
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
	
	double[] getWearableFeature(String Instance){
		String[] tmp = Instance.split(",");
		double[] f = new double[tmp.length-1];
		for(int i=0; i<f.length; i++){
			f[i] = Double.valueOf(tmp[i]);
		}
		return f;
	}
	String getWearableLabel(String Instance){
		String[] tmp = Instance.split(",");
		return tmp[tmp.length-1];
	}
	
	
}


