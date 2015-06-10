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
	double threadshold = 0.03;
	Vector<Integer> partInstance = new Vector<Integer>();
	Vector<Integer> partLength = new Vector<Integer>();
	Vector<Integer> Instance = new Vector<Integer>();
	Vector<Integer> TotalLength = new Vector<Integer>();
	Vector<Double> InstanceRatio = new Vector<Double>();
	
	int[] listernId;
	
	public LabelingScreen(String path, String filename) {
		Path = path;
		iFile = filename;
		loadResult();
		generateInstance();
		
		int width = 0;
	    int height = 100;
		int totalWidth = 1200;
		int widthShift = 40;

		JFrame f=new JFrame("JLabel1");
	    f.setSize(totalWidth+80,height+250);
	    f.setLocationRelativeTo(null);
	    //f.setVisible(true);
	    f.getContentPane().setLayout(null);
	    int locationX = widthShift;
	    
	    
	    locationX = widthShift;
	    JLabel[] labels = new JLabel[numOfPartInstance];	    
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
	    int len = (int)(numOfAllInstance/60);
	    System.out.println(len);
	    JLabel[] times = new JLabel[len];
	    locationX = widthShift;
	    width = getMinuteLocation(60,totalWidth);
	    for(int i=0,t=0; i<times.length; i++,t+=60){
	    	System.out.println(locationX+", "+getMinute(t));
	    	times[i] = new JLabel(getMinute(t));
	    	times[i].setBounds(locationX, 5+height+1, width, 20);
	    	times[i].setOpaque(true);
	    	locationX += width;
	    }
	    
	    for(int i=0; i<times.length; i++){
	    	f.getContentPane().add(times[i]);
	    }
	    
	    

	    String[] activityList = {"Sleep","Sweep","Meal","Walk","Exercise","Read","Watch TV","PlayPad"};
	    int intervalSpace = 50;
	    int locationY =  5+height+51;
	    int listHeight = 30;
	    width = 100;
	    locationX = widthShift;
	    JLabel[] labelHint = new JLabel[numOfColor];
	    Choice[] labelList = new Choice[numOfColor];
	    ItemListener[] listerner = new ItemListener[numOfColor];
	    listernId = new int[numOfColor];
	    
	    for(int i=0,clu=0; i<numOfColor; i++){
	    	if(locationX>totalWidth){
	    		locationX = widthShift;
	    		locationY += listHeight+10;
	    	}
	    	
	    	labelHint[i] = new JLabel();
	    	labelHint[i].setBounds(locationX-25, locationY, 20, 20);
	    	
	    	while(colorVectors.get(clu).size()<4){
    			clu++;
    		}
	    	
	    	int r = colorVectors.get(clu).get(0);
	    	int g = colorVectors.get(clu).get(1);
	    	int b = colorVectors.get(clu).get(2);
	    	Color color = new Color(r,g,b);
	    	clu++;
	    	labelHint[i].setBackground(color);
	    	labelHint[i].setOpaque(true);
	    	f.getContentPane().add(labelHint[i]);
	    	
	    	
	    	labelList[i] = new Choice();
	    	labelList[i].setBounds(locationX,locationY, width, listHeight);
	    	for(int j=0; j<activityList.length; j++){
	    		labelList[i].addItem(activityList[j]);
	    	}
	    	listernId[i] = i;
	    	listerner[i] = new ItemListener(){
	            public void itemStateChanged(ItemEvent ie)
	            {
	            	String tmp = ie.getSource().toString().replace("[", "");
	            	System.out.println(tmp);
	            	String[] tmp1 = tmp.split("Choice");
	            	System.out.println(tmp1[1]);
	            	String[] tmp2 = tmp1[1].split(",");
	            	int id = Integer.valueOf(tmp2[0]);
	            	System.out.println("You selected id "+id+", it's seleted of " + ie.getItem());
	            }
	        }; 
	    	labelList[i].setName(String.valueOf(i));
	    	labelList[i].addItemListener(listerner[i]);
	    	locationX += width + intervalSpace;
	    	f.getContentPane().add(labelList[i]);
	    }
	    
	    locationY += listHeight+10;
	    
	    JButton confirmLabelBtn = new JButton("Confirm");
	    confirmLabelBtn.setBounds(totalWidth-100, locationY, 80, 30);
	    confirmLabelBtn.setBackground(new Color(200,200,200));
	    confirmLabelBtn.setOpaque(true);
	    confirmLabelBtn.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				String tmp = e.getSource().toString().replace("[", "");
            	System.out.println(tmp);
            	System.out.println("You selected id "+tmp);
            	
            	for(int i=0; i<labelList.length; i++){
            		String tmpQQ = labelList[i].getItem(0).toString();
            		System.out.println(tmpQQ);
            	}
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
	    f.getContentPane().add(confirmLabelBtn);
	    

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
