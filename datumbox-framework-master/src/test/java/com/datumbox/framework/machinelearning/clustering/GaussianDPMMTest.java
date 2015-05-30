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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Vasilis Vryniotis <bbriniotis at datumbox.com>
 */
public class GaussianDPMMTest {
	String[] axisEnum = new String[3];
	String readFile = "axis_z";
	String outFile = readFile+"_out";
	String calTimeFile = readFile+"_time";
	//int axis = 2;
    public GaussianDPMMTest() {
    		axisEnum[0] = "x";
    		axisEnum[1] = "y";
    		axisEnum[2] = "z";
    }

    private Dataset generateDataset(){
    		Dataset trainingData = new Dataset();
    		try {
				BufferedReader in = new BufferedReader(new FileReader(readFile));
				String s = "";
				try {
					while((s=in.readLine())!=null){
						//String[] tmp = s.split(",");
						//double data1 = Double.valueOf(tmp[0]);
						//double data2 = Double.valueOf(tmp[1]);
						double data = Double.valueOf(s);
						trainingData.add(Record.newDataVector(new Object[] {data}, "c1"));
						//trainingData.add(Record.newDataVector(new Object[] {data1, data2}, "c1"));
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    		
    		return trainingData;
    	
    }
    
    
    private Dataset generateDataset2() {
        Random rnd = RandomValue.randomGenerator;
        Dataset trainingData = new Dataset();
        /*
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
        */
        int observationsPerCluster = 500;
        /*for(int i=0;i<observationsPerCluster;++i) {
            trainingData.add(Record.newDataVector(new Object[] {rnd.nextGaussian(),rnd.nextGaussian()}, "c1"));
        }
        
        for(int i=0;i<observationsPerCluster;++i) {
            trainingData.add(Record.newDataVector(new Object[] {100+rnd.nextGaussian(),50+rnd.nextGaussian()}, "c2"));
        }
        
        for(int i=0;i<observationsPerCluster;++i) {
            trainingData.add(Record.newDataVector(new Object[] {50+rnd.nextGaussian(),100+rnd.nextGaussian()}, "c3"));
        }*/
        for(int j=0; j<10; j++){
	        for(int i=0;i<observationsPerCluster;++i) {
	        		String label = "c"+ j;
	            trainingData.add(Record.newDataVector(new Object[] {j}, label));
	        }
        }
        return trainingData;
    }
    

    private Dataset generateDataset3(int axis, int version) {
    		Dataset trainingData = new Dataset();
    		try {
				FileReader fr_wk = new FileReader("Feature_" + axis + "_v" + version + "_walk");
				BufferedReader br_wk = new BufferedReader(fr_wk);
				FileReader fr_std = new FileReader("Feature_" + axis + "_v" + version + "_stand");
				BufferedReader br_std = new BufferedReader(fr_std);
				FileReader fr_sit = new FileReader("Feature_" + axis + "_v" + version + "_sit");
				BufferedReader br_sit = new BufferedReader(fr_sit);
				FileReader fr_run = new FileReader("Feature_" + axis + "_v" + version + "_run");
				BufferedReader br_run = new BufferedReader(fr_run);
				
				int dataNum = 0;
				String line;
				Boolean end = false;
				try {
					double[] MEAN = new double[3];
					double[] VAR = new double[3];
					/*
					while((line = br_wk.readLine())!=null){
						//line = br.readLine();
						System.out.println(line);
						double mean = Double.valueOf(line);
						MEAN[dataNum%3] = mean;
						if ((line = br_wk.readLine())==null)
							end = true;
						double var = Double.valueOf(line);
						VAR[dataNum%3] = var;
						//trainingData.add(Record.newDataVector(new Object[] { mean, var}, "walk"));
						dataNum++;
						if(dataNum%3==0){
							trainingData.add(Record.newDataVector(new Object[] { MEAN[0], VAR[0], MEAN[1], VAR[1], MEAN[2], VAR[2]}, "walk"));
						}	
					}
					fr_wk.close();
					dataNum = 0;
					end = false;
					
					while((line = br_std.readLine())!=null){
						//line = br.readLine();
						System.out.println(line);
						double mean = Double.valueOf(line);
						MEAN[dataNum%3] = mean;
						if ((line = br_std.readLine())==null)
							end = true;
						double var = Double.valueOf(line);
						VAR[dataNum%3] = var;
						//trainingData.add(Record.newDataVector(new Object[] { mean, var}, "walk"));
						dataNum++;
						if(dataNum%3==0){
							trainingData.add(Record.newDataVector(new Object[] { MEAN[0], VAR[0], MEAN[1], VAR[1], MEAN[2], VAR[2]}, "stand"));
						}		
					}
					fr_std.close();
					dataNum = 0;
					end = false;
					
					while((line = br_sit.readLine())!=null){
						//line = br.readLine();
						System.out.println(line);
						double mean = Double.valueOf(line);
						MEAN[dataNum%3] = mean;
						if ((line = br_sit.readLine())==null)
							end = true;
						double var = Double.valueOf(line);
						VAR[dataNum%3] = var;
						//trainingData.add(Record.newDataVector(new Object[] { mean, var}, "walk"));
						dataNum++;
						if(dataNum%3==0){
							trainingData.add(Record.newDataVector(new Object[] { MEAN[0], VAR[0], MEAN[1], VAR[1], MEAN[2], VAR[2]}, "sit"));
						}		
					}
					fr_sit.close();
					dataNum = 0;
					end = false;
					
					while((line = br_run.readLine())!=null){
						//line = br.readLine();
						System.out.println(line);
						double mean = Double.valueOf(line);
						MEAN[dataNum%3] = mean;
						if ((line = br_run.readLine())==null)
							end = true;
						double var = Double.valueOf(line);
						VAR[dataNum%3] = var;
						//trainingData.add(Record.newDataVector(new Object[] { mean, var}, "walk"));
						dataNum++;
						if(dataNum%3==0){
							trainingData.add(Record.newDataVector(new Object[] { MEAN[0], VAR[0], MEAN[1], VAR[1], MEAN[2], VAR[2]}, "run"));
						}	
					}
					fr_run.close();
					dataNum = 0;
					end = false;
					*/
					
					while((line = br_wk.readLine())!=null){
						//line = br.readLine();
						System.out.println(line);
						double mean = Double.valueOf(line);
						if ((line = br_wk.readLine())==null)
							end = true;
						double var = Double.valueOf(line);
						trainingData.add(Record.newDataVector(new Object[] { mean, var}, "walk"));
						dataNum++;
					}
					fr_wk.close();
					dataNum = 0;
					end = false;
					
					while((line = br_std.readLine())!=null){
						//line = br.readLine();
						System.out.println(line);
						double mean = Double.valueOf(line);
						if ((line = br_std.readLine())==null)
							end = true;
						double var = Double.valueOf(line);
						trainingData.add(Record.newDataVector(new Object[] { mean, var}, "stand"));
						dataNum++;
					}
					fr_std.close();
					dataNum = 0;
					end = false;
					while((line = br_sit.readLine())!=null){
						//line = br.readLine();
						System.out.println(line);
						double mean = Double.valueOf(line);
						if ((line = br_sit.readLine())==null)
							end = true;
						double var = Double.valueOf(line);
						trainingData.add(Record.newDataVector(new Object[] { mean, var}, "sit"));
						dataNum++;
					}
					fr_sit.close();
					
					dataNum = 0;
					end = false;
					while((line = br_run.readLine())!=null){
						//line = br.readLine();
						System.out.println(line);
						double mean = Double.valueOf(line);
						if ((line = br_run.readLine())==null)
							end = true;
						double var = Double.valueOf(line);
						trainingData.add(Record.newDataVector(new Object[] { mean, var}, "run"));
						dataNum++;
					}
					fr_run.close();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        return trainingData;
    }
    
    private Dataset generateDataset4( int version) {
    		Dataset trainingData = new Dataset();
		try {
			
			String[] ACTIVITY = new String[4];
			ACTIVITY[0] = "walk";
			ACTIVITY[1] = "stand";
			ACTIVITY[2] = "sit";
			ACTIVITY[3] = "run";
			for(int i=0; i<ACTIVITY.length; i++){
				FileReader frX = new FileReader("Feature_0_v" + version + "_" + ACTIVITY[i]);
				BufferedReader brX = new BufferedReader(frX);
				FileReader frY = new FileReader("Feature_1_v" + version + "_" + ACTIVITY[i]);
				BufferedReader brY = new BufferedReader(frY);
				FileReader frZ = new FileReader("Feature_2_v" + version + "_" + ACTIVITY[i]);
				BufferedReader brZ = new BufferedReader(frZ);
				String line;
				while((line = brX.readLine())!=null){
					//line = br.readLine();
					System.out.println(line);
					double[] M = new double[3];
					double[] V = new double[3];
					M[0] = Double.valueOf(line);
					V[0] = Double.valueOf(brX.readLine());
					M[1] = Double.valueOf(brY.readLine());
					V[1] = Double.valueOf(brY.readLine());
					M[2] = Double.valueOf(brZ.readLine());
					V[2] = Double.valueOf(brZ.readLine());
					trainingData.add(Record.newDataVector(new Object[] { M[0], V[0], M[1], V[1], M[2], V[2]}, ACTIVITY[i]));
				}
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
     * Test of predict method, of class GaussianDPMM.
     */
    @Test
    public void testPredict() {
    		int version = 1;
    		int axis = 0;
    		//while(axis<3){

	        System.out.println("predict"); 
	        
	        //Dataset trainingData = generateDataset3(axis,version);
	        //Dataset trainingData = generateDataset4(version);
	        Dataset trainingData = generateDataset313(8);
	        Dataset validationData = trainingData;
	        

	        MemoryConfiguration memoryConfiguration = new MemoryConfiguration();
	        
	        String dbName = "JUnitClusterer";
	        
	        GaussianDPMM instance = new GaussianDPMM(dbName);
	        
	        GaussianDPMM.TrainingParameters param = instance.getEmptyTrainingParametersObject();
	        param.setAlpha(0.01);
	        //param.setAlpha(0.01);
	        param.setMaxIterations(100);
	        param.setInitializationMethod(BaseDPMM.TrainingParameters.Initialization.ONE_CLUSTER_PER_RECORD);
	        param.setKappa0(0);
	        param.setNu0(1);
	        //param.setMu0(new double[]{0, 0});
	        //param.setMu0(new double[]{1, 0, 0, 0, 0, 0});
	        param.setMu0(new double[]{1.0});
	        //param.setPsi0(new double[][]{{1,0},{0,10}});
	        //param.setPsi0(new double[][]{{1,0,0,0,0,0},{0,1,0,0,0,0},{0,0,1,0,0,0},{0,0,0,1,0,0},{0,0,0,0,1,0},{0,0,0,0,0,1}});
	        param.setPsi0(new double[][]{{1.0}});
	        instance.initializeTrainingConfiguration(memoryConfiguration, param);
	        instance.train(trainingData, validationData);
	        
	        
	        instance = null;
	        instance = new GaussianDPMM(dbName);
	        instance.setMemoryConfiguration(memoryConfiguration);
	        instance.predict(validationData);
	        
	        
	        Map<Integer, Object> expResult = new HashMap<>();
	        Map<Integer, Object> result = new HashMap<>();
	        
	        Map<Integer, GaussianDPMM.Cluster> clusters = instance.getClusters();
	        
	        try {
	        		int ClusterNum = 0;
				//FileWriter fw = new FileWriter(outFile);
	        		//FileWriter fw = new FileWriter("axis_"+axisEnum[axis]+"_out");
	        		//FileWriter fw = new FileWriter("axis_all_out");
	    			FileWriter fw = new FileWriter("testResult.txt");
	    			FileWriter fw2 = new FileWriter("testResult_probability.txt");
		        for(Record r : validationData) {
		            expResult.put(r.getId(), r.getY());
		            Integer clusterId = (Integer) r.getYPredicted();
		            Object label = r.getY();// clusters .get(clusterId).getLabelY();
		            //Object label = clusters.get(clusterId).getLabelY();
		            if(label==null) {
		                label = clusterId;
		            }
		            result.put(r.getId(), label);
		            //fw.write(label.toString()+"\t"+clusterId+"\n");
		            String writeTmp4  = r.getY()+"\t"+r.getYPredictedProbabilities()+"\n";
		            String writeTmp3 = r.getY()+"\t"+ r.getYPredicted()+"\n";
		            fw.write(writeTmp3);
		            fw2.write(writeTmp4);
		            if(clusterId.intValue() >ClusterNum)
		            		ClusterNum = clusterId.intValue();
		            
		            System.out.println(r.getX()+"\t"+clusterId);
		            //System.out.println(label.toString()+"\t"+clusterId);
		        }

		        fw.flush();
		        fw.close();
		        
		        fw = new FileWriter("ClusterNum",false);
		        //fw = new FileWriter("ClusterNum",true);
		        fw.write((ClusterNum+1)+"\n");
		        fw.flush();
		        fw.close();
		        fw2.flush();
		        fw.close();
	        } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        
	        //assertEquals(expResult, result);
	        
	        instance.erase(true);

			axis++;
    		//}
    }

    
    /**
     * Test of kFoldCrossValidation method, of class GaussianDPMM.
     */
   /*
    @Test
    public void testKFoldCrossValidation() {
        System.out.println("kFoldCrossValidation");
        RandomValue.randomGenerator = new Random(42); 
        int k = 5;
        
        Dataset trainingData = generateDataset();
        
        MemoryConfiguration memoryConfiguration = new MemoryConfiguration();
        
        
        String dbName = "JUnitRegressor";


        
        
        
        GaussianDPMM instance = new GaussianDPMM(dbName);
        
        GaussianDPMM.TrainingParameters param = instance.getEmptyTrainingParametersObject();
        param.setAlpha(0.01);
        param.setMaxIterations(100);
        param.setInitializationMethod(BaseDPMM.TrainingParameters.Initialization.ONE_CLUSTER_PER_RECORD);
        param.setKappa0(0);
        param.setNu0(1);
        //param.setMu0(new double[]{0.0, 0.0});
        //param.setPsi0(new double[][]{{1.0,0.0},{0.0,1.0}});
        param.setMu0(new double[]{0.0});
        param.setPsi0(new double[][]{{1.0}});
        instance.initializeTrainingConfiguration(memoryConfiguration, param);
        GaussianDPMM.ValidationMetrics vm = instance.kFoldCrossValidation(trainingData, k);

        
        double expResult = 1.0;
        double result = vm.getPurity();
        assertEquals(expResult, result, TestConfiguration.DOUBLE_ACCURACY_MEDIUM);
        instance.erase(true);
    }
*/
    
}
