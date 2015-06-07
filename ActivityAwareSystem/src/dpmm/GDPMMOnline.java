package dpmm;

import java.util.Random;

import com.datumbox.common.dataobjects.Dataset;
import com.datumbox.common.dataobjects.Record;
import com.datumbox.common.utilities.RandomValue;
import com.datumbox.configuration.MemoryConfiguration;
import com.datumbox.framework.machinelearning.clustering.GaussianDPMM;

public class GDPMMOnline {
	GaussianDPMM Model;
	String version;
    String ModelName ="";
    
    public GDPMMOnline(String path, String modelName) {
		version = path;
	    ModelName = modelName;
	    
	    lodaModel();
    }
	
	public int predict(Object[] feature){
		MemoryConfiguration memoryConfiguration = new MemoryConfiguration();
		Dataset Instance = new Dataset();

		Instance.add(Record.newDataVector(feature, "Other"));
		Model.predict(Instance);
		Record r = Instance.get(0);
		
		int predictId = Integer.valueOf(String.valueOf(r.getYPredicted()));
		
		System.out.println(Model.getDBname()+" is predictes as "+predictId);
		
		return predictId;
	}

	public void lodaModel() {
	    RandomValue.randomGenerator = new Random(42); 
	    MemoryConfiguration memoryConfiguration = new MemoryConfiguration();
	                    
	    Model = new GaussianDPMM(ModelName);
	    //Model.setDBname(ModelName);
	    System.out.println("GDPMM Model Name is "+Model.getDBname());
	    Model.setMemoryConfiguration(memoryConfiguration);
	}
}
