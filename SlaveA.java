import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SlaveA 
{

	public static void main(String[] args) 
	{

		// Hardcode in IP and Port here if required
		//args = new String[] { "127.0.0.1", "30121" };

		if (args.length != 2) 
		{
			System.err.println("Usage: java EchoClient <host name> <port number>");
			System.exit(1);
		}
		String hostName = args[0];
		int portNumber = Integer.parseInt(args[1]);

		String job = null;
		
		//print statement to make sure the program is running
		System.out.println("Creating Writer");
		try (
			Socket clientSocket = new Socket(hostName, portNumber); 
			// stream to write text requests to server
			PrintWriter requestWriter =new PrintWriter(clientSocket.getOutputStream(), true); 
			// stream to read text response from server
			BufferedReader responseReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			) 
		{
			//keep slave program running
			while (true) 
			{
				//waits for master to send a job
				while ((job = responseReader.readLine()) != null) 
				{
					System.out.println("\"" + job + "\" received");
					try 
					{
						//if job is non optimal (sent with ! at the end), sleep for longer
						if (job.charAt(job.length() - 1) == ('!')) 
						{
							Thread.sleep(10000);
						} 
						else 
						{
							Thread.sleep(2000);
						}
					} 
					catch (InterruptedException e) 
					{
						e.printStackTrace();
					}
					System.out.println("Job " + job + " completed");
					
					//send that job is completed to master
					requestWriter.println(job);
				}
			}
		}
		catch (IOException e) 
		{
			System.out.println("Exception caught when trying to listen on port " + portNumber + " or listening for a connection");
		}
	}
}
