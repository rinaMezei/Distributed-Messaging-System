import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ClientToMasterThread extends Thread
{	
	private String userInputType;
	private BufferedReader stdIn;
	private PrintWriter requestWriter;
	
	//Constructor
	public ClientToMasterThread(BufferedReader stdIn, PrintWriter requestWriter)
	{
		this.requestWriter=requestWriter;
		this.stdIn=stdIn;
	}
	
	@Override
	public void run()
	{
        try {
        	System.out.println("Please input type: ");
        	
        	//wait for user to input type
			while ((userInputType = stdIn.readLine()) != null) 
			{
				//input validation
				while(!userInputType.equalsIgnoreCase("A") && !userInputType.equalsIgnoreCase("B"))
				{
					System.out.println("This is an invalid type. Please re-enter input type: ");
					userInputType=stdIn.readLine();
				}
				// send request to server
			    requestWriter.println(userInputType); 
			    System.out.println("Sending " + userInputType + " to master");
			    
			    //ask for next user input
			    System.out.println("Please input type: ");	
			} 
		} 
        catch (IOException e) 
        {
			e.printStackTrace();
		}
	}
}
