package ambient;
import java.awt.Label;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import org.omg.CORBA.ACTIVITY_COMPLETED;


public class USWRawDataExtra {
	String IFilename = "";
	//String IAccelFilename = "";
	String ITimestampFilename = "";
	String OFilename = "";
	/*Vector<String> Sensor = new Vector<String>();
	Vector<Double> Data = new Vector<Double>();
	Vector<String> Lable = new Vector<String>();
	SparseMatrix<Double> Dimension = new SparseMatrix<Double>(0.0);
	*/
	Vector<Double> NOW = new Vector<Double>(0);
	Vector<Double> PAST = new Vector<Double>(0);
	String NOWLABLE = "";
	String PASTLABLE = "";
	boolean pastActivity = false;
	
	USWRawDataExtra(String iFilename, String oFilename){
		IFilename = iFilename;
		OFilename = oFilename;
	}
	
	public void loadFeature(){
		try {
			FileReader fr = new FileReader(IFilename);
			BufferedReader br = new BufferedReader(fr);
			FileWriter fw;
			for(int i=0; i<40; i++){
				NOW.add(i, 0.0);
				PAST.add(i, 0.0);
			}
			String line = "";
			try {
				fw = new FileWriter(OFilename);
				int l =0;
				boolean label = false;
				while( l<10000 && (line = br.readLine())!=null){
					
					boolean activity = pastActivity;
					
					//System.out.println(line);
					
					String[] tmp = line.split(" ");
					
					if(tmp.length==6){
						if(tmp[5].contains("begin")){
							label = true;
						}
						else if(tmp[5].contains("end")){
							label = false;
						}
					}
					
					if( label && tmp.length>3 ){
						
						
						
						//Sensor.add(tmp[2]);
						String data = tmp[3];
						double value = 0.0;
						if(data.contains("ON")){
							//Data.add(1.0); 
							value = 1.0;
						}
						else if(data.contains("OFF")){ 
							//Data.add(0.0);
							value = 0.0;
						}
						else if(data.contains("OPEN")){
							//Data.add(1.0);
							value = 1.0;
						}
						else if(data.contains("CLOSE")){
							//Data.add(0.0);
							value = 0.0;
						}
						else{
							System.out.println(l+": "+data);
							//Data.add(Double.valueOf(data));
							value = Double.valueOf(data);
						}
						
						
						
						for(int i=0; i<40; i++){
							//Dimension.set(l+1, i, Dimension.get(l, i));
							NOW.set(i,PAST.get(i));
						}
						
						data = tmp[2];
						if(data.equals("LEAVEHOME")){activity = false;}
						else if(data.equals("ENTERHOME")){activity = false;}
						else if(data.contains("M")){
							String[] tmp2 = data.split("M");
							int count = Integer.valueOf(tmp2[tmp2.length-1])-1;
							//Dimension.set(l, count-1, value);
							NOW.set(count, value);
						}
						else if(data.contains("T")){ 
							String[] tmp2 = data.split("T");
							int count = Integer.valueOf(tmp2[tmp2.length-1])+30;
							NOW.set(count, value);
						}
						else if(data.contains("D")){
							String[] tmp2 = data.split("D");
							int count = Integer.valueOf(tmp2[tmp2.length-1])+35;
							//Dimension.set(l, count-1, value);
							NOW.set(count, value);
						}
						for(int i=0; i<40; i++){
							//Dimension.set(l+1, i, Dimension.get(l, i));
							PAST.set(i,NOW.get(i));
						}
						//System.out.println(Dimension.get(l, 0));
						
						if(tmp.length==6){
							//Lable.add(tmp[4]);
							NOWLABLE = tmp[4];
							if(tmp[5].contains("begin"))
								activity = true;
							else 
								activity = false;
							
							
						}
						else{
							//Lable.add(Lable.get(Lable.size()-1));
							NOWLABLE = PASTLABLE;
						}
						PASTLABLE = NOWLABLE;
						//fw.write(line+"\n");
						//System.out.println(Sensor.get(Sensor.size()-1)+", "+Data.get(Data.size()-1)+", "+Lable.get(Lable.size()-1));
						if(activity==true){
							boolean nomotion = true;
							for(int i=0; i<31; i++){
								if(NOW.get(i)!=0.0){
									nomotion = false;
									break;
								}
							}
							if(nomotion)
								activity=false;
						}
						
						if(activity){
							String line2 = "";
							for(int j=0; j<40; j++){
								//double tmp3 = Dimension.get(l, j);
								double tmp3 = NOW.get(j);
								line2 += String.valueOf(tmp3)+",";
								//fw.write(line+"/n");
							}
							fw.write(line2+NOWLABLE+"\n");
							
							//System.out.println(line2+NOWLABLE);
						}
						
						pastActivity = activity;
					}	
					l++;
				}
				br.close();
				fr.close();
				fw.flush();
				fw.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
}
