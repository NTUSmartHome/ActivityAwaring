package wearable;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import elements.Acceleration;
import elements.Features;
import elements.Label;
import elements.Orientation;

public class SwingMotionFeatureExtration {
	
	private String IFileName;
	private String OFileName;
	private String Path = "";
	
	
	Acceleration A = new Acceleration();
	Orientation O = new Orientation();
	Features F = new Features();
	Label L = new Label();
	
	boolean withGravity = false;
	boolean SM = false;
	
	Vector<String> Timestamp = new Vector<String>();
	
	// Offline
	public SwingMotionFeatureExtration(String filePath, String iFilename, String oFilename, boolean withSM, boolean withG) {
		// TODO Auto-generated constructor stub
		Path = filePath;
		IFileName = iFilename;
		OFileName = oFilename;
		SM = withSM;
		withGravity = withG;
		F.setOrienThreashold(360);
		
		readRawData();
	}
	
	//Online
	public SwingMotionFeatureExtration(boolean withSM, boolean withG) {
		// TODO Auto-generated constructor stub
		SM = withSM;
		withGravity = withG;
		F.setOrienThreashold(360);
		
	}
	
	public String readRawDataOnline(String[] raw){
		String feature = "";
		//7 parameters are: Label, Gx, Gy, Gz, Y, P, R
		for(int i=0; i<7; i++){
			readLine(raw[0]);
		}
		
		F.initialize();
		getAccelFeatures(SM);
		getOrientationFeatures();
		
		for(int i=0; i<2; i++) feature += F.getOrientation(i)+",";

		for(int i=0; i<3; i++) feature += F.getDeltaAngleMean(i)+",";

		for(int i=0; i<3; i++)	feature += shift(round(F.getAccelMean(i)))+",";
		for(int i=0; i<3; i++)	feature += round(F.getAccelVar(i))+",";
		feature += round(F.getAccelSVM())+",";
		
		feature += L.getACT();
		feature += "\n";
		
		cleanVariable();
		L.clearACT();
		
		return feature;
	}
	
	
	private void getAccelSVM(){
		int len = A.size();
		double svm=0;
		for(int i=0; i<len; i++){
			double tmpSvm = 0;
			for(int j=0; j<3; j++)
				tmpSvm += Math.pow(A.get(j, i), 2);
			svm += Math.sqrt(tmpSvm);
		}
		F.setAccelSVM( svm/len );
	}
	
	private void getAccelMean(){
		int len = A.size();
		for(int i=0; i<3; i++){
			double mean = 0;
			for(int j=0; j<len; j++) mean += A.get(i,j);
			F.setAccelMean(i, mean/len);
		}
	}
	
	private void getAccelVar(){
		int len = A.size();
		for(int i=0; i<3; i++){
			double var = 0;
			for(int j=0; j<len; j++) 
				var += Math.pow(F.getAccelMean(i) - A.get(i,j),2);
			F.setAccelVar( i, Math.sqrt(var/len) );
		}
	}
	
	
	//Get Pitch & Roll Orientation for mean of abs angle
	private void getOrienMean(){
		for(int i=0; i<2; i++){
			F.setOrientation(i, includedAngle(i));
		}
		
	}
	//Get Pitch & Roll Orientation for variance of abs angle
	private void getOrienVar(){
		int len = O.size()-1;
		for(int i=0; i<2; i++){
			double var = 0;
			for(int j=0; j<len; j++)
				var +=  Math.pow(F.getOrientation(i) - O.get(i, j),2);
			F.setOrientationVar(i, Math.sqrt(var/len) );
		}
		
	}
	
	//Get Pitch & Roll Orientation variance of delta angle
	private void getDeltaOrienMean(){
		int len = O.size()-1;
		for(int i=0; i<3; i++){
			double mean = 0;
			for(int j=0; j<len; j++)
				mean += deltaAngle(i, j);
			F.setDeltaAngleMean(i, mean / len );
		}
	}
	
	//Get Pitch & Roll Orientation mean of delta angle
	private void getDeltaOrienVar(){
		int len = O.size()-1;
		for(int i=0; i<3; i++){
			double var = 0;
			for(int j=0; j<len; j++)
				var +=  Math.pow(F.getDeltaAngleMean(i) - deltaAngle(i, j),2);
			F.setDeltaAngleVar(i, Math.sqrt(var/len) );
		}
	}	

	private double includedAngle(int orien){ 
		double Angle = absAngle(O.get(orien, 0));
		//if(Angle>180) Angle -= 360;
		for(int i=0; i<2; i++)
			for(int j=1; j<O.size(); j++){
				Angle += deltaAngle(i, 0, j)/O.size();
			}
		return absAngle(Angle);	
	}
	
	private double deltaAngle(int orien, int i, int j){	
		double preAngle = absAngle(O.get(orien, i));
		double Angle = absAngle(O.get(orien, j));
		if(preAngle>=0 && preAngle<90){
			if(Angle>270 && Angle<=360){
				Angle -= 360;
			}
		}
		if(Angle>=0 && Angle<90){
			if(preAngle>270 && preAngle<=360){
				Angle += 360;
			}
		}
		return Angle - preAngle; 
	}
	
