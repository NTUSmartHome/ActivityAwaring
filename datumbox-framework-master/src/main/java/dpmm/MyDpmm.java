package dpmm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import com.datumbox.framework.machinelearning.clustering.GaussianDPMM;
public class MyDpmm {

	List<Point> pointList = new ArrayList<>();
	//add records in pointList
	 
	//Dirichlet Process parameter
	Integer dimensionality = 2;
	double alpha = 1.0;
	 
	//Hyper parameters of Base Function
	int kappa0 = 0;
	int nu0 = 1;
	RealVector mu0 = new ArrayRealVector(new double[]{0.0, 0.0});
	RealMatrix psi0 = new BlockRealMatrix(new double[][]{{1.0,0.0},{0.0,1.0}});
	 
	//Create a DPMM object
	//BaseDPMM dpmm2 = new BaseDPMM("dbName");
	GaussianDPMM dpmm1; // = new GaussianDPMM("dbName");
	
	//GaussianDPMM dpmm = new GaussianDPMM(dimensionality, alpha, kappa0, nu0, mu0, psi0);
	 
	int maxIterations = 100;
	
	//dpmm1.
	
	//int performedIterations = dpmm.//.Clusters();//.Cluster(pointList, maxIterations);
	//int performedIterations = dpmm.cluster(pointList, maxIterations);
	 
	//get a list with the point ids and their assignments
	//Map<Integer, Integer> zi = dpmm.getPointAssignments();
	
	
}
