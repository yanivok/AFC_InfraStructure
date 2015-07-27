import java.util.HashMap;

public class AgentMessage extends Message 
{
	private HashMap<Integer,Integer> cpa;
	private HashMap<Integer,Integer> conflicts;

	AgentMessage(Status_opt messageType,int sender,int senderCurrentVal,int recipient) 
	{
		super(messageType,sender,senderCurrentVal,recipient);
		
		if(cpa == null)
			this.cpa = new HashMap<Integer, Integer>();
	}


	AgentMessage(Status_opt messageType,int sender,int senderCurrentVal,int recipient,  HashMap<Integer,Integer>  prevCpa) 
	{
		super(messageType,sender,senderCurrentVal,recipient);
		
		// Duplicate the object -> prevent memory sharing
		this.cpa= new HashMap<Integer,Integer>(prevCpa);
		this.conflicts = new HashMap<Integer, Integer>(prevCpa);
	}
	
	
	public HashMap<Integer, Integer> getSenderAssign()
	{
		return this.cpa;
	}
	
	public HashMap<Integer, Integer> getConflicts()
	{
		return conflicts;
	}
	
}
