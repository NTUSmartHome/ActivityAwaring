package GUI;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
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
	double threshold = 0.03;
	Vector<Integer> partInstance = new Vector<Integer>();
	Vector<Integer> partLength = new Vector<Integer>();
	Vector<Integer> Instance = new Vector<Integer>();
	Vector<Integer> TotalLength = new Vector<Integer>();
	Vector<Double> InstanceRatio = new Vector<Double>();
	
	Vector<Vector<String>> eachInstanceFeature = new Vector<Vector<String>>();
	Vector<Integer> eachInstanceClu = new Vector<Integer>();
	
	Choice[] labelList;
	String[] labelStr;
	
	public LabelingScreen(String path, String filename, double dropThreshold) {
		Path = path;
		iFile = filename;
		threshold = dropThreshold;
		loadResult();
		generateInstance();
		
		int width = 0;
	    int height = 100;
		int totalWidth = 1200;
		int widthShift = 40;
		int locationY =  5;
		
		// Build Frame Window
		JFrame f=new JFrame("JLabel1");
	    f.setSize(totalWidth+80,height+250);
	    f.setLocationRelativeTo(null);
	    f.getContentPane().setLayout(null);
	    int locationX = widthShift;
	    
	    locationX = widthShift;
	    JLabel[] labels = new JLabel[numOfPartInstance];	    
	    for(int i=0; i<numOfPartInstance; i++){
	    	labels[i] = new JLabel("");
	    	
	    	width = (int)((partLength.get(i)*totalWidth)/numOfAllInstance);
    		labels[i].setBounds(locationX, locationY, width, height);

	    	Color color = getColor(partInstance.get(i));
	    	labels[i].setBackground(color);
	    	labels[i].setOpaque(true);
	    	
	    	f.getContentPane().add(labels[i]);
	    	
	    	locationX += width;
	    }
	    
	    // Build Timestamp label
	    int len = (int)(numOfAllInstance/60);
	    JLabel[] times = new JLabel[len];
	    locationX = widthShift;
	    locationY += height+1;
	    width = getMinuteLocation(60,totalWidth);
	    for(int i=0,t=0; i<times.length; i++,t+=60){
	    	times[i] = new JLabel(getMinute(t));
	    	times[i].setBounds(locationX, locationY, width, 20);
	    	times[i].setOpaque(true);

	    	f.getContentPane().add(times[i]);
	    	
	    	locationX += width;
	    }
	    
	    // Build Labeling List (awt.choice)
	    String[] activityList = {"Sleep","Sweep","Meal","Walk","Exercise","Read","WatchTV","PlayPad"};
	    int intervalSpace = 50;
	    int listHeight = 30;
	    width = 100;
	    locationX = widthShift;
	    locationY += 50;
	    JLabel[] labelHint = new JLabel[numOfColor];
	    labelList = new Choice[numOfColor];
	    ItemListener[] listerner = new ItemListener[numOfColor];
	    for(int i=0; i<numOfColor; i++){
	    	// if lists are larger than window size, it should move to next row
	    	if(locationX>totalWidth){
	    		locationX = widthShift;
	    		locationY += listHeight+10;
	    	}
	    	// Add color hint for each high ratio cluster
	    	labelHint[i] = new JLabel();
	    	labelHint[i].setBounds(locationX-25, locationY, 20, 20);
	    	
	    	int r = colorVectors.get(i).get(0);
	    	int g = colorVectors.get(i).get(1);
	    	int b = colorVectors.get(i).get(2);
	    	Color color = new Color(r,g,b);
	    	labelHint[i].setBackground(color);
	    	labelHint[i].setOpaque(true);
	    	
	    	f.getContentPane().add(labelHint[i]);
	    	
	    	// Add label list for each high ratio cluster
	    	labelList[i] = new Choice();
	    	labelList[i].setBounds(locationX, locationY, width, listHeight);
	    	for(int j=0; j<activityList.length; j++){
	    		labelList[i].addItem(activityList[j]);
	    	}
	    	locationX += width + intervalSpace;
	    	
	    	f.getContentPane().add(labelList[i]);
	    }
	    
	    // Add confirm button 
	    locationY += listHeight+10;
	    JButton confirmLabelBtn = new JButton("Confirm");
	    confirmLabelBtn.setBounds(totalWidth-100, locationY, 80, 30);
	    confirmLabelBtn.setBackground(new Color(200,200,200));
	    confirmLabelBtn.setOpaque(true);
	    confirmLabelBtn.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
            	try {
            		String[] labelStr = new String[numOfClu];
            		boolean[] labelClu = new boolean[numOfClu];
            		for(int i=0; i<labelClu.length; i++) labelClu[i] = false;
            		
            		new File(Path+"/SVM").mkdirs();
					FileWriter fw = new FileWriter(Path+"/SVM/"+"LabeledResult.txt");
					for(int i=0; i<labelList.length; i++){
	            		int id = colorVectors.get(i).get(3);
	            		labelClu[id] = true;
	            		String labelResult = labelList[i].getSelectedItem().toString();
	            		labelStr[id] = labelResult;
	            	}
					for(int i=0; i<labelClu.length; i++){
						if(!labelClu[i]){
							labelStr[i] = "NotForTrain";
						}
					}
					/*
					for(int i=0; i<labelClu.length; i++){
						fw.write(labelStr[i]+"\t"+String.valueOf(i)+"\r\n");
					}
					fw.flush();
					fw.close();
					*/
					for(int i=0; i<eachInstanceClu.size(); i++){
						int cluId = eachInstanceClu.get(i);
						if(labelClu[cluId]){
							for(int j=0; j<eachInstanceFeature.get(i).size(); j++){
								fw.write(eachInstanceFeature.get(i).get(j)+",");
							}
							fw.write(labelStr[cluId]+"\r\n");
						}
					}
					fw.flush();
					fw.close();
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseClicked(MouseEvent e) {}
		});
	    
	    f.getContentPane().add(confirmLabelBtn);

	    // Set window size
	    f.setSize(totalWidth+80,locationY+80);
	    f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	    f.setVisible(true);
	}
	
	
	
	private int getMinuteLocation(int time, int totalWidth){
		return (int)(((double)time/(double)numOfAllInstance)*totalWidth);
	}
	private String getMinute(int time){
		String HHMM= "";
		int minute = (int)(time/12);
		int shitHout = 1;
		if(minute>=60){
			int hour = Math.round((minute/60)) + shitHout;
			HHMM = String.valueOf(hour);
			HHMM += ":";
			minute = minute%60;
		}
		else{
			int hour = shitHout;
			HHMM = String.valueOf(hour);
			HHMM += ":";
		}
		if(minute<10){
			HHMM += "0";
			HHMM += String.valueOf(minute);
		}
		else{
			HHMM += String.valueOf(minute);
		}
		
		return HHMM;
	}
	
	private Color getColor (int id){
		Vector<Integer> color = new Vector<Integer>();
		int[] rgb = new int[3];
		for(int i=0; i<3; i++){
			rgb[i] = 255;
			color.add(255);
		}
		for(int i=0; i<colorVectors.size(); i++){
			if(id == colorVectors.get(i).get(3)){
				for (int j = 0; j < 3; j++) {
					color.set(j, colorVectors.get(i).get(j));
					rgb[j] = colorVectors.get(i).get(j);
				}
			}
		}
		return new Color(rgb[0],rgb[1],rgb[2]);
	}
	
	
	public void generateAllColor(){
		for(int i=0; i<numOfColor; i++){
			colorVectors.add(generateColor());
		}
	}
	
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
		int averageDis = (int)(200/numOfColor);
		int averageDis2 = (int)(200/(numOfColor*2/3));
		for(int i=0; i<colorVectors.size(); i++){
			int[] tmpAxisDis = new int[3];
			for(int j=0; j<3; j++){
				tmpAxisDis[j] =Math.abs(colorVectors.get(i).get(j)-color.get(j)); 
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
		}
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
		}
		for(int i=0; i<Instance.size(); i++){
			if(InstanceRatio.get(i)>threshold){
				numOfColor++;
			}
		}
		
		generateAllColor();
		
		for(int i=0,j=0; i<Instance.size(); i++){
			if(InstanceRatio.get(i)>threshold){
				colorVectors.get(j).add(Instance.get(i));
				j++;
			}
		}
	}
	
	public void loadResult(){
		new File(Path+"/DPMM").mkdirs();
		try {
			FileReader fr = new FileReader(Path+"/DPMM/"+iFile+"Result");
			BufferedReader br = new BufferedReader(fr);
			FileReader frF = new FileReader(Path+"/Features/"+iFile+"Feature.txt");
			BufferedReader brF = new BufferedReader(frF);
			String line = "";
			int preClu = -1;
			while((line = br.readLine())!=null){
				// load cluster info
				String[] instanceResultStr = line.split("	");
				int clu = Integer.valueOf(instanceResultStr[1]);
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
				
				// load instance info
				line = brF.readLine();
				String[] instanceStr = line.split(",");
				Vector<String> feature = new Vector<String>();
				for(int i=0; i<(instanceStr.length-1); i++){
					feature.add(instanceStr[i]);
				}
				eachInstanceFeature.add(feature);
				eachInstanceClu.add(clu);
			}
			fr.close();
			br.close();
			frF.close();
			brF.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
