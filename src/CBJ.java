import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;


public class CBJ extends Agent
{
	
	private Heuristics_opt heuristic;
	private HashMap<Integer,Integer> cpa = new HashMap<Integer,Integer>();   
	private int previous_sender;
	private int max_agent_in_conflict;
	private int max_agent_in_order_conflict;
	private HashMap<Integer, Integer> save_conflicts = new HashMap<Integer, Integer>();
	private int l_bound_con = 0;
	private int u_bouns_con = 0;


	public CBJ(Problem_generator p, ArrayList<Integer> d, int id,Heuristics_opt h) 
	{
		super(p,d,id);
		heuristic = h;
		max_agent_in_conflict = -1;
	}
	
	public void setLbound(int n)
	{
		this.l_bound_con = Math.max(l_bound_con, n);
	}
	
	public void setUbound(int n)
	{
		this.u_bouns_con = Math.min(u_bouns_con+n, this.domain.size());
	}

	public void initialAssign()
	{
		if (agent_id == 0)
		{
			this.setCurrentValue(0);
			this.cpa.put(agent_id,current_value);
			
			try 
			{
				SendMessageByHuristic(Status_opt.OK);
			} 
			catch (InterruptedException e) 
			{
				System.out.print("Exception - Initial Assign :"+e.toString());
			}
		}
	}

