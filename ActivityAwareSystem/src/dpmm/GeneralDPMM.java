package dpmm;

public class GeneralDPMM {
	static String input_filename = "";
	static String output_filename = "";
	static double alpha = 1;
	static double alphaWords = 5;
	static int iter = 100;
	
	public GeneralDPMM (){
		
	}
	
	public static void main(String args[]) {
		if(args.length==5){
			input_filename = args[0];
			output_filename = args[1];
			alpha = Double.valueOf(args[2]);
			alphaWords = Double.valueOf(args[3]);
			iter = Integer.valueOf(args[4]);
			
			//MDPMMTrain MDPMM = new MDPMMTrain(input_filename,output_filename,alpha,alphaWords,iter);
			System.out.println("End Training.");
		}
	}
	
	
}
