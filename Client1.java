import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client1 
{

	public static void main(String[] args) 
	{

		// Hardcode in IP and Port here if required
    	//args = new String[] {"127.0.0.1", "30121"};
    	
        if (args.length != 2) 
        {
            System.err.println("Usage: java EchoClient <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
        
        //this print statement shows that the program has started running
        System.out.println("Creating Writer");
        try (
	            Socket clientSocket = new Socket(hostName, portNumber);
        		// stream to write text requests to server
	            PrintWriter requestWriter = new PrintWriter(clientSocket.getOutputStream(), true);
        		// stream to read text response from server
	            BufferedReader responseReader= new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); 
        		// standard input stream to get user's requests`
	            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))
	        ) 
        {
        	//create thread that gets users input and sends job to master
			ClientToMasterThread clientMaster=new ClientToMasterThread( stdIn, requestWriter);
    		clientMaster.start();
    		
    		//create thread to receive completed jobs from master
    		ClientReceivingFromMaster masterResponse=new ClientReceivingFromMaster(responseReader);
    		masterResponse.start();
    		
    		//to keep the client program running
            while(true);      
	    } 
        catch (UnknownHostException e) 
        {
        	System.err.println("Don't know about host " + hostName);
	        System.exit(1);
	    } 
        catch (IOException e) 
        {	
        	System.err.println("Couldn't get I/O for the connection to " + hostName);
	        System.exit(1);
	    } 
	}

}
