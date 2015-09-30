package dpmm;

import java.util.Random;
import java.util.Vector;

import com.datumbox.common.dataobjects.Dataset;
import com.datumbox.common.dataobjects.Record;
import com.datumbox.common.utilities.RandomValue;
import com.datumbox.configuration.MemoryConfiguration;
import com.datumbox.framework.machinelearning.clustering.GaussianDPMM;
import com.datumbox.framework.machinelearning.clustering.MultinomialDPMM;

public class MDPMMOnline {
    MultinomialDPMM Model;
    String version;
    String ModelName = "";

    public MDPMMOnline(String path, String modelName) {
        version = path;
        ModelName = path + "/" + modelName;
        lodaModel();
    }

    public int predict(Vector<Double> feature) {
        MemoryConfiguration memoryConfiguration = new MemoryConfiguration();
        Dataset Instance = new Dataset();

        Object[] featureObjects = new Object[feature.size()];
        for (int i = 0; i < feature.size(); i++) {
            featureObjects[i] = feature.get(i);
        }
        Instance.add(Record.newDataVector(featureObjects, "Other"));

        Model.predict(Instance);
        Record r = Instance.get(0);

        int predictId = Integer.valueOf(String.valueOf(r.getYPredicted()));

        //System.out.println("It's predictes as " + predictId);

        return predictId;
    }

    public int predict(Object[] feature) {
        MemoryConfiguration memoryConfiguration = new MemoryConfiguration();
        Dataset Instance = new Dataset();

        Instance.add(Record.newDataVector(feature, "Other"));

        Model.setModelname(ModelName);

        Model.predict(Instance);
        Record r = Instance.get(0);

        int predictId = Integer.valueOf(String.valueOf(r.getYPredicted()));

        //System.out.println(Model.getDBname() + " is predictes as " + predictId);

        return predictId;
    }

    public int getC() {
        return Model.getModelParameters().getC();
    }

    public int getF() {
        return Model.getModelParameters().getD();
    }

    public int getN() {
        return Model.getModelParameters().getN();
    }

    public void lodaModel() {
        MemoryConfiguration memoryConfiguration = new MemoryConfiguration();

        //System.out.println("(load) Model name: "+ModelName);

        Model = new MultinomialDPMM(ModelName);
        //Model.setDBname(ModelName);
        String nameString = Model.getDBname();


        Model.setMemoryConfiguration(memoryConfiguration);


    }
}
