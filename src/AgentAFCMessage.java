
import java.util.HashMap;


public class AgentAFCMessage extends Message 
{
	private HashMap<Integer,Integer> cpa;
	private HashMap<Integer,Integer> inconsistencies;
	

	AgentAFCMessage(Status_opt messageType,int sender,int senderCurrentVal,int recipient) {
		super(messageType,sender,senderCurrentVal,recipient);
	}

	
	AgentAFCMessage(Status_opt messageType,int sender,int senderCurrentVal,int recipient, HashMap<Integer,Integer>  prevCpa,HashMap<Integer,Integer> set) {
		super(messageType,sender,senderCurrentVal,recipient);
		
		if(prevCpa != null)
		{
			this.cpa = new HashMap<Integer,Integer>();
			for(Integer i :prevCpa.keySet())
				this.cpa.put(i,prevCpa.get(i));
		}
		else
		{
			this.cpa = null;
		}

		
		inconsistencies = new HashMap<Integer,Integer>(set);
	}
	
	
	public HashMap<Integer, Integer> getSenderAssign(){
		return cpa;
	}
	
	public HashMap<Integer,Integer> getInconsistentSet()
	{
		return this.inconsistencies;
	}
	
}
