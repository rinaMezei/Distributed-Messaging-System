public class Job 
{
	private String jobType;
	private int jobID;
	private int client;
	
	//Constructor
	public Job (String jobType, int jobID, int client)
	{
		this.jobType=jobType;
		this.jobID=jobID;
		this.client=client;
	}
	
	public void setJobType(String jobType)
	{
		this.jobType=jobType;
	}
	
	public void setJobID(int jobID)
	{
		this.jobID=jobID;
	}
	
	public int getJobID()
	{
		return jobID;
	}
	
	public String getJobType()
	{
		return jobType;
	}
	
	public int getClient()
	{
		return client;
	}

	@Override
	public String toString()
	{
		return "Job [jobType=" + jobType + ", jobID=" + jobID + ", client=" + client + "]";
	}
}
