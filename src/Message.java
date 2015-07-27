
public class Message 
{
	private Status_opt type;
	private int sender_id;
	private int sender_current_val;
	private int reciver_id;
	private long msgNcccsCounter;

	public Message(Status_opt m_type, int s_id, int s_val,int r_id) 
	{
		this.type = m_type;
		this.sender_id = s_id;
		this.sender_current_val = s_val;
		this.reciver_id = r_id;
		this.msgNcccsCounter = 0;
	}
	
	// TODO: getters & setters
	
	public long getmsgNcccsCounter() 
	{
		return this.msgNcccsCounter;
	}


	public void setmsgNcccsCounter(long newCounter) 
	{
		this.msgNcccsCounter = Math.max(this.msgNcccsCounter, newCounter);
	}
	
	public Status_opt getMessageType()
	{
		return this.type;
	}
	public int getSender()
	{
		return this.sender_id;
	}
	
	public int getReciver()
	{
		return this.reciver_id;
	}
	
	public void setReciver(int id)
	{
		this.reciver_id = id;
	}
	
	public int getValue()
	{
		return this.sender_current_val;
	}
}
