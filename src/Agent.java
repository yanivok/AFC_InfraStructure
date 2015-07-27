import java.util.ArrayList;


public abstract class Agent 
{
	protected int agent_id;
	protected int current_value;
	private Mailing_System agent_mailer;
	protected ArrayList<Integer> domain;// Thread safe, have a lock system
	protected ArrayList<Integer> domain_original; // a copy in case domain gets empty and agent needs to re-create domain
	protected Problem_generator p;
	protected long ncccsCounter;

	public Agent(Problem_generator p,ArrayList<Integer> d, int id)
	{
		// TODO Auto-generated constructor stub
		this.agent_id = id;
		this.current_value = -1;
		this.domain = d;
		this.domain_original = new ArrayList<Integer>(d);
		this.p = p;
		this.ncccsCounter = 0;
	}
	
	public abstract void initialAssign();
	
	public abstract Status_opt handleMessage(Message msg);
	
	
	// TODO: Define setters & getters
	public ArrayList<Integer> getDomain()
	{
		return this.domain;
	}
	
	public void setDomain(ArrayList<Integer> domain)
	{
		this.domain = new ArrayList<Integer>(domain);
	}
	
	// TODO: Send message
	public void sendMessage(Message m) throws InterruptedException
	{
		agent_mailer.sendMessage(m);
	}
	
	
	public int getAgentId()
	{
		return this.agent_id;
	}
	
	public int getValue()
	{
		return this.current_value;
	}
	
	public void setMail(Mailing_System m)
	{
		this.agent_mailer = m;
	}
	public void setCurrentValue(int v)
	{
		this.current_value = v;
	}
	
	public void incNcccsCounter()
	{
		this.ncccsCounter++;
	}
	
	public void decNcccsCounter()
	{
		this.ncccsCounter--;
	}
	
	public long getNcccsCounter()
	{
		return this.ncccsCounter;
	}
	public void setNcccsCounter(long newCounter)
	{
		this.ncccsCounter = Math.max(this.ncccsCounter, newCounter);
	}
	// TODO: Receive message
	public Message recieveMassage(int agent_id) throws InterruptedException
	{
		Message m=agent_mailer.reciveMessage(agent_id);
		setNcccsCounter(m.getmsgNcccsCounter());
		return m;
	}
	// TODO: Stop Running
	public void stopAgentRunning(Status_opt s)  throws InterruptedException
	{ 
		agent_mailer.stopMailingSystemRunning(agent_id, s); 
	}

}
