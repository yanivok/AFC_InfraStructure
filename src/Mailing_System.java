import java.util.concurrent.LinkedBlockingQueue;

/*---------------------------------------------
			Mailing_System
			--------------
In charge of message transformation between agents. 
(Foreach agent there is a queue of messeges).

Contain Messages counter
  ---------------------------------------------*/

public class Mailing_System 
{
	private  LinkedBlockingQueue<Message>[] agents_Messages_Queues; // FIFO, thread safe structure
	private int messages_counter;

	// Constructor 
	@SuppressWarnings("unchecked")
	public Mailing_System(int agent_number) 
	{
		// TODO Auto-generated constructor stub

		this.agents_Messages_Queues = new LinkedBlockingQueue[agent_number];
		
		this.messages_counter = 0;
		
		for(int i=0 ; i<agents_Messages_Queues.length ; i++)
		{
			agents_Messages_Queues[i]=new LinkedBlockingQueue<Message>();
		}
		
	}
	
	// TODO: Add setters & getters
	
	// Receive message
	public Message reciveMessage(int receiver_id) throws InterruptedException
	{
		if(receiver_id != -1) 
		{
			incMessagesCounter();
			Message message=agents_Messages_Queues[receiver_id].take();

			return message;
			
		}
		return null;
		
	}
	
	
	// Send message
	public void sendMessage (Message mes) throws InterruptedException
	{
		Message m;
		if(mes instanceof AgentAFCMessage)
		{
			m = new AgentAFCMessage(mes.getMessageType(),mes.getSender(),mes.getValue(),mes.getReciver(),((AgentAFCMessage)mes).getSenderAssign(),((AgentAFCMessage)mes).getInconsistentSet());
		}
		else if(mes instanceof AgentMessage)
		{
			if(mes.getMessageType() != Status_opt.InitializeAgent)
				m = new AgentMessage(mes.getMessageType(), mes.getSender(), mes.getValue(), mes.getReciver(),((AgentMessage)mes).getSenderAssign());
			else
			{
				m = new AgentMessage(mes.getMessageType(), mes.getSender(), mes.getValue(), mes.getReciver());
			}
		}
		else
		{
			m = new Message(mes.getMessageType(), mes.getSender(), mes.getValue(), mes.getReciver());
			m.setmsgNcccsCounter(mes.getmsgNcccsCounter());
		}
		agents_Messages_Queues[m.getReciver()].put(m); 
		
	}
	
	public void stopMailingSystemRunning(int initiatorAgent, Status_opt status) throws InterruptedException 
	{
		Message msg = new Message(status,-1,-1,-1);
		for(int i=0; i < agents_Messages_Queues.length ; i++)
		{
				agents_Messages_Queues[i].clear(); 
				msg.setReciver(i);
				sendMessage(msg);
		}
	}
	
	public void incMessagesCounter()
	{
		this.messages_counter++;
	}
	
	public long getMessageCounter()
	{
		return this.messages_counter;
	}
	
	


}
