import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class MasterReceivingFromSlave extends Thread
{
	private String slaveResponse;
	private BufferedReader requestReader;
	private ArrayList<String>done;
	private Object done_lock;
	private int count;
	private Object lock;
	private String slaveType;
	
	//Constructor
	public MasterReceivingFromSlave(BufferedReader requestReader, ArrayList<String>done, Object done_lock, 
			int count, Object lock, String slaveType)
	{
		this.requestReader=requestReader;
		this.done=done;
		this.done_lock=done_lock;
		this.count=count;
		this.lock=lock;
		this.slaveType=slaveType;
	}
	
	@Override
	public void run()
	{
		boolean flag;
		try 
		{
			//waiting for slave to respond
			while ((slaveResponse = requestReader.readLine()) != null)
			{
				flag=true;
				
				//if jobID was originally sent with a !(the flag that shows it's not optimal), then we have to subtract from
				//the count 5 times so we set the flag to false
				if(slaveResponse.charAt(slaveResponse.length()-1)==('!'))
				{
					flag=false;
				}
				
				//synchronizing on the count
				synchronized(lock)
				{
					if(flag==true)
					{
						count--;
					}
					else
					{
						count=count-5;
					}
				}
					
				System.out.println("Received job "+ slaveResponse + " from slave " + slaveType);
				
				//synchronizing to add to done ArrayList
				synchronized(done_lock)
				{
					done.add(slaveResponse);
				}
			}
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}
