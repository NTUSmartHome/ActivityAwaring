import TrainingMode.KNNWAModel;
import TrainingMode.TrainingARModel;
import ambient.SimulatedScenario;
import dpmm.MDPMMTrain;
import elements.FileFormat;
import wearable.MeaningfulActionFeatureExtration;
import wearable.SwingMotionFeatureExtration;

import java.io.File;


public class BuildModel {
	public BuildModel(String Path, int timewindow, int overlap){
		FileFormat LowAct  = new FileFormat(Path,"SwingMotion");
		//LowAct.setRawdata("07_08_MingJe.txt");
		//LowAct.setRawdata("07_07_MingJe.txt");
		LowAct.setRawdata("1021DEMO.txt");
		FileFormat HighAct = new FileFormat(Path,"MeaningfulAction");
		FileFormat AmbientAct = new FileFormat(Path,"Ambient");
		FileFormat WAAct = new FileFormat(Path,"WA");

		/* Build Swing Motion Model*/
		
		new File(Path+"/Features").mkdirs();
		LowAct.deletFile("Features/SwingMotionFeature.txt");
		new SwingMotionFeatureExtration(Path,LowAct.getRawdata(),LowAct.getFeature(), true, true);
		new MDPMMTrain(Path,LowAct.getName(),0.5,1,100);
		new Report(Path,LowAct.getResult(),LowAct.getReport());
		new MeaningfulActionFeatureExtration(Path,LowAct.getResult(),HighAct.getFeature(), timewindow, overlap);	
		 
		/* Build Meaningful Action Model*/
		
		new MDPMMTrain(Path,HighAct.getName(),1,2,100);
		new Report(Path,HighAct.getResult(),HighAct.getReport());
		 
		/* Generate Offline (for training) Ambient Features*/
		//new SimulatedScenario(Path, HighAct.getResult());
	
		/* Build Ambient + Wearable Model*/
		/*	
		 * new MergeAWFeature(Path, HighAct.getName(), AmbientAct.getFeature(), WAAct.getFeature(), timewindow, overlap,false);
		 * new GDPMMTrainAuto(Path,WAAct.getName(),1,0.25,100);
		 * new Report(Path,WAAct.getResult(),WAAct.getReport());
		 * */
		
		/*
		new TrainingARModel(Path, HighAct.getResult(), AmbientAct.getFeature(), 0.012);
		new ReportForRuleClustering(Path,AmbientAct.getResult(),AmbientAct.getReport());
		new KNNWAModel(Path, HighAct.getFeature(), AmbientAct.getResult(), "WAOrdinaryResult", 7, timewindow, overlap);
		new ReportForRuleClustering(Path,WAAct.getResult(),WAAct.getReport());
		*/
	}
	
	
}