	@Override
	public Status_opt handleMessage(Message msg)
	{
		boolean flag=false;
		AgentMessage msg_new;
		Status_opt message_type = Status_opt.undefined;  
		
		previous_sender = msg.getSender();
		
		/* respond to an 'OK' message */
		if (msg.getMessageType()==Status_opt.OK)
		{ 	
			cpa = ((AgentMessage)msg).getSenderAssign(); // gets previous assignments
			cpa.remove(agent_id);
			flag = assignNewValue(); 							// tries to assign consistent value
			
			if (!flag) // Couldn't find assignment
			{	
				findMaxAgentConflict();
				if(agent_id == 0 || max_agent_in_conflict == -1) // Backtrack reached agent 0 and there is no value to assign - No Solution
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
						System.out.println("Stopping Agents Exception! (no solution)\n"+e.toString());
					}
				}
				else if(max_agent_in_conflict != -1)
				{
					msg_new = new AgentMessage(Status_opt.NoGood,agent_id,current_value,max_agent_in_conflict,save_conflicts);
					try
					{
						sendMessage(msg_new);
						Main.writeToConsole("Agent "+agent_id+ " sends NoGood message to: "+max_agent_in_conflict+" agent");
					} 
					catch (InterruptedException e)
					{
						System.out.println("Exception! - Send Message : "+e.toString());
					}
				}
			}
			else if(cpa.size() == (p.getAgentsNumber())) // Found new assignment (last agent) - Solution found 
			{	
				message_type = Status_opt.SolutionFound;
				p.status = Status_opt.SolutionFound;
				Main.writeToConsole("Agent "+agent_id+" Assigned value: " +this.getValue());
				Main.writeToConsole("Solution Found: " +cpa.toString());
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
					// stops all runnables
					stopAgentRunning(Status_opt.SolutionFound);
				} 		
				catch (InterruptedException e) 
				{
					System.out.println("Exception! (solution found) - "+e.toString());
				}
			}
			else // Found new assignment
			{
				try 
				{
					SendMessageByHuristic(Status_opt.OK);
				} 
				catch (InterruptedException e1) 
				{
					e1.printStackTrace();
				}
			}
		}	
		/* respond to a 'NoGood' message */
		else if (msg.getMessageType()==Status_opt.NoGood) 
		{
			save_conflicts.putAll(((AgentMessage) msg).getConflicts());
			findMaxAgentConflict();
			for (int i = 0; i < p.getAgentsNumber(); i++)
			{
				try 
				{
					if(!cpa.containsKey(i))
					{
						if(i == 0)
						{
							sendMessage(new AgentMessage(Status_opt.InitializeAgent,agent_id,current_value,i));
							Main.writeToConsole("Agent "+i+ " got initialize");
						}
						else
						{
							sendMessage(new AgentMessage(Status_opt.InitializeAgent,agent_id,current_value,i));
							Main.writeToConsole("Agent "+i+ " got initialize");							
						}
					}
				} 
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
				
			}
			
			cpa.remove(agent_id);
			flag = assignNewValue();

			if(!flag)
			{
				cpa.remove(agent_id);
				current_value = -1;
				
				if(agent_id == 0 || max_agent_in_conflict == -1) // Backtrack reached agent 0 and there is no value to assign - No Solution
				{
					message_type = Status_opt.NoSolution;
					p.status = Status_opt.NoSolution;
					Main.writeToConsole("There is no solution to the problem");
					try {stopAgentRunning(Status_opt.NoSolution);} 
					catch (InterruptedException e) {System.out.println("Stopping Agents Exception! (no solution)\n"+e.toString());}
				}
				else
				{
					findMaxAgentConflict();
					if(max_agent_in_conflict != -1)
					{
						msg_new = new AgentMessage(Status_opt.NoGood,agent_id,current_value,max_agent_in_conflict,save_conflicts);
						
						try 
						{
							sendMessage(msg_new);
							Main.writeToConsole("Agent "+agent_id+ " sends NoGood message to:"+max_agent_in_conflict+"agent");
						} 
						catch (InterruptedException e)
						{
							System.out.println("Send Message Exception!\n"+e.toString());
						}
					}
				}
			}
			else
			{
				try 
				{
					SendMessageByHuristic(Status_opt.NoGood);
				} 
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
		else // Intialize Agent
		{	
			IntializeCBJagent();
		}
		
		return message_type;		
	}
	
	private void IntializeCBJagent()
	{
		// TODO Auto-generated method stub
		save_conflicts = new HashMap<Integer, Integer>();
		cpa = new HashMap<Integer, Integer>();
		current_value = -1;
		max_agent_in_conflict = -1;
		max_agent_in_order_conflict = -1;
		
	}

	private void findMaxAgentConflict() 
	{	
		if(!this.save_conflicts.isEmpty())
		{
			HashMap<Integer, Integer> con = new HashMap<Integer, Integer>(this.save_conflicts);
			for(Integer i : con.keySet())
			{
				if(!this.cpa.containsKey(i))
				{
					this.save_conflicts.remove(i);
				}
			}
			
			this.save_conflicts.remove(agent_id);
			
			for(Entry<Integer, Integer> i : save_conflicts.entrySet())
			{
				 int agent_real_index = i.getKey();
				 int agent_index = i.getValue();
				 if( agent_index >= max_agent_in_conflict)
				 {
					 max_agent_in_conflict = agent_real_index;
					 max_agent_in_order_conflict = agent_index;
				 }
			}
		}
		else
		{
			max_agent_in_conflict = -1;
		}
		
	}

	private boolean assignNewValue()
	{ 
		boolean found = false;
		int try_val;
		
		for ( try_val = current_value + 1; (try_val < getDomain().size()) && (!found); try_val++ )
		{	
			int agent_order = 0;
			setCurrentValue(try_val); 
			found = true;
			
			for(Entry<Integer, Integer> i : cpa.entrySet())
			{
				if(!found)
				{
					break;
				}
				else
				{
					int id = i.getKey();
					int val = i.getValue();
	 
					found = p.check(agent_id , current_value, id, val);  // checks consistency of the new assignment
					if(!found)
					{
						addConflictToSet(id,agent_order);
						current_value = -1;	 
					}
					agent_order++;
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
	
	private void addConflictToSet(int id, int agent_order) 
	{
		// TODO Auto-generated method stub
		save_conflicts.put(id, agent_order);
		if((max_agent_in_conflict == -1)||(agent_order >= max_agent_in_order_conflict))
		{
			max_agent_in_conflict = id;
			max_agent_in_order_conflict = agent_order;
		}
		
	}

	private void SendMessageByHuristic(Status_opt message_type) throws InterruptedException 
	{
		if(heuristic == Heuristics_opt.NogoodTriggered)
		{
			if(message_type == Status_opt.OK)
			{
				// Send the cpa to the next agent that is not in the list
				 for (int i = 0; i < p.getAgentsNumber(); i++)
				 {
					 if (!cpa.containsKey(i))
					 {	
						 sendMessage(new AgentMessage(Status_opt.OK,agent_id,current_value,i,cpa));
						 Main.writeToConsole("Agent "+agent_id+" Assigned value: " +current_value+ " sends OK message to: "+i+" agent");
						 break;
					 }
				 }
			}
			// Return ok message to the one that said the previous assign wasnt fit
			if(message_type == Status_opt.NoGood)
			{
				sendMessage(new AgentMessage(Status_opt.OK,agent_id,current_value,previous_sender,cpa));
				Main.writeToConsole("Agent "+agent_id+" Assigned value: " +current_value+ " sends OK message to: "+previous_sender+" agent");
			}
		}
			
		if(heuristic == Heuristics_opt.Random)
		{
			int left_agents = p.getAgentsNumber() - cpa.size();
			Random rnd = new Random();
			int next_agent = rnd.nextInt(left_agents);
			int count_unfound_agents = 0;
			
			for (int i = 0; i < p.getAgentsNumber(); i++)
			{
				if (!cpa.containsKey(i))
				 {
					 if (count_unfound_agents == next_agent)
					 {
						 sendMessage(new AgentMessage(Status_opt.OK,agent_id,current_value,i,cpa));
					 	break;
					 }
					 else
						 count_unfound_agents++;
				}
			}
		}
		
		if(heuristic == Heuristics_opt.MinDomain)
		{
			int max_lbound_conflicts = -1;
			int max_ubound_conflicts = -1;
			int max_agent_lbound = -1;
			int max_agent_ubound = -1;
			int lbound_conflicts_num;
			int ubound_conflic_num;
			boolean diff = false;
			
			for(int i = 0; i< p.getAgentsNumber(); i++)
			{
				if(!this.cpa.containsKey(i))
				{
					// Calculate the number of conflicts the new assignment have (L_BOUND)
					// Calculate the sum of the former U_BOUND and the number of conflicts
					if(agent_id < i)
					{
						p.constraintedAgents[agent_id][i].constraintNumberForValue(this.current_value, 0); 
					}
					else
					{
						p.constraintedAgents[i][agent_id].constraintNumberForValue(this.current_value, 1);
					}
					
					lbound_conflicts_num = ((CBJ)p.agents[i]).l_bound_con;
					ubound_conflic_num = ((CBJ)p.agents[i]).u_bouns_con;
					
					if (max_lbound_conflicts < lbound_conflicts_num)
					{
						max_agent_lbound = i;
						max_lbound_conflicts = lbound_conflicts_num;
						diff = true;
					}
					else if(max_lbound_conflicts < lbound_conflicts_num)
					{
						diff = false;
					}
					
					if(max_ubound_conflicts < ubound_conflic_num)
					{
						max_ubound_conflicts = ubound_conflic_num;
						max_agent_ubound = i;
					}
				}
			}
			
			// Send by lower bound
			if(diff || max_lbound_conflicts == this.domain.size())
			{
				sendMessage(new AgentMessage(Status_opt.OK,agent_id,current_value,max_agent_lbound,cpa));
				Main.writeToConsole("Agent "+agent_id+" Assigned value: " +current_value+ " sends OK message to: "+max_agent_lbound+" agent");
			}
			// Send by upper bound
			else
			{
				sendMessage(new AgentMessage(Status_opt.OK,agent_id,current_value,max_agent_ubound,cpa));
				Main.writeToConsole("Agent "+agent_id+" Assigned value: " +current_value+ " sends OK message to: "+max_agent_ubound+" agent");
				
			}
			
		}	
	}
}
