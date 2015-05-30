package elements;

import java.util.Vector;

public class Features {
	private double Accel_x_mean;
	private double Accel_y_mean;
	private double Accel_z_mean;
	private double Accel_x_var;
	private double Accel_y_var;
	private double Accel_z_var;
	private double SignalVectorMagnitude;
	private double Orientation_Pitch_mean;
	private double Orientation_Roll_mean;
	private double Orientation_Pitch_var;
	private double Orientation_Roll_var;
	private double DeltaAngle_Pitch_mean;
	private double DeltaAngle_Roll_mean;
	private double DeltaAngle_Azimuth_mean;
	private double DeltaAngle_Pitch_var;
	private double DeltaAngle_Roll_var;
	private double DeltaAngle_Azimuth_var;
	private double Orientation_Threashold = 1;
	
	private AmbientFeature Ambient = new AmbientFeature();
	
	public Features(){
		initialize();
	}
	
		
	public void initialize(){
		initializeAccel();
		initializeOrientation();
	}
	public void initializeAccel(){
		Accel_x_mean = 0;
		Accel_y_mean = 0;
		Accel_z_mean = 0;
		
		Accel_x_var = 0;
		Accel_y_var = 0;
		Accel_z_var = 0;
		
		SignalVectorMagnitude = 0;
	}
	
	public void initializeOrientation(){
		Orientation_Pitch_mean = 0;
		Orientation_Roll_mean = 0;
		
		DeltaAngle_Pitch_mean = 0;
		DeltaAngle_Roll_mean = 0;
		DeltaAngle_Azimuth_mean = 0;
		
		DeltaAngle_Pitch_var = 0;
		DeltaAngle_Roll_var = 0;
		DeltaAngle_Azimuth_var = 0;
		//Orientation_Threashold = 1;
	}
	
	public boolean pitchLowVar(){
		if(Math.abs(DeltaAngle_Pitch_var)<Orientation_Threashold)
			return true;
		return false;
	}
	public boolean rollLowVar(){
		if(Math.abs(DeltaAngle_Roll_var)<Orientation_Threashold)
			return true;
		return false;
	}
	public boolean azimuthLowVar(){
		if(Math.abs(DeltaAngle_Azimuth_var)<Orientation_Threashold)
			return true;
		return false;
	}

	
	public double getAccelMean(int axis){
		switch(axis){
		case 0:
			return Accel_x_mean;
		case 1:
			return Accel_y_mean;
		case 2:
			return Accel_z_mean;
		}
		return 0; 
	}
	public double getAccelVar(int axis){
		switch(axis){
		case 0:
			return Accel_x_var;
		case 1:
			return Accel_y_var;
		case 2:
			return Accel_z_var;
		}
		return 0; 
	}
	
	public double getAccelSVM(){
		return SignalVectorMagnitude; 
	}
	
	public double getOrientation(int orien){
		switch(orien){
		case 0:
			return Orientation_Pitch_mean;
		case 1:
			return Orientation_Roll_mean;
		}
		return 0; 
	}
	public double getOrientationVar(int orien){
		switch(orien){
		case 0:
			return Orientation_Pitch_var;
		case 1:
			return Orientation_Roll_var;
		}
		return 0; 
	}
	
	public double getDeltaAngleMean(int orien){
		switch(orien){
		case 0:
			return DeltaAngle_Pitch_mean;
		case 1:
			return DeltaAngle_Roll_mean;
		case 2:
			return DeltaAngle_Azimuth_mean;
		}
		return 0; 
	}
	
	public double getDeltaAngleVar(int orien){
		switch(orien){
		case 0:
			return DeltaAngle_Pitch_var;
		case 1:
			return DeltaAngle_Roll_var;
		case 2:
			return DeltaAngle_Azimuth_var;
		}
		return 0; 
	}
	
	
	public void setAccelMean(int axis, double value){
		switch(axis){
		case 0:
			Accel_x_mean = value;
		case 1:
			Accel_y_mean = value;
		case 2:
			Accel_z_mean = value;
		} 
	}
	public void setAccelVar(int axis, double value){
		switch(axis){
		case 0:
			Accel_x_var = value;
		case 1:
			Accel_y_var = value;
		case 2:
			Accel_z_var = value;
		} 
	}
	
	public void setAccelSVM(double value){
		SignalVectorMagnitude = value; 
	}
	
	public void setOrientation(int orien, double value){
		switch(orien){
		case 0:
			Orientation_Pitch_mean = value;
		case 1:
			Orientation_Roll_mean = value;
		}
	}
	
	public void setOrientationVar(int orien, double value){
		switch(orien){
		case 0:
			Orientation_Pitch_var = value;
		case 1:
			Orientation_Roll_var = value;
		}
	}
	
	public void setDeltaAngleMean(int orien, double value){
		switch(orien){
		case 0:
			DeltaAngle_Pitch_mean = value;
		case 1:
			DeltaAngle_Roll_mean = value;
		case 2:
			DeltaAngle_Azimuth_mean = value;
		}
	}
	
	public void setDeltaAngleVar(int orien, double value){
		switch(orien){
		case 0:
			DeltaAngle_Pitch_var = value;
		case 1:
			DeltaAngle_Roll_var = value;
		case 2:
			DeltaAngle_Azimuth_var = value;
		}
	}
	
	public void setOrienThreashold(double threashold){
		Orientation_Threashold = threashold;
	}
	public double getOrienThreashold(){
		return Orientation_Threashold;
	}
}
