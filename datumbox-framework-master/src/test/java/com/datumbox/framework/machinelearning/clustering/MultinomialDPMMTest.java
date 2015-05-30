/* 
 * Copyright (C) 2014 Vasilis Vryniotis <bbriniotis at datumbox.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.datumbox.framework.machinelearning.clustering;

import com.datumbox.common.dataobjects.Dataset;
import com.datumbox.common.dataobjects.Record;
import com.datumbox.common.utilities.RandomValue;
import com.datumbox.configuration.MemoryConfiguration;
import com.datumbox.configuration.TestConfiguration;
import com.datumbox.framework.machinelearning.common.bases.basemodels.BaseDPMM;
import com.mongodb.util.StringParseUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import org.apache.commons.math3.geometry.spherical.oned.ArcsSet.Split;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Vasilis Vryniotis <bbriniotis at datumbox.com>
 */
public class MultinomialDPMMTest {
    
	String version;// = "5.20.MingJe";
	
	
	int alpha;
    int iter;
    int alpha_words;
	String filename;
	String filenameProbability;
	Dataset trainingData;
	Dataset validationData;
	MultinomialDPMM instance;
	
    public MultinomialDPMMTest() {
    		setFilepath(1);
    		alpha = 1;
    	    iter = 100;
    	    alpha_words = 5;
    }
    
    public MultinomialDPMMTest(int choose, int a, int aw, int iteration) {
		setFilepath(choose);
		alpha = a;
	    iter = iteration;
	    alpha_words =aw;
    }
    public MultinomialDPMMTest(String path, String iFile, String oFile, int a, int aw, int iteration) {
		version = path;
		alpha = a;
	    iter = iteration;
	    alpha_words =aw;
	    setFilepath(iFile,oFile);
    }
    private void setFilepath(String iFile, String oFile){
	    	new File(version+"/DPMM").mkdirs();
	    	trainingData = generateDatasetFeature(version+"/Features/"+iFile);
		filename =  version+"/DPMM/"+oFile;
		filenameProbability = version+"/DPMM/"+oFile+"_probability.txt";
    }
    
    private void setFilepath(int choose){
    		new File(version+"/"+"DPMM").mkdirs();
	    	switch (choose) {
			case 0:
				trainingData = generateDatasetFeature(version+"/"+"SwingMotionFeature.txt");
				filename =  "/Users/rainbowbow/Documents/workspace/Parsecluster/"+version+"/DPMM/"+"SwingMotionResult";
				filenameProbability = "/Users/rainbowbow/Documents/workspace/Parsecluster/"+version+"/DPMM/"+"SwingMotionResult_probability.txt";
				break;
			case 1:
				trainingData = generateDatasetFeature(version+"/"+"HistogramFeature.txt");
				filename =  "/Users/rainbowbow/Documents/workspace/Parsecluster/"+version+"/MeaningfulActionResult";
				break;
			case 2:
				trainingData = generateDatasetFeature("PCASwingMotionFeature.txt");
				filename =  "/Users/rainbowbow/Documents/workspace/Parsecluster/"+version+"/SwingMotionResult";
				break;
			case 3:
				trainingData = generateDatasetFeature("APFeature.csv");
				filename =  "/Users/rainbowbow/Documents/workspace/Parsecluster/"+version+"/AmbientResult";
				break;
			default:
				trainingData = generateDatasetFeature("SwingMotionFeature.txt");
				filename =  "/Users/rainbowbow/Documents/workspace/Parsecluster/"+version+"/SwingMotionResult";
				break;
		}
    }
    

    /**
     * Test of predict method, of class MultinomialDPMM.
     */
    //@Test
    public void testPredict() {
        System.out.println("predict"); 
        RandomValue.randomGenerator = new Random(42); 
        
        MemoryConfiguration memoryConfiguration = new MemoryConfiguration();
        
        validationData = trainingData;
        
        String dbName = "JUnitClusterer";
        
        instance = new MultinomialDPMM(dbName);
        
        MultinomialDPMM.TrainingParameters param = instance.getEmptyTrainingParametersObject();
        param.setAlpha(alpha);
        param.setMaxIterations(iter);
        param.setInitializationMethod(BaseDPMM.TrainingParameters.Initialization.ONE_CLUSTER_PER_RECORD);
        param.setAlphaWords(alpha_words);
        instance.initializeTrainingConfiguration(memoryConfiguration, param);
        
        instance.train(trainingData, validationData);
        
        instance = null;
        instance = new MultinomialDPMM(dbName);
        instance.setMemoryConfiguration(memoryConfiguration);
        instance.predict(validationData);
        
        printClusterResult();
        printProbabilityResult();
	     
	    /*
        // 5 Cross validation
        MultinomialDPMM.ValidationMetrics vm = instance.kFoldCrossValidation(trainingData, 5);        
        double expResult = 1.0;
        double result = vm.getPurity();
       // assertEquals(expResult, result, TestConfiguration.DOUBLE_ACCURACY_MEDIUM);
        System.out.println("Cross Validation Result: "+result);
        */
        
        //assertEquals(expResult, result);
        instance.erase(true);
    }

