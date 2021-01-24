import java.io.PrintWriter;
import java.util.ArrayList;

public class MasterWritingToClient extends Thread
{
	private PrintWriter responseWriter1;
	private PrintWriter responseWriter2;
	private ArrayList<String>done;
	private Object done_lock;
	private ArrayList<Job>jobs;
	private Object lock;
	
	//Constructor
	public MasterWritingToClient(PrintWriter responseWriter1, PrintWriter responseWriter2, ArrayList<String>done, 
			Object done_lock, ArrayList<Job>jobs, Object lock)
	{
		this.responseWriter1=responseWriter1;
		this.responseWriter2=responseWriter2;
		this.done=done;
		this.done_lock=done_lock;
		this.jobs=jobs;
		this.lock=lock;
	}
	
	@Override
	public void run()
	{
		int jobID;
		Job job;
		int clientNum;
		boolean empty;
		
		//keeps the thread running
		while(true)
		{			
			//loop while done ArrayList is empty
			do{
				synchronized(done_lock)
				{
					empty=done.isEmpty();
				}
			}while(empty);
			
			//synchronizing to get job from done ArrayList
			synchronized(done_lock)
			{
				//if there's an ! (meaning it was non optimal), get rid of it
				if(done.get(0).charAt(done.get(0).length()-1)==('!'))
				{
					jobID=Integer.parseInt(done.get(0).substring(0,done.get(0).length()-1));
				}
				else
				{
					jobID=Integer.parseInt(done.get(0));
				}
				//remove job from ArrayList because we're sending it back to client
				done.remove(0);
			}
			
			//synchronizing to get which client to send the job back to (where it originally came from)
			synchronized(lock)
			{
				job=jobs.get(jobID);
			}
			//use getter from job class to get the client number, which we set originally
			clientNum=job.getClient();
			
			//send to respective client
			if(clientNum==1)
			{
				responseWriter1.println("Job " + jobID + " done! ");
			}
			else
			{
				responseWriter2.println("Job " + jobID + " done! ");
			}
			System.out.println("Sending completed job " + jobID + " to client " + clientNum);
		}
		
	}
}
