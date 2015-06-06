
import java.io.File;
import java.io.FileWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class SocketServer extends java.lang.Thread {
	

    private boolean OutServer = false;
    private ServerSocket server;
    private boolean isDataReady = false;
    private boolean isTransmittionRequest = false;
    private final int ServerPort = 8765;// �n�ʱ���port
    private String[] dataStrings;
    public SocketServer() {
        try {
         
            server = new ServerSocket(ServerPort);




        } catch (java.io.IOException e) {
            System.out.println("Socket�Ұʦ����D !");
            System.out.println("IOException :" + e.toString());
        }
    }

    public void run() {
        Socket socket;
        java.io.BufferedInputStream in;

        System.out.println("���A���w�Ұ� !");
        while (!OutServer) {
            socket = null;
            try {
                synchronized (server) {
                    socket = server.accept();
                }
                System.out.println("���o�s�u : InetAddress = "
                        + socket.getInetAddress());
                // TimeOut�ɶ�
                socket.setSoTimeout(15000);

                in = new java.io.BufferedInputStream(socket.getInputStream());
                byte[] b = new byte[1024];
                String data = "";
                int length;
                
                while ((length = in.read(b)) > 0)// <=0���ܴN�O�����F
                {
                    data += new String(b, 0, length);
                }
                //notify();
                
                if(isTransmittionRequest){
                	String[] receive;
                	receive = data.split("\\s+");
                	this.dataStrings = new String[7];
                	this.dataStrings[0] = receive[0];
                	for(int i = 1; i < 7; i++)
                		this.dataStrings[i] = receive[i+3];
                	isTransmittionRequest = false;
                	isDataReady = true;
                }
                
                //System.out.println("�ڨ��o����:" + data);
                
                
                in.close();
                in = null;
                socket.close();
                

            } catch (java.io.IOException e) {
                System.out.println("Socket�s�u�����D !");
                System.out.println("IOException :" + e.toString());
            }

        }
    }
    public String[] onRequestData(){
    	setPriority(10);
    	isTransmittionRequest = true;
    	while(!isDataReady){
    		try {
				sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
    	isDataReady = false;
    	return this.dataStrings;
    }

   /* public static void main(String args[]) {
        (new SocketServer()).start();
    }*/


}