	private double absAngle(double angle){ 
		double modAngle = angle%360;
		if(modAngle>=0) return modAngle;
		return modAngle+360;	
	}

	private double deltaAngle(int orien, int i){	
		double preAngle = absAngle(O.get(orien, i));
		double Angle = absAngle(O.get(orien, i+1));
		if(preAngle>=0 && preAngle<90){
			if(Angle>270 && Angle<=360){
				Angle -= 360;
			}
		}
		if(Angle>=0 && Angle<90){
			if(preAngle>270 && preAngle<=360){
				Angle += 360;
			}
		}
		return Angle - preAngle; 
	}
	
	public void readRawData(){
		try {
			FileReader fr = new FileReader(IFileName);
			BufferedReader br = new BufferedReader(fr);
			String line = "";
			try {
				while((line = br.readLine())!=null){
					
					if(line.contains("T")){
						
						line = br.readLine();
						if(!line.contains("L"))
							for(int i=0; i<9; i++) line = br.readLine();
						else{
							readLine(line);
							for(int i=0; i<9; i++){ 
								readLine(br.readLine());
							}
						}
					}
				}
				br.close();
				fr.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void getAccelFeatures(boolean withSVM){
		if(SM)	getAccelSVM();
		getAccelMean();
		getAccelVar();
	}
	private void getOrientationFeatures(){
		getDeltaOrienMean();
		getDeltaOrienVar();
		getOrienMean();
		getOrienVar();

	}
	private double shift(double value){
		return value + 10;
	}
	private double round(double value){
		double result = (double) (Math.round(value*100000)/100000.0);
		if(result>0.001 || result<-0.001)
			return result;
		return 0;
	}

	public void printFeature(){
		try {
			new File(Path).mkdirs();
			new File(Path+"/Features").mkdirs();
			FileWriter fw = new FileWriter(Path+"/Features/"+OFileName,true);
			F.initialize();
			getAccelFeatures(SM);
			getOrientationFeatures();
			
			for(int i=0; i<2; i++) fw.write(F.getOrientation(i)+",");
			
			//for(int i=0; i<2; i++) fw.write(F.getOrientationVar(i)+",");
			
			for(int i=0; i<3; i++) fw.write(F.getDeltaAngleMean(i)+",");
			//for(int i=0; i<3; i++) fw.write(F.getDeltaAngleVar(i)+",");
			
			//for(int i=0; i<3; i++)	fw.write(shift(round(F.getAccelMean(i)))+","+round(F.getAccelVar(i))+",");
			for(int i=0; i<3; i++)	fw.write(shift(round(F.getAccelMean(i)))+",");
			for(int i=0; i<3; i++)	fw.write(round(F.getAccelVar(i))+",");
			fw.write(round(F.getAccelSVM())+",");
			
			fw.write(L.getACT());
			fw.write("\n");
			
			fw.flush();
			fw.close();
						
			cleanVariable();
			L.clearACT();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void readLine(String line){
		String[] str = line.split(":");
		//System.out.println(line);
		if(str[0].contains("L")){
			L.setACT(str[1]);
		}else if(str[0].contains("T")){
			
		}else if(str[0].contains("A") && !withGravity){
			String[] tmp = str[1].split(";");
			String[] value = tmp[0].split(",");
			int len = value.length;
			for(int i=0; i<len; i++){
				if(str[0].contains("x")){
					A.addX(Double.valueOf(value[i]));
					
				}else if(str[0].contains("y")){
					A.addY(Double.valueOf(value[i]));
					System.out.println(A.getX(i));
				}else if(str[0].contains("z")){
					A.addZ(Double.valueOf(value[i]));
				}
			}
		}else if(str[0].contains("G") && withGravity){
			String[] tmp = str[1].split(";");
			String[] value = tmp[0].split(",");
			int len = value.length;
			for(int i=0; i<len; i++){
				if(str[0].contains("x")){
					A.addX(Double.valueOf(value[i]));
				}else if(str[0].contains("y")){
					A.addY(Double.valueOf(value[i]));
				}else if(str[0].contains("z")){
					A.addZ(Double.valueOf(value[i]));
				}
			}
		}else if(str[0].contains("Y")){
			String[] tmp = str[1].split(";");
			String[] value = tmp[0].split(",");
			int len = value.length;
			for(int i=0; i<len; i++){
				O.addA(Double.valueOf(value[i]));
			}
		}else if(str[0].contains("P")){
			String[] tmp = str[1].split(";");
			String[] value = tmp[0].split(",");
			int len = value.length;
			for(int i=0; i<len; i++){
				O.addP(Double.valueOf(value[i]));
			}
		}else if(str[0].contains("R")){
			String[] tmp = str[1].split(";");
			String[] value = tmp[0].split(",");
			int len = value.length;
			for(int i=0; i<len; i++){
				O.addR(Double.valueOf(value[i]));
			}
			
			printFeature();
			
		}
	}
	
	private void cleanVariable(){
		// Accelerometer Cleaning
		A.removeAllElements();

		//Pitch & Roll Cleaning
		O.removeAllElements();
	}
	

	
}