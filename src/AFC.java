import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;


public class AFC extends Agent{
	
	private Status_opt message_type = Status_opt.undefined;      // indicates the result of the algorithm
	private HashMap<Integer,Integer> inconsistent_set = new HashMap<Integer, Integer>();
	

	public AFC(Problem_generator p, ArrayList<Integer> d, int id) {
		super(p, d, id);
	}

	/* The first agent performs an assignment to its variable
	 * and sends the assignment on a message to the next agent. */
	public void initialAssign(){
		if (agent_id == 0){
			setCurrentValue(0);
			HashMap<Integer , Integer> cpa = new HashMap<Integer , Integer>();
			cpa.put(agent_id, current_value);			
			AgentAFCMessage msg = new AgentAFCMessage(Status_opt.OK,agent_id, current_value, agent_id+1 , cpa , inconsistent_set);
			send_fc_CPA(cpa,inconsistent_set);
			try {
				sendMessage(msg);
				Main.writeToConsole("Agent "+agent_id+" Assigned value: " +this.getValue()+ " sending OK message to the next agent");}
			catch (InterruptedException e) {System.out.println("Send Message Exception!\n"+e.toString());}
		}
	}
	
	public Status_opt handleMessage(Message msg){
		Status_opt msg_recieved = msg.getMessageType();
		
		switch (msg_recieved)
		{
			case OK:
				HashMap<Integer,Integer> cpa = ((AgentAFCMessage)msg).getSenderAssign();
				if (inconsistent_set.isEmpty()){					// no inconsistencies - send 'ok' message forward 
					handle_OK(cpa,inconsistent_set);
				}
				else{													// else - send 'NotOK' forward
					AgentAFCMessage msg_new = new AgentAFCMessage(Status_opt.NotOK,agent_id,current_value,agent_id-1 , cpa , inconsistent_set);
					try {
						sendMessage(msg_new);
						Main.writeToConsole("Agent "+agent_id+ " sending NotOK message to agent "+(agent_id-1));} 
					catch (InterruptedException e) {System.out.println("Send Message Exception!\n"+e.toString());}
				}
				break;
				
			/* Forward Checking case - clean domain from conflicted values */	
			case FC_CPA:
				setDomain(domain_original);
				HashMap<Integer,Integer> fc_cpa = ((AgentAFCMessage)msg).getSenderAssign();
				inconsistent_set = ((AgentAFCMessage)msg).getInconsistentSet();
				updateDomain(fc_cpa,inconsistent_set);
				if (!inconsistent_set.isEmpty()){
					Main.writeToConsole("Agent "+agent_id+ " sending Inconsistency message **starting from agent "+fc_cpa.size());
					for (int i=fc_cpa.size(); i<p.getAgentsNumber(); i++){
						AgentAFCMessage msg_new = new AgentAFCMessage(Status_opt.Inconsistency,agent_id,current_value,i,fc_cpa,inconsistent_set);
						try {
							sendMessage(msg_new);} 
						catch (InterruptedException e) {System.out.println("Send Message Exception!\n"+e.toString());}
					}
				}
				
				break;
				
			case Inconsistency:
				inconsistent_set = ((AgentAFCMessage)msg).getInconsistentSet();
				break;
							
			case NotOK:
				HashMap<Integer,Integer> cpa1 = ((AgentAFCMessage)msg).getSenderAssign();
				inconsistent_set = ((AgentAFCMessage)msg).getInconsistentSet();
				
				if (inconsistent_set.isEmpty()){							// no inconsistencies - send 'NotOK' message back 
					handle_NotOK(cpa1,inconsistent_set);
				}
				else{
					if (isRelevant(cpa1,inconsistent_set)){									// inconsistencies are a subset of the cpa
						if (inconsistent_set.containsKey(agent_id)){		// this agent has an assignment in inconsistencies
							inconsistent_set.remove(agent_id);
							if (inconsistent_set.isEmpty()){
								handle_NotOK(cpa1,inconsistent_set);
							}
							else{
								cpa1.remove(agent_id);
								setCurrentValue(-1);
								AgentAFCMessage msg_new = new AgentAFCMessage(Status_opt.NotOK,agent_id,current_value,agent_id-1,cpa1,inconsistent_set);
								try {
									sendMessage(msg_new);
									Main.writeToConsole("Agent "+agent_id+ " sends NotOK message to agent "+(agent_id-1));} 
								catch (InterruptedException e) {System.out.println("Send Message Exception!\n"+e.toString());}
								inconsistent_set = new HashMap<Integer, Integer>();
							}
						}
						else{												// this agent has no assignment in inconsistencies
							setCurrentValue(-1);
							cpa1.remove(agent_id);
							AgentAFCMessage msg_new = new AgentAFCMessage(Status_opt.NotOK,agent_id,current_value,agent_id-1,cpa1,inconsistent_set);
							try {
								sendMessage(msg_new);
								Main.writeToConsole("Agent "+agent_id+ " sends NotOK message to agent "+(agent_id-1));} 
							catch (InterruptedException e) {System.out.println("Send Message Exception!\n"+e.toString());}
							inconsistent_set = new HashMap<Integer, Integer>();
						}
					}
					else{												// inconsistencies irrelevant
						inconsistent_set = new HashMap<Integer, Integer>();
						handle_NotOK(cpa1,inconsistent_set);
					}
				}
				
				break;
				
			default:
				break;
		}
		
		return message_type;
	}
	
	
	/* Checks whether current inconsistency still a part of the cpa */
	public boolean isRelevant(HashMap<Integer, Integer> cpa, HashMap<Integer, Integer> inconsistent_set){
		boolean relevant = true;
		HashMap<Integer,Integer> assignments_copy = new HashMap<Integer, Integer>(cpa);
		
		Iterator<Entry<Integer,Integer>> iterator = inconsistent_set.entrySet().iterator();
		
		while (iterator.hasNext() && relevant){ 	
			Entry<Integer,Integer> entry = iterator.next();
			int id = entry.getKey();
			int val = entry.getValue();
			
			relevant = ( assignments_copy.containsKey(id) && assignments_copy.get(id)==val );
		}	
		return relevant;
	}

