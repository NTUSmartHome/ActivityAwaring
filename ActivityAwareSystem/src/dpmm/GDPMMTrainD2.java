package dpmm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import com.datumbox.common.dataobjects.Dataset;
import com.datumbox.common.dataobjects.Record;
import com.datumbox.common.utilities.RandomValue;
import com.datumbox.configuration.MemoryConfiguration;
import com.datumbox.framework.machinelearning.clustering.GaussianDPMM;
import com.datumbox.framework.machinelearning.common.bases.basemodels.BaseDPMM;

public class GDPMMTrainD2 {
	String version;// = "5.20.MingJe";
	
	double alpha;
    int iter;
    double m1,m2;
    //int alpha_words;
	String filename;
	String filenameProbability;
	Dataset trainingData;
	Dataset validationData;
	GaussianDPMM instance;
	String ModelName = "";
    
    public GDPMMTrainD2(String path, String name, double a, double mu1, double mu2, int iteration) {
		version = path;
		alpha = a;
	    iter = iteration;
	    m1 = mu1;
	    m2 = mu2;
	    ModelName = name;
	    //alpha_words =aw;
	    setFilepath(name);
	    testPredict();
    }
    private void setFilepath(String name){
    	new File(version+"/DPMM").mkdirs();
    	trainingData = generateDatasetFeature(version+"/Features/"+name+"Feature.txt");
		filename =  version+"/DPMM/"+name;
		filenameProbability = version+"/DPMM/"+name+"_probability.txt";
    }
    
    public void testPredict() {
        System.out.println("predict"); 
        RandomValue.randomGenerator = new Random(42); 
        
        MemoryConfiguration memoryConfiguration = new MemoryConfiguration();
        
        validationData = trainingData;
        
        String dbName = ModelName;
        
        instance = new GaussianDPMM(dbName);
        
        GaussianDPMM.TrainingParameters param = instance.getEmptyTrainingParametersObject();
        param.setAlpha(alpha);
        param.setMaxIterations(iter);
        param.setInitializationMethod(BaseDPMM.TrainingParameters.Initialization.ONE_CLUSTER_PER_RECORD);
        //param.setAlphaWords(alpha_words);
        
        param.setKappa0(0);
        param.setNu0(0);
        param.setMu0(new double[]{0,0});
        param.setPsi0(new double[][]{{m1,m2*0.2},{m1*0.2,m2}});
        instance.initializeTrainingConfiguration(memoryConfiguration, param);
        instance.setModelname(ModelName);
        instance.train(trainingData, validationData);
        
        instance = null;
        instance = new GaussianDPMM(dbName);
        instance.setMemoryConfiguration(memoryConfiguration);
        instance.predict(validationData);
        
        printClusterResult();
        printProbabilityResult();

        instance.erase(true);
    }

