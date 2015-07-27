import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;


public class CreatGraph  extends ApplicationFrame implements ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ChartPanel chartPanel ;
	
	public CreatGraph(long[] problemNccsCounters, long[] problemMessagesCounters,Status_opt[] status, String title) 
	{
		super(title);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		long a_m_t =0;
		long n_sol =0;
		long a_nccs=0;
		for(int i=0; i<problemMessagesCounters.length; i++)
		{
			a_m_t+=problemMessagesCounters[i];
			if(status[i] == Status_opt.SolutionFound)
				n_sol+= 1;
			a_nccs += problemNccsCounters[i];
			
		}
		a_m_t/=problemMessagesCounters.length;
		a_nccs/=problemMessagesCounters.length;
		
		String str = "Number of solution:"+n_sol+"       Avarage Nccs:"+a_nccs+"       Avarage Messages transfer:"+a_m_t;
        
		XYDataset dataset = createDataset(problemNccsCounters, problemMessagesCounters, status);
        JFreeChart chart = createChart(dataset,"");
        chartPanel = new ChartPanel(chart);
        this.add(chartPanel,BorderLayout.CENTER);
        this.add(new JLabel(str),BorderLayout.NORTH);
        
        JButton b_end = new JButton("Close");      
        b_end.addActionListener(this);
        JPanel control = new JPanel();
        control.add(b_end);
        this.add(control, BorderLayout.SOUTH);
	}
	
	public CreatGraph(long[] problemNccsCounters1, long[] problemNccsCounters2, long[] problemNccsCounters3, String title)
	{
		super(title);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		XYDataset dataset = createDataset(problemNccsCounters1, problemNccsCounters2, problemNccsCounters3);
        JFreeChart chart = createChart(dataset,title);
        chartPanel = new ChartPanel(chart);
        this.add(chartPanel,BorderLayout.CENTER);
        JButton b_end = new JButton("Close");      
        b_end.addActionListener(this);
        JPanel control = new JPanel();
        control.add(b_end);
        this.add(control, BorderLayout.SOUTH);
		// TODO Auto-generated constructor stub
	}
	
	public CreatGraph(long[] problemNccsCounters, long[] problemMessagesCounters, String title)
	{
		super(title);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		XYDataset dataset = createDataset(problemNccsCounters, problemMessagesCounters);
        JFreeChart chart = createChart(dataset,title);
        chartPanel = new ChartPanel(chart);
        this.add(chartPanel,BorderLayout.CENTER);
        JButton b_end = new JButton("Close");      
        b_end.addActionListener(this);
        JPanel control = new JPanel();
        control.add(b_end);
        this.add(control, BorderLayout.SOUTH);
		// TODO Auto-generated constructor stub
	}
	
	 private XYDataset createDataset(long[] problemNccsCounters, long[] problemMessagesCounters, Status_opt[] status) 
	 {
		 	final XYSeries solution_serias = new XYSeries("solution");
	        final XYSeries ncccs_serias = new XYSeries("Ncccs");
	        final XYSeries message_serias = new XYSeries("Message Transfer");
	        
	        for(int i=0; i< problemNccsCounters.length ; i++)
	        {
	        	if(status[i] != Status_opt.NoSolution)
	        	{
	        		solution_serias.add(i+1,problemNccsCounters[i]);
	        		//solution_serias.add(i,problemMessagesCounters[i]);
	        	}
	        	ncccs_serias.add(i+1, problemNccsCounters[i]);
	        	message_serias.add(i+1,problemMessagesCounters[i]);
	        }
	        
	        final XYSeriesCollection dataset = new XYSeriesCollection();
	        dataset.addSeries(solution_serias);
	        dataset.addSeries(ncccs_serias);
	        dataset.addSeries(message_serias);

	                
	        return dataset;
	        
	    }
	 
	 private XYDataset createDataset(long[] problemNccsCounters, long[] problemMessagesCounters) 
	 {
	        final XYSeries ncccs_serias = new XYSeries("Ncccs");
	        final XYSeries message_serias = new XYSeries("MessegesTransered");
	        
	        double j = 0.1;
	        for(int i=0; i< problemNccsCounters.length ; i++)
	        {
	        	ncccs_serias.add(j, problemNccsCounters[i]);
	        	message_serias.add(j,problemMessagesCounters[i]);
	        	j+=0.1;
	        }
	        
	        final XYSeriesCollection dataset = new XYSeriesCollection();
	        dataset.addSeries(ncccs_serias);
	        dataset.addSeries(message_serias);
	                
	        return dataset;
	        
	    }
	 
	 private XYDataset createDataset(long[] problemNccsCounters1, long[] problemNccsCounters2, long[] problemNccsCounters3) 
	 {
		 	final XYSeries solution_serias = new XYSeries("SBT");
	        final XYSeries ncccs_serias = new XYSeries("AFC");
	        final XYSeries message_serias = new XYSeries("CBJ");
	        
	        for(int i=0; i< problemNccsCounters1.length ; i++)
	        {
	        	solution_serias.add(i+1,problemNccsCounters1[i]);
	        	ncccs_serias.add(i+1, problemNccsCounters2[i]);
	        	message_serias.add(i+1,problemNccsCounters3[i]);
	        }
	        
	        final XYSeriesCollection dataset = new XYSeriesCollection();
	        dataset.addSeries(solution_serias);
	        dataset.addSeries(ncccs_serias);
	        dataset.addSeries(message_serias);
	                
	        return dataset;
	        
	    }
	 
	
	 private JFreeChart createChart(final XYDataset dataset, String properties) {
		 String str = "Current Experiment ";
		 if(Main.SBTrun)
			 str+=" - SBT";
		 else if(Main.AFCrun)
			 str+=" - AFC";
		 else if (Main.CBJrun)
			 str+= " - CBJ";
		 else
		 {
			 str = "Comperable - " + properties;
		 }
	        String axis_y = "Experiment Number";
	        
	        if (Main.vary_p2)
	        {
	        	axis_y = "P2";
	        }
	        // create the chart...
	        final JFreeChart chart = ChartFactory.createXYLineChart(
	            str,      // chart title
	            axis_y,// x axis label
	            "Value",              // y axis label         
	            dataset,                  // data
	            PlotOrientation.VERTICAL,
	            true,                     // include legend
	            true,                     // tooltips
	            false                     // urls
	        );

	        chart.setBackgroundPaint(Color.white);


	        final XYPlot plot = chart.getXYPlot();
	        plot.setBackgroundPaint(Color.white);

	        plot.setDomainGridlinePaint(Color.lightGray);
	        plot.setRangeGridlinePaint(Color.lightGray);
	        
	        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
	        
	        if(Main.ComparAlgo || Main.vary_p2)
	        {
	        	renderer.setSeriesLinesVisible(1, true);
	        	renderer.setSeriesShapesVisible(0, true);
	        }
	        else
	        {
	        	renderer.setSeriesLinesVisible(0, false);
	        	renderer.setSeriesShapesVisible(0, true);
	        }
	        plot.setRenderer(renderer);


	        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
	        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

	                
	        return chart;
	        
	    }
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		// TODO Auto-generated method stub
		this.dispose();
		this.setVisible(false);
		
	}

}
