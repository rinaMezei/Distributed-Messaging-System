import java.net.*;
import java.util.ArrayList;
import java.io.*;

public class Master 
{
	public static void main(String[] args) throws IOException 
	{
		//args=new String[] {"30121"};

		if (args.length != 1) 
		{
			System.err.println("Usage: java EchoServer <port number>");
			System.exit(1);
		}
		
		int portNumber = Integer.parseInt(args[0]);
		
		//print statement to see that the program is running
		System.out.println("Creating connection- waiting for client");
		
		//initialize ArrayList that holds all of the jobs
		ArrayList<Job> jobs=new ArrayList<Job>();
		
		//initialize ArrayLists that hold jobs sent to respective slave
		ArrayList<String>sentA=new ArrayList<String>();
		ArrayList<String>sentB=new ArrayList<String>();
		
		//initialize ArrayLists that hold jobs completed from respective slaves
		ArrayList<String>doneA=new ArrayList<String>();
		ArrayList<String>doneB=new ArrayList<String>();
		
		//creating sockets
		try (
			ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));
			Socket clientSocket1 = serverSocket.accept();
			PrintWriter responseWriter1 = new PrintWriter(clientSocket1.getOutputStream(), true);
			BufferedReader requestReader1 = new BufferedReader(new InputStreamReader(clientSocket1.getInputStream()));
			
			Socket clientSocket2 = serverSocket.accept();
			PrintWriter responseWriter2 = new PrintWriter(clientSocket2.getOutputStream(), true);
			BufferedReader requestReader2 = new BufferedReader(new InputStreamReader(clientSocket2.getInputStream()));
		
			Socket slaveSocketA = serverSocket.accept();
			PrintWriter responseWriterA = new PrintWriter(slaveSocketA.getOutputStream(), true);
			BufferedReader requestReaderA = new BufferedReader(new InputStreamReader(slaveSocketA.getInputStream()));
			
			Socket slaveSocketB= serverSocket.accept();
			PrintWriter responseWriterB= new PrintWriter(slaveSocketB.getOutputStream(), true);
			BufferedReader requestReaderB= new BufferedReader(new InputStreamReader(slaveSocketB.getInputStream()));
			) 
		{
			//creating object locks for synchronizing
			Object lock=new Object();
			Object counter_lock=new Object();
			Object done_lock=new Object();
			Object sentA_lock = new Object();
			Object sentB_lock=new Object();
			
			//initializing count of jobs already sent to each slave
			int countA=0;
			int countB=0;
			
			//creating threads
			MasterReceivingFromClientThread readFromClient1=new MasterReceivingFromClientThread(requestReader1, jobs, lock, 1);
			MasterReceivingFromClientThread readFromClient2=new MasterReceivingFromClientThread(requestReader2, jobs, lock, 2);
			MasterSendingToSlave sendingSlaveA=new MasterSendingToSlave(responseWriterA, sentA, sentA_lock, "A");
			MasterSendingToSlave sendingSlaveB=new MasterSendingToSlave(responseWriterB, sentB, sentB_lock, "B");
			MasterReceivingFromSlave receivingSlaveA=new MasterReceivingFromSlave(requestReaderA, doneA, done_lock, countA, lock, "A");
			MasterReceivingFromSlave receivingSlaveB=new MasterReceivingFromSlave(requestReaderB, doneB, done_lock, countB, lock, "B");
			MasterWritingToClient writeToClient1=new MasterWritingToClient(responseWriter1, responseWriter2, doneA, done_lock, jobs, lock);
			MasterWritingToClient writeToClient2=new MasterWritingToClient(responseWriter1, responseWriter2, doneB, done_lock, jobs, lock);
			
			//starting threads
			readFromClient1.start();
			readFromClient2.start();
			sendingSlaveA.start();
			sendingSlaveB.start();
			receivingSlaveA.start();
			receivingSlaveB.start();
			writeToClient1.start();
			writeToClient2.start();

			//variable to hold the next job that was sent by the user, through the client
			Job nextJob=null;
			
			//the job ID of the next job
			String jobIDString;
			
			//flags to keep do whiles going
			boolean empty;
			boolean jobFinished;
			
			//last index filled in the jobs ArrayList
			int i=0;
			
			//loop while jobs ArrayList is empty
			do{
				synchronized(lock)
				{
					empty=jobs.isEmpty();
				}
			}while(empty);
			
			//allows the master program to keep running 
			while(true)
			{
				//loop while all jobs were already sent to slaves
				do{
					synchronized(lock)
					{
						jobFinished=i>jobs.size()-1;
					}
				}while(jobFinished);
				
				//get job type from jobs
				synchronized(lock)
				{
					nextJob=jobs.get(i);
					//increment the index so we know which job we're up to in the jobs ArrayList
					i++;							
				}
				
				//convert job ID to string
				jobIDString=String.valueOf(nextJob.getJobID());
				
				//if the jobType is A
				if (nextJob.getJobType().equalsIgnoreCase("A"))
				{
					//synchronizing the counter so we know which slave to send the job to 
					synchronized(counter_lock) 
					{
						//if slaveA has less than 5 jobs that were sent to it, or its count is less than or equals to the 
						//number of jobs that were sent to slaveB, then send this job to A
						if (countA<5 || countA<=countB)
						{
							//synchronizing ArrayList to add jobID to
							synchronized(sentA_lock) 
							{
								sentA.add(jobIDString);
							}
							countA++;
						}
						//if not, then send this job to slaveB with flag(!), to show that this is not optimal
						else
						{
							synchronized(sentB_lock) 
							{
								sentB.add(jobIDString+"!");
							}
							countB=countB+5;
						}
					}
				}
				//if the jobType is B
				else
				{
					//synchronizing the counter so we know which slave to send the job to 
					synchronized(counter_lock) 
					{
						//if slaveB has less than 5 jobs that were sent to it, or its count is less than or equals to the 
						//number of jobs that were sent to slaveA, then send this job to B
						if(countB<5 || countB<=countA)
						{
							synchronized(sentB_lock) 
							{
								sentB.add(jobIDString);
							}
							countB++;
						}
						//if not, then send this job to slaveA with flag(!), to show that this is not optimal
						else
						{
							synchronized(sentA_lock) 
							{
								sentA.add(jobIDString + "!");
							}
							countA=countA+5;								
						}
					}
				}	
			}
		} 
		catch (IOException e) 
		{
				System.out.println("Exception caught when trying to listen on port " + portNumber + 
						" or listening for a connection");
				System.out.println(e.getMessage());
		}

	}

}
