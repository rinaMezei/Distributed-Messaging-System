import java.io.BufferedReader;
import java.io.IOException;

public class ClientReceivingFromMaster extends Thread
{	
	private BufferedReader responseReader;
	private String serverResponse;
	
	//Constructor
	public ClientReceivingFromMaster(BufferedReader responseReader)
	{
		this.responseReader=responseReader;
	}

	@Override
	public void run()
	{
		 try 
		 {
			 //waiting for alert from master that job is completed
			 while((serverResponse=responseReader.readLine())!=null)
			 {
			 	System.out.println("MASTER RESPONDS: \"" + serverResponse + "\"");
			 }
		 } 
		 catch (IOException e) 
		 {
			e.printStackTrace();
		 }
	}
}
