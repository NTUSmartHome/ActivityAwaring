package elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FileFormat {
	String Rawdata;
	String Result;
	String Report;
	String Feature;
	String version;
	String PCA;
	String Name;
	public FileFormat(String V){
		version = V;
	}
	public FileFormat(String V, String name){
		version = V;
		Name = name;
		setAll();
	}
	
	public String Path(){return version;}
	public void setAll(){
		Feature = Name + "Feature.txt";
		Report = Name + "Report.txt";
		Result = Name + "Result";
		PCA = "PCA_" + Name + ".txt";
	}
	
	public void setRawdata(String name){Rawdata = name;	}
	public void setFeature(String name){Feature = name;	}
	public void setReport(String name){ Report  = name;	}
	public void setPCA(String name){PCA = name;}
	public void setResult(String name){Result = name;}
	
	public String getRawdata(){return Rawdata;	}
	public String getFeature(){return Feature;	}
	public String getReport(){ return Report;	}
	public String getPCA(){ return PCA;	}
	public String getPCAFeature(){ return "PCA"+Feature;	}
	public String getResult(){return Result;}
	
	
	public void deletFile(String filename){
		File file = new File(version+"/"+filename);
		if(file.exists()) file.delete();
	}
	
	
	public void copyToDatumbox(String filename){
		try {
			int bytesum = 0;
			int byteread = 0;
			String oldPath = version+"/"+filename;
			String newPath = "/Users/rainbowbow/Documents/workspace/datumbox-framework-master/"+filename;
			
			
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { 
				InputStream inStream = new FileInputStream(oldPath);
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				while ( (byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; //位元組數 檔案大小
					System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
			}
		}
		catch(Exception e) {
			e.printStackTrace(); 
		}
		
	}
	
}
