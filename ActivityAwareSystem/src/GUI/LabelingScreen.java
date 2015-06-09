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

import com.datumbox.common.dataobjects.Dataset;

public class LabelingScreen {
	String Path;
	String iFile;
	Vector<Vector<Integer>> colorVectors = new Vector<Vector<Integer>>();
	int numOfClu = 0;
	int numOfColor = 0;
	int numOfAllInstance = 0;
	int numOfPartInstance = 0;
	double threadshold = 0.03;
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
		
		int width = 50;
	    int height = 50;
		int totalWidth = 0;
		totalWidth = 550;

		JFrame f=new JFrame("JLabel1");
	    f.setSize(totalWidth+10,height+50);
	    f.setLocationRelativeTo(null);
	    f.setVisible(true);
	    
	   // generateAllColor();
	    
	    JLabel[] labels = new JLabel[numOfPartInstance];
	    int locationX = 0;
	    for(int i=0; i<numOfPartInstance; i++){
	    	labels[i] = new JLabel(String.valueOf(i));
	    	//labels[i] = new JLabel(String.valueOf(partInstance.get(i)));
	    	width = (int)((partLength.get(i)*500)/numOfAllInstance);
	    	locationX += width;
	    	labels[i].setBounds(locationX, 0, width, height);
	    	
	    	int cluId = partInstance.get(i);
	    	Vector<Integer> getcolor = getColor(cluId);
	    	int r = getcolor.get(0);
	    	int g = getcolor.get(1);
	    	int b = getcolor.get(2);
	    	Color color = new Color(r,g,b);
	    	labels[i].setBackground(color);
	    	
			System.out.print(i+": "+r+","+g+","+b+"\n");
	    	
	    	labels[i].setOpaque(true);
	    	
	    }
	    f.getContentPane().setLayout(null);
	    for(int i=0; i<numOfPartInstance; i++){
	    	f.getContentPane().add(labels[i]);
	    }
	    
	    f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
	
	private Vector<Integer> getColor (int id){
		Vector<Integer> color = new Vector<Integer>();
		for(int i=0; i<3; i++){
			color.add(100);
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
		//System.out.println(id+":");
		//id ++;
		Vector<Integer> color = new Vector<Integer>();
		Random random = new Random();
		for(int i=0; i<3; i++){
			
			color.add(random.nextInt(220)+20);
		}
		
		if(compareSimilar(color)){
			color = generateColor();
		}
		
		
		return color;
		
	}
	private boolean compareSimilar(Vector<Integer> color){
		int minDis = 800;
		int averageDis = (int)(660/numOfColor);
		for(int i=0; i<colorVectors.size(); i++){
			int tmpDis = 0; 
			int[] tmpAxisDis = new int[3];
			for(int j=0; j<3; j++){
				tmpAxisDis[j] =Math.abs(colorVectors.get(i).get(j)-color.get(j)); 
				tmpDis += tmpAxisDis[j];
			}
			if(tmpDis<minDis){
				minDis = tmpDis;
			}
		}
		boolean similar = false;
		
		if(minDis<averageDis){
			similar = true;
		}
		
		return similar;
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
	
	
}