    private void printClusterResult(){
    	try {
			FileWriter fw = new FileWriter(filename);
        
	        Map<Integer, Object> expResult = new HashMap<>();
	        Map<Integer, Object> result = new HashMap<>();
	        
	        Map<Integer, MultinomialDPMM.Cluster> clusters = instance.getClusters();
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
	            System.out.println(InstanceResult);
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
	        
	        Map<Integer, MultinomialDPMM.Cluster> clusters = instance.getClusters();
	        for(Record r : validationData) {
	            expResult.put(r.getId(), r.getY());
	            Integer clusterId = (Integer) r.getYPredicted();
	            Object label = clusters.get(clusterId).getLabelY();
	            if(label==null) {
	                label = clusterId;
	            }
	            
	            result.put(r.getId(), label);
	            
	            String tmpStr = r.getYPredictedProbabilities().toString().substring(1, r.getYPredictedProbabilities().toString().length()-1);
	            String[] tmp = tmpStr.split(",");
	            //double[] probability = new double[tmp.length];
	            for(int i=0; i<tmp.length; i++){
	            		String[] tmp2 = tmp[i].split("=");
	            		fw.write(tmp2[1]+",");
	            }
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
    
    private Dataset generateDataset313( int version) {
		Dataset trainingData = new Dataset();
		try {
		
			String[] ACTIVITY = new String[6];
			ACTIVITY[0] = "PlayingKinect";
			ACTIVITY[1] = "WatchingTV";
			ACTIVITY[2] = "Studying";
			ACTIVITY[3] = "UsingNotebook";
			ACTIVITY[4] = "UsingPC";
			ACTIVITY[5] = "Sleeping";
			FileReader fr = new FileReader( "ambient_v" + version+".csv");
			BufferedReader br = new BufferedReader(fr);
			String line = br.readLine();
			while((line = br.readLine())!=null){
				String[] feature = line.split(",");
				int FEATURENUM = feature.length - 1;
				Object[] instance = new Object[FEATURENUM];
				for(int i=0; i<instance.length; i++){
					if(feature[i+1].contains("off")){
						feature[i+1] = "0";
					}
					else if(feature[i+1].contains("standby")){
						feature[i+1] = "1";
					}
					else if(feature[i+1].contains("on")){
						feature[i+1] = "2";
					}
					//System.out.print(feature[i+1]+",");
					instance[i] =Double.valueOf(feature[i+1]);
				}
				trainingData.add(Record.newDataVector(instance, feature[0]));
			}
			br.close();
			fr.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return trainingData;
    }
    
    
    /**
     * Test of kFoldCrossValidation method, of class MultinomialDPMM.
     */
   // @Test
    /*
    public void testKFoldCrossValidation() {
        System.out.println("kFoldCrossValidation");
        RandomValue.randomGenerator = new Random(42); 
        int k = 5;
        
        Dataset trainingData = generateDataset();
        
        MemoryConfiguration memoryConfiguration = new MemoryConfiguration();
        
        
        String dbName = "JUnitRegressor";

        MultinomialDPMM instance = new MultinomialDPMM(dbName);
        
        MultinomialDPMM.TrainingParameters param = instance.getEmptyTrainingParametersObject();
        param.setAlpha(0.01);
        param.setMaxIterations(100);
        param.setInitializationMethod(BaseDPMM.TrainingParameters.Initialization.ONE_CLUSTER_PER_RECORD);
        param.setAlphaWords(1);
        instance.initializeTrainingConfiguration(memoryConfiguration, param);
        MultinomialDPMM.ValidationMetrics vm = instance.kFoldCrossValidation(trainingData, k);

        
        double expResult = 1.0;
        double result = vm.getPurity();
        assertEquals(expResult, result, TestConfiguration.DOUBLE_ACCURACY_MEDIUM);
        instance.erase(true);
    }
     */
    
}
