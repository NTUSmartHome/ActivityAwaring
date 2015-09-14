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
package com.datumbox.framework.machinelearning.classification;


import com.datumbox.common.dataobjects.Dataset;
import com.datumbox.common.dataobjects.Record;

import com.datumbox.configuration.MemoryConfiguration;
import com.datumbox.framework.machinelearning.datatransformation.SimpleDummyVariableExtractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


import libsvm.svm_parameter;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Vasilis Vryniotis <bbriniotis at datumbox.com>
 */
public class SupportVectorMachineTrain {
	String version;// = "5.20.MingJe";
	
	double alpha;
    int iter;
    int featureNum = 0;
    String dbName;
    String filename;
    Dataset trainingData;
	Dataset validationData;
    
    public SupportVectorMachineTrain(String path, String name) {
    	version = path;
    	setFilepath(name);
    	buildModel();
    }

    private void setFilepath(String name){
    	new File(version+"/SVM").mkdirs();
    	trainingData = generateDatasetFeature(version+"/Features/"+name+"Feature.txt");
		filename =  version+"/SVM/"+name+"Result";
		dbName = name;
    }
    

    /**
     * Test of predict method, of class SupportVectorMachine.
     */
    //@Test
    public void buildModel() {
        System.out.println("predict");
        //trainingData = generateDatasetFeature(filename);
        //Dataset validationData = generateDatasetFeature(filename);
        Dataset validationData = trainingData;
        
       
        MemoryConfiguration memoryConfiguration = new MemoryConfiguration();
        
        SimpleDummyVariableExtractor df = new SimpleDummyVariableExtractor(dbName);
        df.initializeTrainingConfiguration(memoryConfiguration, df.getEmptyTrainingParametersObject());
        df.transform(trainingData, true);
        df.normalize(trainingData);
        df.transform(validationData, false);
        df.normalize(validationData);
        
        SupportVectorMachine instance = new SupportVectorMachine(dbName);
        instance.setModelname(dbName);
        
        SupportVectorMachine.TrainingParameters param = instance.getEmptyTrainingParametersObject();
        param.getSvmParameter().kernel_type = svm_parameter.RBF;
        instance.initializeTrainingConfiguration(memoryConfiguration, param);
        instance.train(trainingData, validationData);
        System.out.println("after train2");
        
        /*
        instance = null;
        instance = new SupportVectorMachine(dbName);
        instance.setMemoryConfiguration(memoryConfiguration);
        instance.predict(validationData);
        */
        /*
        df.denormalize(trainingData);
        df.denormalize(validationData);
        df.erase(true);
         */
        System.out.println("Try to print predict model...");
        //try {
			//FileWriter fw = new FileWriter(filename);
        	
			Map<Integer, Object> expResult = new HashMap<>();
	        Map<Integer, Object> result = new HashMap<>();
	        for(Record r : validationData) {
	            expResult.put(r.getId(), r.getY());
	            result.put(r.getId(), r.getYPredicted());
	            System.out.print(r.getX()+"\t");
	            System.out.println(r.getY()+"\t"+r.getYPredictedProbabilities());
	            //fw.write(r.getY()+"\t"+r.getYPredicted()+"\r\n");
	        }
	        
	        //fw.flush();
	        //fw.close();
	        //instance.erase(true);
		/*} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
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
			trainingData = tmpData;
			/*
			for(int i=0; i<tmpData.size(); i++){
				trainingData.add(tmpData.get(i));
			}*/
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return trainingData;
    }
    
    
   
}
