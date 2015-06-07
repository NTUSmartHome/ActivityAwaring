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
package com.datumbox.framework.machinelearning.common.bases.mlmodels;

import com.datumbox.framework.machinelearning.common.bases.validation.ModelValidation;
import com.datumbox.common.dataobjects.Dataset;
import com.datumbox.framework.machinelearning.common.bases.BaseTrainable;
import com.datumbox.common.objecttypes.Learnable;
import com.datumbox.common.objecttypes.Parameterizable;
import com.datumbox.common.persistentstorage.factories.BigDataStructureFactory;
import com.datumbox.common.persistentstorage.interfaces.BigDataStructureContainer;
import com.datumbox.configuration.GeneralConfiguration;
import com.datumbox.configuration.MemoryConfiguration;
import com.datumbox.configuration.StorageConfiguration;
import com.datumbox.framework.machinelearning.common.dataobjects.MLmodelKnowledgeBase;
import com.datumbox.framework.machinelearning.common.dataobjects.TrainableKnowledgeBase;
import java.lang.reflect.InvocationTargetException;

/**
 * Abstract Class for a Machine Learning algorithm.
 * 
 * @author Vasilis Vryniotis <bbriniotis at datumbox.com>
 * @param <MP>
 * @param <TP>
 * @param <VM>
 */
public abstract class BaseMLmodel<MP extends BaseMLmodel.ModelParameters, TP extends BaseMLmodel.TrainingParameters, VM extends BaseMLmodel.ValidationMetrics> extends BaseTrainable<MP, TP, MLmodelKnowledgeBase<MP, TP, VM>> {

    //internal variables
    private final ModelValidation<MP, TP, VM> modelValidator;

    
    /**
     * Parameters/Weights of a trained model: For example in regression you have the weights of the parameters learned.
     */
    public static abstract class ModelParameters implements BigDataStructureContainer {
        
        @Override
        public void bigDataStructureInitializer(BigDataStructureFactory bdsf, MemoryConfiguration memoryConfiguration) {
            
        }
        
        @Override
        public void bigDataStructureCleaner(BigDataStructureFactory bdsf) {
            
        }
            
        //here goes the parameters of the Machine Learning model
    }
    
    /**
     * Training Parameters of an algorithm: For example in regression you have the number of total regressors
     */
    public static abstract class TrainingParameters implements Parameterizable, TrainableKnowledgeBase.SelfConstructible<BaseMLmodel.TrainingParameters> {    

        public TrainingParameters() {
            //here goes initialization of parameters that are private and must be overriden by inherited classes
        }
        
        /**
         * This method allows us to build a new empty object of the current object
         * directly from it. Casting to the appropriate type is required.
         * 
         * @return 
         */
        @Override
        public BaseMLmodel.TrainingParameters getEmptyObject() {
            try {
                return this.getClass().getConstructor().newInstance();
            } 
            catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
                throw new RuntimeException(ex);
            }
        }
        
