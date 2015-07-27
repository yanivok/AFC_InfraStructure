import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class SBT extends Agent{
	
	private HashMap<Integer,Integer> cpa = new HashMap<Integer,Integer>(); 
	

	public SBT(Problem_generator p, ArrayList<Integer> d, int id) 
	{
		super(p, d, id);
	}


	/* Agent receives a message and responds */
	public Status_opt handleMessage(Message msg)
	{
		boolean flag=false;
		AgentMessage msg_new;
		Status_opt message_type = Status_opt.undefined;  
		
		/* respond to an 'OK' message */
		if (msg.getMessageType()==Status_opt.OK)
		{ 	
			cpa = ((AgentMessage)msg).getSenderAssign(); // gets previous assignments
			flag = assignNewValue(); 							// tries to assign consistent value
			
			if (!flag) // Couldn't find assignment
			{
				msg_new = new AgentMessage(Status_opt.NoGood,agent_id,current_value,agent_id-1);
				try
				{
					sendMessage(msg_new);
					Main.writeToConsole("Agent "+agent_id+ " sends NoGood message to: "+(agent_id-1));
				} 
				catch (InterruptedException e)
				{
					System.out.println("Send Message Exception! - "+e.toString());
				}
			}
			// Solution found
			else if(cpa.size() == (p.getAgentsNumber()))  
			{	
				message_type = Status_opt.SolutionFound;
				p.status = Status_opt.SolutionFound;
				Main.writeToConsole("Agent "+agent_id+" Assigned value: " +this.getValue());
				Main.writeToConsole("Solution Found: " +cpa.toString());
				
				// Check if the solution is real one
				for(int i=0; i<cpa.size(); i++)
				{
					for(int j= i+1 ; j<cpa.size() ; j++)
					{
						if(!p.check(i, cpa.get(i), j, cpa.get(j)))
						{
							Main.writeToConsole("This is not Solution!!!!!!!!agent: "+i + " - agent: "+j);
							break;
						}
					}
				}
				try
				{
					stopAgentRunning(Status_opt.SolutionFound);
				} 		
				catch (InterruptedException e)
				{
					System.out.println("Exception! - solution found "+e.toString());
				}
			}
			else // Found new assignment
			{
				msg_new = new AgentMessage(Status_opt.OK,agent_id, current_value, agent_id+1, cpa);
				
				try
				{
					sendMessage(msg_new);
					Main.writeToConsole("Agent "+agent_id+" Assigned value: " +this.getValue()+ " sends OK message to "+(agent_id+1));
				} 
				catch (InterruptedException e)
				{
					System.out.println("Exception! - Send Message"+e.toString());
				}
					
			}
		}
		
		/* respond to a 'NoGood' message */
		if (msg.getMessageType()==Status_opt.NoGood) 
		{
			cpa.remove(agent_id); 
			flag = assignNewValue();
			
			// Couldn't find assignment
			if(!flag)
			{
				current_value = -1;
				cpa.remove(agent_id);
				
				if(agent_id == 0) 
				{
					message_type = Status_opt.NoSolution;
					p.status = Status_opt.NoSolution;
					Main.writeToConsole("There is no solution to the problem");
					try
					{
						stopAgentRunning(Status_opt.NoSolution);
					} 
					catch (InterruptedException e)
					{
						System.out.println("Exception! (no solution): "+e.toString()+ "\n");
					}
				}
				else 			
				{
					msg_new = new AgentMessage(Status_opt.NoGood,agent_id,current_value,agent_id-1);
					try 
					{
						sendMessage(msg_new);
						Main.writeToConsole("Agent "+agent_id+ " sends NoGood message to "+(agent_id-1));
					} 
					catch (InterruptedException e)
					{
						System.out.println("Exception! - Send Message : "+e.toString());
					}
				}
						
			}
			else // Found new assignment after receiving NoGood
			{	
				msg_new = new AgentMessage(Status_opt.OK,agent_id, current_value, agent_id+1, cpa);
				
				try
				{
					sendMessage(msg_new);
					Main.writeToConsole("Agent "+agent_id+" Assigned value: " +getValue()+ " sends OK message to"+ (agent_id+1));
				} 
				catch (InterruptedException e) 
				{
					System.out.println("Exception! - Send Message  "+e.toString()+"\n");
				}
			}
		}
		return message_type;		
	}
	
	/* Tries to find a new consistent assignment */
	private boolean assignNewValue()
	{ 
		boolean found = false;
		int try_val;
		
		for (try_val = current_value+1; (try_val < getDomain().size()) && (!found); try_val++ )
		{	
			setCurrentValue(try_val); 
			found = true;
			
			for(Entry<Integer, Integer> i : cpa.entrySet())
			{
				if(!found)
					break;
				else
				{
					int id = i.getKey();
					int val = i.getValue();
					found = p.check(agent_id , current_value, id, val);  
					if(!found)
					{
						current_value = -1;
					}
				}
			}
		}
	
		if(found)
		{
			this.setCurrentValue(try_val-1);
			cpa.put(agent_id, try_val-1);
		}
					
		return found;
	}

	
	/* The first agent performs an assignment to its variable
	 * and sends the assignment on a message to the next agent. */
	public void initialAssign()
	{
		if (agent_id == 0)
		{
			setCurrentValue(0);
			cpa.put(agent_id,current_value);
			
			AgentMessage msg = new AgentMessage(Status_opt.OK,agent_id, current_value, agent_id+1, cpa);
			try 
			{
				sendMessage(msg);
				Main.writeToConsole("Agent "+agent_id+" Assigned value: " +getValue()+ " sends OK message to " +(agent_id+1));
			} 
			catch (InterruptedException e) 
			{
				System.out.println("Exception! : "+e.toString());
			}
		}
	}
}