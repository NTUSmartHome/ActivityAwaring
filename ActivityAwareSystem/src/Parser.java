import java.io.File;

import org.apache.commons.collections4.Factory;

import wearable.MeaningfulActReport;
import wearable.NormalizeWearableFeature;
import wearable.PCAWearableFeatureExtration;
import wearable.SwingMotionReport;
import wearable.WearableFeatureExtration;
import ambient.SimulatedScenario;

import com.datumbox.framework.machinelearning.clustering.MultinomialDPMMTest;

import elements.FileFormat;


public class Parser<E> {
	
	public static void main(String args[]) {
		//int CHOOSE = 10;
		//FileFormat F = new FileFormat("5.20.MingJe");
		boolean TrainLowLevelACT = false;
		boolean PCA = false;
		boolean TrainHighLevelACT = false;
		boolean TrainAmbient = true;
		boolean TrainWA = true;
		String Path = "5.20.MingJe_woDAV";
		int timewindow = 60;
		int overlap = 50;
		
		FileFormat LowAct  = new FileFormat(Path);
		LowAct.setRawdata("05_20_MingJe.txt");
		LowAct.setFeature("SwingMotionFeature.txt");
		LowAct.setResult("SwingMotionResult");
		LowAct.setReport("SwingMotionReport.txt");
		
		FileFormat HighAct = new FileFormat(Path);
		HighAct.setFeature("HistogramFeature.txt");
		HighAct.setResult("MeaningfulActionResult");
		HighAct.setReport("MeaningfulActionReport.txt");
		
		FileFormat AmbientAct = new FileFormat(Path);
		AmbientAct.setFeature("AmbientFeatures.txt");
		AmbientAct.setResult("AmbientResult");
		
		FileFormat WAAct = new FileFormat(Path);
		WAAct.setFeature("WAFearture.txt");
		WAAct.setResult("WAResult");
		
		if(TrainLowLevelACT){
			new File(Path+"/Features").mkdirs();
			LowAct.deletFile("Features/SwingMotionFeature.txt");
			new WearableFeatureExtration(Path,LowAct.getRawdata(),LowAct.getFeature(), true, true);
			if(PCA){		
				LowAct.setPCA("NormailzePCAResult.txt");
				new NormalizeWearableFeature(Path,LowAct.getFeature());
				new PCAWearableFeatureExtration(Path,LowAct.getFeature(),LowAct.getPCA(),8);
				buildClusteringModel(Path,LowAct.getPCAFeature(),LowAct.getResult(),1,5,100);
			}
			else{
				buildClusteringModel(Path,LowAct.getFeature(),LowAct.getResult(),1,5,100);
			}
			new SwingMotionReport(Path,LowAct.getResult(),LowAct.getReport(),HighAct.getFeature(), timewindow, overlap);
			new SimulatedScenario(Path, LowAct.getResult());
		}
		if(TrainHighLevelACT){		
			buildClusteringModel(Path,HighAct.getFeature(),HighAct.getResult(),1,5,100);
			new MeaningfulActReport(Path,HighAct.getResult(),HighAct.getReport());
		}
		if(TrainAmbient){
			buildClusteringModel(Path,AmbientAct.getFeature(),AmbientAct.getResult(),1,1,10);
		}
		if(TrainWA){
			MergeAWFeature Merge = new MergeAWFeature(Path, HighAct.getResult(), AmbientAct.getResult(), WAAct.getFeature(), timewindow, overlap);
			buildClusteringModel(Path,WAAct.getFeature(),WAAct.getResult(),1,1,3);
		}
		
		
	}
	
	private static void buildClusteringModel(String path,String iFile, String oFile, int a, int aw, int iteration){		

		MultinomialDPMMTest MultiDPMM = new MultinomialDPMMTest(path,iFile,oFile,a,aw,iteration);
		MultiDPMM.testPredict();		
		
	}
	
	
}


