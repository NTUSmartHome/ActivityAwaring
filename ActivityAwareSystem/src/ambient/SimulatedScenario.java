package ambient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Vector;

import elements.AmbientFeature;
import elements.Features;
import elements.Label;

public class SimulatedScenario {
	Vector<String> LABEL = new Vector<String>();
	Vector<Vector<Integer>> Instances = new Vector<Vector<Integer>>();
	Vector<String> GroundTruth = new Vector<String>();
	AmbientFeature F = new AmbientFeature();
	
	private String Path;
	private String IFilename;
	
	private String Kitchen = "Kitchen";
	private String Livingroom = "Livingroom";
	private String Studyingroom = "Studyingroom";
	private String Bedroom = "Bedroom";
	private String Movable = "Movable";
	private String Pad = "Pad";
	private String TV = "TV";
	private String Light = "Light";
	private String Lamp = "Lamp";
	private String Fan = "Fan";
	private String off = "off";
	private String on = "on";
	
	public SimulatedScenario(String path, String iFilename) {
		// TODO Auto-generated constructor stub
		LABEL.add("Exercise");
		LABEL.add("Sweep");
		LABEL.add("Walk");
		LABEL.add("Meal");
		LABEL.add("WashDishes");
		LABEL.add("PlayPad");
		LABEL.add("WatchTV");
		LABEL.add("Read");
		LABEL.add("Sleep");
		LABEL.add("Other");
		
		Path = path;
		IFilename = iFilename;
		
		loadExpResult();
		printAmbientFeature();
	}
	
	
	
	public void loadExpResult(){
		try {
			FileReader fr = new FileReader(Path+"/DPMM/"+IFilename);
			BufferedReader br = new BufferedReader(fr);
			String line = "";
			try {
				while((line = br.readLine())!=null){
					String[] tmp = line.split("\t");
					F = new AmbientFeature();
					setScenario(tmp[0]);
					generateInstance();
					
					GroundTruth.add(tmp[0]);
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
	
	public void printAmbientFeature(){
		FileWriter fw;
		try {
			new File(Path).mkdirs();
			new File(Path+"/Features").mkdirs();
			fw = new FileWriter(Path+"/Features/"+"AmbientFeature.txt");
			for(int i=0; i<Instances.size(); i++){
				for(int j=0; j<Instances.get(i).size(); j++){
					fw.write(Instances.get(i).get(j)+",");
				}
				fw.write(GroundTruth.get(i)+"\n");
			}
			fw.flush();
			fw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	private void generateInstance(){
		Vector<Integer> instance = new Vector<Integer>();
		for(int i=0; i<F.getRoomSize();  i++){
			instance.add(F.getPeople(i));
		}
		for(int i=0; i<F.getApplianceSize();  i++){
			instance.add(F.getStatus(i));
		}
		Instances.add(instance);
		//F.init();
	}
	int count = 0;
	
	private void setScenario(String label){
		Random random = new Random();
		double r = (double)(random.nextInt(1000))/1000;
		//System.out.println(r);
		switch (label) {
		case "Exercise":
			if(r<0.97){
				F.set(Livingroom, 1);
				F.set(Livingroom, Light, true);
			}
			else{
				F.set(Studyingroom, 1);
				F.set(Livingroom, Light, true);
			}
			break;
		case "Sweep":
			if(r<0.94){
				F.set(Studyingroom, 1);
				F.set(Studyingroom, Light, true);
			}
			else if(r<0.98){
				F.set(Livingroom, 1);
				F.set(Studyingroom, Light, true);
			}
			else {
				F.set(Kitchen, 1);
				F.set(Studyingroom, Light, true);
			}
			break;
		case "Walk":
			break;
		case "Meal":
			if(r<0.96){
				F.set(Livingroom, 1);
				F.set(Livingroom, Light, true);
			}
			else{
				F.set(Kitchen, 1);
				F.set(Kitchen, Light, true);
			}
			break;
		case "PlayPad":
			if(r<0.98){
				F.set(Studyingroom, 1);
				F.set(Studyingroom, Light, true);
				F.set(Movable, Pad, true);
			}
			else{
				F.set(Studyingroom, 1);
				F.set(Studyingroom, Light, true);
				//F.set(Movable, Pad, true);
			}
			break;
		case "WatchTV":
			if(r<0.98){
				F.set(Livingroom, 1);
				F.set(Livingroom, Light, true);
				F.set(Livingroom, TV, true);
			}
			else if(r<0.993){
				F.set(Livingroom, 1);
				F.set(Livingroom, Light, true);
			}
			else{
				F.set(Studyingroom, 1);
				F.set(Livingroom, Light, true);
			}
			break;
		case "Read":
			count++;
			if(count<75){
				F.set(Livingroom, 1);
				F.set(Livingroom, Light, true);
				F.set(Livingroom, Lamp, true);
			}
			else{
				F.set(Studyingroom, 1);
				F.set(Studyingroom, Light, true);
			}
			break;
		case "Sleep":
			if(r<0.97){
				F.set(Bedroom, 1);
				F.set(Livingroom, Light, true);
			}
			else{
			}
			break;
		case "WashDishes":
			if(r<0.97){
				F.set(Kitchen, 1);
				F.set(Kitchen, Light, true);
			}
			else{
				//F.set(Kitchen, 1);
				F.set(Kitchen, Light, true);
			}
			break;
		case "Other":
			
			if(r<0.1){
				F.set(Livingroom, Light, true);
			}
			else if(r<0.2){
				F.set(Kitchen, Light, true);
			}
			else if(r<0.3){
			}
			else if(r<0.4){
				F.set(Studyingroom, Light, true);
			}
			else if(r<0.5){
				F.set(Livingroom, Light, true);
				F.set(Livingroom, TV, true);
			}else if(r<0.6){
				F.set(Studyingroom, Light, true);
				F.set(Movable, Pad, true);
			}
			else if(r<0.7){
				F.set(Livingroom, Light, true);
			}
			else if(r<0.8){
				F.set(Studyingroom, Light, true);
			}
			else if(r<0.9){
				F.set(Livingroom, Light, true);
			}
			break;
		default:
			break;
		}
		
	}
	
	
}
