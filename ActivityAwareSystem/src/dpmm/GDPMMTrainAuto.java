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
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

public class GDPMMTrainAuto {
	String version;// = "5.20.MingJe";
	
	double alpha;
    int iter;
    int featureNum = 0;
    double m1;
    //int alpha_words;
	String filename;
	String filenameProbability;
	Dataset trainingData;
	Dataset validationData;
	Dataset unseenData = new Dataset();
	Dataset unseenData2 = new Dataset();
	GaussianDPMM instance;
	
    String ModelName ="";
    public GDPMMTrainAuto(String path, String iFile, String oFile, double a, double mu1, int iteration) {
		version = path;
		alpha = a;
	    iter = iteration;
	    m1 = mu1;
	    ModelName = oFile;
	    //alpha_words =aw;
	    setFilepath(iFile,oFile);
	    testPredict();
    }
    private void setFilepath(String iFile, String oFile){
	    	new File(version+"/DPMM").mkdirs();
	    	trainingData = generateDatasetFeature(version+"/Features/"+iFile);
		filename =  version+"/DPMM/"+oFile;
		filenameProbability = version+"/DPMM/"+oFile+"_probability.txt";
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
        
        param.setKappa0(0);
        param.setNu0(0);
        double[] mu = new double[featureNum];
        for(int i=0; i<mu.length; i++) mu[i] = 0;
        param.setMu0(mu);
        
        double[][] psi = new double [featureNum][featureNum];
        for(int i=0; i<psi.length; i++){
        		for(int j=0; j<psi.length; j++){
        			if(i==j){
        				psi[i][j] = m1;
        				/*if (j<12){
        					psi[i][j] = m1;
        				}
        				else{
        					psi[i][j] = m1*2;
        				}*/
        			}
        			else {
        				psi[i][j] = 0;
				}
        		}
        }
        param.setPsi0(psi);
        instance.initializeTrainingConfiguration(memoryConfiguration, param);
        
        instance.setModelname(ModelName);
        System.out.println(ModelName);
        
        instance.train(trainingData, validationData);
        
        instance.predict(validationData);
        
        System.out.println("DB name:\t"+instance.getDBname());
        System.out.println("Number of cluster:\t"+instance.getModelParameters().getC().toString());
        System.out.println("Number of features:\t"+instance.getModelParameters().getD().toString());
        System.out.println("Number of instances:\t"+instance.getModelParameters().getN().toString());
        
        printClusterResult(validationData);
        printProbabilityResult(validationData);
	    
        //assertEquals(expResult, result);
        //instance.erase(true);
    }

    private void printClusterResult(Dataset validationData){
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
	            String printInstanceResult = r.getY()+"\t"+ r.getYPredicted()+"\n";
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
    private void printProbabilityResult(Dataset validationData){
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
    				fw.write("1\n");
	    			}
	    			else{
	    				fw.write("0\n");
	    			}
	        }
	        fw.flush();
	        fw.close();

	        
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    
    private Dataset generateDatasetFeature(String filename ) {
		Dataset trainingData = new Dataset();
		try {
			FileReader fr = new FileReader(filename);
			BufferedReader br = new BufferedReader(fr);
			String line = "";
			Dataset tmpData = new Dataset();
			while((line = br.readLine())!=null){
				String[] tmp = line.split(",");
				featureNum = tmp.length-1;
				Object[] feature = new Object[featureNum];
				for(int i=0; i<feature.length; i++){
					feature[i] = Math.abs(Double.valueOf(tmp[i]));
				}
				String lable = tmp[featureNum];
				tmpData.add(Record.newDataVector(feature, lable));
			}
			
			for(int i=0; i<tmpData.size(); i++){
				trainingData.add(tmpData.get(i));
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return trainingData;
    }
    
    
}
