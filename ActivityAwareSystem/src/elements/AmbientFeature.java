package elements;

import java.util.Vector;

public class AmbientFeature {
	private Vector<String> Room = new Vector<String>();
	private Vector<Vector<String>>People = new Vector<Vector<String>>();
	private Vector<Vector<String>> Appliances = new Vector<Vector<String>>();
	private String Kitchen = "Kitchen";
	private String Livingroom = "Livingroom";
	private String Studyingroom = "Studyingroom";
	private String Bedroom = "Bedroom";
	private String Movable = "Movable";
	private String Microwave = "Microwave";
	private String Pad = "Pad";
	private String TV = "TV";
	private String Light = "Light";
	private String Lamp = "Lamp";
	private String Fan = "Fan";
	private String off = "off";
	private String on = "on";
	
	public AmbientFeature() {
		// TODO Auto-generated constructor stub
		init();
	}
	
	public void init(){
		initLiving();
		initStudying();
		initBedroom();
		initKitchen();
		initMovable();
	}

	public void initLiving(){
		Vector<String> people = new Vector<String>();
		people.add("0");
		people.add(Livingroom);
		People.add(people);
		
		Vector<String> appliance = new Vector<String>();
		appliance.add(Light);
		appliance.add(off);
		appliance.add(Livingroom);
		Appliances.add(appliance);
		
		appliance = new Vector<String>();
		appliance.add(TV);
		appliance.add(off);
		appliance.add(Livingroom);
		Appliances.add(appliance);
		
		appliance = new Vector<String>();
		appliance.add(Lamp);
		appliance.add(off);
		appliance.add(Livingroom);
		Appliances.add(appliance);
		
		appliance = new Vector<String>();
		appliance.add(Fan);
		appliance.add(off);
		appliance.add(Livingroom);
		Appliances.add(appliance);
	}
	public void initStudying(){
		Vector<String> people = new Vector<String>();
		people.add("0");
		people.add(Studyingroom);
		People.add(people);
		
		Vector<String> appliance = new Vector<String>();
		appliance.add(Light);
		appliance.add(off);
		appliance.add(Studyingroom);
		Appliances.add(appliance);
		
		appliance = new Vector<String>();
		appliance.add(Lamp);
		appliance.add(off);
		appliance.add(Studyingroom);
		Appliances.add(appliance);
		
	}
	public void initBedroom(){
		Vector<String> people = new Vector<String>();
		people.add("0");
		people.add(Bedroom);
		People.add(people);
		
		Vector<String> appliance = new Vector<String>();
		appliance.add(Light);
		appliance.add(off);
		appliance.add(Bedroom);
		Appliances.add(appliance);
		
	}
	public void initKitchen(){
		Vector<String> people = new Vector<String>();
		people.add("0");
		people.add(Kitchen);
		People.add(people);
		
		Vector<String> appliance = new Vector<String>();
		appliance.add(Light);
		appliance.add(off);
		appliance.add(Kitchen);
		Appliances.add(appliance);
		
		appliance = new Vector<String>();
		appliance.add(Microwave);
		appliance.add(off);
		appliance.add(Kitchen);
		Appliances.add(appliance);
		
	}
	public void initMovable(){
		Vector<String> people = new Vector<String>();
		people.add("0");
		people.add(Movable);
		People.add(people);
		
		Vector<String> appliance = new Vector<String>();
		appliance.add(Pad);
		appliance.add(off);
		appliance.add(Movable);
		Appliances.add(appliance);
		
	}
	
	public void set(String room, int people){
		for(int i=0;i<People.size();i++){
			if(People.get(i).get(1).contains(room)){
				People.get(i).set(0,String.valueOf(people));
				break;
			}
		}
	}
	
	public void set(String room, String appliance, boolean status){
		for(int i=0; i<Appliances.size(); i++){
			if(Appliances.get(i).get(2).contains(room) && Appliances.get(i).get(0).contains(appliance)){
				if(status)
					Appliances.get(i).set(1, on);
				else
					Appliances.get(i).set(1, off);
				break;
			}
		}
	}
	public String get(String room, String appliance){
		String status = "";
		for(int i=0; i<Appliances.size(); i++){
			if(Appliances.get(i).get(2).contains(room) && Appliances.get(i).get(0).contains(appliance)){
				status = Appliances.get(i).get(1);
				break;
			}
		}
		return status;
	}
	
	public int getPeople(int room){return Integer.valueOf(People.get(room).get(0));}
	public int getPeople(String room){
		String people = "";
		for(int i=0;i<People.size();i++){
			if(People.get(i).get(1).contains(room)){
				people = People.get(i).get(0);
			}
		}
		return Integer.valueOf(people);
	}
	public int getRoomSize(){return People.size();}
	public int getApplianceSize(){return Appliances.size();}
	public String getAppliance(int appliance){return Appliances.get(appliance).get(0);}
	public int getStatus(int appliance){
		if(Appliances.get(appliance).get(1).equals("on"))
			return 1;
		else if(Appliances.get(appliance).get(1).equals("off"))
			return 0;
		return 0;
	}
	public String getRoom(int appliance){return Appliances.get(appliance).get(2);}
	
}