    private void printClusterResult(){
    	try {
			FileWriter fw = new FileWriter(filename);
        
	        Map<Integer, Object> expResult = new HashMap<>();
	        Map<Integer, Object> result = new HashMap<>();
	        
	        Map<Integer, GaussianDPMM.Cluster> clusters = instance.getClusters();
	        for(Record r : validationData) {
	            expResult.put(r.getId(), r.getY());
	            Integer clusterId = (Integer) r.getYPredicted();
	            Object label = clusters.get(clusterId).getLabelY();
	            if(label==null) {
	                label = clusterId;
	            }
	            
	            result.put(r.getId(), label);
	            
	            String InstanceResult = "Label: "+r.getY()+", predict: "+ r.getYPredicted();
	            String printInstanceResult = r.getY()+"\t"+ r.getYPredicted()+"\r\n";
	            //System.out.println(InstanceResult);
	            fw.write(printInstanceResult);
	        }
	        fw.flush();
	        fw.close();
	        
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    private void printProbabilityResult(){
    	try {
    			Vector<String> LABEL = new Vector<String>();
			LABEL.add("Exercise");
			LABEL.add("Sweep");
			LABEL.add("Walk");
			LABEL.add("Meal");
			LABEL.add("WashDishes");
			LABEL.add("PlayPad");
			LABEL.add("WatchTV");
			LABEL.add("Read");
			LABEL.add("Sleep");
			LABEL.add("Other");  
			
			FileWriter fw = new FileWriter(filenameProbability);
	        Map<Integer, Object> expResult = new HashMap<>();
	        Map<Integer, Object> result = new HashMap<>();
	        
	        Map<Integer, GaussianDPMM.Cluster> clusters = instance.getClusters();
	        for(Record r : validationData) {
	            expResult.put(r.getId(), r.getY());
	            Integer clusterId = (Integer) r.getYPredicted();
	            Object label = clusters.get(clusterId).getLabelY();
	            if(label==null) {
	                label = clusterId;
	            }
	            
	            result.put(r.getId(), label);
	            double max = 0;
	            int maxId = 0;
	            String tmpStr = r.getYPredictedProbabilities().toString().substring(1, r.getYPredictedProbabilities().toString().length()-1);
	            String[] tmp = tmpStr.split(",");
	            //double[] probability = new double[tmp.length];
	            for(int i=0; i<tmp.length; i++){
	            		String[] tmp2 = tmp[i].split("=");
	            		fw.write(tmp2[1]+",");
	            		double p = Double.valueOf(tmp2[1]);
	            		if(p>max){
	            			max = p;
	            			maxId = i;
	            		}
	            }
	            //Predicted Result
	            fw.write("Predicted Result:,"+r.getYPredicted()+",");
	            for(int i=0; i<tmp.length; i++){
	            		if(maxId==i){
	            			fw.write("1,");
	            		}
	            		else{
	            			fw.write("0,");
					}
	            }
	            //GroundTruth
	            fw.write(r.getY()+",");
	            for(int i=0; i<LABEL.size()-1; i++){
            			if(r.getY().equals(LABEL.get(i))){
            				fw.write("1,");
            			}
            			else{
            				fw.write("0,");
            			}
	            }
	            if(r.getY().equals(LABEL.get(LABEL.size()-1))){
    				fw.write("1\r\n");
	    			}
	    			else{
	    				fw.write("0\r\n");
	    			}
	        }
	        fw.flush();
	        fw.close();

	        
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private Dataset generateDataset() {
        Dataset trainingData = new Dataset();
        //cluster 1
        trainingData.add(Record.newDataVector(new Object[] {10.0,13.0, 5.0,6.0,5.0,4.0, 0.0,0.0,0.0,0.0}, "c1"));
        trainingData.add(Record.newDataVector(new Object[] {11.0,11.0, 6.0,7.0,7.0,3.0, 0.0,0.0,1.0,0.0}, "c1"));
        trainingData.add(Record.newDataVector(new Object[] {12.0,12.0, 10.0,16.0,4.0,6.0, 0.0,0.0,0.0,2.0}, "c1"));
        //cluster 2
        trainingData.add(Record.newDataVector(new Object[] {10.0,13.0, 0.0,0.0,0.0,0.0, 5.0,6.0,5.0,4.0}, "c2"));
        trainingData.add(Record.newDataVector(new Object[] {11.0,11.0, 0.0,0.0,1.0,0.0, 6.0,7.0,7.0,3.0}, "c2"));
        trainingData.add(Record.newDataVector(new Object[] {12.0,12.0, 0.0,0.0,0.0,2.0, 10.0,16.0,4.0,6.0}, "c2"));
        //cluster 3
        trainingData.add(Record.newDataVector(new Object[] {10.0,13.0, 5.0,6.0,5.0,4.0, 5.0,6.0,5.0,4.0}, "c3"));
        trainingData.add(Record.newDataVector(new Object[] {11.0,11.0, 6.0,7.0,7.0,3.0, 6.0,7.0,7.0,3.0}, "c3"));
        trainingData.add(Record.newDataVector(new Object[] {12.0,12.0, 10.0,16.0,4.0,6.0, 10.0,16.0,4.0,6.0}, "c3"));
        return trainingData;
    }
    
    private Dataset generateDatasetFeature(String filename ) {
		Dataset trainingData = new Dataset();
		try {
			FileReader fr = new FileReader(filename);
			BufferedReader br = new BufferedReader(fr);
			String line = "";
			while((line = br.readLine())!=null){
				String[] tmp = line.split(",");
				int featureNum = tmp.length-1;
				Object[] feature = new Object[featureNum];
				for(int i=0; i<feature.length; i++){
					feature[i] = Math.abs(Double.valueOf(tmp[i]));
				}
				String lable = tmp[featureNum];
				trainingData.add(Record.newDataVector(feature, lable));
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return trainingData;
    }
    
    
}