        //here goes public fields that are used as initial training parameters
    } 

    /**
     * Validation metrics: For example in regression you have the likelihood, the R^2 etc
     */
    public static abstract class ValidationMetrics implements Learnable, TrainableKnowledgeBase.SelfConstructible<BaseMLmodel.ValidationMetrics> {
        
        /**
         * This method allows us to build a new empty object of the current object
         * directly from it. Casting to the appropriate type is required.
         * 
         * @return 
         */
        @Override
        public BaseMLmodel.ValidationMetrics getEmptyObject() {
            try {
                return this.getClass().getConstructor().newInstance();
            } 
            catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
                throw new RuntimeException(ex);
            }
        }
        
        //here goes public fields that are generated by the validation algorithm
    }
    
    
    
    
    
    /**
     * Generates a new instance of a BaseMLmodel by providing the dbName and 
 the Class of the algorithm.
     * 
     * @param <M>
     * @param dbName
     * @param aClass
     * @return 
     */
    public static <M extends BaseMLmodel> M newInstance(Class<M> aClass, String dbName) {
        M algorithm = null;
        try {
            algorithm = (M) aClass.getConstructor(String.class).newInstance(dbName);
        } 
        catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
            throw new RuntimeException(ex);
        }
        
        return algorithm;
    }

    
    
    
    
    
    
    /*
        IMPORTANT METHODS FOR THE FUNCTIONALITY
    */
    protected BaseMLmodel(String dbName, Class<MP> mpClass, Class<TP> tpClass, Class<VM> vmClass, ModelValidation<MP, TP, VM> modelValidator) {
        //the line below calls an overrided method in the constructor. This is not elegant but the only thing that this method does is to rename the short name of classifier
        String methodName = shortMethodName(); //this.getClass().getSimpleName();
        if(!dbName.contains(methodName)) { //patch for the K-fold cross validation which already contains the name of the algorithm in the dbname
            dbName += StorageConfiguration.getDBnameSeparator() + methodName;
        }
        
        this.dbName = dbName;
        knowledgeBase = new MLmodelKnowledgeBase<>(dbName, mpClass, tpClass, vmClass);
        knowledgeBase.setOwnerClass(this.getClass());
        this.modelValidator = modelValidator;
    } 
    
    /**
     * Performs k-fold cross validation on the dataset and returns the ValidationMetrics
     * Object.
     * 
     * @param trainingData
     * @param k
     * @return  
     */
    @SuppressWarnings("unchecked")
    public VM kFoldCrossValidation(Dataset trainingData, int k) {
        if(GeneralConfiguration.DEBUG) {
            System.out.println("kFoldCrossValidation()");
        }
        
        return modelValidator.kFoldCrossValidation(trainingData, k, dbName, this.getClass(), knowledgeBase.getTrainingParameters(), knowledgeBase.getMemoryConfiguration());
    }
     
    
    String Modelname = "";
    public void setModelname(String dataset){
    		Modelname = dataset;
    }
    
    
    /**
     * Trains a model with the trainingData and validates it with the validationData.
     * 
     * @param trainingData
     * @param validationData
     */
    @SuppressWarnings("unchecked")
    public void train(Dataset trainingData, Dataset validationData ) {    
        //Check if training can be performed
        if(!knowledgeBase.isConfigured()) {
            throw new RuntimeException("The training configuration is not set.");
        }
        else if(knowledgeBase.isTrained()) {
            throw new RuntimeException("The algorithm is already trainned. Reinitialize it or erase it.");
        }
        
        if(GeneralConfiguration.DEBUG) {
            System.out.println("train()");
        }
        
        
        if(GeneralConfiguration.DEBUG) {
            System.out.println("estimateModelParameters()");
        }
        
        //train the model to get the parameters
        estimateModelParameters(trainingData);        
        
        
        if(!validationData.isEmpty()) {
        
            if(GeneralConfiguration.DEBUG) {
                System.out.println("validateModel()");
            }

            //validate the model with the validation data and update the validationMetrics
            VM validationMetrics = validateModel(validationData);
            knowledgeBase.setValidationMetrics(validationMetrics);

        }
         
        /*
        //store database if not temporary model
        if(isTemporary()==false) {
            if(GeneralConfiguration.DEBUG) {
                System.out.println("Saving model");
            }
            knowledgeBase.save(true);
        }
        */
        System.out.println("Saving model: "+Modelname);
        knowledgeBase.setModelnameForTrainable(Modelname);
        knowledgeBase.save(true);
        
        knowledgeBase.setTrained(true);
    }
    
    
    
    /**
     * Calculates the predictions for the newData and stores the predictions
     * inside the object.
     * 
     * @param newData 
     */
    public void predict(Dataset newData) { 
        /*
        if(GeneralConfiguration.DEBUG) {
            System.out.println("predict()");
        }*/
        knowledgeBase.setModelname(Modelname);
        knowledgeBase.load();
        
        predictDataset(newData);
        

    }
    
    /**
     * Tests the model against the testingData and returns the validationMetrics;
     * It does not update the validationMetrics.
     * 
     * @param testingData
     * @return 
     */
     public VM test(Dataset testingData) {  
        
        if(GeneralConfiguration.DEBUG) {
            System.out.println("test()");
        }
        
        knowledgeBase.load();

        //validate the model with the testing data and update the validationMetrics
        VM validationMetrics = validateModel(testingData);
        
        return validationMetrics;
    }
    
    
    /**
     * Updates the ValidationMetrics of the algorithm. Usually used to set the
     * metrics after running a test() or when doing K-fold cross validation.
     * 
     * @param validationMetrics 
     */
    public void setValidationMetrics(VM validationMetrics) {
        knowledgeBase.setValidationMetrics(validationMetrics);
        if(isTemporary()==false) {
            if(GeneralConfiguration.DEBUG) {
                System.out.println("Updating model");
            }
            knowledgeBase.save(false);
        }
    }
    
    public VM getValidationMetrics() {
        return knowledgeBase.getValidationMetrics();
    }
    
    /**
     * Returns whether the algorithm or the configuration modifies the data.
     * @return 
     */
    public boolean modifiesData() {
        //check if the algorithm itself modifies the data
        try { 
            Boolean dataSafe = (Boolean) this.getClass().getDeclaredField("DATA_SAFE_CALL_BY_REFERENCE").get(this);
            //see if the data are safe mearning that algorithm does not modify the data internally.
            //if the data are not safe, mark it for deep copy
            if(dataSafe!=true) {
                return true;
            }
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException ex) {
            return true; //if no information available play it safe and mark it as true
        }
        
        return false;
    }
    
    protected abstract VM validateModel(Dataset validationData);
    
    protected abstract void estimateModelParameters(Dataset trainingData);
    
    protected abstract void predictDataset(Dataset newData);


}
