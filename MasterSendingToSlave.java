import java.io.PrintWriter;
import java.util.ArrayList;

public class MasterSendingToSlave extends Thread 
{
	private PrintWriter responseWriter;
	private ArrayList<String>sent;
	private Object lock;
	private String slaveType;
	
	//Constructor
	public MasterSendingToSlave(PrintWriter responseWriter, ArrayList<String>sent, Object lock, String slaveType)
	{
		this.responseWriter=responseWriter;
		this.sent=sent;
		this.lock=lock;
		this.slaveType=slaveType;
	}
	
	@Override
	public void run()
	{
		//keep the thread going
		while(true)
		{
			//creating variable to send jobID to slave
			String jobID;
			
			//flag to see if sent ArrayList is empty
			boolean empty=false;
			do{
				synchronized(lock)
				{
					empty=sent.isEmpty();
				}
			}while(empty);
			
			//synchronize to get jobID from sent ArrayList and remove it
			synchronized(lock)
			{
				jobID=sent.get(0);
				sent.remove(0);
			}
			//write job to slave
			responseWriter.println(jobID);
			System.out.println("Sending job " + jobID + " to slave " + slaveType);
		}
	}
}
