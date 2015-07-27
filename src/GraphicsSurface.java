import java.awt.Checkbox;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class GraphicsSurface extends JFrame implements ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	JComboBox<String> chooseAgentNum = null;
    JComboBox<String> chooseDomain = null;
    JComboBox<String> chooseP1 = null;
    JComboBox<String> chooseP2 = null;
    JComboBox<String> ChooseHeuristic = null;
    JComboBox<String> ChooseAalgo1 = null;
    JComboBox<String> ChooseAalgo2 = null;
    
    JTextField chooseExperimentNumber = new JTextField("1");
    JFrame frame;
	public boolean stopRunning = false;
	Checkbox cb_Vary = new Checkbox("Vary Tightness");
	
	public GraphicsSurface(int height, int width)
	{
		frame = new JFrame("AFC Mini-Project");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}
	
	public void run()
	{

        JPanel contentPane = new JPanel();
        contentPane.setOpaque(true);
        contentPane.setBackground(Color.WHITE);
        contentPane.setLayout(null);
        
        String[] numberOfAgents = {"2","3","4","5","6","7","8","9","10"};
	    String[] domainValue = {"2","3","4","5","6","7","8","9","10"};
	    String[] density_p1 = {"0.2","0.3","0.4","0.5","0.6","0.7","0.8"};
	    String[] thightness_p2 = {"0.1","0.2","0.3","0.4","0.5","0.6","0.7","0.8","0.9"};
	    String[] heuristicStrings = {"Min Domain","NoGood Triggered","Random"};
	   
	    chooseAgentNum = new JComboBox<String>(numberOfAgents);
	    chooseAgentNum.setSelectedIndex(8);
	    chooseDomain = new JComboBox<String>(domainValue);
	    chooseDomain.setSelectedIndex(8);
	    chooseP1 = new JComboBox<String>(density_p1);
	    chooseP1.setSelectedIndex(3);
	    chooseP2 = new JComboBox<String>(thightness_p2);
	    chooseP2.setSelectedIndex(4);
	    ChooseHeuristic = new JComboBox<String>(heuristicStrings);
	    ChooseHeuristic.setSelectedIndex(2);
	    chooseExperimentNumber = new JTextField("30");

        JLabel label_agentNum = new JLabel("Select Agent Number:");
        JLabel label_experNum = new JLabel("Select Experiment number:");
        JLabel label_p1Num = new JLabel("Select p1 value:");
        JLabel label_p2Num = new JLabel("Select p2 value:");
        JLabel label_domain = new JLabel("Select Domain:");
        JLabel label_heuristic = new JLabel("Select Heuristic:");
        
        label_agentNum.setSize(200, 40);
        label_agentNum.setLocation(0, 0);
        
        chooseAgentNum.setSize(50, 20);
        chooseAgentNum.setLocation(170, 10);
        
        label_experNum.setSize(200, 40);
        label_experNum.setLocation(0, 30);
        
        chooseExperimentNumber.setSize(50, 20);
        chooseExperimentNumber.setLocation(170, 40);
        
        label_p1Num.setSize(200, 40);
        label_p1Num.setLocation(0, 60);
        
        chooseP1.setSize(50, 20);
        chooseP1.setLocation(170, 70);
        
        label_p2Num.setSize(200, 40);
        label_p2Num.setLocation(0, 90);
        
        chooseP2.setSize(50, 20);
        chooseP2.setLocation(170, 100);
        
        label_domain.setSize(200, 40);
        label_domain.setLocation(0, 120);
        
        chooseDomain.setSize(50, 20);
        chooseDomain.setLocation(170, 130);
         
        
        JButton sbt_start = new JButton("Run SBT");
        sbt_start.setSize(140, 20);
        sbt_start.setLocation(30, 160);        
        sbt_start.addActionListener(this);
        
        JButton afc_start = new JButton("Run AFC");
        afc_start.setSize(140, 20);
        afc_start.setLocation(30, 190);        
        afc_start.addActionListener(this);
        
        label_heuristic.setSize(200, 40);
        label_heuristic.setLocation(0, 210);
        
        ChooseHeuristic.setSize(110, 20);
        ChooseHeuristic.setLocation(120, 220);
        
        JButton cbj_start = new JButton("Run CBJ");
        cbj_start.setSize(140, 20);
        cbj_start.setLocation(30, 250);        
        cbj_start.addActionListener(this);
        
        JButton b_comp = new JButton("Compare all");
        b_comp.setSize(140, 20);
        b_comp.setLocation(30, 280);        
        b_comp.addActionListener(this);
        
        
        cb_Vary.setSize(140, 20);
        cb_Vary.setLocation(50, 310);        
        
        JButton b_end = new JButton("Close");
        b_end.setSize(140, 20);
        b_end.setLocation(30, 340);        
        b_end.addActionListener(this);
        
        contentPane.add(label_agentNum);
        contentPane.add(label_experNum);
        contentPane.add(label_p1Num);
        contentPane.add(label_p2Num);
        contentPane.add(label_domain);
        contentPane.add(label_heuristic);
        contentPane.add(chooseAgentNum);
        contentPane.add(chooseP1);
        contentPane.add(chooseP2);
        contentPane.add(chooseDomain);
        contentPane.add(chooseExperimentNumber);
        contentPane.add(ChooseHeuristic);
        contentPane.add(sbt_start);
        contentPane.add(afc_start);
        contentPane.add(cbj_start);
        contentPane.add(b_comp);
        contentPane.add(cb_Vary);
        contentPane.add(b_end);
        
        frame.setContentPane(contentPane);
        frame.setSize(250, 405);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);

	}
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if(this.cb_Vary.getState())
		{
			Main.vary_p2 = true;
		}
		else
		{
			Main.vary_p2 = false;
		}
		
		if (e.getActionCommand() == "Run SBT")
		{
			Main.ComparAlgo = false;
			Main.StartPress = true;
			Main.SBTrun = true;
			Main.AFCrun = false;
			Main.CBJrun = false;
		}
		else if (e.getActionCommand() == "Run AFC")
		{
			Main.ComparAlgo = false;
			Main.StartPress = true;
			Main.SBTrun = false;
			Main.AFCrun = true;
			Main.CBJrun = false;
			
		}
		else if(e.getActionCommand() == "Run CBJ")
		{
			Main.ComparAlgo = false;
			Main.StartPress = true;
			Main.SBTrun = false;
			Main.AFCrun = false;
			Main.CBJrun = true;
		}
		else if(e.getActionCommand() == "Compare all")
		{
			Main.StartPress = true;
			Main.ComparAlgo = true;
			Main.SBTrun = true;
			Main.AFCrun = false;
			Main.CBJrun = false;
		}
		else
		{
			this.stopRunning = true;
			frame.setVisible(false);
			frame.dispose();
		}
	}
	
	public float getP1()
	{
		return Float.parseFloat((String) chooseP1.getSelectedItem());
	}
	
	public float getP2()
	{
		return Float.parseFloat((String) chooseP2.getSelectedItem());
	}
	
	public int getDomain()
	{
		return Integer.parseInt((String) chooseDomain.getSelectedItem());
		
	}
	
	public int getExperimentNumber()
	{
		return Integer.parseInt((String) chooseExperimentNumber.getText());
		
	}
	
	public int getAgentNumber()
	{
		return Integer.parseInt((String) chooseAgentNum.getSelectedItem());
		
	}
	
	public Heuristics_opt getHeuristic()
	{
		Heuristics_opt opt;
		
		if(ChooseHeuristic.getSelectedIndex() == 0)
		{
			opt = Heuristics_opt.MinDomain;
		}
		else if(ChooseHeuristic.getSelectedIndex() == 1)
		{
			opt = Heuristics_opt.NogoodTriggered;
		}
		else
		{
			opt = Heuristics_opt.Random;
		}
		
		return opt;
		
	}
}
