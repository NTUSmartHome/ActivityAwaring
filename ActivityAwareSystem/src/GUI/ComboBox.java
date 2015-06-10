package GUI;
import javax.swing.*;

import java.awt.event.*;
import java.awt.*;
public class ComboBox extends JFrame 
{
 String name[] = {"Abhi","Adam","Alex","Ashkay"};  //list of name. 
 public ComboBox() 
 {
  JComboBox jc = new JComboBox(name);	//initialzing combo box with list of name. 
  add(jc);				//adding JComboBox to frame. 
  setLayout(new FlowLayout());
  setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  setSize(400, 400);
  setVisible(true);
 }
 public static void main(String[] args)
 {
	 JFrame jFrame = new JFrame();
	 
	 JLabel label = new JLabel("123");
	 label.setBounds(100, 50, 30, 50);
	 jFrame.getContentPane().setLayout(null);
	 jFrame.getContentPane().add(label);
	 
  	
  	String list[] = {"Abhi","Adam","Alex","Ashkay"};
  	JComboBox jc = new JComboBox(list);
  	
  	
  	//jFrame.setLayout( null);
  	jFrame.setLayout(new FlowLayout());
  	jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  	jFrame.setSize(800,800);
  	jFrame.setVisible(true);
  	jFrame.add(jc);
  	jc.setBounds(100, 100, 100, 100);
 }
}