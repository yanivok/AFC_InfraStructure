
public class Agent_Runnable implements Runnable 
{
	private Agent agent;
	private Status_opt status;

	public Agent_Runnable(Agent a, Mailing_System m) throws InterruptedException 
	{
		this.agent = a;
		this.status=Status_opt.undefined;
		this.agent.setMail(m);
		this.agent.initialAssign();
	}

	@Override
	public void run() 
	{
		Message msg = null;
		
		while(status==Status_opt.undefined)
		{
			try
			{
				msg=agent.recieveMassage(agent.getAgentId());
				if (msg != null)
				{
				if(msg.getMessageType()==Status_opt.SolutionFound || 
				   msg.getMessageType()==Status_opt.NoSolution )
				{// End Running
					if(msg.getMessageType()==Status_opt.SolutionFound )
					{
						status=Status_opt.SolutionFound;
					}
					else
					{
						status=Status_opt.NoSolution;
					}
				}
				
				else
					{
							agent.setNcccsCounter(msg.getmsgNcccsCounter());
							status = agent.handleMessage(msg);
					}
						
		
				}
				else
					Main.writeToConsole("null Message");
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}

		}
		
	}
	
	public Status_opt getStatus()
	{
		return this.status;
	}

}
