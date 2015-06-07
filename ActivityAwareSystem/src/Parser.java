import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;

import javax.print.DocFlavor;

import org.apache.commons.collections4.Factory;
import org.apache.commons.collections4.Get;

import wearable.MeaningfulActReport;
import wearable.NormalizeWearableFeature;
import wearable.PCAWearableFeatureExtration;
import wearable.MeaningfulActionFeatureExtration;
import wearable.SwingMotionFeatureExtration;
import adaption.KNNModel;
import adaption.SimilarityFunction;
import ambient.SimulatedScenario;

import com.datumbox.framework.machinelearning.clustering.MultinomialDPMMTest;

import dpmm.GDPMMOnline;
import dpmm.MDPMMOnline;
import dpmm.GDPMMTrainAuto;
import dpmm.GDPMMTrainD2;
import dpmm.MDPMMTrain;
import elements.FileFormat;


public class Parser{
	
	public static void main(String args[]) {
		boolean TrainLowLevelACT = false;
		boolean PCA = false;
		boolean TrainHighLevelACT = false;
		boolean TrainAmbient = false;
		boolean TrainWA = false;
		boolean Online = true;
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
		SocketServer server = new SocketServer();
		server.start();
		
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
			//SimilarityFunction SimilarityFun =new SimilarityFunction(Path,"Cluster_Mean.txt",20);
			SimilarityFunction SimilarityFun =new SimilarityFunction(Path,"Cluster_Mean.txt",20);
			MDPMMOnline ActionPredict = new MDPMMOnline(Path, HighAct.getResult());

			SwingMotionFeatureExtration swingMotionFeatureExtration = new SwingMotionFeatureExtration(true, true);

			GDPMMOnline WAPredict = new GDPMMOnline(Path, WAAct.getResult());
			KNNModel KNN = new KNNModel(Path, 1);
			Scanner scanner = new Scanner(System.in);
			
			MergeAWFeature MergeAW = new MergeAWFeature(Path, HighAct.getResult()+"_Mean_Cluster.txt");

			
			MDPMMOnline SwingMotionPredict = new MDPMMOnline(Path, LowAct.getResult());

			int numOfActionFeature = MergeAW.getWF();
			
			boolean actionPredictFlag = false;
			Vector<Integer> swingMotionOnline = new Vector<Integer>();
			
			int actionResult=0;
			while(true){
				String[] request = server.onRequestData();
				String line = swingMotionFeatureExtration.readRawDataOnline(request);
				//System.out.println(line);
				
				String[] feature = line.split(",");
				Double[] featureDoubles = new Double[feature.length-1];
				for (int i = 0; i < featureDoubles.length; i++)
					featureDoubles[i] = Double.valueOf(feature[i]);
				
				int swingMotionResult = SwingMotionPredict.predict(featureDoubles);
				swingMotionOnline.add(swingMotionResult);
				
				System.out.print(swingMotionOnline.size()+"\n");
				
				if(swingMotionOnline.size()==timewindow){
					actionResult = ActionPredict.predict(generateActionFeature(swingMotionOnline,numOfActionFeature));
					for(int i=0; i<(timewindow-overlap); i++){
						swingMotionOnline.remove(0);
					}
					Double[] ambientFeature = new Double[12];
					for(int i=0; i<ambientFeature.length; i++){
						System.out.print("Please input ambient feature "+i+": ");
						ambientFeature[i] = Double.valueOf(scanner.next());
					}
					
					
					String actionFeatureStr = "";
					actionFeatureStr = MergeAW.generateWAActionFeatureOnline(actionResult);
					String[] actionFeature = actionFeatureStr.split(",");
					Double[] AWFeature = new Double[actionFeature.length+ambientFeature.length];
					for(int i=0; i<AWFeature.length; i++){
						if(i<actionFeature.length){
							AWFeature[i] = Double.valueOf(actionFeature[i]);
						}
						else{
							AWFeature[i] = ambientFeature[i-actionFeature.length];
						}						
					}
					
					
					boolean unseen = SimilarityFun.reasoningUnseen(AWFeature);
					if(unseen){ System.out.println("Unseen instance!\n");}
					else{ 
						System.out.println("Seen instance\n");
					 	KNN.predict(AWFeature);
						int KNNResult = KNN.getNeareast();
						System.out.println("KNN Result is "+KNNResult+"\n");
						KNN.clear();
						
						int DPMMResult = WAPredict.predict(AWFeature);
						System.out.println("DPMM Result is "+DPMMResult);
					}
					
				}
				
					
					
					
			}
				
				
			
		}
			
		
	}
	
	private static Vector<Double> generateActionFeature(Vector<Integer> swingMotionOnline, int numOfActionClu){
		Vector<Double> actionFeature = new Vector<Double>();
		for(int i=0; i<numOfActionClu; i++)
			actionFeature.add(0.0);
		
		for(int i=0; i<swingMotionOnline.size(); i++){
			int index = swingMotionOnline.get(i);
			actionFeature.set(index, actionFeature.get(index)+1);
		}
		return actionFeature;
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


