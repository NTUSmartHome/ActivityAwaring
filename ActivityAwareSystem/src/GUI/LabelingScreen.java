package GUI;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.Vector;

import javax.swing.*;

public class LabelingScreen {
	String Path;
	String iFile;
	Vector<Vector<Integer>> colorVectors = new Vector<Vector<Integer>>();
	int numOfClu = 0;
	int numOfColor = 0;
	int numOfAllInstance = 0;
	int numOfPartInstance = 0;
	double threadshold = 0.01;
	Vector<Integer> partInstance = new Vector<Integer>();
	Vector<Integer> partLength = new Vector<Integer>();
	Vector<Integer> Instance = new Vector<Integer>();
	Vector<Integer> TotalLength = new Vector<Integer>();
	Vector<Double> InstanceRatio = new Vector<Double>();
	public LabelingScreen(String path, String filename) {
		Path = path;
		iFile = filename;
		loadResult();
		generateInstance();
		
		int width = 0;
	    int height = 100;
		int totalWidth = 1200;

		JFrame f=new JFrame("JLabel1");
	    f.setSize(totalWidth+80,height+250);
	    f.setLocationRelativeTo(null);
	    f.setVisible(true);
	    f.getContentPane().setLayout(null);
	    
	    JLabel[] labels = new JLabel[numOfPartInstance];
	    int locationX = 40;
	    for(int i=0; i<numOfPartInstance; i++){
	    	int cluId = partInstance.get(i);
	    	labels[i] = new JLabel(String.valueOf(cluId));
	    	
	    	width = (int)((partLength.get(i)*totalWidth)/numOfAllInstance);
	    	//width += 5;
	    	System.out.println("\tWidth is "+ width);
	    	
    		labels[i].setBounds(locationX, 5, width, height);

	    	locationX += width;
	    	Vector<Integer> getcolor = getColor(cluId);
	    	int r = getcolor.get(0);
	    	int g = getcolor.get(1);
	    	int b = getcolor.get(2);
	    	Color color = new Color(r,g,b);
	    	labels[i].setBackground(color);
	    	
			System.out.print(cluId+" in this part of length:"+width+" and its rgb is "+r+","+g+","+b+"\n");

	    	System.out.println("\tLoacted at "+ locationX);
	    	labels[i].setOpaque(true);
	    	
	    }
	    
	    for(int i=0; i<numOfPartInstance; i++){
	    	f.getContentPane().add(labels[i]);
	    }
	    
	    f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
	
	private Vector<Integer> getColor (int id){
		Vector<Integer> color = new Vector<Integer>();
		for(int i=0; i<3; i++){
			color.add(255);
		}
		for(int i=0; i<colorVectors.size(); i++){
			if(id == colorVectors.get(i).get(3)){
				for (int j = 0; j < 3; j++) {
					color.set(j, colorVectors.get(i).get(j));
				}
			}
		}
		return color;
	}
	
	
	public void generateAllColor(){
		for(int i=0; i<numOfColor; i++){
			colorVectors.add(generateColor());
		}
		
	}
	
	int count = 0;
	int id = 0;
	public Vector<Integer> generateColor(){
		Vector<Integer> color = new Vector<Integer>();
		Random random = new Random();
		for(int i=0; i<3; i++){
			color.add(random.nextInt(200)+50);
		}
		if(compareSimilar(color)){
			color = generateColor();
		}
		return color;
	}
	private boolean compareSimilar(Vector<Integer> color){
		int minDis = 800;
		int averageDis = (int)(200/numOfColor);
		int averageDis2 = (int)(200/(numOfColor*2/3));
		for(int i=0; i<colorVectors.size(); i++){
			int tmpDis = 0; 
			int[] tmpAxisDis = new int[3];
			for(int j=0; j<3; j++){
				tmpAxisDis[j] =Math.abs(colorVectors.get(i).get(j)-color.get(j)); 
				tmpDis += tmpAxisDis[j];
			}
			if(tmpAxisDis[2]<averageDis && tmpAxisDis[2]<averageDis && tmpAxisDis[0]<averageDis){
				return true;
			}
			else if(tmpAxisDis[0]<averageDis2 && tmpAxisDis[1]<averageDis2){
				return true;
			}
			else if(tmpAxisDis[2]<averageDis2 && tmpAxisDis[1]<averageDis2){
				return true;
			}
			else if(tmpAxisDis[2]<averageDis2 && tmpAxisDis[0]<averageDis2){
				return true;
			}
			/*
			if(tmpDis<minDis){
				minDis = tmpDis;
			}*/
		}
		
		/*
		if(minDis<averageDis){
			return true;
		}*/
		
		return false;
	}	

	private void generateInstance(){
		numOfPartInstance = partInstance.size();
		for(int i=0; i<partInstance.size(); i++){
			boolean seen = false;
			for(int j=0; j<Instance.size(); j++){
				if(partInstance.get(i)==Instance.get(j)){
					seen = true;
					int len = TotalLength.get(j) + partLength.get(i);
					TotalLength.set(j, len);
				}
			}
			if(!seen){
				Instance.add(partInstance.get(i));
				TotalLength.add(partLength.get(i));
			}
			numOfAllInstance += partLength.get(i);
		}
		numOfClu = Instance.size();
		for(int i=0; i<Instance.size(); i++){
			InstanceRatio.add((double)TotalLength.get(i)/numOfAllInstance);
			System.out.println(Instance.get(i)+"'s ratio is "+InstanceRatio.get(i));
		}
		for(int i=0; i<Instance.size(); i++){
			if(InstanceRatio.get(i)>threadshold){
				numOfColor++;
			}
		}
		System.out.println("Number of Color is "+(numOfColor+1));
		generateAllColor();
		
		for(int i=0,j=0; i<Instance.size(); i++){
			if(InstanceRatio.get(i)>threadshold){
				colorVectors.get(j).add(Instance.get(i));
				j++;
			}
			
		}
	}
	
	public void loadResult(){
		new File(Path+"/DPMM").mkdirs();
		FileReader fr;
		try {
			fr = new FileReader(Path+"/DPMM/"+iFile);
			BufferedReader br = new BufferedReader(fr);
			String line = "";
			int preClu = -1;
			while((line = br.readLine())!=null){
				String[] instanceStr = line.split("	");
				int clu = Integer.valueOf(instanceStr[1]);
				if(clu!=preClu){
					partInstance.add(clu);
					partLength.add(1);
				}
				else{
					int index = partLength.size()-1;
					int len = partLength.get(index) + 1;
					partLength.set(index,len);
				}
				preClu = clu;
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