	/* Tries to find a new consistent assignment */
	private boolean assignNewValue(HashMap<Integer,Integer> cpa){ 
		boolean found = false;
		int newValue=-1;
		int i=getDomain().indexOf(current_value);
		i++;
		
		while ( (i<getDomain().size()) && (!found) )
		{
			newValue = getDomain().get(i);
			setCurrentValue(newValue); 
			found = true;
			Iterator<Entry<Integer,Integer>> iterator = cpa.entrySet().iterator();
			
			while (iterator.hasNext() && found){ 	// Checks Consistency with other assignments
				Entry<Integer,Integer> entry = iterator.next();
				int id = entry.getKey();
				int val = entry.getValue();
 
				found = p.check(agent_id , current_value, id, val);  // checks consistency of the new assignment
				if(!found)
					current_value = -1;	 
			}	
			i++;
		}
		
		if(found){
			cpa.put(agent_id, newValue);
		}
					
		return found;
	}
	
	
	private void handle_OK(HashMap<Integer,Integer> cpa, HashMap<Integer, Integer> inconsistent_set){
		AgentAFCMessage msg_new; 		
		boolean flag = assignNewValue(cpa); 							// tries to assign consistent value
		if (!flag) // Couldn't find assignment - Initiates BackTracking
		{
			msg_new = new AgentAFCMessage(Status_opt.NotOK,agent_id,current_value,agent_id-1 , cpa , inconsistent_set);
			try {
				sendMessage(msg_new);
				Main.writeToConsole("Agent "+agent_id+ " sends NotOK message to agent "+(agent_id-1));} 
			catch (InterruptedException e) {System.out.println("Send Message Exception!\n"+e.toString());}
		}
		
		else if (cpa.size() == (p.getAgentsNumber())) // Found new assignment (last agent) - Solution found 
		{	
			
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
			
			message_type = Status_opt.SolutionFound;
			p.status = Status_opt.SolutionFound;
			Main.writeToConsole("Agent "+agent_id+" Assigned value " +this.getValue());
			Main.writeToConsole("Solution Found: " +cpa.toString());
			try {stopAgentRunning(Status_opt.SolutionFound);} 		// stops all runnables
			catch (InterruptedException e) {System.out.println("Stopping Agents Exception! (solution found)\n"+e.toString());}
		}
		else // Found new assignment
		{
			send_fc_CPA(cpa,inconsistent_set);	
			msg_new = new AgentAFCMessage(Status_opt.OK,agent_id, current_value, agent_id+1,cpa,inconsistent_set);
			try {
				sendMessage(msg_new);
				Main.writeToConsole("Agent "+agent_id+" Assigned value: " +this.getValue()+ " sends OK message to the next agent. CPA: "+cpa.toString());} 
			catch (InterruptedException e) {System.out.println("Send Message Exception!\n"+e.toString());}
			
		}
	}

