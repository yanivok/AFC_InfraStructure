import java.util.Random;


public class Constraint {

		// TODO Auto-generated constructor stub
		private boolean thereIsConstraint;
		private boolean domainsConstraints[][] = null; //conflicted values
		protected Agent agent1;
		protected Agent agent2;

		// Constructor
		public Constraint(Agent agent1, Agent agent2, double p1,double p2, Random r) 
		{
			this.agent1 = agent1;
			this.agent2 = agent2;
			thereIsConstraint = r.nextDouble() < p1; 

			if (thereIsConstraint) 
			{
				initializeValuesConstraints(p2, r);
			}
		}

		// Foreach constraint check the values that conflict
		private void initializeValuesConstraints(double p2, Random r) 
		{
			domainsConstraints = new boolean[agent1.getDomain().size()][agent2.getDomain().size()];
			boolean thereIsConflict;
			
			for (int i = 0; i < domainsConstraints.length; i++) {
				// for sbt j=i+1
				for (int j = 0; j < domainsConstraints[i].length; j++) 
				{
					thereIsConflict = r.nextDouble() > p2; 
					domainsConstraints[i][j] = thereIsConflict;
				}
			}
		}


		// Check if the agents values compatible by the constraints 
		public boolean constraintsCompatible(int c1,int c2) 
		{
			if (!this.thereIsConstraint) 
			{
				return true;
			}
			else
			{
				return domainsConstraints[c1][c2];

			}
		}
		
		public void constraintNumberForValue(int val, int check_agent)
		{
			int counter = 0;
			
			if(thereIsConstraint)
			{
				for(int i=0; i<agent2.getDomain().size(); i++)
				{
					if(this.domainsConstraints[val][i] == false)
					{
						counter++;
					}
				}
			}
			
			if(check_agent == 0)
			{
				((CBJ) this.agent2).setLbound(counter);
				((CBJ) this.agent2).setUbound(counter);
			}
			else
			{
				((CBJ) this.agent1).setLbound(counter);
				((CBJ) this.agent1).setUbound(counter);
			}
			
		}
		
}
