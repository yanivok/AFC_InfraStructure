import java.util.ArrayList;
import java.util.Random;


public class Problem_generator 
{	
	private int agents_number;
	private double p1;
	private double p2;
	private int k;
	protected Agent[] agents;
	protected Constraint[][] constraintedAgents; 
	public Status_opt status = Status_opt.undefined;
	private int seed;
	
	public Problem_generator(int n, int k, double p1, double p2, Agent[] agents, Constraint[][] AgentConstraints, int s)
	{
		if (agents != null)
		{
			this.agents = agents; 
		}
		else
			this.agents = new Agent[n];
		
		if (AgentConstraints != null)
		{
			this.constraintedAgents = AgentConstraints;
		}
		else
			this.constraintedAgents = new Constraint[n][n];
	
		this.k = k;
		this.p1 = p1;
		this.p2 = p2;
		this.agents_number = n;
		this.seed=s;
		
		initializeAgents();
		initializeRandomConstraints();
	}
	
	private void initializeAgents() 
	{
		// Create agent Domain
		
		try 
		{	
			for (int i = 0; i < agents.length; i++)
			{
				ArrayList<Integer> v = new ArrayList<Integer>();
				
				for (int j = 0 ; j<k ; j++)
				{
					v.add(j);
				}
				
				if(Main.SBTrun)
				{
				agents[i] = new SBT(this, v, i);
				}
				else if(Main.AFCrun)
				{
					agents[i] = new AFC(this, v, i);
				}
				else
				{
					agents[i] = new CBJ(this, v, i,Main.curr_opt);
				}
			}
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void initializeRandomConstraints() 
	{

		Random r = new Random(this.seed);
		for (int i = 0; i < constraintedAgents.length; i++)
		{
			for (int j = i+1; j < constraintedAgents[i].length; j++)
			{
				Constraint constraint = new Constraint(agents[i], agents[j], p1, p2, r);
				constraintedAgents[i][j] = constraint; 
			}
		}
	}
	
	public Agent getAgent(int index)
	{
		return agents[index];
	}
	
	public boolean check(int currentAgent, int currentVal, int agent2, int val2)
	{
		agents[currentAgent].incNcccsCounter();
		
		if (currentAgent < agent2)
		{
			return constraintedAgents[currentAgent][agent2].constraintsCompatible(currentVal, val2);
		}
		else if(currentAgent == agent2)
		{
			return false;
		}
		else
		{
			return constraintedAgents[agent2][currentAgent].constraintsCompatible(val2, currentVal);	
		}
	}
	
	public int getAgentsNumber()
	{
		return this.agents_number;
	}
	
	public long getMaxNcccsCounter()
	{
		long MaxNccsCounter =0;
		for(int i=0; i<agents_number; i++)
		{
			MaxNccsCounter = Math.max(MaxNccsCounter, agents[i].getNcccsCounter());
		}
		return MaxNccsCounter;
	}
	

	
}
