import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class MasterReceivingFromClientThread extends Thread
{
	private BufferedReader requestReader1;
	private String usersRequest;
	private Object lock;
	private ArrayList<Job> jobs;
	private int clientNum;
	
	//Constructor
	public MasterReceivingFromClientThread(BufferedReader requestReader1, ArrayList<Job>jobs, Object lock, int clientNum)
	{
		this.requestReader1=requestReader1;
		this.jobs=jobs;
		this.lock=lock;
		this.clientNum=clientNum;
	}
	
	@Override
	public void run()
	{
		try 
		{
			//wait for client to send user request (job type)
			while ((usersRequest = requestReader1.readLine()) != null) 
			{
				System.out.println("Job " + usersRequest + " received from Client "+ clientNum);
				//synchronize jobs ArrayList to add each received job
				synchronized(lock)
				{
					//create Job object-type, index (ID), client- and add to ArrayList
					jobs.add(new Job(usersRequest, jobs.size(), clientNum));
				}		
			}
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}