	private void handle_NotOK(HashMap<Integer, Integer> cpa, HashMap<Integer, Integer> inconsistent_set){
		cpa.remove(agent_id); 
		boolean flag = assignNewValue(cpa);
		if(!flag) 					// Couldn't find assignment
		{
			current_value = -1;
			cpa.remove(agent_id);
			if (getDomain().isEmpty())
				setDomain(domain_original);
			if(agent_id == 0) 		// Backtrack reached agent 0 and there is no value to assign - No Solution
			{
				message_type = Status_opt.NoSolution;
				p.status = Status_opt.NoSolution;
				Main.writeToConsole("There is no solution to the problem");
				try {stopAgentRunning(Status_opt.NoSolution);} 
				catch (InterruptedException e) {System.out.println("Stopping Agents Exception! (no solution)\n"+e.toString());}
			}
			else 					// Backtrack for other agents
			{
				AgentAFCMessage msg_new = new AgentAFCMessage(Status_opt.NotOK,agent_id,current_value,agent_id-1,cpa,inconsistent_set);
				try {
					sendMessage(msg_new);
					Main.writeToConsole("Agent "+agent_id+ " sends NotOK message to agent "+(agent_id-1));} 
				catch (InterruptedException e) {System.out.println("Send Message Exception!\n"+e.toString());}
			}
					
		}
		
		else 						// Found new assignment
		{	
			send_fc_CPA(cpa,inconsistent_set);
			AgentAFCMessage msg_new = new AgentAFCMessage(Status_opt.OK,agent_id, current_value, agent_id+1,cpa,inconsistent_set);
			try {
				sendMessage(msg_new);
				Main.writeToConsole("Agent "+agent_id+" Assigned value: " +getValue()+ " sending OK message to the next agent. CPA: "+cpa.toString());
				} 
			catch (InterruptedException e) {System.out.println("Send Message Exception!\n"+e.toString());}
			
		}
		
	}
	

	/* Sends a copy of the CPA to the rest of the agents for Forward Checking */
	private void send_fc_CPA(HashMap<Integer,Integer> cpa, HashMap<Integer, Integer> inconsistent_set){
		Main.writeToConsole("Agent "+agent_id+" Sending FC_CPA to all agents with no assignments on the CPA");
		for (int i=agent_id+2; i<p.getAgentsNumber(); i++){
			AgentAFCMessage msg = new AgentAFCMessage(Status_opt.FC_CPA,agent_id, current_value, i, cpa , inconsistent_set);
				try {
					sendMessage(msg);
					} 
				catch (InterruptedException e) {System.out.println("Send Message Exception!\n"+e.toString());}
		}
	}
	
	
	/* clears my domain from conflicted values */
	private void updateDomain(HashMap<Integer,Integer> fc_cpa, HashMap<Integer, Integer> inconsistent_set){
		boolean flag;
		
		A:for ( int i=0; i < getDomain().size() ; i++ )
		{	
			if ( i != getDomain().indexOf(current_value) ) 				// check all other domain values 
			{
				int value_i = getDomain().get(i);
				Iterator<Entry<Integer,Integer>> iterator = fc_cpa.entrySet().iterator();
				B:while (iterator.hasNext())							// Checks Consistency with other assignments
				{ 	
					Entry<Integer,Integer> entry = iterator.next();
					int id = entry.getKey();
					int val = entry.getValue();
					if (id != agent_id){
						flag = p.check(agent_id , value_i, id, val);  // checks consistency of the new assignment
						if (id != fc_cpa.size()-1)
							this.decNcccsCounter();
						
						if(!flag){ 									  // eliminate value_i due to inconsistency
							getDomain().remove(i);	
							i--;
							if (getDomain().isEmpty()){				  // no good values left in domain - add inconsistency and reset the domain
								int inconsistent_agent = fc_cpa.size()-1;
								int inconsistent_val = fc_cpa.get(inconsistent_agent);
								inconsistent_set.put(inconsistent_agent,inconsistent_val);
								Main.writeToConsole("agent "+agent_id+" added to inconsistencies- (agent:"+id+",value:"+val+")");
								setDomain(domain_original);			 //re-create domain
								current_value = -1;
								break A;
							}
							break B;
						}
					}
				}	
			}
		}
	}

}
