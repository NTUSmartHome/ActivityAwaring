package GUI;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;
import java.util.Vector;

import javax.swing.*;

public class LabelingScreen {
	
	Vector<Vector<Integer>> colorVectors = new Vector<Vector<Integer>>();
	int numOfClu = 13;
	
	public LabelingScreen() {
		// TODO Auto-generated constructor stub
		int width = 50;
	    int height = 50;
		int totalWidth = 0;
		for(int i=0; i<numOfClu; i++){
			totalWidth += width*(i+1);
		}
		totalWidth = width*numOfClu;
		JFrame f;
		//JFrame.setDefaultLookAndFeelDecorated(true);
	    //JDialog.setDefaultLookAndFeelDecorated(true);
		
	    f=new JFrame("JLabel1");
	    f.setSize(totalWidth+100,height+100);
	    f.setLocationRelativeTo(null);
	    f.setVisible(true);
	    
	    generateAllColor();
	    
	    JLabel[] labels = new JLabel[numOfClu];
	    int locationX = 0;
	    for(int i=0; i<numOfClu; i++){
	    	labels[i] = new JLabel(String.valueOf(i));
	    	
	    	labels[i].setBounds(width*i, 25, width, height);
	    	//Color color = new Color(colorVectors.get(i).get(0), colorVectors.get(i).get(1), colorVectors.get(i).get(2));
	    	Color color = new Color(45,59,187);
	    	labels[i].setBackground(color);
	    	
	    	System.out.print(i+": ");
			for(int j=0; j<3; j++){
				System.out.print(colorVectors.get(i).get(j)+",");
			}System.out.println("");
	    	
	    	labels[i].setOpaque(true);
	    	locationX += width*i;
	    }
	    f.getContentPane().setLayout(null);
	    for(int i=0; i<numOfClu; i++){
	    	f.getContentPane().add(labels[i]);
	    }
	    
	    f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
	
	public void generateAllColor(){
		for(int i=0; i<numOfClu; i++){
			
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
			for(int j=0; j<500; j++){;}
			color.add(random.nextInt(200)+20);
		}
		/*
		if(compareSimilar(color)){
			color = generateColor();
		}
		
		*/
		return color;
		
	}
	private boolean compareSimilar(Vector<Integer> color){
		int minDis = 800;
		int[] axisDis = new int[3];
		int averageDis = (int)(650/numOfClu);
		for(int i=0; i<colorVectors.size(); i++){
			int tmpDis = 0; 
			int[] tmpAxisDis = new int[3];
			for(int j=0; j<3; j++){
				tmpAxisDis[j] =Math.abs(colorVectors.get(i).get(j)-color.get(j)); 
				//System.out.print(tmpAxisDis[j]+"\t");
				tmpDis += tmpAxisDis[j];
			}
			//System.out.print("\n");
			if(tmpDis<minDis){
				minDis = tmpDis;
				for(int j=0; j<3; j++){
					axisDis[j] = tmpAxisDis[j];
				}
			}
		}
		boolean similar = false;
		/*
		if(minDis<averageDis){
			similar = true;
		}
		*/
		return similar;
	}

}
