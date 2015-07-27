import java.io.FileNotFoundException;
import java.util.ArrayList;
import org.jfree.ui.RefineryUtilities;


public class Main 
{
	public static boolean StartPress = false;
	public static boolean SBTrun = false;
	public static boolean AFCrun = false;
	public static boolean CBJrun = false;
	public static boolean ComparAlgo = false;
	public static Heuristics_opt curr_opt = null;
	public static boolean vary_p2 = false;
	
	public static void main(String[] args) throws InterruptedException, FileNotFoundException 
	{
		CreatGraph graph;
		GraphicsSurface gs = new GraphicsSurface(300, 300);
		gs.run();
		
		long[] problemMessagesCounters1 = null;
		long[] problemNccsCounters1= null;
		
		long[] problemMessagesCounters2= null;
		long[] problemNccsCounters2= null;
		
		long[] problemMessagesCounters3= null;
		long[] problemNccsCounters3= null;
		
		long[] problemMessagesCounters_vary= new long[9];
		long[] problemNccsCounters_vary= new long [9];
		int vary_iterator = 0;
		double thightness_p2 = 0.1;
		while(gs.stopRunning == false)
		{
			if(StartPress==false)
			{
				Thread.sleep(1000);
			}
			else
			{
				writeToConsole("Start app");
				int problemsNumber = gs.getExperimentNumber();
				
				int agent_number = gs.getAgentNumber();
				int k_domain = gs.getDomain();
				double density_p1 = gs.getP1(); // between 0.2 to 0.8 , two experiments
				
				if(!vary_p2)
				{
					thightness_p2 = gs.getP2();// Probability to conflict
				}
				 
				curr_opt = gs.getHeuristic();
				long[] problemMessagesCounters = new long[problemsNumber];
				long[] problemNccsCounters = new long[problemsNumber];
				Status_opt[] problemStatus = new Status_opt[problemsNumber];
				for (int j=0 ; j< problemsNumber; j++)
				{
					writeToConsole("Problem num:"+j);
					// Define mailing system
					writeToConsole("Define Mail");
					Mailing_System mail = new Mailing_System(agent_number);
					writeToConsole("End Define Mail");
					
					// Define Thread vector
					ArrayList<Thread> threads = new ArrayList<Thread>(0);
					
					// TODO: Create problem 
					writeToConsole("Define Problem");
					Problem_generator p = new Problem_generator(agent_number,k_domain,density_p1,thightness_p2,null,null,j);
					writeToConsole("End Define Problem");
					
					// Add threads to the vector
					writeToConsole("Create Threads");
					for (int i = 0; i < agent_number; i++) 
					{
						  Thread t = new Thread(new Agent_Runnable(p.getAgent(i),mail));
						  t.start();
						  threads.add(t);
					}
					
					// Join the treads ; wait until they finished
					for (Thread t : threads) 
					{
						  t.join();
					}
					
					//Print Result
					writeToConsole("Total messages: "+ mail.getMessageCounter());
					writeToConsole("Max Nccs: "+p.getMaxNcccsCounter());
					problemMessagesCounters[j] =  mail.getMessageCounter();
					problemNccsCounters[j] = p.getMaxNcccsCounter();
					problemStatus[j] = p.status;
				}
				
				long messages_average= 0;
				long nccs_averge =0;
				for (int i=0; i< problemsNumber; i++)
				{
					messages_average += problemMessagesCounters[i];
					nccs_averge += problemNccsCounters[i];
				}
				writeToConsole("Messages Aaverage: "+messages_average/problemsNumber);
				writeToConsole("Ncccs Average: "+nccs_averge/problemsNumber);
				writeToConsole("Finish run");
				
				if(ComparAlgo)
				{
					if(SBTrun)
					{
						problemMessagesCounters1 = problemMessagesCounters;
						problemNccsCounters1 = problemNccsCounters;
						Main.StartPress = true;
						Main.SBTrun = false;
						Main.AFCrun = false;
						Main.CBJrun = true;
					}
					else if(CBJrun)
					{
						problemMessagesCounters2 = problemMessagesCounters;
						problemNccsCounters2 = problemNccsCounters;

						Main.StartPress = true;
						Main.SBTrun = false;
						Main.AFCrun = true;
						Main.CBJrun = false;
					}
					else
					{
						problemMessagesCounters3 = problemMessagesCounters;
						problemNccsCounters3 = problemNccsCounters;

						StartPress=false;
						
						Main.SBTrun = false;
						Main.AFCrun = false;
						Main.CBJrun = false;
						graph = new CreatGraph(problemNccsCounters1, problemNccsCounters2,problemNccsCounters3, "Ncccs");
						graph.setSize(810, 600);
						graph.setLocation(600, 0);
						//RefineryUtilities.centerFrameOnScreen(graph);
						graph.setVisible(true);
						
						graph = new CreatGraph(problemMessagesCounters1, problemMessagesCounters2,problemMessagesCounters3, "MessageTransfered");
						graph.setSize(800,600);
						graph.setLocation(0, 0);
						//RefineryUtilities.centerFrameOnScreen(graph);
						graph.setVisible(true);
						ComparAlgo = false;
					}
				}
				else if(vary_p2)
				{
					if(thightness_p2 > 0.9)
					{
						StartPress=false;
						vary_iterator = 0;
						thightness_p2 = 0.1;
						graph = new CreatGraph(problemNccsCounters_vary,problemMessagesCounters_vary, "Vary Tightness");
						graph.setSize(900, 700);
						RefineryUtilities.centerFrameOnScreen(graph);
						graph.setVisible(true);
						// GRAPH
					}
					else
					{
						problemMessagesCounters_vary[vary_iterator] = messages_average/problemsNumber;
						problemNccsCounters_vary[vary_iterator] = nccs_averge/problemsNumber;
						vary_iterator++;
						thightness_p2 += 0.1;
						//Run Again
					}
					
				}
				else
				{
					StartPress=false;
					graph = new CreatGraph(problemNccsCounters, problemMessagesCounters,problemStatus, "Graph");
					graph.setSize(900, 700);
					RefineryUtilities.centerFrameOnScreen(graph);
					graph.setVisible(true);
				}
			}
		}
	}
	   public static void writeToConsole(String text)
	   {
		   System.out.println(text);
	   }
	

}
