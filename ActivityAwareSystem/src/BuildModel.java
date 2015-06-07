import java.io.File;

import wearable.MeaningfulActionFeatureExtration;
import wearable.NormalizeWearableFeature;
import wearable.PCAWearableFeatureExtration;
import wearable.SwingMotionFeatureExtration;
import ambient.SimulatedScenario;
import dpmm.GDPMMTrainAuto;
import dpmm.MDPMMTrain;
import elements.FileFormat;


public class BuildModel {
	public BuildModel(String Path, int timewindow, int overlap){
		FileFormat LowAct  = new FileFormat(Path,"SwingMotion");
		LowAct.setRawdata("05_20_MingJe.txt");
		FileFormat HighAct = new FileFormat(Path,"MeaningfulAction");
		FileFormat AmbientAct = new FileFormat(Path,"Ambient");
		FileFormat WAAct = new FileFormat(Path,"WA");

		/* Build Swing Motion Model*/
		new File(Path+"/Features").mkdirs();
		LowAct.deletFile("Features/SwingMotionFeature.txt");
		new SwingMotionFeatureExtration(Path,LowAct.getRawdata(),LowAct.getFeature(), true, true);
		new MDPMMTrain(Path,LowAct.getFeature(),LowAct.getResult(),1,5,100);
		new Report(Path,LowAct.getResult(),LowAct.getReport());
		new MeaningfulActionFeatureExtration(Path,LowAct.getResult(),HighAct.getFeature(), timewindow, overlap);	
	
		/* Build Meaningful Action Model*/
		new MDPMMTrain(Path,HighAct.getFeature(),HighAct.getResult(),1,5,100);
		new Report(Path,HighAct.getResult(),HighAct.getReport());
	
		/* Generate Offline (for training) Ambient Features*/
		new SimulatedScenario(Path, HighAct.getResult());
	
		/* Build Ambient + Wearable Model*/
		new MergeAWFeature(Path, HighAct.getResult(), AmbientAct.getFeature(), WAAct.getFeature(), timewindow, overlap,false);
		new GDPMMTrainAuto(Path,WAAct.getFeature(),WAAct.getResult(),1,0.25,100);
		new Report(Path,WAAct.getResult(),WAAct.getReport());
	}
	
	
}
